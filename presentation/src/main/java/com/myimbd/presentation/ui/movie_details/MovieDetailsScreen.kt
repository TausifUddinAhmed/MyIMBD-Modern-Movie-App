@file:OptIn(ExperimentalMaterial3Api::class)

package com.myimbd.presentation.ui.movie_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MovieDetailsScreen(
    movieId: Int,
    viewModel: MovieDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val movieFlow = remember(movieId) { viewModel.movie(movieId) }
    val movie = movieFlow.collectAsState(initial = null).value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(movie?.title ?: "Movie") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (movie != null) {
                        IconButton(onClick = { viewModel.toggleWishlist(movie) }) {
                            val icon = if (movie.isWishListed)
                                Icons.Filled.Favorite
                            else
                                Icons.Outlined.FavoriteBorder
                            Icon(icon, contentDescription = "Toggle wishlist")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (movie == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item("poster") {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(movie.posterUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "${movie.title} poster",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                )
            }

            item("title") {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Year: ${movie.year}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (movie.genres.isNotEmpty()) {
                item("genres") {
                    FlowRow(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        movie.genres.forEach { g ->
                            AssistChip(
                                onClick = { /* maybe filter by genre later */ },
                                label = { Text(g) }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            if (movie.director.isNotBlank() || movie.actors.isNotBlank()) {
                item("credits") {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        if (movie.director.isNotBlank()) {
                            Text(
                                text = "Director: ${movie.director}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (movie.actors.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Actors: ${movie.actors}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            if (movie.plot.isNotBlank()) {
                item("plot") {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Plot",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = movie.plot,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item("bottom-space") { Spacer(Modifier.height(24.dp)) }
        }
    }
}
