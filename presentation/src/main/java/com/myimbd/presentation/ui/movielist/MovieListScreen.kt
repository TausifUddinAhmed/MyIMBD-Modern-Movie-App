package com.myimbd.presentation.ui.movielist
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.myimbd.presentation.models.Screen
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun MovieListScreen(
    viewModel: MovieListViewModel = hiltViewModel(),
    onMovieClick: (Movie) -> Unit = {},
    //navController: NavController<Screen>,
    ) {
    val movies = viewModel.movies.collectAsLazyPagingItems()
    MovieListContent(movies, onMovieClick)
}

@Composable
private fun MovieListContent(
    movies: LazyPagingItems<Movie>,
    onMovieClick: (Movie) -> Unit
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Items
            items(
                count = movies.itemCount,
                key = movies.itemKey { it.id } // stable keys improve perf
            ) { index ->
                val item = movies[index]
                if (item == null) {
                    MovieRowPlaceholder()
                } else {
                    MovieRow(item) { onMovieClick(item) }
                }
            }

            // Load state: append (list footer)
            when (val state = movies.loadState.append) {
                is LoadState.Loading -> {
                    item { ListLoading() }
                }
                is LoadState.Error -> {
                    item {
                        ErrorRow(
                            message = state.error.message ?: "Failed to load more",
                            onRetry = { movies.retry() }
                        )
                    }
                }
                else -> Unit
            }

            // Handle initial load (refresh) errors or loading
            when (val state = movies.loadState.refresh) {
                is LoadState.Loading -> {
                    item { FullScreenLoading() }
                }
                is LoadState.Error -> {
                    item {
                        ErrorRow(
                            message = state.error.message ?: "Failed to load movies",
                            onRetry = { movies.retry() }
                        )
                    }
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun MovieRow(
    movie: Movie,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.posterUrl)
                .crossfade(true)
                .build(),
            contentDescription = "${movie.title} poster",
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
            Text(
                text = "Director: ${movie.director}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Genres: ${movie.genres.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Year: ${movie.year}",
                style = MaterialTheme.typography.bodySmall
            )
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
private fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ListLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
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
        TextButton(onClick = onRetry) {
            Text("Retry")
        }
    }
}
