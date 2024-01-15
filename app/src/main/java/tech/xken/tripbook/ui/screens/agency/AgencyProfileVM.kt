package tech.xken.tripbook.ui.screens.agency

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.data.models.NEW_ID
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
    val dialogStatus: AgencyProfileDialogStatus = AgencyProfileDialogStatus.NONE,

    val emailSupportCount: Long = 0L,
    val phoneSupportCount: Long = 0L,
    val refundPoliciesCount: Long = 0L,
    val hasAccount: Boolean = false,
    val hasSocialSupport: Boolean = false,

    val isEmailSupportComplete: Boolean = false,
    val isPhoneSupportComplete: Boolean = false,
    val isRefundPoliciesComplete: Boolean = false,
    val isAccountComplete: Boolean = false,
    val isSocialSupportComplete: Boolean = false,

    val sheetStatus: AgencyProfileSheetStatus = AgencyProfileSheetStatus.NONE,
)

enum class AgencyProfileSheetStatus {
    ACTIONS, NONE
}

enum class AgencyProfileDialogStatus {
    ABOUT_ACCOUNT, ABOUT_PHONE_SUPPORT, ABOUT_EMAIL_SUPPORT, ABOUT_SOCIAL_SUPPORT, ABOUT_REFUND_POLICY, ABOUT_MAIN_PAGE, NONE, FAILED_GET_ACCOUNT, FAILED_GET_PHONE_SUPPORT, FAILED_GET_EMAIL_SUPPORT, FAILED_GET_SOCIAL_SUPPORT, FAILED_GET_REFUND_POLICY
}

@HiltViewModel
class AgencyProfileVM @Inject constructor(
    private val repo: AgencyRepository,
    private val authRepo: AuthRepo,
    val savedStateHandle: SavedStateHandle,
    @NetworkStateFlowAnnot val networkState: NetworkState
) : ViewModel() {
    // To know if we are signing in up or checking the booker profile
    private val _message = MutableStateFlow<Int?>(null)
    private val _isInitComplete = MutableStateFlow(authRepo.agencyId == NEW_ID)
    private val _isComplete = MutableStateFlow(false)
    private val _dialogStatus = MutableStateFlow(AgencyProfileDialogStatus.NONE)

    private val _isAccountComplete = MutableStateFlow(false)
    private val _isPhoneSupportComplete = MutableStateFlow(false)
    private val _isEmailSupportComplete = MutableStateFlow(false)
    private val _isRefundPoliciesComplete = MutableStateFlow(false)
    private val _isSocialSupportComplete = MutableStateFlow(false)
    private val _sheetState = MutableStateFlow(AgencyProfileSheetStatus.NONE)

    val uiState = combine(
        _message,
        _isInitComplete,
        _isComplete,
        _dialogStatus,
        _sheetState,

        repo.countAgencyAccount(authRepo.agencyId).map {
            _isAccountComplete.value = true
            when (it) {
                is Results.Success -> {
                    it.data
                }
                else -> {
                    Log.e(TAG, "Agency Account: ${it.exception}")
                    _dialogStatus.value = AgencyProfileDialogStatus.FAILED_GET_ACCOUNT
                    0L
                }
            }
        }, // Boolean
        repo.countPhoneSupports(authRepo.agencyId).map {
            _isPhoneSupportComplete.value = true
            when (it) {
                is Results.Success -> it.data
                else -> {
                    _dialogStatus.value = AgencyProfileDialogStatus.FAILED_GET_PHONE_SUPPORT
                    Log.e(TAG, "Phone Support: ${it.exception}")
                    0L
                }
            }
        },
        repo.countEmailSupports(authRepo.agencyId).map {
            _isEmailSupportComplete.value = true
            when (it) {
                is Results.Success -> it.data
                else -> {
                    _dialogStatus.value = AgencyProfileDialogStatus.FAILED_GET_PHONE_SUPPORT
                    Log.e(TAG, "Email Support: ${it.exception}")
                    0L
                }
            }
        },
        repo.countSocialAccount(authRepo.agencyId!!).map {
            _isSocialSupportComplete.value = true
            when (it) {
                is Results.Success -> it.data
                is Results.Failure -> {
                    _dialogStatus.value = AgencyProfileDialogStatus.FAILED_GET_SOCIAL_SUPPORT
                    Log.e(TAG, "Social Account: ${it.exception}")
                    0L
                }
            }
        }, // Boolean
        repo.countRefundPolicies(authRepo.agencyId!!).map {
            _isRefundPoliciesComplete.value = true
            when (it) {
                is Results.Success -> it.data
                is Results.Failure -> {
                    _dialogStatus.value = AgencyProfileDialogStatus.FAILED_GET_REFUND_POLICY
                    Log.e(TAG, "Refund policy: ${it.exception}")
                    0L
                }
            }
        },

        _isAccountComplete,
        _isPhoneSupportComplete,
        _isEmailSupportComplete,
        _isRefundPoliciesComplete,
        _isSocialSupportComplete,
    ) {
        AgencyProfileUiState(
            message = it[0] as Int?,
            isInitComplete = it[1] as Boolean,
            isComplete = it[2] as Boolean,
            dialogStatus = it[3] as AgencyProfileDialogStatus,
            sheetStatus = it[4] as AgencyProfileSheetStatus,

            hasAccount = (it[5] as Long) > 0L,
            phoneSupportCount = it[6] as Long,
            emailSupportCount = it[7] as Long,
            hasSocialSupport = it[8] as Long > 0L,
            refundPoliciesCount = it[9] as Long,

            isAccountComplete = it[10] as Boolean,
            isPhoneSupportComplete = it[11] as Boolean,
            isEmailSupportComplete = it[12] as Boolean,
            isRefundPoliciesComplete = it[13] as Boolean,
            isSocialSupportComplete = it[14] as Boolean,
        )
    }.stateIn(
        scope = viewModelScope, started = WhileUiSubscribed, initialValue = AgencyProfileUiState()
    )

    fun onSheetStateChange(new: AgencyProfileSheetStatus) {
        _sheetState.value = new
    }

    fun onDialogStateChange(new: AgencyProfileDialogStatus) {
        _dialogStatus.value = new
    }

    fun onMessageChange(resId: Int?) {
        _message.value = resId
    }

    fun onCompleteChange(new: Boolean) {
        _isComplete.value = new
    }

    companion object {
        const val TAG = "AP_VM"
    }
}