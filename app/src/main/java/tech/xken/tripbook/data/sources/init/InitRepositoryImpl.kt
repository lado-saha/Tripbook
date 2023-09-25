//package tech.xken.tripbook.data.sources.init
//
//import kotlinx.coroutines.CoroutineDispatcher
//import tech.xken.tripbook.data.models.agency.Job
//
//class InitRepositoryImpl(
//    private val localDataSource: InitDataSource,
//    private val ioDispatcher: CoroutineDispatcher,
//) : InitRepository {
//    override suspend fun saveJobs(jobs: List<Job>) = localDataSource.saveJobs(jobs)
//
//    override suspend fun jobs() = localDataSource.jobs()
//
//    override suspend fun jobsFromIds(ids: List<String>) = localDataSource.jobsFromIds(ids)
//}