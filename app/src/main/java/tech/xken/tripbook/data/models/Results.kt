package tech.xken.tripbook.data.models

import tech.xken.tripbook.data.models.Results.Success


/**
 * A generic class to manage the state of async operations
 * @param <T>
 */
sealed class Results<out R> {

    data class Failure(val exception: Exception) : Results<Nothing>()
    data class Success<out T>(val data: T) : Results<T>()

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
/*sealed class Results<T> {
    data class Success<T>(val data: T) : Results<T>()
    data class Failure<T>(val exception: Exception) : Results<T>()

    *//**
 * Used for debugging and testing
 *//*
    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Failure -> "Error[exception=$exception]"
        }
    }
    companion object{
        fun <T> success(data: T) = Success(data)
        fun <T> failure(exception: Exception) = Failure<T>(exception)
    }

}*/

val <T> Results<T>.data get() = (this as Success).data
val <T> Results<T>.exception get() = (this as Results.Failure).exception


/**
 * `true` if [Results] is of type [Success] & holds non-null success data
 */
val Results<*>.succeeded get() = this is Success && data != null

// We are still loading when no decision has been taken yet. Neither success nor failure
//val Results<*>.loading get() = this !is Success && this !is Results.Failure