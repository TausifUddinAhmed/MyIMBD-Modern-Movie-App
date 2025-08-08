package com.myimbd.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface Screen : Parcelable {

    @Parcelize
    object MovieList : Screen

    @Parcelize
    object WishList : Screen


}
