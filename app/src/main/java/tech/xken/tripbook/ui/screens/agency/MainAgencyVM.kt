package tech.xken.tripbook.ui.screens.agency

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.data.models.DbAction
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.Results.Failure
import tech.xken.tripbook.data.models.Results.Success
import tech.xken.tripbook.data.sources.agency.AgencyRepository
import tech.xken.tripbook.domain.NetworkState
import tech.xken.tripbook.domain.di.NetworkStateFlowAnnot
import javax.inject.Inject


@HiltViewModel
class MainAgencyVM @Inject constructor(
    val authRepo: AuthRepo,
    private val agencyRepo: AgencyRepository,
    @NetworkStateFlowAnnot val networkState: NetworkState,
    private val datastore: DataStore<Preferences>
) : ViewModel() {
    init {
        viewModelScope.launch {
            agencyRepo.agencyAccountLogFlow(authRepo.agencyId!!)
                .onEach {
                    when(it){
                        is Failure -> TODO()
                        is Success -> {
                            agencyRepo.agencyAccount()
                        }
                    }
                }
                .catch { Failure(it as Exception) }
                .collect {
                  when(it){
                      is Failure -> TODO()
                      is Success -> {
                          when(it.data.dbAction){
                              DbAction.INSERT -> {
                                  agencyRepo.createAgencyAccount(offline = true, it.)
                              }
                              DbAction.UPDATE -> {

                              }
                              DbAction.DELETE -> {

                              }
                          }
                      }
                  }
                }
        }
    }
}