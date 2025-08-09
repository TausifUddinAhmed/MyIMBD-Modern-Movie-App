package com.myimbd.domain.features.app

import javax.inject.Inject

class IsMovieDataStoreAlreadyShownUseCase @Inject constructor(
    private val repository: AppRepository
) {

    suspend operator fun invoke(): Boolean {
        return repository.isMovieDataAlreadyStored()
    }

}