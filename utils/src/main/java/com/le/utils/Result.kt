package com.le.utils

interface FailureEntity

data class NetworkFailure(val throwable: Throwable) : FailureEntity

sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure<T>(val error: FailureEntity) : Result<T>()

    fun <R> ifSuccess(ifSuccess: (T) -> R) {
        if (this is Success) {
            ifSuccess(data)
        }
    }

    fun <R> ifFailure(ifFailure: (FailureEntity) -> R) {
        if (this is Failure) {
            ifFailure(error)
        }
    }

    fun <R> fold(ifSuccess: (T) -> R, ifFailure: (FailureEntity) -> R) {
        when (this) {
            is Success -> {
                ifSuccess(data)
            }
            is Failure -> {
                ifFailure(error)
            }
        }

    }
}

fun <T> T.asSuccess(): Result.Success<T> =
    Result.Success(this)
fun Throwable.asNetworkFailure(): Result.Failure<Nothing> =
    Result.Failure(
        NetworkFailure(this)
    )
