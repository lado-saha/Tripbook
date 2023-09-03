//package tech.xken.tripbook.data.sources.init
//
//import tech.xken.tripbook.data.models.agency.Job
//import tech.xken.tripbook.data.models.Results
//
//interface InitRepository {
//    suspend fun saveJobs(jobs: List<Job>)
//    suspend fun jobs(): Results<List<Job>>
//    suspend fun jobsFromIds(ids: List<String>): Results<List<Job>>
//}