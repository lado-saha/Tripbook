package tech.xken.tripbook.ui.screens.booking

import android.telephony.PhoneNumberUtils
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tech.xken.tripbook.R
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.booker.BookerMoMoAccount
import tech.xken.tripbook.data.models.codeCountryMap
import tech.xken.tripbook.data.sources.booker.BookerRepository
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.domain.diffFields
import tech.xken.tripbook.domain.isMTNPhoneValid
import tech.xken.tripbook.domain.isPhoneInvalid
import javax.inject.Inject

data class BookerMoMoAccountsUiState(
    val isLoading: Boolean = false,
    val message: Int? = null,
    val accounts: List<BookerMoMoAccount> = listOf(),
    val isComplete: Boolean = false,
    val dialogState: BookerMoMoAccountsDialogState = BookerMoMoAccountsDialogState.NONE,
    val isInitComplete: Boolean = false,
    val toDelete: List<String> = listOf()
)

enum class BookerMoMoAccountsDialogState {
    NONE, HELP_MAIN_PAGE, COULD_NOT_GET_ACCOUNTS, LEAVING_WITHOUT_ACCOUNTS, DELETE_ACCOUNTS_WARNING
}

@HiltViewModel
class BookerMoMoAccountsVM @Inject constructor(
    private val repo: BookerRepository,
    private val authRepo: AuthRepo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<Int?>(null)
    private val _isComplete = MutableStateFlow(false)
    private val _dialogState = MutableStateFlow(BookerMoMoAccountsDialogState.NONE)
    private val _isInitComplete = MutableStateFlow(false)
    private val _toDelete = MutableStateFlow(listOf<String>())

    // Details
    private var oldAccountCopy = BookerMoMoAccount()
    private val _isEditMode = MutableStateFlow(false)
    private val _accountDetails = MutableStateFlow(BookerMoMoAccount())
    private val _isDetailsComplete = MutableStateFlow(false)
    private val _detailsDialogState = MutableStateFlow(BookerMoMoAccountDetailsDialogState.NONE)


    val uiState = combine(
        _isLoading,
        _message,
        _isComplete,
        repo.bookerMoMoAccounts(authRepo.bookerId!!)
            .onStart { onInitComplete(false) }
            .map {
                onInitComplete(true)
                when (it) {
                    is Results.Failure -> {
                        _message.value = R.string.msg_get_momo_accounts_error
                        onDialogStateChange(BookerMoMoAccountsDialogState.COULD_NOT_GET_ACCOUNTS)
                        listOf()
                    }

                    is Results.Success -> {
                        it.data
                    }

                }
            },
        _dialogState,
        _toDelete,

        ) { args ->
        BookerMoMoAccountsUiState(
            isLoading = args[0] as Boolean,
            message = args[1] as Int?,
            isComplete = args[2] as Boolean,
            accounts = args[3] as List<BookerMoMoAccount>,
            dialogState = args[4] as BookerMoMoAccountsDialogState,
            toDelete = args[5] as List<String>
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = BookerMoMoAccountsUiState()
    )

    val detailsUiState = combine(
        _isLoading,
        _message,
        _accountDetails,
        _isDetailsComplete,
        _detailsDialogState,
        _isEditMode,
    ) {
        BookerMoMoAccountDetailsUiState(
            isLoading = it[0] as Boolean,
            message = it[1] as Int?,
            account = it[2] as BookerMoMoAccount,
            isDetailsComplete = it[3] as Boolean,
            isEditMode = it[5] as Boolean,
            detailsDialogState = it[4] as BookerMoMoAccountDetailsDialogState,
        ).also {
//            if (_isInitComplete.value)
//                _isInitComplete.value = false
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = BookerMoMoAccountDetailsUiState()
    )

    fun onNavigateToEdit(account: BookerMoMoAccount) {
        oldAccountCopy = account
        _accountDetails.value = account
        _isEditMode.value = true

        _isInitComplete.value = false
    }

    fun onNavigateToNew() {
        oldAccountCopy = BookerMoMoAccount()
        _accountDetails.value = oldAccountCopy
        _isEditMode.value = false
        _isInitComplete.value = false
    }

    fun doBeforeNavBackToAccounts() {
//        _accountDetails.value = BookerMoMoAccount()
//        _isEditMode.value = false
//        oldAccountCopy = BookerMoMoAccount()
    }

    fun onToDeleteChange(new: String) {
        _toDelete.value = _toDelete.value.toMutableList()
            .apply { if (_toDelete.value.contains(new)) remove(new) else add(new) }
    }

    fun clearOnToDelete() {
        _toDelete.value = listOf()
    }

    fun deleteAccounts() {
        viewModelScope.launch {
            onLoadingChange(true)
            repo.deleteBookerMoMoAccounts(
                authRepo.authClient.currentUserOrNull()!!.id,
                _toDelete.value
            ).also {
                when (it) {
                    is Results.Failure -> {
                        _message.value = R.string.msg_del_accounts_error
                        Log.e(TAG, "Del Accounts: ${it.exception}")
                        onLoadingChange(false)
                    }

                    is Results.Success -> {
                        _toDelete.value = listOf()
                        _message.value = R.string.msg_del_accounts_success
                        onLoadingChange(false)
                    }
                }
            }
        }
    }

    fun onLoadingChange(new: Boolean) {
        _isLoading.value = new
    }

    fun onMessageChange(new: Int) {
        _message.value = new
    }

    fun onDialogStateChange(new: BookerMoMoAccountsDialogState) {
        _dialogState.value = new
    }

    fun onCompleteChange(new: Boolean) {
        _isComplete.value = new
    }

    fun onInitComplete(new: Boolean) {
        _isInitComplete.value = new
        _isLoading.value = !new
    }

    // Details
    fun onDetailsDialogStateChange(new: BookerMoMoAccountDetailsDialogState) {
        _detailsDialogState.value = new
    }

    fun onDetailsCompleteChange(new: Boolean) {
        _isDetailsComplete.value = new
    }

    fun onLoading(new: Boolean) {
        _isLoading.value = new
    }

    fun onMessageChange(new: Int?) {
        _message.value = new
    }

    fun onPhoneChange(new: String) {
        _accountDetails.value = _accountDetails.value.copy(phoneNumber = new)
    }

    fun onIsActiveChange(new: Boolean) {
        _accountDetails.value = _accountDetails.value.copy(isActive = new)
    }

    fun phoneErrorText(text: String? = _accountDetails.value.phoneNumber) = when {
        text.isNullOrBlank() -> R.string.msg_required_field
        isPhoneInvalid(_accountDetails.value.phoneNumber, "237") -> R.string.msg_invalid_field
        !isMTNPhoneValid(_accountDetails.value.phoneNumber) -> R.string.msg_invalid_mtn_number
        else -> null
    }

    private val isNoError get() = phoneErrorText() == null

    fun saveOrUpdateAccount(doOnStart: () -> Unit) {
        if (isNoError) {
            onLoadingChange(true)
            doOnStart()
            if (!hasAnyFieldChanged) {
                onDetailsCompleteChange(true)
                return
            }
            viewModelScope.launch {
                if (_isEditMode.value) {
                    repo.updateBookerMoMoAccount(_accountDetails.value).also { result ->
                        when (result) {
                            is Results.Failure -> {
                                Log.e(TAG, result.exception.toString())
                                onLoading(false)
                                onMessageChange(R.string.msg_error_update_account)
                            }

                            is Results.Success -> {
                                onLoading(false)
                                onDetailsCompleteChange(true)
                            }
                        }
                    }
                } else {
                    _accountDetails.value =
                        _accountDetails.value.copy(bookerId = authRepo.bookerId!!)
                    repo.createBookerMoMoAccounts(listOf(_accountDetails.value)).also { result ->
                        when (result) {
                            is Results.Failure -> {
                                Log.e(TAG, result.exception.toString())
                                onLoading(false)
                                onMessageChange(R.string.msg_error_saving_account)
                            }

                            is Results.Success -> {
                                onLoading(false)
                                onDetailsCompleteChange(true)
                            }
                        }
                    }
                }
            }
        } else {
            onMessageChange(R.string.msg_fields_contain_errors)
        }
    }

    val hasAnyFieldChanged
        get() = diffFields(_accountDetails.value, oldAccountCopy).isNotEmpty()

    companion object {
        const val TAG = "MOMO ACCOUNTS"
    }
}

/** ----------------------------------DETAILS --------------------------------------------*/
data class BookerMoMoAccountDetailsUiState(
    val isLoading: Boolean = false,
    val message: Int? = null,
    val account: BookerMoMoAccount = BookerMoMoAccount(),
    val isDetailsComplete: Boolean = false,
    val isEditMode: Boolean = false,
    val detailsDialogState: BookerMoMoAccountDetailsDialogState = BookerMoMoAccountDetailsDialogState.NONE,
) {
    val formattedPhone: String
        get() = PhoneNumberUtils.formatNumber(
            account.phoneNumber,
            codeCountryMap["237"]!!
        )
}

enum class BookerMoMoAccountDetailsDialogState {
    NONE, HELP_MAIN_PAGE, LEAVING_WITHOUT_SAVING, LEAVING_WITH_EMPTY_ACCOUNT, HELP_IS_ENABLED
}


