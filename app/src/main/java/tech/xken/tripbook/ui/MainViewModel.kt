package tech.xken.tripbook.ui

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.sources.booker.BookerRepository
import tech.xken.tripbook.domain.NetworkState
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.domain.di.NetworkStateFlowAnnot
import tech.xken.tripbook.ui.SheetState.NETWORK_STATUS_MINIMAL_OFFLINE
import tech.xken.tripbook.ui.SheetState.NETWORK_STATUS_MINIMAL_ONLINE
import tech.xken.tripbook.ui.SheetState.NONE
import javax.inject.Inject
import tech.xken.tripbook.data.models.BookingKeys as BK

data class MainUiState(
    val isConnected: Boolean = true,
    val sheetState: SheetState = NONE,
    val isInitComplete: Boolean = false
)

data class CacheSyncUiState(
    val hasSyncMoMo: Boolean? = null,
    val hasSyncOM: Boolean? = null,
    val hasSyncAccount: Boolean? = null,
    val dialogStatus: CacheSyncDialogStatus = CacheSyncDialogStatus.NONE,
    val syncMoMoDone: Boolean = false,
    val syncOMDone: Boolean = false,
    val syncAccountDone: Boolean = false,
    val isSignedIn: Boolean = false
) {
    val syncDone =
        !isSignedIn || (hasSyncAccount == true && hasSyncOM == true && hasSyncMoMo == true)
    val syncMoMoFailed = syncMoMoDone && hasSyncMoMo != true
    val syncOMFailed = syncOMDone && hasSyncOM != true
    val syncAccountFailed = syncAccountDone && hasSyncAccount != true
}

@HiltViewModel
class MainViewModel @Inject constructor(
    val authRepo: AuthRepo,
    private val bookerRepo: BookerRepository,
    @NetworkStateFlowAnnot val networkState: NetworkState,
    private val datastore: DataStore<Preferences>
) : ViewModel() {
    private val _sheetState = MutableStateFlow(NONE)
    private val _isInitComplete = MutableStateFlow(authRepo.bookerId == null)
    private val _syncDialogStatus = MutableStateFlow(CacheSyncDialogStatus.NONE)
    private val _syncMoMoDone = MutableStateFlow(false)
    private val _syncOMDone = MutableStateFlow(false)
    private val _syncAccountDone = MutableStateFlow(false)


    val integrityUiState = combine(
        datastore.data.map { value -> value[BK.HAS_SYNC_ACCOUNT] },
        datastore.data.map { value -> value[BK.HAS_SYNC_MOMO_ACCOUNTS] },
        datastore.data.map { value -> value[BK.HAS_SYNC_OM_ACCOUNTS] },
        _syncDialogStatus,
        _syncMoMoDone,
        _syncOMDone,
        _syncAccountDone,
    ) { it ->
        CacheSyncUiState(
            hasSyncAccount = it[0] as Boolean?,
            hasSyncMoMo = it[1] as Boolean?,
            hasSyncOM = it[2] as Boolean?,
            dialogStatus = it[3] as CacheSyncDialogStatus,
            syncAccountDone = it[4] as Boolean,
            syncMoMoDone = it[5] as Boolean,
            syncOMDone = it[6] as Boolean,
            isSignedIn = authRepo.isSignedIn
        ).also { state ->
            Log.d("MainActivity", state.toString())
            if (!state.syncDone) {
                bookerRepo.syncCache(
                    syncUis = state,
                    onAccountComplete = {
                        _syncAccountDone.value = true
                        viewModelScope.launch {
                            datastore.edit { pref ->
                                pref[BK.HAS_SYNC_ACCOUNT] = it is Results.Success
                            }
                        }
                    },
                    onMoMoAccountComplete = {
                        _syncMoMoDone.value = true
                        viewModelScope.launch {
                            datastore.edit { pref ->
                                pref[BK.HAS_SYNC_MOMO_ACCOUNTS] = it is Results.Success
                            }
                        }
                    },
                    onOMAccountComplete = {
                        _syncOMDone.value = true
                        viewModelScope.launch {
                            datastore.edit { pref ->
                                pref[BK.HAS_SYNC_OM_ACCOUNTS] = it is Results.Success
                            }
                        }
                    }
                )
            }
        }

    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = CacheSyncUiState()
    )

    val uiState = combine(
        networkState.isConnected,
        _sheetState,
        _isInitComplete
    ) { args ->
//        when {
//            args[0] == true && (args[1] == NETWORK_STATUS_MINIMAL_OFFLINE || args[1] == NETWORK_STATUS_EXPANDED_OFFLINE) ->
//                _sheetState.value = NETWORK_STATUS_MINIMAL_ONLINE
//
//            args[0] == false && (args[1] == NETWORK_STATUS_EXPANDED_OFFLINE) -> _sheetState.value =
//                NETWORK_STATUS_MINIMAL_OFFLINE
//        }
        when {
            args[0] == true -> _sheetState.value = NETWORK_STATUS_MINIMAL_ONLINE
            args[0] == false -> _sheetState.value = NETWORK_STATUS_MINIMAL_OFFLINE
        }
        MainUiState(
            isConnected = args[0] as Boolean,
            sheetState = args[1] as SheetState,
            isInitComplete = args[2] as Boolean

        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = MainUiState()
    )

    fun onSheetStateChange(new: SheetState) {
        _sheetState.value = new
    }

    fun onSyncDialogStatusChange(new: CacheSyncDialogStatus) {
        _syncDialogStatus.value = new
    }

    fun onSyncAccountDoneChange(new: Boolean) {
        _syncAccountDone.value = new
    }

    fun onSyncMoMoDoneChange(new: Boolean) {
        _syncMoMoDone.value = new
    }

    fun onSyncOMDoneChange(new: Boolean) {
        _syncOMDone.value = new
    }

    companion object {
        const val TAG = "MACT_VM"
    }

}

enum class CacheSyncDialogStatus {
    HELP_ACCOUNT, HELP_MOMO, HELP_OM, HELP_CREDIT_CARD, HELP_AGENCY_SETTINGS, HELP_MAIN_PAGE, NONE, FAILED_GET_ACCOUNT, FAILED_GET_MOMO, FAILED_GET_OM
}

enum class SheetState {
    NETWORK_STATUS_MINIMAL_ONLINE,
    NETWORK_STATUS_EXPANDED_OFFLINE,
    NONE,
    NETWORK_STATUS_MINIMAL_OFFLINE
}