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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
fun FinalLearningApp( onPlaceChangeAnimate:( onAnimate:()->Unit )->Unit, mapView:MapView){

    val context = LocalContext.current
    val places = mutableListOf<MapPlace>(

        MapPlace(
            id = "Istanbul41.008238",
            name = "Istanbul",
            GeoPoint(41.008238, 28.978359),
            description = "Istanbul, historically known as Byzantium and Constantinople, is Turkey's largest city and a cultural and historical hub spanning Europe and Asia.",
            imageResId = R.drawable.istanbul,
            focusZoomLvl = 12.0
        ),
        MapPlace(
            id = "KizKulesi41.021075",
            name = "Kız Kulesi",
            GeoPoint(41.021075, 29.004618),
            description = "Kız Kulesi, or Maiden's Tower, is a historic tower located on a small islet at the southern entrance of the Bosphorus in Istanbul, offering breathtaking views and rich legends.",
            imageResId = R.drawable.istanbul,
            focusZoomLvl = 15.0
        ),
        MapPlace(
            id = "TopkapiSarayi41.011740",
            name = "Topkapı Sarayı",
            GeoPoint(41.011740, 28.985107),
            description = "Topkapı Sarayı, the Topkapi Palace, was the administrative center and residence of the Ottoman sultans for almost 400 years. It is a UNESCO World Heritage site, renowned for its stunning architecture and rich history.",
            imageResId = R.drawable.istanbul,
            focusZoomLvl = 14.0
        ),
        MapPlace(
            id = "Nusret41.022115",
            name = "Nusret Steakhouse",
            GeoPoint(41.022115, 28.983242),
            description = "Nusret Steakhouse, owned by renowned chef Nusret Gökçe, is famous for its luxurious dining experience and unique steak preparations, including the viral 'Salt Bae' move.",
            imageResId = R.drawable.istanbul,
            focusZoomLvl = 16.0
        ),

    )

    val markersIdOnMap = remember{ mutableStateListOf<String>() }
    val markersWithInfoWindow = remember { mutableStateListOf<Marker>() }

    Surface(
        modifier= Modifier.fillMaxSize()
    ) {

        Box(
            modifier=Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ){


            MapView(
                places = places, mapView,
                markersWithWindowOnMap = markersWithInfoWindow,
                onAddNewMarker = {
                    markersIdOnMap.add(it)
                }
            )

            PlacesListItem(places = places, onItemClickListener = { itemPlace ->
                mapView.onAnimateToNewPlace(
                    itemPlace,
                    markersWithInfoWindow
                )
            })

        }

    }



}

private fun MapView.onAnimateToNewPlace(
    itemPlace: MapPlace,
    marksWithWindowOnMap: SnapshotStateList<Marker>,
) {
    // Remove all markers with open info windows before animating
    this removeAllMarksFrom marksWithWindowOnMap

    // Animate the map view to the new place
    controller.animateTo(itemPlace.coordinates, itemPlace.focusZoomLvl, 2000L)
    Log.d("FinalMapLearnLogs", "Going to: ${itemPlace.id}")

    // Check if the marker already exists and show the info window
    val marker = overlays.find {
        (it is Marker && it.id == itemPlace.id)
    } as? Marker

    marker?.apply {
        showInfoWindow()
        marksWithWindowOnMap.add(this)
        Log.d("FinalLearning", "Showing window on: ${id}")
    } ?: Log.d("FinalLearning", "Null Marker for Window")
}


infix fun MapView.removeAllMarksFrom( markerWithWindowLis:SnapshotStateList<Marker>){
    markerWithWindowLis.apply{
        forEach {
            it.closeInfoWindow()
        }
        clear()
    }
}

@Composable
private fun MapView(places:List<MapPlace>, mapView:MapView, onAddNewMarker:(String)->Unit, markersWithWindowOnMap:SnapshotStateList<Marker>){

    val context = LocalContext.current

    Box(
        modifier=Modifier.fillMaxSize()
    ){
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        ){
            it.apply{
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                Log.d("ANDROID_MAPVIEW","Re-rendered")
            }

        }
    }

    LaunchedEffect(key1 = Unit, block = {

        val mapEventsOverlay = MapEventsOverlay(
            object:MapEventsReceiver{
                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                    if(markersWithWindowOnMap.isNotEmpty()){
                        mapView removeAllMarksFrom markersWithWindowOnMap
                    }
                    return true
                }

                override fun longPressHelper(p: GeoPoint?): Boolean {
                    return false
                }
            }
        )

        mapView.apply {
            controller.zoomTo(6, 1000L)
            setMapConfigurations()
            overlays.add(mapEventsOverlay)


            for (i in places.indices) {
                onAddNewMarker(places[i].id)
                addMarkertoMap(
                    context,
                    places[i],
                    onNextclick = {
                        onAnimateToNewPlace(
                            if(i < places.size-1) places[i+1] else places[0],
                            markersWithWindowOnMap
                        )
                    }
                )
            }
        }
    })

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PlacesDetailItem(mapPlace:MapPlace, onPlaceClick:()->Unit){

    Card(
        modifier= Modifier
            .height(250.dp)
            .width(200.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.White,
        elevation = 6.dp,
        onClick = { onPlaceClick() }
    ) {

        Column(
            modifier= Modifier
                .fillMaxSize()
                .padding(8.dp)
        ){

            Image(
                painter = painterResource(id = mapPlace.imageResId), contentDescription = mapPlace.name,
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
                modifier=Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = mapPlace.description,
                fontSize = 14.sp,
                color = Color.Black,
                modifier=Modifier.fillMaxWidth()
            )

        }

    }

}

@Composable
private fun PlacesListItem(places: List<MapPlace>, onItemClickListener: (MapPlace) -> Unit) {
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
                        onItemClickListener(mapPlace) // Call listener when item is clicked
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

@Composable
private fun MapSearchBar(){

    Box(
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .padding(8.dp)
            .clip(RoundedCornerShape(24.dp))
            .height(24.dp)
    ){
        Text(text = "Search", color = Color(0xFFACACAC))
    }

}


fun MapView.setMapConfigurations() {
    val context = this.context
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics

    val rotationGestureOverlay = RotationGestureOverlay(this)
    setMultiTouchControls(true)
    overlays.add(rotationGestureOverlay)

    val compassOverlay = CompassOverlay(context, this)
    compassOverlay.enableCompass()
    overlays.add(compassOverlay)
}

fun MapView.addMarkertoMap(
    context: Context,
    geoPoint:GeoPoint,
    markTitle:String,
    description:String,
    id:String
){
    val marker = Marker(this)
    marker.apply{
        position = geoPoint
        icon = ResourcesCompat.getDrawable(resources, R.drawable.map_marker, null)
        title = markTitle
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        this.subDescription = description
        this.id = id
    }

    overlays.add(marker)
    invalidate()

}

fun MapView.addMarkertoMap(
    context: Context,
    place: MapPlace,
    onNextclick: () -> Unit
) {
    val marker = Marker(this)
    marker.apply {
        position = place.coordinates
        icon = ResourcesCompat.getDrawable(resources, R.drawable.map_marker, null)
        title = place.name
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        this.subDescription = place.description
        this.id = place.id
        infoWindow = CustomMarkerWindow(
            this@addMarkertoMap,
            place = place,
            onNextClick = onNextclick
        )
    }
    overlays.add(marker)  // Marker'ı haritaya ekle
    invalidate() // Ensure the map is re-rendered after adding the marker
    Log.d("FinalMapLearnLogs", "Marker added for place: ${place.name}")
}