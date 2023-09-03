//package tech.xken.tripbook.domain
//
//import android.util.Log
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import tech.xken.tripbook.data.models.*
//import java.util.*
//import kotlin.coroutines.CoroutineContext
//
//
//// univStrings format
//// town[0] subdivision[1] division[2] region[3] lon[4] lat[5] xm[6] ym[7] country[8] continent[9] planet[10]
//
//fun parsePlanets() =
//    listOf(
//        Planet(
//            id = UUID.randomUUID().toString(),
//            name = "earth",
//            timestamp = Date().time
//        )
//    )
//
//suspend fun parseContinents(
//    ioDispatcher: CoroutineContext = Dispatchers.IO,
//    planetFromContinentName: suspend (name: String) -> Planet,
//) = withContext(ioDispatcher) {
//    listOf(
//        Continent(
//            id = UUID.randomUUID().toString(),
//            name = "africa",
//            timestamp = Date().time,
//            planet = planetFromContinentName("earth").id
//        )
//    )
//}
//
//suspend fun parseCountries(
//    ioDispatcher: CoroutineContext = Dispatchers.IO,
//    continentFromCountryName: suspend (name: String) -> Continent,
//) = withContext(ioDispatcher) {
//    listOf(
//        Country(
//            countryId = UUID.randomUUID().toString(),
//            name = "cameroon",
//            addedOn = Date().time,
//            continent = continentFromCountryName("africa").id
//        )
//    )
//}
//
//suspend fun parseRegions(
//    univStrings: Array<String>,
//    ioDispatcher: CoroutineContext = Dispatchers.IO,
//    countryFromRegionName:suspend (name: String) -> Country,
//) = withContext(ioDispatcher) {
//    univStrings.map {
//        it.split(" ")[3].propre
//    }.toSet().map {
//        Region(
//            regionId = UUID.randomUUID().toString(),
//            name = it,
//            addedOn = Date().time,
//            countryId = countryFromRegionName("cameroon").countryId,
//            isCapital = false
//        )
//    }
//}
//
//suspend fun parseDivisions(
//    univStrings: Array<String>,
//    ioDispatcher: CoroutineContext = Dispatchers.IO,
//    regionFromDivisionName: suspend (name: String) -> Region,
//) = withContext(ioDispatcher) {
//    data class DivisionAndRegion(
//        val division: String,
//        val region: String,
//    )
//    univStrings.map {
//        DivisionAndRegion(
//            division = it.split(" ")[2].propre,
//            region = it.split(" ")[3].propre
//        )
//    }. toSet().map {
//        Division(
//            divisionId = UUID.randomUUID().toString(),
//            name = it.division,
//            addedOn = Date().time,
//            regionId = regionFromDivisionName(it.region).regionId,
//            isCapital = false
//        )
//    }
//}
//
//suspend fun parseSubdivisions(
//    univStrings: Array<String>,
//    ioDispatcher: CoroutineContext = Dispatchers.IO,
//    divisionFromSubdivisionName: suspend (name: String) -> Division,
//) = withContext(ioDispatcher) {
//    data class SubdivisionAndDivision(
//        val subdivision: String,
//        val division: String,
//    )
//    univStrings.map {
//        SubdivisionAndDivision(
//            subdivision = it.split(" ")[1].propre,
//            division = it.split(" ")[2].propre
//        )
//    }.toSet().map {
//        Subdivision(
//            subdivisionId = UUID.randomUUID().toString(),
//            name = it.subdivision,
//            addedOn = Date().time,
//            divisionId = divisionFromSubdivisionName(it.division).divisionId,
//            isCapital = false
//        )
//    }
//}
//
//suspend fun parseTowns(
//    univStrings: Array<String>,
//    ioDispatcher: CoroutineContext = Dispatchers.IO,
//    subdivisionFromTownName: suspend (name: String) -> Subdivision,
//) = withContext(ioDispatcher) {
//    univStrings.map {
//        Log.d("Parser Universe", it.propre)
//        Town(
//            townId = UUID.randomUUID().toString(),
//            name = it.split(" ")[0].propre,
//            addedOn = Date().time,
//            subdivisionId = subdivisionFromTownName(it.split(" ")[1].propre).subdivisionId,
//            isCapital = false,
//            lon = it.split(" ")[4].toDouble(),
//            lat = it.split(" ")[5].toDouble(),
//            xm = it.split(" ")[6].toDouble(),
//            ym = it.split(" ")[7].toDouble()
//        )
//    }
//}
//
///**
// * For every string road, we create a [Road] object. Now, for the itinerary, we return the id of any town
// * which are members of the this if they exist else we create a town with the name
// */
//suspend fun parseStrRoads(
//    allRoads: Array<String>,
//    towns: List<Town>,
//    ioDispatcher: CoroutineContext = Dispatchers.IO,
//    forEachRoad: (through: String, remaining: Int) -> Unit,
//) = withContext(ioDispatcher) {
//    val size = allRoads.size
//    allRoads.mapIndexed { index, strRoad ->
////        Wum Yabassi Wum-Befang-Bamenda-Babadjou-Mbouda-Bafoussam-Bafang-Bakou-Nkondjock-Yabassi 333
////        Splitting towns and distance, then replacing all underscores used for separation to the normal spaces
//        val info = strRoad.trim().split(" ").map { it.trim().lowercase().replace('_', ' ') }
//        //We get the ids of the first town, last town and all the other towns which makes up the road
//        val strThrough =
//            info[2].split('-').fold(initial = mutableListOf<String>()) { acc, townName ->
//                acc.apply { add(towns.find { it.name == townName }!!.townId) }
//            }.toList()
//
//        //We then emit the current progression together with the left amount
//        forEachRoad(strThrough.toString(), size - index)
//        Road(
//            roadId = UUID.randomUUID().toString(),
//            name = null,
//            distance = info[3].toDouble(),
//            shape = null,
//            town1Id = strThrough.first(),
//            town2Id = strThrough.last(),
//            addedOn = Date().time
//        ).apply {
//            itineraryTownsIds = strThrough
//        }
//    }
//}
