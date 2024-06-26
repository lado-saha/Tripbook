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
import tech.xken.tripbook.data.models.SortField
import tech.xken.tripbook.data.models.booker.BookerOMAccount
import tech.xken.tripbook.data.models.codeCountryMap
import tech.xken.tripbook.data.sources.booker.BookerRepository
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.domain.diffFields
import tech.xken.tripbook.domain.isOrangePhoneValid
import tech.xken.tripbook.domain.isPhoneInvalid
import javax.inject.Inject

data class BookerOMAccountsUiState(
    val isLoading: Boolean = false,
    val message: Int? = null,
    val accounts: List<BookerOMAccount> = listOf(),
    val isComplete: Boolean = false,
    val dialogState: BookerOMAccountsDialogState = BookerOMAccountsDialogState.NONE,
    val isInitComplete: Boolean = false,
    val toDelete: List<String> = listOf(),
    val sortFields: List<SortField> = BookerOMAccount.sortFields,
    val searchFields: List<SortField> = BookerOMAccount.sortFields,
    val selectedSortField: SortField? = null,
    val selectedSearchField: SortField? = null,
    val sheetStatus: BookerOMAccountsSheetState = BookerOMAccountsSheetState.NONE,
    val isSearching: Boolean = false,
//    val selectedField:
    val query: String = ""
)

enum class BookerOMAccountsSheetState {
    ACTIONS, NONE
}

enum class BookerOMAccountsDialogState {
    NONE, ABOUT_MAIN_PAGE, COULD_NOT_GET_ACCOUNTS, LEAVING_WITHOUT_ACCOUNTS, DELETE_ACCOUNTS_WARNING, SORTING
}

