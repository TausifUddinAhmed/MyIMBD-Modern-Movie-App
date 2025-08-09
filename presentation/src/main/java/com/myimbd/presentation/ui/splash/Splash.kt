package com.myimbd.presentation.ui.splash


import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.myimbd.presentation.R
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSuccess: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    var startAnimation by remember { mutableStateOf(false) }
    val context = LocalContext.current



    // Ask the VM to decide once when we enter
    LaunchedEffect(Unit) {
        viewModel.decideFetch()
    }

    // Once decision is known, either fetch or go ahead
    LaunchedEffect(viewModel.shouldFetchDataFromRemote) {
        if (viewModel.shouldFetchDataFromRemote) {
            if (isNetworkAvailable(context)) {
                viewModel.loadMovies()
            } else {
                Toast
                    .makeText(context, "No Internet Connection", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // Collect state
    val state by viewModel.state.collectAsState()

    // When success, give the animation a moment then go
    LaunchedEffect(state) {
        if (state is SplashViewModel.SplashUiState.Success) {
            delay(300) // tiny buffer so the scale finishes nicely
            onSuccess()
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 800, easing = EaseOutBack),
        label = "splash-scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.mymdb),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale)
        )

        when (state) {
            SplashViewModel.SplashUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.BottomCenter))
            }
            is SplashViewModel.SplashUiState.Error -> {
                Text(
                    text = "Failed to load. Retrying…",
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
                // Optional: auto-retry after a short delay
                LaunchedEffect("retry") {
                    delay(1000)
                    viewModel.loadMovies()
                }
            }
            else -> Unit
        }
    }
}

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NET_CAPABILITY_INTERNET)
}