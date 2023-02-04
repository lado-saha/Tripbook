package tech.xken.tripbook.ui.screens.booking

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.*
import tech.xken.tripbook.data.models.Gender.Companion.gendersStrRes
import tech.xken.tripbook.data.sources.booking.BookingRepository
import tech.xken.tripbook.domain.Async
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.domain.isCodeInvalid
import tech.xken.tripbook.domain.isPhoneInvalid
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates


data class BookerSignUpUiState(
    val isLoading: Boolean = false,
    val message: Int? = null,
    val booker: Booker = Booker.new(),
    val isPeekingPassword: Boolean = false,
    val isGenderExpanded: Boolean = false,
    val calendar: Calendar = Calendar.getInstance(),
    val selectedGender: Int = gendersStrRes[0],
    val photoUri: Uri? = null,
    val photoBitmap: Bitmap? = null,
    val isInitComplete: Boolean = false,
    val isComplete: Boolean = false,
) {
    val isEditMode get() = if (isInitComplete) when (booker.id) {
        NEW_ID -> false
        else -> true
    } else null
}

@HiltViewModel
class BookerSignUpVM @Inject constructor(
    private val repo: BookingRepository,
) : ViewModel() {
    // To know if we are signing in up or checking the booker profile
    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<Int?>(null)
    private val _booker = MutableStateFlow(Booker.new())
    private val _isPeekingPassword = MutableStateFlow(false)
    private val _isGenderExpanded = MutableStateFlow(false)
    private val _calendar = MutableStateFlow(Calendar.getInstance())
    private val _selectedGender = MutableStateFlow(gendersStrRes[0])
    private val _photoUri = MutableStateFlow<Uri?>(null)
    private val _photoBitmap = MutableStateFlow<Bitmap?>(null)
    private val _isInitComplete = MutableStateFlow(false)
    private val _isComplete = MutableStateFlow(false)

    private val _currentBookerAsync =
        repo.getCurrentBookerStream().map { handleResults(it) }.onStart { onLoading(true) }

    val uiState = combine(
        _isLoading,//0
        _message,//1
        _booker,//2
        _isPeekingPassword,//3
        _isGenderExpanded,//4
        _calendar,//5
        _selectedGender,//6
        _photoUri,//7
        _photoBitmap,//8
        _currentBookerAsync,//9
        _isInitComplete,//10
        _isComplete//11
    ) { args ->
        if (!_isInitComplete.value) {
            val currentBookerAsync = args[9] as Async.Success<CurrentBooker?>
            //If there is a current user(There is a user logged in), we get the id and then continue to ge the full booker details
            if (currentBookerAsync.data == null) {
                _isInitComplete.value = true
                onLoading(false)
            } else _booker.value = (args[2] as Booker).run { copy(id = currentBookerAsync.data.id) }
        }

        BookerSignUpUiState(
            isLoading = args[0] as Boolean,
            message = args[1] as Int?,
            booker = args[2] as Booker,
            isPeekingPassword = args[3] as Boolean,
            isGenderExpanded = args[4] as Boolean,
            calendar = args[5] as Calendar,
            selectedGender = args[6] as Int,
            photoUri = args[7] as Uri?,
            photoBitmap = args[8] as Bitmap?,
            isInitComplete = args[10] as Boolean,
            isComplete = args[11] as Boolean
        ).also { loadBookerDetails() }
    }.stateIn(
        scope = viewModelScope, started = WhileUiSubscribed, initialValue = BookerSignUpUiState()
    )

    /**
     * Load the booker details only when the booker is not new and the initialisation is still ongoing
     */
    private fun loadBookerDetails() {
        if (!_isInitComplete.value) viewModelScope.launch {
            onLoading(true)
            repo.bookersFromIds(listOf(_booker.value.id)).also {
                when (it) {
                    is Results.Failure -> {
                        onChangeMessage(R.string.msg_unexpexted_error)
                        _isComplete.value = true
                        //TODO: navigate away. This can rarely happen
                    }
                    is Results.Success -> {
                        val booker = it.data.first()
                        _booker.value = booker
                        _photoUri.value = booker.photoUrl?.toUri()
                        _selectedGender.value = Gender.strGenderToGender(booker.gender).stringResId
                        onChangeMessage(R.string.msg_welcome_back)
                    }
                }
                _isInitComplete.value = true
            }
            onLoading(false)
        }
    }

    fun onPhotoUriChange(context: Context, new: Uri?) {
        _photoUri.value = new
        _booker.value = _booker.value.copy(photoUrl = new?.toString())
        _photoBitmap.value = null
        if (new == null) {
            return
        }
        //We try to load the image
        loadPhotoBitmap(context)
    }

    private fun handleResults(currentBookerResults: Results<CurrentBooker?>) =
        when (currentBookerResults) {
            is Results.Failure -> Async.Success(null)
            is Results.Success -> Async.Success(currentBookerResults.data)
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
            //TODO: Handle Exception
        }
    }

    private fun onLoading(new: Boolean) {
        _isLoading.value = new
    }

    fun onChangeMessage(resId: Int?) {
        _message.value = resId
    }

    fun onCompleteChange(new: Boolean) {
        _isComplete.value = new
    }

    fun onNameChange(new: String) {
        _booker.value = _booker.value.copy(name = new)
    }

    fun onEmailChange(new: String) {
        _booker.value = _booker.value.copy(email = new)
    }

    fun onPasswordChange(new: String) {
        _booker.value = _booker.value.copy(password = new)
    }

    fun invertPasswordPeeking() {
        _isPeekingPassword.value = !_isPeekingPassword.value
    }

    fun onBirthdayChange(new: Long) {
        _booker.value = _booker.value.copy(birthday = new)
        _calendar.value = _calendar.value.apply { timeInMillis = new }
    }

    fun formattedBirthday(): String {
        return SimpleDateFormat(
            "EEEE dd MMMM yyyy", Locale.getDefault()
        ).format(_calendar.value.time)
    }

    fun onSelectedGenderChange(new: Int) {
        _selectedGender.value = new
        _booker.value = _booker.value.copy(gender = Gender.resIdToGender(new).strGender)
    }

    fun onGenderExpansionChange(new: Boolean) {
        _isGenderExpanded.value = !_isGenderExpanded.value
    }

    fun onPhoneCodeChange(new: String) {
        _booker.value = _booker.value.copy(phoneCode = new)
    }

    val countryFromCode get() = codeCountryMap[_booker.value.phoneCode] ?: ""

    fun onPhoneChange(new: String) {
        _booker.value = _booker.value.copy(phone = new)
    }

    fun onNationalityChange(new: String) {
        _booker.value = _booker.value.copy(nationality = new)
    }

    fun onOccupationChange(new: String) {
        _booker.value = _booker.value.copy(occupation = new)
    }

    fun nameErrorText(text: String? = _booker.value.name) =
        if (text.isNullOrBlank()) R.string.msg_required_field else null

    fun emailErrorText(text: String? = _booker.value.email) =
        if (text.isNullOrBlank()) R.string.msg_required_field else null

    fun passwordErrorText(text: String? = _booker.value.password) = when {
        text.isNullOrBlank() -> R.string.msg_required_field
        text.length < 8 -> R.string.msg_invalid_password
        else -> null
    }

    fun birthdayErrorText() = when {
        (_booker.value.birthday ?: 0L) > Date().time -> R.string.msg_invalid_field
        else -> null
    }

    val genderErrorText = null

    fun phoneCodeErrorText(text: String? = _booker.value.phoneCode) = when {
        text.isNullOrBlank() -> null
        isCodeInvalid(text) -> R.string.msg_invalid_field
        else -> null
    }

    fun phoneErrorText(text: String? = _booker.value.phone) = when {
        text.isNullOrBlank() -> null
        isPhoneInvalid(text, _booker.value.phoneCode) -> R.string.msg_invalid_field
        else -> null
    }

    val nationalityErrorText = null

    val occupationErrorText = null

    val isNoError
        get() = nameErrorText() == null && nationalityErrorText == null && phoneErrorText() == null && emailErrorText() == null && passwordErrorText() == null && genderErrorText == null && occupationErrorText == null && birthdayErrorText() == null

    fun save() {
        if (isNoError) viewModelScope.launch {
            onLoading(true)
            val booker = _booker.value.copy(
                id = if (uiState.value.isEditMode == false) UUID.randomUUID()
                    .toString() else _booker.value.id
            )
            repo.saveBooker(booker)
            onChangeMessage(R.string.msg_success_saving_booker)
            _isComplete.value = true
            onLoading(false)
        }
        else onChangeMessage(R.string.msg_fields_contain_errors)
    }
}
