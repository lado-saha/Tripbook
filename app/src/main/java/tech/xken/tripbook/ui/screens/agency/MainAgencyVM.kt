package tech.xken.tripbook.ui.screens.agency

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.data.sources.agency.AgencyRepository
import tech.xken.tripbook.domain.NetworkState
import tech.xken.tripbook.domain.WhileUiSubscribed
import tech.xken.tripbook.domain.di.NetworkStateFlowAnnot
import javax.inject.Inject

enum class SyncState {
    ONE_TIME, REALTIME, OFF
}

data class SyncBackgroundState(
    val currentSyncState: SyncState = SyncState.ONE_TIME
)

@HiltViewModel
class MainAgencyVM @Inject constructor(
    val authRepo: AuthRepo,
    private val agencyRepo: AgencyRepository,
    @NetworkStateFlowAnnot val networkState: NetworkState,
    private val datastore: DataStore<Preferences>
) : ViewModel() {
    private val _currentSyncState = MutableStateFlow(SyncState.ONE_TIME)
    private var onetimeSyncScope = viewModelScope
    private var realtimeSyncScope = viewModelScope

    init {
        onetimeSync()
        realtimeSync()
    }


    /**
     * This is a list of background functions which will be launched and forgotten as long as the
     */
    fun onetimeSync() = with(viewModelScope ) {
        authRepo.agencyId.let {
            launch { agencyRepo.agencyAccountFullSync(it) }
            launch { agencyRepo.emailSupportsFullSync(it) }
            launch { agencyRepo.phoneSupportsFullSync(it) }
            launch { agencyRepo.socialSupportFullSync(it) }
            launch { agencyRepo.refundPoliciesFullSync(it) }
        }
    }/*.invokeOnCompletion {
        Log.d("Sync Scope", " The sync scope is complete ${it?.message}")
    }*/


    fun realtimeSync() = with(viewModelScope) {
        authRepo.agencyId.let {
            launch { agencyRepo.agencyAccountLogFlow(it).collect {} }
            launch { agencyRepo.emailSupportsLogFlow(it).collect {} }
            launch { agencyRepo.phoneSupportsLogFlow(it).collect {} }
            launch { agencyRepo.socialSupportLogFlow(it).collect {} }
            launch { agencyRepo.refundPoliciesLogFlow(it).collect {} }
        }
    }


    val syncState = combine(
        _currentSyncState,
    ) {
        SyncBackgroundState(
            currentSyncState = it[0]
        )
    }.stateIn(
        viewModelScope,
        WhileUiSubscribed,
        SyncBackgroundState()
    )


}