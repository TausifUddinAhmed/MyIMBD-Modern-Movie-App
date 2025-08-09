package com.myimbd.domain.features.app

import javax.inject.Inject

class SetMovieDataStoredShownUseCase @Inject constructor(
    private val repository: AppRepository
) {

    suspend operator fun invoke() {
        return repository.setMovieDataStored()
    }

}