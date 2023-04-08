package tech.xken.tripbook.data.sources.init.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tech.xken.tripbook.data.models.Booker
import tech.xken.tripbook.data.models.CurrentBooker
import tech.xken.tripbook.data.models.Job

@Dao
interface InitDao {
    //Current Booker
    @Insert
    fun saveJobs(jobs: List<Job>)

    @Query("SELECT * FROM Job")
    fun jobs(): List<Job>

    @Query("SELECT * FROM Job WHERE id in (:ids)")
    fun jobsFromIds(ids: List<String>): List<Job>
}