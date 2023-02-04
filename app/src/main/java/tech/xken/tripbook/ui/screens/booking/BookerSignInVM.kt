package tech.xken.tripbook.ui.screens.booking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.Booker
import tech.xken.tripbook.data.sources.booking.BookingRepository
import tech.xken.tripbook.domain.WhileUiSubscribed
import javax.inject.Inject

data class BookerSignInUiState(
    val isLoading: Boolean = false,
    val message: Int? = null,
    val booker: Booker = Booker.new(),
    val isPeekingPassword: Boolean = false,
    val isComplete: Boolean = false,
)

@HiltViewModel
class BookerSignInVM @Inject constructor(
    private val repo: BookingRepository,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<Int?>(null)
    private val _booker = MutableStateFlow(Booker.new())
    private val _isPeekingPassword = MutableStateFlow(false)
    private val _isComplete = MutableStateFlow(false)

    val uiState = combine(
        _isLoading,
        _message,
        _booker,
        _isPeekingPassword,
        _isComplete
    ) { args ->
        BookerSignInUiState(
            args[0] as Boolean,
            args[1] as Int?,
            args[2] as Booker,
            args[3] as Boolean,
            args[4] as Boolean,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = BookerSignInUiState()
    )

    fun onCompleteChange(new: Boolean){
        _isComplete.value = new
    }
    fun onLoading(new: Boolean) {
        _isLoading.value = new
    }

    fun onMessageChange(new: Int?) {
        _message.value = new
    }

    fun onNameChange(new: String) {
        _booker.value = _booker.value.copy(name = new)
    }

    fun onPasswordChange(new: String) {
        _booker.value = _booker.value.copy(password = new)
    }

    fun invertPasswordPeeking() {
        _isPeekingPassword.value = !_isPeekingPassword.value
    }

    fun nameErrorText(text: String? = _booker.value.name) =
        if (text.isNullOrBlank()) R.string.msg_required_field else null

    fun passwordErrorText(text: String? = _booker.value.password) = when {
        text.isNullOrBlank() -> R.string.msg_required_field
        text.length < 8 -> R.string.msg_invalid_password
        else -> null
    }

    private val isNoError get() = nameErrorText() == null && passwordErrorText() == null

    fun signInBooker() {
        onLoading(true)
        if (isNoError) viewModelScope.launch {
            try {
                repo.signInBookerFromNameCredentials(
                    _booker.value.name!!,
                    _booker.value.password!!
                )
                onMessageChange(R.string.msg_welcome_back)
                onCompleteChange(true)
            } catch (e: Exception) {
                Log.d("SIGN_IN", e.message.toString())
                onMessageChange(R.string.msg_invalid_name_credentials)
            }
        } else onMessageChange(R.string.msg_fields_contain_errors)

        onLoading(false)
    }

}