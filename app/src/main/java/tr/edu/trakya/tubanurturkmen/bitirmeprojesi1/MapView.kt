package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.util.CustomMarkerWindow
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.util.MapPlace

@Composable
fun FinalLearningApp(placeId: String? = null) {
    val placeViewModel: PlaceViewModel = viewModel()
    val favoriteViewModel: FavoriteViewModel = viewModel()
    val visitedPlaceViewModel: VisitedPlaceViewModel = viewModel()
    val placeDto = placeViewModel.places.value

    var visitedPlaces by remember { mutableStateOf<List<VisitedPlaceDto>>(emptyList()) }
    var favoritePlaces by remember { mutableStateOf<List<FavoriteDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun getDrawableResourceByPlaceName(placeName: String): Int {
        return when (placeName.lowercase()) {
            "saray muhallebicisi" -> R.drawable.saray1
            "gülhane parkı"->R.drawable.gulhane1
            "yıldız parkı"->R.drawable.yildiz1
            "mandabatmaz"->R.drawable.mandabatmaz1
            "yerebatan sarnıcı"->R.drawable.yerebatan
            "sultanahmet camii"->R.drawable.sultanahmet
            "kız kulesi"->R.drawable.kiz1
            "vialand tema park"->R.drawable.vialand1
            "arkeoloji müzesi"->R.drawable.arkeoloji1
            "ayasofya camii"->R.drawable.ayasofya1
            "balat"->R.drawable.balat1
            "binbirdirek sarnıcı"->R.drawable.binbirdirek1
            "beylerbeyi sarayı"->R.drawable.beylerbeyi1
            "pierre loti tepesi" -> R.drawable.pierre1
            "madame tussauds müzesi" -> R.drawable.madame1
            "çamlıca kulesi" -> R.drawable.camlica1
            "büyükada"->R.drawable.buyukada1
            "çemberlitaş"->R.drawable.cemberlitas1
            "eminönü"->R.drawable.eminonu1
            "emirgan korusu"->R.drawable.emirgan4
            "galata kulesi"->R.drawable.galata1
            "gülhane parkı"->R.drawable.gulhane1
            "topkapı sarayı"->R.drawable.topkapi1
            "yeni cami"->R.drawable.yeni
            "haydarpaşa tren garı"->R.drawable.haydarpasa1
            "istanbul akvaryum"->R.drawable.istakvaryum1
            "kapalıçarşı"->R.drawable.kapali1
            "süleymaniye cami"->R.drawable.suleymaniye1
            "rumeli hisarı"->R.drawable.rumeli1
            "ortaköy cami"->R.drawable.ortakoy1
            "dolmabahçe sarayı"->R.drawable.dolmabahce1
            "rahmi koç müzesi"->R.drawable.rahmi1
            "pera palace otel"->R.drawable.pera1
            "pelit çikolata müzesi" -> R.drawable.pelit1
            "nusr-et steakhouse " -> R.drawable.nusret1
            "kariye camii (eski chora kilisesi)" -> R.drawable.kariye1

            else -> R.drawable.istanbul // Varsayılan görsel
        }
    }


    // Fetch favorite and visited places
    LaunchedEffect(Unit) {
        favoriteViewModel.fetchUserFavorites { favorites, error ->
            if (favorites != null) {
                favoritePlaces = favorites
                isLoading = false
            } else {
                errorMessage = error
                isLoading = false
            }
        }
        visitedPlaceViewModel.fetchUserVisitedPlaces { visited, error ->
            if (visited != null) {
                visitedPlaces = visited
                isLoading = false
            } else {
                errorMessage = error
                isLoading = false
            }
        }
    }

    val places = placeDto.map {
        MapPlace(
            id = "${it.placeName}_${it.latitude}_${it.longitude}", // Unique ID
            name = it.placeName,
            coordinates = GeoPoint(it.latitude, it.longitude),
            description = it.description,
            imageResId = getDrawableResourceByPlaceName(it.placeName),
            focusZoomLvl = 15.0
        )
    }

// Parametreden gelen placeId'ye göre MapPlace bul
    val selectedPlace = placeDto.find { it.placeId.toString() == placeId } // Find the matching placeDto

    val selectedMapPlace = selectedPlace?.let {
        MapPlace(
            id = "${it.placeName}_${it.latitude}_${it.longitude}", // Unique ID
            name = it.placeName,
            coordinates = GeoPoint(it.latitude, it.longitude),
            description = it.description,
            imageResId = getDrawableResourceByPlaceName(it.placeName),
            focusZoomLvl = 15.0
        )
    }
    Log.d("MapDebug", "Selected Map Place: ${selectedMapPlace?.name}, Coordinates: ${selectedMapPlace?.coordinates}, Zoom Level: ${selectedMapPlace?.focusZoomLvl}")
    val context = LocalContext.current
    val mapView = remember(selectedMapPlace) { // Recreate mapView if selectedMapPlace changes
        MapView(context).apply {
            val initialLocation = GeoPoint(41.008238, 28.978359) // Use selectedMapPlace if available
            val initialZoom = 15.0
            controller.setZoom(initialZoom)
            controller.setCenter(initialLocation)
            onResume()
        }
    }

    DisposableEffect(mapView) {
        onDispose {
            mapView.onPause()
        }
    }

    val markersIdOnMap = remember { mutableStateListOf<String>() }
    val markersWithInfoWindow = remember { mutableStateListOf<Marker>() }

// Eğer placeId geçerli bir yeri temsil ediyorsa haritayı odakla
    LaunchedEffect(selectedMapPlace) {
        selectedMapPlace?.let {
            mapView.controller.setZoom(it.focusZoomLvl)
            mapView.controller.setCenter(it.coordinates)
        }
    }


    Surface(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                MapView(
                    places = places,
                    mapView = mapView,
                    markersWithWindowOnMap = markersWithInfoWindow,
                    onAddNewMarker = { markersIdOnMap.add(it) },
                    favoritePlaces = favoritePlaces,
                    visitedPlaces = visitedPlaces
                )
                // Trigger `onAnimateToNewPlace` for `selectedMapPlace`
                LaunchedEffect(selectedMapPlace) {
                    selectedMapPlace?.let { mapView.onAnimateToNewPlace(it, markersWithInfoWindow) }
                }
                PlacesListItem(
                    places = places,
                    onItemClickListener = { itemPlace ->
                        mapView.onAnimateToNewPlace(itemPlace, markersWithInfoWindow)
                    }
                )
                // Zoom Kontrolleri
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    Row {
                        IconButton(
                            onClick = {
                                val currentZoom = mapView.zoomLevelDouble
                                mapView.controller.setZoom(currentZoom + 1) // Zoom In
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.7f), // Şeffaf beyaz arka plan
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add, // "+" simgesi
                                contentDescription = "Zoom In",
                                tint = Color.Black // İkon rengi siyah
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))  // Fixed width space

                        IconButton(
                            onClick = {
                                val currentZoom = mapView.zoomLevelDouble
                                mapView.controller.setZoom(currentZoom - 1) // Zoom Out
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.7f), // Şeffaf beyaz arka plan
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove, // "−" simgesi
                                contentDescription = "Zoom Out",
                                tint = Color.Black // İkon rengi siyah
                            )
                        }
                }
            }}
        }
    }
}

