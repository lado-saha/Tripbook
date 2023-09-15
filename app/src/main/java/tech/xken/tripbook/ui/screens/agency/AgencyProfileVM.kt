package tech.xken.tripbook.ui.screens.agency

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.exception
import tech.xken.tripbook.data.sources.agency.AgencyRepository
import tech.xken.tripbook.domain.NetworkState
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.domain.di.NetworkStateFlowAnnot
import javax.inject.Inject

data class AgencyProfileUiState(
    val message: Int? = null,
    val isInitComplete: Boolean = false,
    val isComplete: Boolean = false,
    val dialogStatus: AgencyProfileDialogState = AgencyProfileDialogState.NONE,
    val emailSupportCount: Long = 0,
    val phoneSupportCount: Long = 0,
    val hasAccount: Boolean = false,
    val hasAccountLogo: Boolean = false,
    val hasSocialSupport: Boolean = false,
    val bookerCreditCardCount: Long = 0,
    val isAccountComplete: Boolean? = null,
    val isMoMoAccountComplete: Boolean? = null,
    val isOMAccountComplete: Boolean? = null,
    val isAgencySettingsComplete: Boolean? = null,
    val sheetStatus: AgencyProfileSheetState = AgencyProfileSheetState.NONE,
)
enum class AgencyProfileSheetState {
    ACTIONS, NONE
}

class AgencyProfileVM @Inject constructor(
    private val repo: AgencyRepository,
    private val authRepo: AuthRepo,
    val savedStateHandle: SavedStateHandle,
    @NetworkStateFlowAnnot val networkState: NetworkState
) : ViewModel() {
    // To know if we are signing in up or checking the booker profile
    private val _message = MutableStateFlow<Int?>(null)
    private val _isInitComplete = MutableStateFlow(!authRepo.hasAccount)
    private val _isComplete = MutableStateFlow(false)
    private val _dialogStatus = MutableStateFlow(AgencyProfileDialogState.NONE)
    private val _hasAccount = MutableStateFlow(authRepo.hasAccount)
    private val _hasAccountLogo = MutableStateFlow(authRepo.hasAccountLogo)
    private val _bookerCreditCardCount = MutableStateFlow(0L)
    private val _hasAgencyConfigs = MutableStateFlow(false)
    private val _isAccountComplete = MutableStateFlow<Boolean?>(null)
    private val _isMoMoAccountComplete = MutableStateFlow<Boolean?>(null)
    private val _isOMAccountComplete = MutableStateFlow<Boolean?>(null)
    private val _isAgencySettingsComplete = MutableStateFlow<Boolean?>(null)
    private val _sheetState = MutableStateFlow(AgencyProfileSheetState.NONE)
    init {
        initProfile()
    }

    val uiState = combine(
        // To know if we are signing in up or checking the booker profile
        _message,
        _isInitComplete,
        _isComplete,
        _dialogStatus,
        repo.countBookerMoMoAccounts(authRepo.bookerId!!)
            .map {
                when (it) {
                    is Results.Success -> {
                        _isMoMoAccountComplete.value = true
                        it.data
                    }

                    else -> {
                        _dialogStatus.value = AgencyProfileDialogState.FAILED_GET_MOMO
                        _isMoMoAccountComplete.value = false
                        Log.e(TAG, "MOMO: ${it.exception}")
                        0L
                    }
                }
            },
        repo.countBookerOMAccounts(authRepo.bookerId!!)
            .map {
                when (it) {
                    is Results.Success -> {
                        _isOMAccountComplete.value = true
                        it.data
                    }

                    is Results.Failure -> {
                        _isOMAccountComplete.value = false
                        _dialogStatus.value = AgencyProfileDialogState.FAILED_GET_OM
                        Log.e(TAG, "OM: ${it.exception}")
                        0L
                    }
                }
            },
        _hasAccount,
        _hasAccountLogo,
        _hasAgencyConfigs,
        _bookerCreditCardCount,
        _isAccountComplete,
        _isMoMoAccountComplete,
        _isOMAccountComplete,
        _isAgencySettingsComplete,
        _sheetState,
    ) {
        AgencyProfileUiState(
            message = it[0] as Int?,
            isInitComplete = it[1] as Boolean,
            isComplete = it[2] as Boolean,
            dialogStatus = it[3] as AgencyProfileDialogState,
            emailSupportCount = it[4] as Long,
            phoneSupportCount = it[5] as Long,
            hasAccount = it[6] as Boolean,
            hasAccountLogo = it[7] as Boolean,
            hasSocialSupport = it[8] as Boolean,
            bookerCreditCardCount = it[9] as Long,
            isAccountComplete = it[10] as Boolean?,
            isMoMoAccountComplete = it[11] as Boolean?,
            isOMAccountComplete = it[12] as Boolean?,
            isAgencySettingsComplete = it[13] as Boolean?,
            sheetStatus = it[14] as AgencyProfileSheetState
        )
    }.stateIn(
        scope = viewModelScope, started = WhileUiSubscribed, initialValue = AgencyProfileUiState()
    )

    fun initProfile() {
//        Accounts
        if (_isAccountComplete.value == null)
            viewModelScope.launch {
                try {
                    authRepo.forceRefreshUser()
                    _hasAccount.value = authRepo.hasAccount
                    _hasAccountLogo.value = authRepo.hasAccountLogo
                    _isAccountComplete.value = true
                } catch (e: Exception) {
                    _isAccountComplete.value = false
                    _dialogStatus.value = AgencyProfileDialogState.FAILED_GET_ACCOUNT
                    Log.e(TAG, "MAIN ACCOUNT: $e")
                }
            }

//        My Agency Settings
        if (_isAgencySettingsComplete.value == null)
            viewModelScope.launch {

            }

    }

    fun onSheetStateChange(new: AgencyProfileSheetState){
        _sheetState.value = new
    }

    fun onDialogStateChange(new: AgencyProfileDialogState) {
        _dialogStatus.value = new
    }

    fun onMessageChange(resId: Int?) {
        _message.value = resId
    }

    fun onCompleteChange(new: Boolean) {
        _isComplete.value = new
    }

    fun onMoMoCompleteChange(new: Boolean?) {
        _isMoMoAccountComplete.value = new
    }

    fun onOMCompleteChange(new: Boolean?) {
        _isOMAccountComplete.value = new
    }

    fun onAccountCompleteChange(new: Boolean?) {
        _isAccountComplete.value = new
    }

    fun onAgencySettingsCompleteChange(new: Boolean?) {
        _isAgencySettingsComplete.value = new
    }

    companion object {
        const val TAG = "BP_VM"
    }
}

enum class AgencyProfileDialogState {
    DELETE_ACCOUNT_3, DELETE_ACCOUNT_2, DELETE_ACCOUNT_1, ABOUT_ACCOUNT, ABOUT_MOMO, ABOUT_OM, ABOUT_CREDIT_CARD, ABOUT_AGENCY_SETTINGS, ABOUT_MAIN_PAGE, NONE, FAILED_GET_ACCOUNT, FAILED_GET_MOMO, FAILED_GET_OM, FAILED_GET_AGENCY_SETTINGS
}