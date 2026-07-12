package com.innovaction.finance.util

/**
 * Wrapper générique pour les résultats d'opérations asynchrones.
 * Utilisé dans les ViewModels pour exposer l'état Loading / Success / Error.
 */
sealed class Result<out T> {
    data object Loading : Result<Nothing>()
    data class  Success<T>(val data: T)     : Result<T>()
    data class  Error(val message: String, val cause: Throwable? = null) : Result<Nothing>()
}

/** Extension pratique pour éviter les when() répétitifs. */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (String, Throwable?) -> Unit): Result<T> {
    if (this is Result.Error) action(message, cause)
    return this
}
