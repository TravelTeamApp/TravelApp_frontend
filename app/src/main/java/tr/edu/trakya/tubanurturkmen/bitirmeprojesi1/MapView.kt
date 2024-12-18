package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1
import android.content.Context
import android.util.DisplayMetrics
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.R
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
fun FinalLearningApp() {
    val placeViewModel: PlaceViewModel = viewModel()
    val favoriteViewModel: FavoriteViewModel = viewModel()
    val placeDto = placeViewModel.places.value


    var favoritePlaces by remember { mutableStateOf<List<FavoriteDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    // Fetch favorite places
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
    }

    val places = placeDto.map {
        MapPlace(
            id = "${it.placeName}_${it.latitude}_${it.longitude}", // Unique ID
            name = it.placeName,
            coordinates = GeoPoint(it.latitude, it.longitude),
            description = it.description,
            imageResId = R.drawable.istanbul,
            focusZoomLvl = 15.0
        )
    }

    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            val defaultLocation = GeoPoint(41.008238, 28.978359)
            controller.setZoom(15.0)
            controller.setCenter(defaultLocation)
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

    Surface(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            // Display loading UI
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
                    favoritePlaces = favoritePlaces
                )

                PlacesListItem(
                    places = places,
                    onItemClickListener = { itemPlace ->
                        mapView.onAnimateToNewPlace(itemPlace, markersWithInfoWindow)
                    }
                )
            }
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
    favoritePlaces: List<FavoriteDto> // IDs or names of favorite places
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
                    val isFavorite = favoritePlaces.any { it.placeName == place.name} // Correct comparison
                    addMarkerToMap(
                        context,
                        place,
                        isFavorite = isFavorite,
                        onNextclick = {
                            onAnimateToNewPlace(place, markersWithWindowOnMap)
                        }
                    )
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
    onNextclick: () -> Unit
) {
    val marker = Marker(this).apply {
        position = place.coordinates
        icon = if (isFavorite) {
            ResourcesCompat.getDrawable(resources, R.drawable.heart_marker, null) // Heart icon
        } else {
            ResourcesCompat.getDrawable(resources, R.drawable.map_marker, null) // Default marker
        }
        title = place.name
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        subDescription = place.description
        id = place.id
        infoWindow = CustomMarkerWindow(this@addMarkerToMap, place, onNextclick)
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
    val description = mapPlace.description
    val maxWords = 20

    // Açıklamayı en fazla 50 kelimeyle sınırlıyoruz
    val truncatedDescription = description
        .split(" ")
        .take(maxWords)
        .joinToString(" ")

    // Eğer açıklama sınırlıysa "..." ekliyoruz
    val finalDescription = if (description.split(" ").size > maxWords) {
        "$truncatedDescription..."
    } else {
        truncatedDescription
    }

    Card(
        modifier = Modifier
            .height(250.dp)
            .width(200.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.White,
        elevation = 6.dp,
        onClick = { onPlaceClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = mapPlace.imageResId),
                contentDescription = mapPlace.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = mapPlace.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = finalDescription,
                fontSize = 14.sp,
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

