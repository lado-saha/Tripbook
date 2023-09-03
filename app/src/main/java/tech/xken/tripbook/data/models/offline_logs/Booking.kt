package tech.xken.tripbook.data.models.offline_logs
//
//import androidx.room.Entity
//import kotlinx.datetime.Instant
//import tech.xken.tripbook.data.models.CI
//import tech.xken.tripbook.data.models.OpType
//
//sealed class BookingLogs {
//    @Entity(tableName = "log_booker", primaryKeys = ["booker_id", "op_type"])
//    data class LogBooker(
//        @CI(name = "booker_id") val bookerId: String,
//        @CI("op_type") val opType: OpType,
//        @CI(name = "added_on") val addedOn: Instant,
//    ) : BookingLogs(){
//
//    }
//
//    @Entity(
//        tableName = "log_booker_momo_account",
//        primaryKeys = ["booker_id", "phone_number", "op_type"]
//    )
//    data class LogBookerMoMoAccount(
//        @CI(name = "booker_id") val bookerId: String,
//        @CI(name = "phone_number") val phoneNumber: String,
//        @CI("op_type") val opType: OpType,
//        @CI(name = "added_on") val addedOn: Instant,
//    ) : BookingLogs()
//
//    @Entity(
//        tableName = "log_booker_om_account",
//        primaryKeys = ["booker_id", "phone_number", "op_type"]
//    )
//    data class LogBookerOMAccount(
//        @CI(name = "booker_id") val bookerId: String,
//        @CI(name = "phone_number") val phoneNumber: String,
//        @CI("op_type") val opType: OpType,
//        @CI(name = "added_on") val addedOn: Instant,
//    ) : BookingLogs()
//
//}