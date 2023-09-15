package tech.xken.tripbook.domain

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.telephony.PhoneNumberUtils
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.codeCountryMap
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation





val DATE_NOW
    get() = Calendar.getInstance().run {
        LocalDate(
            this[Calendar.YEAR],
            this[Calendar.MONTH],
            this[Calendar.DAY_OF_MONTH]
        )
    }

fun LocalDate.format(
    pattern: String = "EEEE dd MMMM yyyy",
    locale: Locale = Locale.getDefault()
): String {
    val calendarTime =
        Calendar.getInstance().apply { set(year, monthNumber, dayOfMonth) }.timeInMillis
    return SimpleDateFormat(pattern, locale).format(calendarTime)
}

/*//Password
            OutTextField(
                modifier = Modifier
                    .padding(fieldPadding)
                    .fillMaxWidth(),
                value = uis.bookerCredentials.password ?: "",
                errorText = { vm.passwordErrorText(it) },
                onValueChange = { vm.onPasswordChange(it) },
                visualTransformation = if (uis.isPeekingPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { vm.invertPasswordPeeking() }) {
                        Icon(
                            imageVector = if (uis.isPeekingPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = "",
                        )
                    }
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Password, contentDescription = "")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    autoCorrect = false, imeAction = ImeAction.Go
                ),
                label = { Text(stringResource(R.string.lb_password).caps) },
                keyboardActions = KeyboardActions(
                    onGo = {
                        vm.signInBooker()
                    }
                )
            )*/
/*TextButton(onClick = { *//*TODO*//* }) {
                Text(
                    stringResource(id = R.string.lb_forgot_pswd).caps,
                    style = MaterialTheme.typography.caption
                )
            }

            TextButton(onClick = { *//*TODO*//* }, modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    stringResource(id = R.string.lb_no_account_yet_sign_up).caps,
                    style = MaterialTheme.typography.caption
                )
            }*/
fun isCodeInvalid(code: String?) = codeCountryMap[code ?: ""] == null

/**
 * Returns true if the phone number is invalid and true else
 */
fun isPhoneInvalid(phone: String?, code: String?): Boolean {
    val country = codeCountryMap[code ?: ""]
    val formatted = PhoneNumberUtils.formatNumber(phone ?: "", country ?: "")
    return isCodeInvalid(code) || formatted == null || formatted == (phone ?: "")
}

/**
 * Returns true if the phone number is invalid and true else
 */
fun isMTNPhoneValid(phone: String) = listOf("7", "8", "5").contains(phone[1].toString())
fun isOrangePhoneValid(phone: String) = listOf("9", "8", "5").contains(phone[1].toString())


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
//sealed class Async<out T> {
//    object Loading : Async<Nothing>()
//    data class Success<out T>(val data: T) : Async<T>()
//}

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
    get() = this.lowercase().replace("é", "e").replace("è", "e").replace(
        "-", " " +
                ""
    )

/**
 * This is used in searching. It works by comparing a shorter string [inputStr] to the equivalent part of [referenceStr].
 * To do this, we first make sure [inputStr] length is less than or equals to [referenceStr]. In this case, we cut (starting
 * from the beginning and with equal length as [inputStr]), [referenceStr] and compare with [inputStr] to see if they are the same.
 * @param inputStr is the supposed shorter string which is usually the input we want to compare with [str2
 * @param referenceStr is the supposed longer reference string.
 */
infix fun String.subsetOf(referenceStr: String): Boolean {
    val inputStr1 = filterNot { it == ' ' }
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


val DEFAULT_UNIV_QUERY_FIELDS = arrayOf(
    R.string.lb_town,
    R.string.lb_subdivision,
    R.string.lb_division,
    R.string.lb_region,
    R.string.lb_country,
    R.string.lb_continent,
    R.string.lb_planet,
)

const val TAG = "MyTagConnectionManager"


class NetworkState(private val context: Context) {
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isConnected.value = true
        }

        override fun onLost(network: Network) {
            _isConnected.value = false
        }
    }

    init {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }
}