@HiltViewModel
class BookerOMAccountsVM @Inject constructor(
    private val repo: BookerRepository,
    private val authRepo: AuthRepo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<Int?>(null)
    private val _isComplete = MutableStateFlow(false)
    private val _dialogState = MutableStateFlow(BookerOMAccountsDialogState.NONE)
    private val _isInitComplete = MutableStateFlow(false)
    private val _toDelete = MutableStateFlow(listOf<String>())
    private val _sheetState = MutableStateFlow(BookerOMAccountsSheetState.NONE)
    // Details
    private var oldAccountCopy = BookerOMAccount()
    private val _isEditMode = MutableStateFlow(false)
    private val _accountDetails = MutableStateFlow(BookerOMAccount())
    private val _isDetailsComplete = MutableStateFlow(false)
    private val _detailsDialogState = MutableStateFlow(BookerOMAccountDetailsDialogState.NONE)
    private val _selectedSortField = MutableStateFlow<SortField?>(null)
    private val _selectedSearchField = MutableStateFlow<SortField?>(null)
    private val _isSearching = MutableStateFlow(false)
    private val _sortFields = MutableStateFlow(BookerOMAccount.sortFields)
    private val _searchFields = MutableStateFlow(BookerOMAccount.searchFields)
    private val _query =  MutableStateFlow("")


    val uiState = combine(
        _isLoading,
        _message,
        _isComplete,
        repo.bookerOMAccounts(authRepo.bookerId!!)
            .onStart { onInitComplete(false) }
            .map { result ->
                onInitComplete(true)
                when (result) {
                    is Results.Failure -> {
                        _message.value = R.string.msg_get_om_accounts_error
                        onDialogStateChange(BookerOMAccountsDialogState.COULD_NOT_GET_ACCOUNTS)
                        listOf()
                    }

                    is Results.Success -> {
                        onInitComplete(true)
                        result.data
                    }
                }
            },
        _dialogState,
        _toDelete,
        _sortFields,
        _selectedSortField,
        _sheetState,
        _isSearching,
        _selectedSearchField,
        _query,
        _searchFields
    ) { args ->
        BookerOMAccountsUiState(
            isLoading = args[0] as Boolean,
            message = args[1] as Int?,
            isComplete = args[2] as Boolean,
            accounts = args[3] as List<BookerOMAccount>,
            dialogState = args[4] as BookerOMAccountsDialogState,
            toDelete = args[5] as List<String>,
            sortFields = args[6] as List<SortField>,
            selectedSortField = args[7] as SortField?,
            sheetStatus = args[8] as BookerOMAccountsSheetState,
            isSearching = args[9] as Boolean,
            selectedSearchField = args[10] as SortField?,
            query = args[11] as String,
            searchFields = args[12] as List<SortField>
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = BookerOMAccountsUiState()
    )

    val detailsUiState = combine(
        _isLoading,
        _message,
        _accountDetails,
        _isDetailsComplete,
        _detailsDialogState,
        _isEditMode,
    ) {
        BookerOMAccountDetailsUiState(
            isLoading = it[0] as Boolean,
            message = it[1] as Int?,
            account = it[2] as BookerOMAccount,
            isDetailsComplete = it[3] as Boolean,
            isEditMode = it[5] as Boolean,
            detailsDialogState = it[4] as BookerOMAccountDetailsDialogState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = BookerOMAccountDetailsUiState()
    )

    fun onNavigateToEdit(account: BookerOMAccount) {
        oldAccountCopy = account
        _accountDetails.value = account
        _isEditMode.value = true
    }

    fun onNavigateToNew() {
        oldAccountCopy = BookerOMAccount()
        _accountDetails.value = oldAccountCopy
        _isEditMode.value = false
    }

    fun onSelectedSortFieldChange(new: SortField) {
        _selectedSortField.value = new
        _sortFields.value = BookerOMAccount.sortFields.toMutableList().apply {
            val i = indexOfFirst { it.nameRes == new.nameRes }
            this[i] = new
        }
    }

    fun onToDeleteChange(new: String) {
        _toDelete.value = _toDelete.value.toMutableList()
            .apply { if (_toDelete.value.contains(new)) remove(new) else add(new) }
    }

    fun clearOnToDelete() {
        _toDelete.value = listOf()
    }

    fun onSheetStateChange(new: BookerOMAccountsSheetState){
        _sheetState.value = new
    }

    fun deleteAccounts() {
        viewModelScope.launch {
            onLoadingChange(true)
            repo.deleteBookerOMAccounts(
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

    fun onDialogStateChange(new: BookerOMAccountsDialogState) {
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
    fun onDetailsDialogStateChange(new: BookerOMAccountDetailsDialogState) {
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
        _accountDetails.value = _accountDetails.value.copy(isEnabled = new)
    }

    fun phoneErrorText(text: String? = _accountDetails.value.phoneNumber) = when {
        text.isNullOrBlank() -> R.string.msg_required_field
        isPhoneInvalid(_accountDetails.value.phoneNumber, "237") -> R.string.msg_invalid_field
        !isOrangePhoneValid(_accountDetails.value.phoneNumber) -> R.string.msg_invalid_orange_number
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
                    repo.updateBookerOMAccount(_accountDetails.value).also { result ->
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
                } else {
                    _accountDetails.value =
                        _accountDetails.value.copy(bookerId = authRepo.bookerId!!)
                    repo.createBookerOMAccounts(listOf(_accountDetails.value)).also { result ->
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

    fun onQueryChanged(new: String) {
        _query.value = new
    }

    fun onSearchFieldChange(new: SortField) {
        _selectedSearchField.value = new
        _searchFields.value.toMutableList().apply {
            val i = indexOf(new)
            val temp = this[0]
            this[0] = new
            this[i] = temp
        }.also{
            _searchFields.value = it
        }
    }

    fun onIsSearchingChange(new: Boolean) {
        _isSearching.value = new
    }

    val hasAnyFieldChanged
        get() = diffFields(_accountDetails.value, oldAccountCopy).isNotEmpty()

    companion object {
        const val TAG = "OM ACCOUNTS"
    }
}

/** ----------------------------------DETAILS --------------------------------------------*/
data class BookerOMAccountDetailsUiState(
    val isLoading: Boolean = false,
    val message: Int? = null,
    val account: BookerOMAccount = BookerOMAccount(),
    val isDetailsComplete: Boolean = false,
    val isEditMode: Boolean = false,
    val detailsDialogState: BookerOMAccountDetailsDialogState = BookerOMAccountDetailsDialogState.NONE,
) {
    val formattedPhone: String
        get() = PhoneNumberUtils.formatNumber(
            account.phoneNumber,
            codeCountryMap["237"]!!
        )

}

enum class BookerOMAccountDetailsDialogState {
    NONE, ABOUT_MAIN_PAGE, LEAVING_WITHOUT_SAVING, LEAVING_WITH_EMPTY_ACCOUNT, HELP_IS_ENABLED
}

