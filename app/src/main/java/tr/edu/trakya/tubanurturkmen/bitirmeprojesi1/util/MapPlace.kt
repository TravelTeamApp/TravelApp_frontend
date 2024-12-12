package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.util

import org.osmdroid.util.GeoPoint

data class MapPlace (
    val id:String,
    val name:String,
    val coordinates: GeoPoint,
    val description:String,
    val imageResId: Int,
    val focusZoomLvl:Double
)

