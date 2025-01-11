package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.util

import android.widget.ImageView
import android.widget.TextView
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.R

class CustomMarkerWindow(
    private val mapView: MapView,
    val place: MapPlace,
) : InfoWindow(R.layout.info_window, mapView) {

    lateinit var windowTitle: TextView
    lateinit var windowDescription: TextView
    lateinit var windowPlaceImage: ImageView

    override fun onOpen(item: Any?) {
        mView.apply {
            windowTitle = findViewById(R.id.placeTitle) ?: return
            windowDescription = findViewById(R.id.placeDescription) ?: return
            windowPlaceImage = findViewById(R.id.placeImage) ?: return
        }

        windowTitle.text = place.name

        // Limit description to 30 words
        val description = place.description.split(" ").take(30).joinToString(" ")
        windowDescription.text = if (description.length < place.description.length) "$description..." else description

        windowPlaceImage.setImageResource(place.imageResId)

        mView.setOnClickListener {
            this.close() // Close the window on click
        }
    }

    override fun onClose() {
        // Handle onClose if needed
    }
}
