package tech.xken.tripbook.ui.screens.booking

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import tech.xken.tripbook.R
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.models.booker.Booker
import tech.xken.tripbook.data.models.booker.Sex
import tech.xken.tripbook.data.sources.booker.BookerRepository
import tech.xken.tripbook.data.sources.storage.StorageRepository
import tech.xken.tripbook.domain.NOW
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.domain.diffFields
import javax.inject.Inject

data class BookerAccountUiState(
    val isLoading: Boolean = false,
    val message: Int? = null,
    val booker: Booker = Booker(),
    val isSexExpanded: Boolean = false,
    val photoUri: Uri? = null,
    val photoBitmap: Bitmap? = null,
    val isInitComplete: Boolean = false,
    val isComplete: Boolean = false,
    val isEditMode: Boolean = false,
    val isLoadingPhoto: Boolean = false,
    val dialogStatus: BookerAccountDialogStatus = BookerAccountDialogStatus.NONE,
    val isPhotoFailed: Boolean = false,
    val isPhotoInitComplete: Boolean = false
)


@HiltViewModel
class BookerAccountVM @Inject constructor(
    private val repo: BookerRepository,
    private val authRepo: AuthRepo,
    private val storageRepo: StorageRepository
) : ViewModel() {
    // To know if we are signing in up or checking the booker profile
    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<Int?>(null)
    private val _booker = MutableStateFlow(Booker())
    private val _isGenderExpanded = MutableStateFlow(false)
    private val _photoUri = MutableStateFlow<Uri?>(null)
    private val _photoBitmap = MutableStateFlow<Bitmap?>(null)
    private val _isAccountInitComplete = MutableStateFlow(!authRepo.hasAccount)
    private val _isComplete = MutableStateFlow(false)
    private val _isEditMode = MutableStateFlow(authRepo.hasAccount)
    private val _isPhotoLoading = MutableStateFlow(false)
    private val _dialogStatus = MutableStateFlow(BookerAccountDialogStatus.NONE)
    private val _isPhotoFailed = MutableStateFlow(false)
    private val _isPhotoInitComplete = MutableStateFlow(false)

    // Stores the old copy of the booker to be used during comparisons after updates
    private var oldBookerCopy: Booker? = null

    val uiState = combine(
        _isLoading,//0
        _message,//1
        _booker,//2
        _isGenderExpanded,//3
        _photoUri,//4
        _photoBitmap,//5
        _isAccountInitComplete,//6
        _isComplete,//7,
        _isEditMode,//8
        _isPhotoLoading,//9
        _dialogStatus,
        _isPhotoFailed,
        _isPhotoInitComplete
    ) { args ->
        BookerAccountUiState(
            isLoading = args[0] as Boolean,
            message = args[1] as Int?,
            booker = args[2] as Booker,
            isSexExpanded = args[3] as Boolean,
            photoUri = args[4] as Uri?,
            photoBitmap = args[5] as Bitmap?,
            isInitComplete = args[6] as Boolean,
            isComplete = args[7] as Boolean,
            isEditMode = args[8] as Boolean,
            isLoadingPhoto = args[9] as Boolean,
            dialogStatus = args[10] as BookerAccountDialogStatus,
            isPhotoFailed = args[11] as Boolean,
            isPhotoInitComplete = args[12] as Boolean
        ).also { loadBookerAccount() }
    }.stateIn(
        scope = viewModelScope, started = WhileUiSubscribed, initialValue = BookerAccountUiState()
    )

    /**
     * Returns True if there
     */
    val hasAnyFieldChanged
        get() = if (_isEditMode.value && oldBookerCopy != null) {
            val changedFields = diffFields(_booker.value, oldBookerCopy!!).isNotEmpty()
            (changedFields)
        } else false

    val hasPhotoChanged: Boolean
        get() {
            return if (_isPhotoFailed.value) false
            else {
                !authRepo.hasAccountPhoto && _photoUri.value != null || (_photoBitmap.value == null && authRepo.hasAccountPhoto)
            }
        }

    /**
     * Load the booker details only when the booker is not new and the initialisation is still ongoing
     */
    private fun loadBookerAccount() {
        viewModelScope.launch {
            if (!_isAccountInitComplete.value && _isEditMode.value) {
                onLoading(true)
                repo.bookerFromId(authRepo.bookerId!!).also {
                    when (it) {
                        is Failure -> {
                            onAccountInitComplete(true)
                            _dialogStatus.value = BookerAccountDialogStatus.COULD_NOT_GET_ACCOUNT
                            Log.e("$TAG Load_Account", it.exception.message!!)
                        }

                        is Success -> when (val booker = it.data) {
                            null -> {
                                onAccountInitComplete(true)
                                _dialogStatus.value =
                                    BookerAccountDialogStatus.COULD_NOT_GET_ACCOUNT
                            }

                            else -> {
                                // To Know fields which have changed
                                booker.also { value ->
                                    _booker.value = value
                                    oldBookerCopy = value
                                }
                                onAccountInitComplete(true)
                                onMessageChange(R.string.msg_welcome_back)
                            }
                        }
                    }
                    cancel()
                }
            }
        }

        viewModelScope.launch {
            if (!_isPhotoInitComplete.value && _isEditMode.value && authRepo.hasAccountPhoto) {
                onLoadingPhoto(true)
                storageRepo.profilePhoto(_booker.value.bookerId)
                    .also { photoArray ->
                        when (photoArray) {
                            is Failure -> {
                                Log.e(
                                    "$TAG Load_Photo",
                                    "${photoArray.exception}"
                                )
                                onDialogStatusChange(BookerAccountDialogStatus.COULD_NOT_GET_PHOTO)
                                _isPhotoFailed.value = true
                                onPhotoInitComplete(true)
                            }

                            is Success -> {
                                onPhotoBitmapChange(
                                    BitmapFactory.decodeByteArray(
                                        photoArray.data,
                                        0,
                                        photoArray.data.size
                                    )
                                )
                                _isPhotoFailed.value = false
                                onPhotoInitComplete(true)
                            }
                        }
                        cancel()
                    }
            }
        }
    }

    private fun onPhotoBitmapChange(new: Bitmap?) {
        _photoBitmap.value = new
    }

    fun onPhotoUriChange(context: Context, new: Uri?) {
        _photoUri.value = new
        _photoBitmap.value = null
        if (new == null)
            return

        //We try to load the image
        loadPhotoBitmap(context)
    }

    /**
     * If the [_photoUri] is not null we load the [_photoBitmap]
     */
    fun loadPhotoBitmap(context: Context) {
        if (_photoBitmap.value == null && _photoUri.value != null) try {
            _photoUri.value?.let {
                _photoBitmap.value = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
            }
        } catch (e: Exception) {
            onMessageChange(R.string.msg_error_get_photo)
            _isPhotoFailed.value = true
        }
    }


    fun onDialogStatusChange(new: BookerAccountDialogStatus) {
        _dialogStatus.value = new
    }

    private fun onLoading(new: Boolean) {
        _isLoading.value = new
    }

    private fun onLoadingPhoto(new: Boolean) {
        _isPhotoLoading.value = new
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
        _isGenderExpanded.value = !_isGenderExpanded.value
    }

    fun onNationalityChange(new: String) {
        _booker.value = _booker.value.copy(nationality = new)
    }

    fun onOccupationChange(new: String) {
        _booker.value = _booker.value.copy(occupation = new)
    }


    fun nameErrorText(text: String? = _booker.value.name) =
        if (text.isNullOrBlank()) R.string.msg_required_field else null

    fun idCardNumberErrorText(text: String? = _booker.value.idCardNumber) =
        if (text.isNullOrBlank()) R.string.msg_required_field
        else if (text.length != 19) R.string.msg_invalid_field
        else null

    fun birthdayErrorText() = when {
        _booker.value.birthday > NOW -> R.string.msg_invalid_field
        else -> null
    }

    val nationalityErrorText = null

    val occupationErrorText = null

    val isNoError
        get() = nameErrorText() == null && nationalityErrorText == null && idCardNumberErrorText() == null && occupationErrorText == null && birthdayErrorText() == null

    fun saveOrUpdateAccount() {
        if (isNoError) viewModelScope.launch {
            onLoading(true)
            val booker = _booker.value.copy(
                bookerId = authRepo.bookerId!!
            )
            when {
                _isEditMode.value -> {
                    val changedFields = diffFields(_booker.value, oldBookerCopy!!)
                    if (changedFields.isEmpty()) {
                        updateOrDeletePhoto {
                            onMessageChange(R.string.msg_success_saving_booker)
                            onCompleteChange(true)
                        }
                        onCompleteChange(true)
                    } else repo.updateBooker(_booker.value).also {
                        if (it is Failure) {
                            onMessageChange(R.string.msg_error_update_account)
                            Log.e("$TAG Update_Profile", it.exception.message!!)
                        } else {
                            updateOrDeletePhoto {
                                onMessageChange(R.string.msg_success_saving_booker)
                                onCompleteChange(true)
                            }

                        }
                    }
                }

                else -> {
                    repo.createBooker(booker).also {
                        if (it is Failure) {
                            onMessageChange(R.string.msg_error_saving_account)
                            Log.e("$TAG Create_Profile", it.exception.message!!)
                        } else {
                            updateOrDeletePhoto {
                                onMessageChange(R.string.msg_success_saving_booker)
                                onCompleteChange(true)
                            }
                        }
                    }
                }
            }
            onLoading(false)
        }
        else onMessageChange(R.string.msg_fields_contain_errors)
    }

    /**
     * Either update to the new photo or if null delete its
     */
    private suspend fun updateOrDeletePhoto(doOnSuccess: () -> Unit) {
        when {
            !_isPhotoFailed.value && _photoBitmap.value == null && authRepo.hasAccountPhoto -> storageRepo.deleteProfilePhoto(
                _booker.value.bookerId
            ).also {
                if (it is Failure) {
                    onMessageChange(R.string.msg_error_del_photo)
                    Log.e("$TAG Del_Photo", it.exception.message!!)
                } else doOnSuccess()
            }

            !_isPhotoFailed.value && _photoUri.value != null && !authRepo.hasAccountPhoto -> storageRepo.saveProfilePhoto(
                _booker.value.bookerId,
                _photoUri.value!!
            ).also {
                if (it is Failure) {
                    onMessageChange(R.string.msg_error_saving_photo)
                    Log.e("$TAG Save_Photo", it.exception.message!!)
                } else {
                    doOnSuccess()
                }
            }

            else -> doOnSuccess()
        }
    }

    fun onAccountInitComplete(new: Boolean) {
        _isAccountInitComplete.value = new
        _isLoading.value = !new
    }

    fun onPhotoInitComplete(new: Boolean) {
        _isPhotoInitComplete.value = new
        _isPhotoLoading.value = !new
    }

    companion object {
        val TAG = "BAcc_VM"
    }
}

enum class BookerAccountDialogStatus {
    COULD_NOT_GET_PHOTO, COULD_NOT_GET_ACCOUNT, HELP_MAIN_PAGE, HELP_ON_JOB_SEEKING, LEAVING_WITHOUT_SAVING, LEAVING_WITH_EMPTY_PROFILE, WELCOME, NONE
}