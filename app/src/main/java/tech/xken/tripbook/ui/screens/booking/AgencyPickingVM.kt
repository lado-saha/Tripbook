package tech.xken.tripbook.ui.screens.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import tech.xken.tripbook.data.sources.univ.UniverseRepository
import javax.inject.Inject

@HiltViewModel
class AgencyPickingVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
//    private val agencyRepo: AgencyRepo,
    private val universeRepo: UniverseRepository,
): ViewModel() {

}