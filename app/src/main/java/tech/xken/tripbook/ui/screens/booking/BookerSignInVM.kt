package tech.xken.tripbook.ui.screens.booking

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tech.xken.tripbook.R
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.booker.BookerCredentials
import tech.xken.tripbook.data.models.codeCountryMap
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.domain.isCodeInvalid
import tech.xken.tripbook.domain.isPhoneInvalid
import tech.xken.tripbook.ui.navigation.BookingNavArgs
import javax.inject.Inject

data class BookerSignInUiState(
    val isLoading: Boolean = false,
    val message: Int? = null,
    val bookerCredentials: BookerCredentials = BookerCredentials(),
    val isPeekingPassword: Boolean = false,
    val isFirstSignIn: Boolean = true,
    val isRequestingToken: Boolean = false,
    val resendAfterInMillis: Long = 0L,
    val isComplete: Boolean = false,
    val shouldSignOut: Boolean = false,
    val dialogState: BookerSignInDialogState = BookerSignInDialogState.NONE
)

@HiltViewModel
class BookerSignInVM @Inject constructor(
    private val authRepo: AuthRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<Int?>(null)
    private val _credentials = MutableStateFlow(BookerCredentials())
    private val _shouldSignOut =
        MutableStateFlow(savedStateHandle.get<Boolean>(BookingNavArgs.SHOULD_SIGN_OUT)!!)
    private val _isPeekingPassword = MutableStateFlow(false)
    private val _isComplete = MutableStateFlow(false)
    private val _isFirstSignIn = MutableStateFlow(true)
    private val _isRequestingToken = MutableStateFlow(false)
    private val _resendAfterInMillis = MutableStateFlow(0L)
    private val _dialogState = MutableStateFlow(BookerSignInDialogState.NONE)

    init {
        if (_shouldSignOut.value)
            _dialogState.value = BookerSignInDialogState.CONFIRM_SIGN_OUT
    }

    private val _resendCountDown = object : CountDownTimer(61_000L, 1000L) {
        override fun onTick(p0: Long) {
            _resendAfterInMillis.value = p0
        }

        override fun onFinish() {
            _resendAfterInMillis.value = 0L
        }
    }

    val uiState = combine(
        _isLoading,
        _message,
        _credentials,
        _isPeekingPassword,
        _isComplete,
        _isFirstSignIn,
        _isRequestingToken,
        _resendAfterInMillis,
        _shouldSignOut,
        _dialogState
    ) { args ->
        BookerSignInUiState(
            isLoading = args[0] as Boolean,
            message = args[1] as Int?,
            bookerCredentials = args[2] as BookerCredentials,
            isPeekingPassword = args[3] as Boolean,
            isComplete = args[4] as Boolean,
            isFirstSignIn = args[5] as Boolean,
            isRequestingToken = args[6] as Boolean,
            resendAfterInMillis = args[7] as Long,
            shouldSignOut = args[8] as Boolean,
            dialogState = args[9] as BookerSignInDialogState
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = BookerSignInUiState()
    )

    fun signOut(
        onSuccessful: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            authRepo.signOut().also {
                when (it) {
                    is Results.Failure -> {
                        _message.value = R.string.msg_sign_out_fail
                        Log.e("SIGNOUT", it.exception.toString())
                        _isLoading.value = false
                    }

                    is Results.Success -> {
                        _message.value = R.string.msg_sign_out_passed
                        _dialogState.value = BookerSignInDialogState.NONE
                        _isLoading.value = false
                        onSuccessful()
                    }
                }
            }
        }
    }

    fun onDialogStateChange(new: BookerSignInDialogState) {
        _dialogState.value = new
    }

    fun onCompleteChange(new: Boolean) {
        _isComplete.value = new
    }

    fun onLoading(new: Boolean) {
        _isLoading.value = new
    }

    fun onMessageChange(new: Int?) {
        _message.value = new
    }

    fun onPhoneCodeChange(new: String) {
        _credentials.value = _credentials.value.copy(phoneCode = new)
    }

    fun cancelVerification() {
        _isRequestingToken.value = false
    }

    val countryFromCode get() = codeCountryMap[_credentials.value.phoneCode] ?: ""

    fun onPhoneChange(new: String) {
        _credentials.value = _credentials.value.copy(phoneNumber = new)
    }

    fun onTokenChange(new: String) {
        _credentials.value = _credentials.value.copy(token = new)
    }

    fun phoneCodeErrorText(text: String? = _credentials.value.phoneCode) = when {
        text.isNullOrBlank() -> null
        isCodeInvalid(text) -> R.string.msg_invalid_field
        else -> null
    }

    fun phoneErrorText(text: String? = _credentials.value.phoneNumber) = when {
        text.isNullOrBlank() -> R.string.msg_required_field
        isPhoneInvalid(text, _credentials.value.phoneCode) -> R.string.msg_invalid_field
        else -> null
    }

    fun tokenErrorText(text: String? = _credentials.value.token) = when {
        !_isRequestingToken.value -> null
        text.isNullOrBlank() -> R.string.msg_required_field
        text.length != 10 -> R.string.msg_invalid_field
        else -> null
    }

    private val isNoError get() = phoneErrorText() == null && phoneCodeErrorText() == null

    fun verifyBookerPhone(doOnStart: () -> Unit, doOnFinish: () -> Unit) {
        if (tokenErrorText() == null) viewModelScope.launch {
            onLoading(true)
            doOnStart()
            authRepo.bookerPhoneVerification(_credentials.value).also {
                when (it) {
                    is Results.Failure -> {
                        Log.e("Verification", it.exception.toString())
                        _isRequestingToken.value = true
                        onMessageChange(R.string.msg_invalid_phone_token)
                        onLoading(false)
                    }

                    is Results.Success -> {
                        doOnFinish()
                        _isRequestingToken.value = false
                        onTokenChange("")
                        _isComplete.value = true
                        onLoading(false)
                    }
                }
            }

        } else {
            onMessageChange(R.string.msg_fields_contain_errors)
        }
    }

    fun signInBooker(doOnStart: () -> Unit, doOnFinish: () -> Unit) {
        if (isNoError) viewModelScope.launch {
            doOnStart()
            onLoading(true)
            authRepo.bookerPhoneAuth(_credentials.value).also {
                when (it) {
                    is Results.Failure -> {
                        Log.e("SIGN_IN", it.exception.toString())
                        _message.value = R.string.msg_send_sms_error
                    }

                    is Results.Success -> {
                        _isRequestingToken.value = true
                        _resendCountDown.start()
                        doOnFinish()
                    }
                }
            }
            onLoading(false)
        } else onMessageChange(R.string.msg_fields_contain_errors)

    }

    fun resendConfirmationToken(doOnStart: () -> Unit, doOnFinish: () -> Unit) {
        if (_isRequestingToken.value) viewModelScope.launch {
            onLoading(true)
            doOnStart()
            authRepo.resendPhoneToken(_credentials.value).also {
                when (it) {
                    is Results.Failure -> {
                        _message.value = R.string.msg_resend_sms_error
                        Log.e("SIGN_IN", it.exception.toString())
                    }

                    is Results.Success -> {
                        _isRequestingToken.value = false
                        _resendCountDown.start()
                        _isRequestingToken.value = true
                        doOnFinish()
                    }
                }
            }
            onLoading(false)
        }
    }

}

enum class BookerSignInDialogState {
    HELP_MAIN_PAGE, CONFIRM_SIGN_OUT, NONE
}