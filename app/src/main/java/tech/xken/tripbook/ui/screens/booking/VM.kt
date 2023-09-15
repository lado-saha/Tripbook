package tech.xken.tripbook.ui.screens.booking

import android.telephony.PhoneNumberUtils
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
import tech.xken.tripbook.data.models.SortField
import tech.xken.tripbook.data.models.codeCountryMap
import tech.xken.tripbook.data.sources.booker.BookerRepository
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.domain.diffFields
import tech.xken.tripbook.domain.isOrangePhoneValid
import tech.xken.tripbook.domain.isPhoneInvalid
import javax.inject.Inject

class ClassObject{
    companion object {
        var sortFields: List<SortField> = listOf()
        var searchFields: List<SortField> = listOf()
    }
    fun backingField(x: Int) = ""
}

data class ObjectsUiState(
    val isLoading: Boolean = false,
    val message: Int? = null,
    val objects: List<ClassObject> = listOf(),
    val isComplete: Boolean = false,
    val dialogState: ObjectsDialogState = ObjectsDialogState.NONE,
    val isInitComplete: Boolean = false,
    val toDelete: List<String> = listOf(),
    val sortFields: List<SortField> = ClassObject.sortFields,
    val searchFields: List<SortField> = ClassObject.sortFields,
    val selectedSortField: SortField? = null,
    val selectedSearchField: SortField? = null,
    val sheetStatus: ObjectsSheetState = ObjectsSheetState.NONE,
    val isSearching: Boolean = false,
//    val selectedField:
    val query: String = ""
)

enum class ObjectsSheetState {
    ACTIONS, NONE
}

enum class ObjectsDialogState {
    NONE, ABOUT_MAIN_PAGE, COULD_NOT_GET_OBJECTS, LEAVING_WITHOUT_OBJECTS, DELETE_OBJECTS_WARNING, SORTING
}

