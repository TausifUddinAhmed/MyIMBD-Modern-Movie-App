package com.myimbd.domain.features.app

interface AppRepository {

    suspend fun isPlayStoreReviewAlertAlreadyShown(): Boolean
    suspend fun setPlayStoreReviewAlertShown()

}
