package com.myimbd.presentation.models

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.myimbd.presentation.R

enum class BottomNavigationItem(val route: Screen, val icon: ImageVector, @StringRes val title: Int) {

    Market(
        route = Screen.MovieList,
        icon = Icons.AutoMirrored.Filled.MenuBook,
        title = R.string.movies
    ),
    Favourites(
        route = Screen.WishList,
        icon = Icons.Default.Star,
        title = R.string.wishlist
    ),


}