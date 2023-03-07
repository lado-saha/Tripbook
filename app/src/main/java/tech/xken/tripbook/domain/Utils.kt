package tech.xken.tripbook.domain

import android.telephony.PhoneNumberUtils
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.*
import java.util.*
import kotlin.coroutines.CoroutineContext

fun isCodeInvalid(code: String?) = codeCountryMap[code ?: ""] == null

/**
 * Returns true if the phone number is invalid and true else
 */
fun isPhoneInvalid(phone: String?, code: String?): Boolean {
    val country = codeCountryMap[code ?: ""]
    val formatted = PhoneNumberUtils.formatNumber(phone ?: "", country ?: "")
    return isCodeInvalid(code) || formatted == null || formatted == (phone ?: "")
}

/*
//Reverses a Pair (x , y) to (y, x)
val <A, B> Pair<A, B>.reverse: Pair<B, A>
    get() {
        return second to first
    }
*/
/**
 * A generic class that holds a loading signal or a [Results].
 */
sealed class Async<out T> {
    object Loading : Async<Nothing>()
    data class Success<out T>(val data: T) : Async<T>()
}

/**
 * Maximum time after which the state observers must stop observing any flow
 */
private const val StopTimeoutMillis: Long = 5000

/**
 * A [SharingStarted] meant to be used with a [StateFlow] to expose data to the UI.
 *
 * When the UI stops observing, upstream flows stay active for some time to allow the system to
 * come back from a short-lived configuration change (such as rotations). If the UI stops
 * observing for longer, the cache is kept but the upstream flows are stopped. When the UI comes
 * back, the latest value is replayed and the upstream flows are executed again. This is done to
 * save resources when the app is in the background but let users switch between apps quickly.
 */
val WhileUiSubscribed: SharingStarted = SharingStarted.WhileSubscribed(StopTimeoutMillis)

/**
 * Capitalises the string
 */
val String.caps
    get() = replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }

/**
 * Capitalises each word
 */
val String.titleCase
    get() = this.split(" ").map { it.caps }.fold("") { acc, s -> "$acc $s" }

val String.propre
    get() = this.lowercase().replace("é", "e").replace("è", "e").replace("-", " " +
            "")

/**
 * This is used in searching. It works by comparing a shorter string [inputStr] to the equivalent part of [referenceStr].
 * To do this, we first make sure [inputStr] length is less than or equals to [referenceStr]. In this case, we cut (starting
 * from the beginning and with equal length as [inputStr]), [referenceStr] and compare with [inputStr] to see if they are the same.
 * @param inputStr is the supposed shorter string which is usually the input we want to compare with [str2
 * @param referenceStr is the supposed longer reference string.
 */
fun strAreTheSame(inputStr: String, referenceStr: String): Boolean {
    val inputStr1 = inputStr.filterNot { it == ' ' }
    val referenceStr1 = referenceStr.filterNot { it == ' ' }
    return if (inputStr1.length > referenceStr1.length) false
    else
        referenceStr1.substring(inputStr1.indices).contentEquals(inputStr1, ignoreCase = true)
}


/**
 * This disables all the children of the composable
 * @param disabled If true, all children are disable
 */
fun Modifier.disableComposable(disabled: Boolean = true) =
    if (disabled)
        pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    awaitPointerEvent(PointerEventPass.Initial)
                        .changes
                        .forEach(PointerInputChange::consume)
                }
            }
        }
    else this