@HiltViewModel
class ClassObjectsVM @Inject constructor(
    private val repo: BookerRepository,
    private val authRepo: AuthRepo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<Int?>(null)
    private val _isComplete = MutableStateFlow(false)
    private val _dialogState = MutableStateFlow(ObjectsDialogState.NONE)
    private val _isInitComplete = MutableStateFlow(false)
    private val _toDelete = MutableStateFlow(listOf<String>())
    private val _sheetState = MutableStateFlow(ObjectsSheetState.NONE)
    // Details
    private var oldAccountCopy = ClassObject()
    private val _isEditMode = MutableStateFlow(false)
    private val _accountDetails = MutableStateFlow(ClassObject())
    private val _isDetailsComplete = MutableStateFlow(false)
    private val _objectDialogState = MutableStateFlow(ObjectDialogState.NONE)
    private val _selectedSortField = MutableStateFlow<SortField?>(null)
    private val _selectedSearchField = MutableStateFlow<SortField?>(null)
    private val _isSearching = MutableStateFlow(false)
    private val _sortFields = MutableStateFlow(ClassObject.sortFields)
    private val _searchFields = MutableStateFlow(ClassObject.searchFields)
    private val _query =  MutableStateFlow("")


    val uiState = combine(
        _isLoading,
        _message,
        _isComplete,
        TODO("Getting list from the repository"),
/*
        repo.ClassObjects(authRepo.bookerId!!)
            .onStart { onInitComplete(false) }
            .map { result ->
                onInitComplete(true)
                when (result) {
                    is Results.Failure -> {
                        _message.value = R.string.msg_get_om_accounts_error
                        onDialogStateChange(ObjectsDialogState.COULD_NOT_GET_OBJECTS)
                        listOf()
                    }

                    is Results.Success -> {
                        onInitComplete(true)
                        result.data
                    }
                }
            },
*/
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
        ObjectsUiState(
            isLoading = args[0] as Boolean,
            message = args[1] as Int?,
            isComplete = args[2] as Boolean,
            objects = args[3] as List<ClassObject>,
            dialogState = args[4] as ObjectsDialogState,
            toDelete = args[5] as List<String>,
            sortFields = args[6] as List<SortField>,
            selectedSortField = args[7] as SortField?,
            sheetStatus = args[8] as ObjectsSheetState,
            isSearching = args[9] as Boolean,
            selectedSearchField = args[10] as SortField?,
            query = args[11] as String,
            searchFields = args[12] as List<SortField>
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = ObjectsUiState()
    )

    val detailsUiState = combine(
        _isLoading,
        _message,
        _accountDetails,
        _isDetailsComplete,
        _objectDialogState,
        _isEditMode,
    ) {
        ObjectUiState(
            isLoading = it[0] as Boolean,
            message = it[1] as Int?,
            account = it[2] as ClassObject,
            isDetailsComplete = it[3] as Boolean,
            isEditMode = it[5] as Boolean,
            detailsDialogState = it[4] as ObjectDialogState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = ObjectUiState()
    )

    fun onNavigateToEdit(account: ClassObject) {
        oldAccountCopy = account
        _accountDetails.value = account
        _isEditMode.value = true
    }

    fun onNavigateToNew() {
        oldAccountCopy = ClassObject()
        _accountDetails.value = oldAccountCopy
        _isEditMode.value = false
    }

    fun onSelectedsSortFieldChange(new: SortField) {
        _selectedSortField.value = new
        _sortFields.value = ClassObject.sortFields.toMutableList().apply {
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

    fun onSheetStateChange(new: ObjectsSheetState){
        _sheetState.value = new
    }

    fun deleteObjects() {
        viewModelScope.launch {
            onLoadingChange(true)
            TODO("DELETING OBJECTS")
            /*repo.deleteClassObjects(
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
            }*/
        }
    }

    fun onLoadingChange(new: Boolean) {
        _isLoading.value = new
    }

    fun onMessageChange(new: Int) {
        _message.value = new
    }

    fun onDialogStateChange(new: ObjectsDialogState) {
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
    fun onObjectStateChange(new: ObjectDialogState) {
        _objectDialogState.value = new
    }

    fun onObjectCompleteChange(new: Boolean) {
        _isDetailsComplete.value = new
    }

    fun onLoading(new: Boolean) {
        _isLoading.value = new
    }

    fun onMessageChange(new: Int?) {
        _message.value = new
    }

//    fun onPhoneChange(new: String) {
//        _accountDetails.value = _accountDetails.value.copy(phoneNumber = new)
//    }
//
//    fun onIsActiveChange(new: Boolean) {
//        _accountDetails.value = _accountDetails.value.copy(isActive = new)
//    }

   /* fun phoneErrorText(text: String? = _accountDetails.value.phoneNumber) = when {
        text.isNullOrBlank() -> R.string.msg_required_field
        isPhoneInvalid(_accountDetails.value.phoneNumber, "237") -> R.string.msg_invalid_field
        !isOrangePhoneValid(_accountDetails.value.phoneNumber) -> R.string.msg_invalid_orange_number
        else -> null
    }*/

    private val isNoError: Boolean get() = TODO("Combine all is error ")

    fun saveOrUpdateAccount(doOnStart: () -> Unit) {
        if (isNoError) {
            onLoadingChange(true)
            doOnStart()
            if (!hasAnyFieldChanged) {
                onObjectCompleteChange(true)
                return
            }
            viewModelScope.launch {
                if (_isEditMode.value) {
                    TODO("Update object")
                    /*repo.updateClassObject(_accountDetails.value).also { result ->
                        when (result) {
                            is Results.Failure -> {
                                Log.e(TAG, result.exception.toString())
                                onLoading(false)
                                onMessageChange(R.string.msg_error_saving_account)
                            }

                            is Results.Success -> {
                                onLoading(false)
                                onObjectCompleteChange(true)
                            }
                        }
                    }*/
                } else {
                    TODO("Update object")
                    /*_accountDetails.value =
                        _accountDetails.value.copy(bookerId = authRepo.bookerId!!)
                    repo.createClassObjects(listOf(_accountDetails.value)).also { result ->
                        when (result) {
                            is Results.Failure -> {
                                Log.e(TAG, result.exception.toString())
                                onLoading(false)
                                onMessageChange(R.string.msg_error_saving_account)
                            }

                            is Results.Success -> {
                                onLoading(false)
                                onObjectCompleteChange(true)
                            }
                        }
                    }*/
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
        const val TAG = "MOMO ACCOUNTS"
    }
}

/** ----------------------------------DETAILS --------------------------------------------*/
data class ObjectUiState(
    val isLoading: Boolean = false,
    val message: Int? = null,
    val account: ClassObject = ClassObject(),
    val isDetailsComplete: Boolean = false,
    val isEditMode: Boolean = false,
    val detailsDialogState: ObjectDialogState = ObjectDialogState.NONE,
) {
    /*val formattedPhone: String
        get() = PhoneNumberUtils.formatNumber(
            account.phoneNumber,
            codeCountryMap["237"]!!
        )*/

}

enum class ObjectDialogState {
    NONE, ABOUT_MAIN_PAGE, LEAVING_WITHOUT_SAVING, LEAVING_WITH_EMPTY_ACCOUNT, HELP_IS_ENABLED
}

