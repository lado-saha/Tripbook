package tech.xken.tripbook.ui.screens.booking

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tech.xken.tripbook.R
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.data.models.ImageUiState
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.models.booker.Booker
import tech.xken.tripbook.data.models.booker.Sex
import tech.xken.tripbook.data.sources.booker.BookerRepository
import tech.xken.tripbook.data.sources.storage.StorageRepository
import tech.xken.tripbook.domain.DATE_NOW
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.domain.diffFields
import javax.inject.Inject

data class BookerAccountUiState(
    val isLoading: Boolean = false,
    val message: Int? = null,
    val booker: Booker = Booker(),
    val isSexExpanded: Boolean = false,
    val isInitComplete: Boolean = false,
    val isComplete: Boolean = false,
    val isEditMode: Boolean = false,
    val dialogStatus: BookerAccountDialogState = BookerAccountDialogState.NONE,
    val sheetStatus: BookerAccountSheetState = BookerAccountSheetState.NONE,
    val photoUiState: ImageUiState = ImageUiState.bookerAccountUiState(),
    val isImageNotPDF: Boolean? = null,
    val hideErrors: Boolean = true
)

@HiltViewModel
class BookerAccountVM @Inject constructor(
    private val repo: BookerRepository,
    private val authRepo: AuthRepo,
    private val storageRepo: StorageRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val parser: Json = Json { encodeDefaults = true }

    // To know if we are signing in up or checking the booker profile
    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<Int?>(null)
    private val _booker = MutableStateFlow(Booker())
    private val _isGenderExpanded = MutableStateFlow(false)
    private val _isInitComplete = MutableStateFlow(false)
    private val _isComplete = MutableStateFlow(false)
    private val _dialogStatus = MutableStateFlow(BookerAccountDialogState.NONE)
    private val _sheetStatus = MutableStateFlow(BookerAccountSheetState.NONE)
    private val _photoUiS = MutableStateFlow(
        ImageUiState.bookerAccountUiState()
    )
    private val _hideErrors = MutableStateFlow(true)

    init {


        // Observer of the results of the image viewer
        /**
         * We observe the incomming image status uistate and using the caller ID, we can determin the sender. If it matches
         * to ours, we collect and delete the argument from the app navigation
         */
    }

    val encodedImageUis get() = parser.encodeToString(_photoUiS.value)

    // Stores the old copy of the booker to be used during comparisons after updates
    private var oldBookerCopy: Booker? = null

    val uiState = combine(
        _isLoading,//0
        _message,//1
        _booker,//2
        _isGenderExpanded,//3
        _isInitComplete,//4
        _isComplete,//5,
        repo.countBookerAccount(authRepo.bookerId!!).map { account ->
            when (account) {
                is Failure -> {
                    onInitCompleted(true)
                    false
                }

                is Success -> {
                    (account.data > 0L).also {
                        // No need to initialise if there is no account
                        onInitCompleted(!it)
                    }
                }
            }
        },//6
        _dialogStatus,//7
        _sheetStatus,//8
        _photoUiS,//9
        _hideErrors
    ) { args ->
        loadBookerAccount()

        BookerAccountUiState(
            isLoading = args[0] as Boolean,
            message = args[1] as Int?,
            booker = args[2] as Booker,
            isSexExpanded = args[3] as Boolean,
            isInitComplete = args[4] as Boolean,
            isComplete = args[5] as Boolean,
            isEditMode = args[6] as Boolean,
            dialogStatus = args[7] as BookerAccountDialogState,
            sheetStatus = args[8] as BookerAccountSheetState,
            photoUiState = args[9] as ImageUiState,
            hideErrors = args[10] as Boolean
        )
    }.stateIn(
        scope = viewModelScope, started = WhileUiSubscribed, initialValue = BookerAccountUiState()
    )

    fun onHideErrorsChange(new: Boolean) {
        _hideErrors.value = new
    }


    fun onSheetStatusChange(new: BookerAccountSheetState) {
        _sheetStatus.value = new
    }

    /**
     * Returns True if there
     */
    val hasAnyFieldChanged
        get() = if (uiState.value.isEditMode && oldBookerCopy != null) {
            val changedFields = diffFields(_booker.value, oldBookerCopy!!).isNotEmpty()
            changedFields || _booker.value.accountPhotoUri != null
        } else _booker.value.accountPhotoUri != null


    private fun loadBookerAccount() {
        viewModelScope.launch {
            if (!_isInitComplete.value && uiState.value.isEditMode) {
                onLoading(true)
                repo.bookerFromId(authRepo.bookerId!!).also { state ->
                    when (state) {
                        is Failure -> {
                            onInitCompleted(true)
                            _dialogStatus.value = BookerAccountDialogState.COULD_NOT_GET_ACCOUNT
                            Log.e("$TAG Load_Account", state.exception.message!!)
                        }

                        is Success -> when (val booker = state.data) {
                            null -> {
                                onInitCompleted(true)
                                _dialogStatus.value = BookerAccountDialogState.WELCOME_NEW_BOOKER
                            }

                            else -> {
                                // We are in edit mode
                                booker.initUrls(storageRepo).also {
                                    _booker.value = it.copy()
                                    // To Know fields which have changed
                                    oldBookerCopy = it.copy()
                                    onInitCompleted(true)
                                    onMessageChange(R.string.msg_welcome_back)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun onPhotoUiStateChange(new: ImageUiState) {
        _photoUiS.value = new
    }

    fun onDialogStateChange(new: BookerAccountDialogState) {
        _dialogStatus.value = new
    }


    private fun onLoading(new: Boolean) {
        _isLoading.value = new
    }

    fun onMessageChange(resId: Int?) {
        _message.value = resId
    }

    fun onCompleteChange(new: Boolean) {
        _isComplete.value = new
    }

    fun onNameChange(new: String) {
        _booker.value = _booker.value.copy(name = new)
    }

    fun onIdCardNumberChange(new: String) {
        _booker.value = _booker.value.copy(idCardNumber = new)
    }

    fun onBirthdayChange(new: LocalDate) {
        _booker.value = _booker.value.copy(birthday = new)
    }

    fun onSelectedSexChange(new: Sex) {
        _booker.value = _booker.value.copy(bookerSex = new)
    }

    fun onGenderExpansionChange(new: Boolean) {
        _isGenderExpanded.value = new
    }

    fun onNationalityChange(new: String) {
        _booker.value = _booker.value.copy(nationality = new)
    }

    fun onOccupationChange(new: String) {
        _booker.value = _booker.value.copy(occupation = new)
    }

    fun onPhotoUriChange(new: Uri?) {
        _booker.value = _booker.value.setPhotoUri(new)
        _photoUiS.value = _photoUiS.value.copy(localUri = new ?: Uri.EMPTY)
    }

    fun nameErrorText(text: String? = _booker.value.name) =
        if (text.isNullOrBlank() && !_hideErrors.value) R.string.msg_required_field else null

    fun idCardNumberErrorText(text: String? = _booker.value.idCardNumber) =
        if (text.isNullOrBlank() && !_hideErrors.value) R.string.msg_required_field
        else if (text?.length != 19 && !_hideErrors.value) R.string.msg_invalid_field
        else null

    fun birthdayErrorText() = when {
        _booker.value.birthday > DATE_NOW -> R.string.msg_invalid_field
        else -> null
    }

    val nationalityErrorText = null

    val occupationErrorText = null

    val isNoError
        get() = nameErrorText() == null && nationalityErrorText == null && idCardNumberErrorText() == null && occupationErrorText == null && birthdayErrorText() == null

    fun saveOrUpdateAccount() {
        onHideErrorsChange(false)
        if (isNoError) viewModelScope.launch {
            onLoading(true)
            // We create a new object in cas we are not in edit mode

            when {
                uiState.value.isEditMode -> {
                    val changedFields = diffFields(_booker.value, oldBookerCopy!!)
                    if (changedFields.isEmpty() && _booker.value.accountPhotoUri == null)
                        onCompleteChange(true)
                    else repo.updateBooker(_booker.value).also {
                        if (it is Failure) {
                            onMessageChange(R.string.msg_error_update_account)
                            Log.e("$TAG Update_Profile", it.exception.message!!)
                        } else onCompleteChange(true)
                    }
                }

                else -> {
                    repo.createBooker(_booker.value.copy(bookerId = authRepo.bookerId!!)).also {
                        if (it is Failure) {
                            onMessageChange(R.string.msg_error_saving_account)
                            Log.e("$TAG Create_Profile", it.exception.message!!)
                        } else {
                            onCompleteChange(true)
                        }
                    }
                }
            }
            onLoading(false)
        }
        else onMessageChange(R.string.msg_fields_contain_errors)
    }


    fun onInitCompleted(new: Boolean) {
        _isInitComplete.value = new
        _isLoading.value = !new
    }

    companion object {
        const val TAG = "BAcc_VM"
    }
}

enum class BookerAccountSheetState {
    ACTIONS, NONE
}

enum class BookerAccountDialogState {
    ABOUT_ACCOUNT_PHOTO,
    COULD_NOT_GET_PHOTO, COULD_NOT_GET_ACCOUNT, ABOUT_MAIN_PAGE, LEAVING_WITHOUT_SAVING, LEAVING_WITH_EMPTY_ACCOUNT, WELCOME_NEW_BOOKER, NONE
}
