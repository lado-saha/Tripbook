package tech.xken.tripbook.data.sources.init

import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.Booker
import tech.xken.tripbook.data.models.Job
import tech.xken.tripbook.data.models.Results

interface InitDataSource {
    suspend fun saveJobs(jobs: List<Job>)
    suspend fun jobs(): Results<List<Job>>
    suspend fun jobsFromIds(ids: List<String>): Results<List<Job>>
}