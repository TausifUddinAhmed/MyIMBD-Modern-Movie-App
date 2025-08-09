@file:OptIn(ExperimentalMaterial3Api::class)

package com.myimbd.presentation.ui.movielist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.myimbd.domain.models.Movie
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun MovieListScreen(
    viewModel: MovieListViewModel = hiltViewModel(),
    onMovieClick: (Movie) -> Unit = {},
    onWishlistIconClick: () -> Unit = {}
) {
    val movies = viewModel.movies.collectAsLazyPagingItems()

    MovieListContent(
        movies = movies,
        onMovieClick = onMovieClick,
        onWishlistClick = viewModel::onWishlistClick,
        onWishlistIconClick = onWishlistIconClick
    )
}

@Composable
private fun MovieListContent(
    movies: LazyPagingItems<Movie>,
    onMovieClick: (Movie) -> Unit,
    onWishlistClick: (Movie) -> Unit,
    onWishlistIconClick: () -> Unit
) {
    // UI state
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf<String?>(null) }

    // as ganre is fixed, didn't store it room db
    val allGenres = remember { listOf("Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Romance", "Thriller") }

    // Compute wishlist count from items currently loaded
    val wishlistCount by remember(movies.itemSnapshotList) {
        mutableIntStateOf(movies.itemSnapshotList.items.count { it.isWishListed == true })
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("MyIMDB") },
                    actions = {
                        IconButton(onClick = onWishlistIconClick) {
                            BadgedBox(
                                modifier = Modifier.offset(x = (-4).dp, y = (12).dp), // move inside and down
                                badge = {
                                    if (wishlistCount > 0) {
                                        Badge { Text(wishlistCount.toString()) }
                                    }
                                }
                            ) {
                                Icon(Icons.Filled.Favorite, contentDescription = "Wishlist")
                            }
                        }
                    }
                )

                // Search + filter row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        placeholder = { Text("Search..") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search,
                            keyboardType = KeyboardType.Text
                        )
                    )

                    Spacer(Modifier.width(8.dp))

                    GenreFilterDropdown(
                        genres = allGenres,
                        selected = selectedGenre,
                        onSelected = { selectedGenre = it }
                    )
                }
            }
        }
    ) { padding ->
        val listState = rememberLazyListState()

        // Initial load states
        when (val refresh = movies.loadState.refresh) {
            is LoadState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
                return@Scaffold
            }
            is LoadState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorRow(
                        message = refresh.error.message ?: "Failed to load movies",
                        onRetry = { movies.retry() }
                    )
                }
                return@Scaffold
            }
            else -> Unit
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(
                count = movies.itemCount,
                key = movies.itemKey { it.id },
                contentType = { "movie_row" }
            ) { index ->
                val item = movies[index]
                if (item == null) {
                    MovieRowPlaceholder()
                } else {
                    if (matchesFilters(item, searchQuery, selectedGenre)) {
                        MovieRow(
                            movie = item,
                            onClick = { onMovieClick(item) },
                            onWishlistClick = { onWishlistClick(item) }
                        )
                    } else {
                        // Skip drawing row when it doesn't match — zero height item
                        Spacer(Modifier.height(0.dp))
                    }
                }
            }

            // Footer load states
            when (val append = movies.loadState.append) {
                is LoadState.Loading -> item { ListLoading() }
                is LoadState.Error -> item {
                    ErrorRow(
                        message = append.error.message ?: "Failed to load more",
                        onRetry = { movies.retry() }
                    )
                }
                else -> Unit
            }
        }
    }
}

private fun matchesFilters(
    movie: Movie,
    query: String,
    selectedGenre: String?
): Boolean {
    val q = query.trim().lowercase()
    val matchesQuery =
        q.isEmpty() ||
                movie.title.lowercase().contains(q) ||
                movie.director.lowercase().contains(q) ||
                movie.plot.lowercase().contains(q) ||
                movie.actors.any { it.lowercase().contains(q) }

    val matchesGenre = selectedGenre == null || movie.genres.any { it.equals(selectedGenre, ignoreCase = true) }

    return matchesQuery && matchesGenre
}

@Composable
private fun GenreFilterDropdown(
    genres: List<String>,
    selected: String?,
    onSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val label = selected ?: "Genres"

    Box {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = { Icon(Icons.Filled.FilterList, contentDescription = null) },
            modifier = Modifier
                .widthIn(min = 120.dp)
                .clickable { expanded = true },
            enabled = false
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("All") },
                onClick = {
                    onSelected(null)
                    expanded = false
                }
            )
            genres.forEach { genre ->
                DropdownMenuItem(
                    text = { Text(genre) },
                    onClick = {
                        onSelected(genre)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun MovieRow(
    movie: Movie,
    onClick: () -> Unit,
    onWishlistClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.posterUrl)
                .crossfade(true)
                .build(),
            contentDescription = "${movie.title} poster",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(84.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (movie.director.isNotBlank()) {
                Text(
                    text = "Director: ${movie.director}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (movie.genres.isNotEmpty()) {
                Text(
                    text = "Genres: ${movie.genres.joinToString()}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "Year: ${movie.year}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        IconButton(onClick = onWishlistClick) {
            val icon = if (movie.isWishListed) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
            val desc = if (movie.isWishListed) "Remove from wishlist" else "Add to wishlist"
            Icon(imageVector = icon, contentDescription = desc)
        }
    }
}

@Composable
private fun MovieRowPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Surface(
            modifier = Modifier.size(84.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium
        ) {}
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {}
            Spacer(Modifier.height(6.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {}
            Spacer(Modifier.height(6.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {}
        }
    }
}

@Composable
private fun ListLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator() }
}

@Composable
private fun ErrorRow(
    message: String,
    onRetry: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.error
        )
        TextButton(onClick = onRetry) { Text("Retry") }
    }
}
