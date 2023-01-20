package tech.xken.tripbook.data.models

import tech.xken.tripbook.data.models.Results.Success


/**
 * A generic class to manage the state of async operations
 * @param <T>
 */
sealed class Results<out R> {

    data class Success<out T>(val data: T) : Results<T>()
    data class Failure(val exception: Exception) : Results<Nothing>()

    /**
     * Used for debugging and testing
     */
    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Failure -> "Error[exception=$exception]"
        }
    }
}

val <T> Results<T>.data get() = (this as Success).data
val <T> Results<T>.exception get() = (this as Results.Failure).exception


/**
 * `true` if [Results] is of type [Success] & holds non-null success data
 */
val Results<*>.succeeded get() = this is Success && data != null

// We are still loading when no decision has been taken yet. Neither success nor failure
//val Results<*>.loading get() = this !is Success && this !is Results.Failure