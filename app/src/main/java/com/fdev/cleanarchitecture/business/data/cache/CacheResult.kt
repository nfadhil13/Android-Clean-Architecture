package com.fdev.cleanarchitecture.business.data.cache

import com.google.android.gms.common.internal.ConnectionErrorMessages

sealed class CacheResult <out T>{

    data class Success<out T>(val value: T) : CacheResult<T>()

    data class GenericError(
        val errorMessages: String? = null
    ) : CacheResult<Nothing>()

}