//class NetworkState(context: Context) {
//    private val connectivityManager =
//        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//    private val validNetworks: MutableSet<Network> = HashSet()
//    private val _isConnected = MutableStateFlow(false)
//
//    val isConnected: StateFlow<Boolean>
//        get() = _isConnected
//
//    private fun checkValidNetworks() {
//        _isConnected.value = validNetworks.size > 0
//    }
//
//    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
//        override fun onAvailable(network: Network) {
//            Log.d(TAG, "onAvailable: $network")
//            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
//            val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
//            Log.d(TAG, "onAvailable: ${network}, $hasInternetCapability")
//
//            if (hasInternetCapability == true) {
//                // Check if this network actually has internet
//                CoroutineScope(Dispatchers.IO).launch {
//                    val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
//                    if (hasInternet) {
//                        withContext(Dispatchers.Main) {
//                            Log.d(TAG, "onAvailable: adding network. $network")
//                            validNetworks.add(network)
//                            checkValidNetworks()
//                        }
//                    }
//                }
//            }
//        }
//
//        override fun onLost(network: Network) {
//            Log.d(TAG, "onLost: $network")
//            validNetworks.remove(network)
//            checkValidNetworks()
//        }
//    }
//
//    fun start() {
//        val networkRequest = NetworkRequest.Builder()
//            .addCapability(NET_CAPABILITY_INTERNET)
//            .build()
//        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
//    }
//
//    fun stop() {
//        connectivityManager.unregisterNetworkCallback(networkCallback)
//    }
//
//    object DoesNetworkHaveInternet {
//
//        fun execute(socketFactory: SocketFactory): Boolean {
//            // Make sure to execute this on a background thread.
//            return try {
//                Log.d(TAG, "PINGING Google...")
//                val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
//                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
//                socket.close()
//                Log.d(TAG, "PING success.")
//                true
//            } catch (e: IOException) {
//                Log.e(TAG, "No Internet Connection. $e")
//                false
//            }
//        }
//    }
//
//    companion object {
//        private const val TAG = "ConnectionStateFlow"
//    }
//
//
//}
//fun passwordStrength(password: String): Int {
//    val containsNumbers = password.indexOfAny(list())
//}

/**
 * Compare field and return the serialNames for fields which have are not the same
 */
fun <T : Any> diffFields(old: T, new: T): Map<String, Any?> {
    val differentFields = mutableMapOf<String, Any?>()
    for (property in old::class.declaredMemberProperties) {
        if (property.call(old) != property.call(new)) {
            val serialName = property.findAnnotation<SerialName>()?.value ?: property.name
            val newValue = property.call(new)
            differentFields[serialName] = newValue
        }
    }
    return differentFields
}
/*class ConnectionLiveData(context: Context) : LiveData<Boolean>() {
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val connectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = HashSet()

    private fun checkValidNetworks() {
        postValue(validNetworks.size > 0)
    }

    override fun onActive() {
        networkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            Log.d(TAG, "onAvailable: $network")
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
            Log.d(TAG, "onAvailable: ${network}, $hasInternetCapability")

            if (hasInternetCapability == true) {
                // Check if this network actually has internet
                CoroutineScope(Dispatchers.IO).launch {
                    val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
                    if (hasInternet) {
                        withContext(Dispatchers.Main) {
                            Log.d(TAG, "onAvailable: adding network. $network")
                            validNetworks.add(network)
                            checkValidNetworks()
                        }
                    }
                }
            }
        }

        override fun onLost(network: Network) {
            Log.d(TAG, "onLost: $network")
            validNetworks.remove(network)
            checkValidNetworks()
        }
    }

    object DoesNetworkHaveInternet {

        fun execute(socketFactory: SocketFactory): Boolean {
            // Make sure to execute this on a background thread.
            return try {
                Log.d(TAG, "PINGING Google...")
                val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                socket.close()
                Log.d(TAG, "PING success.")
                true
            } catch (e: IOException) {
                Log.e(TAG, "No Internet Connection. $e")
                false
            }
        }
    }
}*/
//class ConnectionStateFlow(override val replayCache: List<Boolean>, override val value: Boolean): StateFlow<Boolean>() {
//    override suspend fun collect(collector: FlowCollector<Boolean>): Nothing {
//        TODO("Not yet implemented")
//    }
//}

