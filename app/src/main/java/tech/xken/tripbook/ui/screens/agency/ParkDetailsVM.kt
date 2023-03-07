package tech.xken.tripbook.ui.screens.agency

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
import tech.xken.tripbook.data.sources.agency.AgencyRepository
import tech.xken.tripbook.data.sources.caches.CachesRepository
import tech.xken.tripbook.domain.Async
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.domain.isCodeInvalid
import tech.xken.tripbook.domain.isPhoneInvalid
import tech.xken.tripbook.ui.navigation.UniverseArgs
import java.util.*
import javax.inject.Inject

data class ParkDetailsUiState(
    val park: Park = Park.new(),
    val isLoading: Boolean = false,
    val message: Int? = null,
    val isInitComplete: Boolean = false,
    val isComplete: Boolean = false,
    val calendar: Calendar = Calendar.getInstance(),
    val photoUri: Uri? = null,
    val photoBitmap: Bitmap? = null,
    val isProfileVisible: Boolean = true,
    val results: List<UnivSelection> = listOf(),
) {
    val countryFromCode1 = codeCountryMap[park.supportPhone1Code] ?: ""
    val countryFromCode2 = codeCountryMap[park.supportPhone2Code] ?: ""
    val isEditMode
        get() = when (park.id) {
            NEW_ID -> false
            else -> true
        }
}

@HiltViewModel
class ParkDetailsVM @Inject constructor(
    private val repo: AgencyRepository,
    private val cachesRepo: CachesRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<Int?>(null)
    private val _calendar = MutableStateFlow(Calendar.getInstance())
    private val _photoUri = MutableStateFlow<Uri?>(null)
    private val _photoBitmap = MutableStateFlow<Bitmap?>(null)
    private val _isInitComplete = MutableStateFlow(false)
    private val _isComplete = MutableStateFlow(false)
    private val _isProfileVisible = MutableStateFlow(false)
    private val _park =
        MutableStateFlow(Park.new().copy(id = savedStateHandle["park_id"] ?: NEW_ID))
    private val _results = cachesRepo.univSelections()
        .map { handleResult(it) }
        .onStart { Async.Loading }

    private fun handleResult(selection: Results<List<UnivSelection>>): Async<List<UnivSelection>?> =
        if (selection is Results.Success) {
            Log.d("SEARCH", selection.data.toString())
            Async.Success(selection.data)
            //We then clear
        } else {
            Async.Success(null)
        }

    val uiState = combine(
        _isLoading,//0
        _message,//1
        _park,//2
        _calendar,//3
        _photoUri,//4
        _photoBitmap,//5
        _isInitComplete,//6
        _isComplete,//7
        _isProfileVisible,
        _results
    ) { args ->
        ParkDetailsUiState(
            isLoading = args[0] as Boolean,
            message = args[1] as Int?,
            park = args[2] as Park,
            calendar = args[3] as Calendar,
            photoUri = args[4] as Uri?,
            photoBitmap = args[5] as Bitmap?,
            isInitComplete = args[6] as Boolean,
            isComplete = args[7] as Boolean,
            isProfileVisible = args[8] as Boolean,
            results = (args[9] as Async.Success<List<UnivSelection>>).data
        ).also { loadParkDetails(it) }
    }.stateIn(
        scope = viewModelScope, started = WhileUiSubscribed, initialValue = ParkDetailsUiState()
    )

    private fun loadParkDetails(uis: ParkDetailsUiState) {
        if (!_isInitComplete.value && uis.isEditMode)
            viewModelScope.launch {
                onLoading(true)
                repo.parksFromIds(listOf(_park.value.id)).also {
                    when (it) {
                        is Results.Failure -> {
                            _isComplete.value = true
                        }
                        is Results.Success -> {
                            val park = it.data.first()
                            _park.value = park
                            _photoUri.value = park.photoUrl?.toUri()
                        }
                    }
                }
                onLoading(false)
                _isInitComplete.value = true
            }
    }

    fun onNameChange(new: String) {
        _park.value = _park.value.copy(name = new)
    }

    fun onEmail1Change(new: String) {
        _park.value = _park.value.copy(supportEmail1 = new)
    }

    fun onEmail2Change(new: String) {
        _park.value = _park.value.copy(supportEmail2 = new)
    }

    fun onPhone1Change(new: String) {
        _park.value = _park.value.copy(supportPhone1 = new)
    }

    fun onPhone2Change(new: String) {
        _park.value = _park.value.copy(supportPhone2 = new)
    }

    fun onPhone1CodeChange(new: String) {
        _park.value = _park.value.copy(supportPhone1Code = new)
    }

    fun onPhone2CodeChange(new: String) {
        _park.value = _park.value.copy(supportPhone2Code = new)
    }

    fun onLatChange(new: Double) {
        _park.value = _park.value.copy(lat = new)
    }

    fun onLonChange(new: Double) {
        _park.value = _park.value.copy(lon = new)
    }

    fun onPhotoUriChange(context: Context, new: Uri?) {
        _photoUri.value = new
        _park.value = _park.value.copy(photoUrl = new?.toString())
        _photoBitmap.value = null
        if (new == null) {
            return
        }
        //We try to load the image
        loadPhotoBitmap(context)
    }

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

    fun onLoading(new: Boolean) {
        _isLoading.value = new
    }

    fun onChangeProfileVisibility(new: Boolean) {
        _isProfileVisible.value = new
    }

    fun onChangeMessage(resId: Int?) {
        _message.value = resId
    }

    fun nameErrorText(text: String? = _park.value.name) =
        if (text.isNullOrBlank()) R.string.msg_required_field else null

    fun email1ErrorText(text: String? = _park.value.supportEmail2) =
        if (text.isNullOrBlank()) R.string.msg_required_field else null

    fun email2ErrorText(text: String? = _park.value.supportEmail1) = null/*TODO: Email checker*/

    fun phone1CodeErrorText(text: String? = _park.value.supportPhone1Code) = when {
        text.isNullOrBlank() -> R.string.msg_required_field
        isCodeInvalid(text) -> R.string.msg_invalid_field
        else -> null
    }

    fun phone2CodeErrorText(text: String? = _park.value.supportPhone2Code) = when {
        text.isNullOrBlank() -> null
        isCodeInvalid(text) -> R.string.msg_invalid_field
        else -> null
    }

    fun phone1ErrorText(text: String? = _park.value.supportPhone1) = when {
        text.isNullOrBlank() -> R.string.msg_required_field
        isPhoneInvalid(text, _park.value.supportPhone1Code) -> R.string.msg_invalid_field
        else -> null
    }

    fun phone2ErrorText(text: String? = _park.value.supportPhone2) = when {
        text.isNullOrBlank() -> null
        isPhoneInvalid(text, _park.value.supportPhone2Code) -> R.string.msg_invalid_field
        else -> null
    }

    val isNoError
        get() = nameErrorText() == null && phone1ErrorText() == null && phone2ErrorText() == null && email1ErrorText() == null && email2ErrorText() == null

    fun save() {
        if (isNoError) viewModelScope.launch {
            onLoading(true)
            val booker = _park.value.copy(
                id = if (!uiState.value.isEditMode) UUID.randomUUID()
                    .toString() else _park.value.id
            )
            repo.savePark(booker)
            onChangeMessage(R.string.msg_success_saving_park)
            _isComplete.value = true
            onLoading(false)
        }
        else onChangeMessage(R.string.msg_fields_contain_errors)
    }

}