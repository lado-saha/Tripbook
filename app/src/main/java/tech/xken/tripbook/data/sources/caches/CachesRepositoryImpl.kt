package tech.xken.tripbook.data.sources.caches

import kotlinx.coroutines.CoroutineDispatcher
import tech.xken.tripbook.data.models.UnivSelection

class CachesRepositoryImpl(
    private val localDataSource: CachesDataSource,
    val ioDispatcher: CoroutineDispatcher,
) : CachesRepository {
    override fun univSelections() = localDataSource.univSelections()

    override suspend fun saveUnivSelections(selections: List<UnivSelection>) {
        localDataSource.saveUnivSelections(selections)
    }

    override suspend fun clearUnivSelections() {
        localDataSource.clearUnivSelections()
    }
}