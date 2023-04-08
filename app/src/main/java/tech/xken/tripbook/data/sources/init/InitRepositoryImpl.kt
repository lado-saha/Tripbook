package tech.xken.tripbook.data.sources.init

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.Booker
import tech.xken.tripbook.data.models.CurrentBooker
import tech.xken.tripbook.data.models.Job
import tech.xken.tripbook.data.models.Results
import java.util.*

class InitRepositoryImpl(
    private val localDataSource: InitDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : InitRepository {
    override suspend fun saveJobs(jobs: List<Job>) = localDataSource.saveJobs(jobs)

    override suspend fun jobs() = localDataSource.jobs()

    override suspend fun jobsFromIds(ids: List<String>) = localDataSource.jobsFromIds(ids)
}