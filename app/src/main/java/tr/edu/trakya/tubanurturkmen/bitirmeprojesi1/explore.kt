package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ExploreScreen(navController: NavController) {
    val context = LocalContext.current
    val attractions = listOf(
        mapOf(
            "name" to "Pera Palace Hotel",
            "description" to "A historic hotel in the heart of Istanbul.",
            "imageId" to R.drawable.istanbul
        ),
        mapOf(
            "name" to "Hagia Sophia",
            "description" to "A monumental building with rich history.",
            "imageId" to R.drawable.istanbul
        ),
        mapOf(
            "name" to "Topkapi Palace",
            "description" to "Home of Ottoman sultans for centuries.",
            "imageId" to R.drawable.istanbul
        )
    )

    val suggestedAttractions = listOf(
        mapOf(
            "name" to "Gülhane Park",
            "description" to "A serene park in the heart of the city.",
            "imageId" to R.drawable.istanbul,
            "category" to "Parklar ve Doğa"
        ),
        mapOf(
            "name" to "Blue Mosque",
            "description" to "An iconic mosque with stunning architecture.",
            "imageId" to R.drawable.istanbul,
            "category" to "DiniYapılar"
        ),
        mapOf(
            "name" to "Balat",
            "description" to "A colorful neighborhood rich in culture.",
            "imageId" to R.drawable.istanbul,
            "category" to "Mimari"
        )
    )

    var searchQuery by remember { mutableStateOf("") }
    var selectedAttraction by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isDarkMode by remember { mutableStateOf(false) }
    var favoriteAttractions by remember { mutableStateOf(mutableSetOf<String>()) }
    var visitedAttractions by remember { mutableStateOf(mutableSetOf<String>()) }
    var selectedCategory by remember { mutableStateOf("Architecture") }

    val categories = listOf( "Popüler Yerler",
        "Tarihi Mekanlar ve Anıtlar",
        "Mimari",
        "Müzeler",
        "Parklar ve Doğa",
        "Dini Yapılar",
        "Eğlence",
        "Restoranlar ve Cafeler",
        "Manzara Noktaları",
        "Gizli Hazineler",
        "Aile Dostu",
        "Kültürel Simgeler")
    val filteredAttractions = attractions.filter {
        it["name"].toString().contains(searchQuery, ignoreCase = true)
    }
    val filteredSuggestions = suggestedAttractions.filter {
        it["category"] == selectedCategory
    }

    val backgroundColor = if (isDarkMode) Color.Black else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        Column {
            // Header Image
            Box(modifier = Modifier.height(200.dp).fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.istanbul),
                    contentDescription = "Istanbul Overview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "ISTANBUL",
                    style = MaterialTheme.typography.headlineLarge.copy(color = Color.White),
                    modifier = Modifier.align(Alignment.Center)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.Gray.copy(alpha = 0.7f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "İstanbul hakkında bilgi:\nHagia Sophia, 537 yılında katedral olarak inşa edilmiş ve sonrasında cami olarak kullanılmıştır. Bugün bir müze olarak ziyaretçilerini ağırlamaktadır.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )}
            }

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.Gray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Arama İkonu",
                        tint = textColor,
                        modifier = Modifier.padding(8.dp)
                    )
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f).padding(8.dp),
                        textStyle = TextStyle(color = textColor, fontSize = 16.sp),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Ara...",
                                    style = TextStyle(color = textColor.copy(alpha = 0.5f))
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            // Top Attractions
            Text(
                text = "Top Attractions",
                style = MaterialTheme.typography.titleLarge.copy(color = textColor),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
                items(filteredAttractions) { attraction ->
                    Card(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(end = 16.dp)
                            .clickable { selectedAttraction = attraction },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.3f))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = attraction["imageId"] as Int),
                                contentDescription = attraction["name"] as String,
                                modifier = Modifier.height(120.dp).fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = attraction["name"] as String,
                                style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }

            // Suggested Attractions
            Text(
                text = "😍 Suggested Attractions",
                style = MaterialTheme.typography.titleLarge.copy(color = textColor),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            TabRow(selectedTabIndex = categories.indexOf(selectedCategory)) {
                categories.forEach { category ->
                    Tab(
                        selected = category == selectedCategory,
                        onClick = { selectedCategory = category },
                        text = { Text(category) }
                    )
                }
            }

            LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
                items(filteredSuggestions) { suggestion ->
                    Card(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(end = 16.dp)
                            .clickable { /* Handle Click */ },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.3f))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = suggestion["imageId"] as Int),
                                contentDescription = suggestion["name"] as String,
                                modifier = Modifier.height(120.dp).fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = suggestion["name"] as String,
                                style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }

            }
        }
    }
}
