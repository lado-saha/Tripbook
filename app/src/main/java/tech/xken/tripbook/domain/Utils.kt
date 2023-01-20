package tech.xken.tripbook.domain

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.Road
import tech.xken.tripbook.data.models.Town
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext

/** Path to the file containing all towns in the country cameroon*/


/**
 * Takes a french abbreviation from of the regions of cameroon
 */
fun regionFromAbbr(abbr: String) =
    when (abbr) {
        "nor" -> "north"
        "sud" -> "south"
        "est" -> "east"
        "oue" -> "west"
        "nou" -> "north west"
        "sou" -> "south west"
        "lit" -> "littoral"
        "cen" -> "center"
        "eno" -> "far north"
        "ada" -> "adamawa"
        else -> "unknown"
    }

suspend fun parseStrTowns(
    allTownStr: Array<String>,
    ioDispatcher: CoroutineContext = Dispatchers.IO,
    forEachTown: (townInfo: String, left: Int) -> Unit,
) = withContext(ioDispatcher) {
    Log.d("INFO_TOWNS", allTownStr.toString())
    allTownStr.mapIndexed { index, strTown ->
//        Buea Buea Fako SOU 9.28085 4.15895 1033139.50058209 460282.657389462
        val info = strTown.trim().split(" ")
            .map { it.trim().lowercase().replace('é', 'e').replace('_', ' ') }
        Log.d("INFO", info.toString())
        forEachTown(strTown, allTownStr.size - index)
        val isIncomplete = info[1] == "null" || info[2] == "null" || info[3] == "null"
        Town(
            id = UUID.randomUUID().toString(),
            name = info[0],
            subdivision = info[1],
            division = info[2],
            region = regionFromAbbr(info[3]),
            lon = info[4].toDouble(),
            lat = info[5].toDouble(),
            xm = info[6].toDouble(),
            ym = info[7].toDouble(),
            timestamp = Date().time,
            isComplete = !isIncomplete,
            country = "cameroon",
        )
    }
}

/**
 * For every string road, we create a [Road] object. Now, for the itinerary, we return the id of any town
 * which are members of the this if they exist else we create a town with the name
 */
suspend fun parseStrRoads(
    allRoads: Array<String>,
    towns: List<Town>,
    ioDispatcher: CoroutineContext = Dispatchers.IO,
    forEachRoad: (through: String, remaining: Int) -> Unit,
) = withContext(ioDispatcher) {
    val size = allRoads.size
    allRoads.mapIndexed { index, strRoad ->
//        Wum Yabassi Wum-Befang-Bamenda-Babadjou-Mbouda-Bafoussam-Bafang-Bakou-Nkondjock-Yabassi 333
//        Splitting towns and distance, then replacing all underscores used for separation to the normal spaces
        val info = strRoad.trim().split(" ").map { it.trim().lowercase().replace('_', ' ') }
        //We get the ids of the first town, last town and all the other towns which makes up the road
        val strThrough =
            info[2].split('-').fold(initial = mutableListOf<String>()) { acc, townName ->
                acc.apply { add(towns.find { it.name == townName }!!.id) }
            }.toList()

        //We then emit the current progression together with the left amount
        forEachRoad(strThrough.toString(), size - index)
        Road(
            id = UUID.randomUUID().toString(),
            name = null,
            distance = info[3].toDouble(),
            shape = null,
            town1 = strThrough.first(),
            town2 = strThrough.last(),
            timestamp = Date().time
        ).apply {
            itineraryTownsIds = strThrough
        }
    }
}

/*
//Reverses a Pair (x , y) to (y, x)
val <A, B> Pair<A, B>.reverse: Pair<B, A>
    get() {
        return second to first
    }
*/
/**
 * A generic class that holds a loading signal or a [Results].
 */
sealed class Async<out T> {
    object Loading : Async<Nothing>()
    data class Success<out T>(val data: T) : Async<T>()
}

/**
 * Maximum time after which the state observers must stop observing any flow
 */
private const val StopTimeoutMillis: Long = 5000

/**
 * A [SharingStarted] meant to be used with a [StateFlow] to expose data to the UI.
 *
 * When the UI stops observing, upstream flows stay active for some time to allow the system to
 * come back from a short-lived configuration change (such as rotations). If the UI stops
 * observing for longer, the cache is kept but the upstream flows are stopped. When the UI comes
 * back, the latest value is replayed and the upstream flows are executed again. This is done to
 * save resources when the app is in the background but let users switch between apps quickly.
 */
val WhileUiSubscribed: SharingStarted = SharingStarted.WhileSubscribed(StopTimeoutMillis)

/**
 * Capitalises the string
 */
val String.caps
    get() = replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }

/**
 * This is used in searching. It works by comparing a shorter string [inputStr] to the equivalent part of [referenceStr].
 * To do this, we first make sure [inputStr] length is less than or equals to [referenceStr]. In this case, we cut (starting
 * from the beginning and with equal length as [inputStr]), [referenceStr] and compare with [inputStr] to see if they are the same.
 * @param inputStr is the supposed shorter string which is usually the input we want to compare with [str2
 * @param referenceStr is the supposed longer reference string.
 */
fun strAreTheSame(inputStr: String, referenceStr: String): Boolean {
    val inputStr1 = inputStr.filterNot { it == ' ' }
    val referenceStr1 = referenceStr.filterNot { it == ' ' }
    return if (inputStr1.length > referenceStr1.length) false
    else
        referenceStr1.substring(inputStr1.indices).contentEquals(inputStr1, ignoreCase = true)
}