private fun MapView.onAnimateToNewPlace(
    itemPlace: MapPlace,
    marksWithWindowOnMap: SnapshotStateList<Marker>
) {
    Log.d("MarkerDebug", "Searching for marker with ID: ${itemPlace.id}")
    Log.d("MarkerDebug", "Current markers: ${overlays.filterIsInstance<Marker>().map { it.id }}")

    this.removeAllMarksFrom(marksWithWindowOnMap)

    controller.animateTo(itemPlace.coordinates, itemPlace.focusZoomLvl, 2000L)
    Log.d("FinalMapLearnLogs", "Going to: ${itemPlace.id}")

    val marker = overlays.filterIsInstance<Marker>().find { it.id == itemPlace.id }
    marker?.apply {
        showInfoWindow()
        marksWithWindowOnMap.add(this)
        Log.d("FinalLearning", "Showing window on: ${id}")
    } ?: Log.d("FinalLearning", "Null Marker for Window")
}

infix fun MapView.removeAllMarksFrom(markerWithWindowList: SnapshotStateList<Marker>) {
    markerWithWindowList.apply {
        forEach { it.closeInfoWindow() }
        clear()
    }
}

@Composable
private fun MapView(
    places: List<MapPlace>,
    mapView: MapView,
    onAddNewMarker: (String) -> Unit,
    markersWithWindowOnMap: SnapshotStateList<Marker>,
    favoritePlaces: List<FavoriteDto>, // IDs or names of favorite places
    visitedPlaces: List<VisitedPlaceDto>
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        ) {
            it.apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                Log.d("ANDROID_MAPVIEW", "Re-rendered")
            }
        }
    }

    LaunchedEffect(key1 = places) {
        if (places.isNotEmpty()) {
            val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                    if (markersWithWindowOnMap.isNotEmpty()) {
                        mapView.removeAllMarksFrom(markersWithWindowOnMap)
                    }
                    return true
                }

                override fun longPressHelper(p: GeoPoint?): Boolean {
                    return false
                }
            })

            mapView.apply {
                controller.zoomTo(6, 1000L)
                setMapConfigurations()
                overlays.add(mapEventsOverlay)

                places.forEach { place ->
                    val isFavorite = favoritePlaces.any { it.placeName == place.name } // Favori kontrolü
                    val isVisited = visitedPlaces.any { it.placeName == place.name } // Gidilen kontrolü

                    // Hem favori hem gidilen durum için
                    if (isFavorite && isVisited) {
                        addMarkerToMap(
                            context,
                            place,
                            isFavorite = true,
                            isVisited = true,
                        )
                    }

                    // Sadece favori durum için
                    if (isFavorite && !isVisited) {
                        addMarkerToMap(
                            context,
                            place,
                            isFavorite = true,
                            isVisited = false,
                        )
                    }

                    // Sadece gidilen durum için
                    if (!isFavorite && isVisited) {
                        addMarkerToMap(
                            context,
                            place,
                            isFavorite = false,
                            isVisited = true,
                        )
                    }

                    // Ne favori ne de gidilen durum için (isteğe bağlı)
                    if (!isFavorite && !isVisited) {
                        addMarkerToMap(
                            context,
                            place,
                            isFavorite = false,
                            isVisited = false,
                        )
                    }
                }

            }
        } else {
            Log.d("MapDebug", "Places list is empty, no markers to add.")
        }
    }

}

