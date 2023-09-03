//package tech.xken.tripbook.data.sources.init.local
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.Query
//
//import tech.xken.tripbook.data.models.agency.Job
//
//@Dao
//interface InitDao {
//    //Current Booker
//    @Insert
//    fun saveJobs(jobs: List<Job>)
//
//    @Query("SELECT * FROM Job")
//    fun jobs(): List<Job>
//
//    @Query("SELECT * FROM Job WHERE id in (:ids)")
//    fun jobsFromIds(ids: List<String>): List<Job>
//}