package com.myimbd.presentation.ui.wishlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
fun WishlistScreen(
    viewModel: WishlistViewModel = hiltViewModel(),
    onMovieClick: (Movie) -> Unit = {}
) {
    val items = viewModel.wishlist.collectAsLazyPagingItems()
    WishlistContent(
        movies = items,
        onMovieClick = onMovieClick,
        onWishlistToggle = viewModel::onWishlistClick
    )
}

@Composable
private fun WishlistContent(
    movies: LazyPagingItems<Movie>,
    onMovieClick: (Movie) -> Unit,
    onWishlistToggle: (Movie) -> Unit
) {
    Scaffold { padding ->
        val listState = rememberLazyListState()

        // Handle initial load first
        when (val refresh = movies.loadState.refresh) {
            is LoadState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(top = 32.dp),
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
                        message = refresh.error.message ?: "Failed to load wishlist",
                        onRetry = { movies.retry() }
                    )
                }
                return@Scaffold
            }
            is LoadState.NotLoading -> {
                // Empty state for wishlist
                if (movies.itemCount == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Your wishlist is empty")
                    }
                    return@Scaffold
                }
            }
        }

        // Normal list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(
                count = movies.itemCount,
                key = movies.itemKey { it.id },
                contentType = { "wishlist_row" }
            ) { index ->
                val movie = movies[index]
                if (movie == null) {
                    MovieRowPlaceholder()
                } else {
                    WishlistRow(
                        movie = movie,
                        onClick = { onMovieClick(movie) },
                        onWishlistToggle = { onWishlistToggle(movie) }
                    )
                }
            }

            // Footer (append)
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

@Composable
private fun WishlistRow(
    movie: Movie,
    onClick: () -> Unit,
    onWishlistToggle: () -> Unit
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

        // Toggle heart
        IconButton(onClick = onWishlistToggle) {
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
private fun ErrorRow(message: String, onRetry: () -> Unit) {
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