fun MapView.addMarkerToMap(
    context: Context,
    place: MapPlace,
    isFavorite: Boolean,
    isVisited: Boolean,
) {
    val marker = Marker(this).apply {
        position = place.coordinates
        icon = when {
            isFavorite && isVisited -> ResourcesCompat.getDrawable(resources, R.drawable.yildiz_icon, null) // Hem favori hem gidilen için özel ikon
            isFavorite -> ResourcesCompat.getDrawable(resources, R.drawable.kalp, null) // Sadece favori için
            isVisited -> ResourcesCompat.getDrawable(resources, R.drawable.kaydedilen, null) // Sadece gidilen için
            else -> ResourcesCompat.getDrawable(resources, R.drawable.map_marker, null) // Varsayılan ikon
        }

        title = place.name
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        subDescription = place.description
        id = place.id
        infoWindow = CustomMarkerWindow(this@addMarkerToMap, place)
    }
    Log.d("MarkerDebug", "Added Marker ID: ${marker.id}, Is Favorite: $isFavorite")

    overlays.add(marker)
    invalidate()
    Log.d("FinalMapLearnLogs", "Marker added for place: ${place.name}, ID: ${place.id}")
}

fun MapView.setMapConfigurations() {
    val context = this.context

    val rotationGestureOverlay = RotationGestureOverlay(this)
    setMultiTouchControls(true)
    overlays.add(rotationGestureOverlay)

    val compassOverlay = CompassOverlay(context, this)
    compassOverlay.enableCompass()
    overlays.add(compassOverlay)
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PlacesDetailItem(mapPlace: MapPlace, onPlaceClick: () -> Unit) {
    val maxWords = 20
    // Limit the description to `maxWords`
    val finalDescription = mapPlace.description
        .split(" ") // Split the description into words
        .take(maxWords) // Take only the first `maxWords` words
        .joinToString(" ") // Join them back into a string
        .let {
            if (it.length < mapPlace.description.length) "$it..." else it
        }

    Card(
        modifier = Modifier
            .height(200.dp)
            .width(150.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.White,
        elevation = 6.dp,
        onClick = { onPlaceClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Image(
                painter = painterResource(id = mapPlace.imageResId),
                contentDescription = mapPlace.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = mapPlace.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )


        }
    }
}


@Composable
private fun PlacesListItem(
    places: List<MapPlace>,
    onItemClickListener: (MapPlace) -> Unit
) {
    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x00FFFFFF), Color(0xC8000000)),
                        startY = 0f,
                        endY = 400f
                    )
                )
        )
        LazyRow(
            contentPadding = PaddingValues(8.dp),
            content = {
                items(places) { mapPlace ->
                    PlacesDetailItem(mapPlace = mapPlace) {
                        onItemClickListener(mapPlace)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        )
    }
}

