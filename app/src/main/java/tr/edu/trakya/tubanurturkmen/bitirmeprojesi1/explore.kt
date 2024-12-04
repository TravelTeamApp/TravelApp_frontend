package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    val allAttractions = listOf(
        mapOf(
            "name" to "Pera Palace Hotel",
            "description" to "A historic hotel in the heart of Istanbul.",
            "imageId" to R.drawable.istanbul,
            "category" to "Tarihi Mekanlar ve Anıtlar"
        ),
        mapOf(
            "name" to "Hagia Sophia",
            "description" to "A monumental building with rich history.",
            "imageId" to R.drawable.istanbul,
            "category" to "Tarihi Mekanlar ve Anıtlar"
        ),
        mapOf(
            "name" to "Topkapi Palace",
            "description" to "Home of Ottoman sultans for centuries.",
            "imageId" to R.drawable.istanbul,
            "category" to "Mimari"
        ),
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
            "category" to "Dini Yapılar"
        )
    )
    val topAttractions = allAttractions
    val categories = listOf(
        "Tarihi Mekanlar ve Anıtlar",
        "Mimari",
        "Parklar ve Doğa",
        "Dini Yapılar",
        "Eğlence",
        "Restoranlar ve Cafeler",
        "Manzara Noktaları",
        "Gizli Hazineler",
        "Aile Dostu",
        "Kültürel Simgeler"
    )

    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var selectedAttraction by remember { mutableStateOf<Map<String, Any>?>(null) }
    var favoriteAttractions by remember { mutableStateOf(mutableSetOf<String>()) }
    var isDarkMode by remember { mutableStateOf(false) }
    var visitedAttractions by remember { mutableStateOf(mutableSetOf<String>()) }
    val backgroundColor = if (isDarkMode) Color.Black else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black
     val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        if (selectedAttraction == null) {
            Column {
                Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState) // Dikey kaydırmayı etkinleştir
                    ){
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
                                    .padding(10.dp)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(12.dp))
                                        .padding(8.dp)
                                        .align(Alignment.Center),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search Icon",
                                        tint = Color.Black,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    BasicTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        modifier = Modifier.weight(1f),
                                        textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                                        decorationBox = { innerTextField ->
                                            if (searchQuery.isEmpty()) {
                                                Text(
                                                    text = "Ara...",
                                                    style = TextStyle(color = Color.Gray)
                                                )
                                            }
                                            innerTextField()
                                        }
                                    )
                                }
                            }
                    }
                        // Fotoğrafın altına "İstanbul hakkında bilgi" kısmı
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Gray.copy(alpha = 0.1f))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "İstanbul hakkında bilgi:\nHagia Sophia, 537 yılında katedral olarak inşa edilmiş ve sonrasında cami olarak kullanılmıştır. Bugün bir müze olarak ziyaretçilerini ağırlamaktadır.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                            )
                        }


                        Text(
                            text = "Top Attractions",
                            style = MaterialTheme.typography.headlineSmall.copy(color = Color.Black),
                            modifier = Modifier.padding(16.dp)
                        )
                        LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
                            items(topAttractions) { attraction ->
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
                                            modifier = Modifier
                                                .height(120.dp)
                                                .fillMaxWidth(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = attraction["name"] as String,
                                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Suggested Attractions",
                            style = MaterialTheme.typography.headlineSmall.copy(color = Color.Black),
                            modifier = Modifier.padding(16.dp)
                        )
                        ScrollableTabRow(selectedTabIndex = categories.indexOf(selectedCategory)) {
                            categories.forEach { category ->
                                Tab(
                                    selected = category == selectedCategory,
                                    onClick = { selectedCategory = category },
                                    text = { Text(category) }
                                )
                            }
                        }

                        LazyRow(modifier = Modifier.padding(16.dp)) {
                            items(allAttractions.filter { it["category"] == selectedCategory }) { attraction ->
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
                                            modifier = Modifier
                                                .height(120.dp)
                                                .fillMaxWidth(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = attraction["name"] as String,
                                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
                Box(modifier = Modifier.weight(1f)) {
                    Image(
                        painter = painterResource(id = selectedAttraction!!["imageId"] as Int),
                        contentDescription = selectedAttraction!!["name"] as String,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Geri ve diğer ikon butonlar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align( Alignment.TopStart),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = { selectedAttraction = null }) { // Geri butonu
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Geri Dön",
                                    tint = Color.White
                                )
                            }
                            Row {
                                // Favori Butonu
                                IconButton(
                                    onClick = {
                                        if (selectedAttraction!!["name"] as String in favoriteAttractions) {
                                            favoriteAttractions.remove(selectedAttraction!!["name"] as String)
                                            Toast.makeText(context, "Favorilerden kaldırıldı", Toast.LENGTH_SHORT).show()
                                        } else {
                                            favoriteAttractions.add(selectedAttraction!!["name"] as String)
                                            Toast.makeText(context, "Favorilere eklendi", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (selectedAttraction!!["name"] as String in favoriteAttractions) {
                                            Icons.Default.Favorite
                                        } else {
                                            Icons.Default.FavoriteBorder
                                        },
                                        contentDescription = "Favorilere Ekle",
                                        tint = if (selectedAttraction!!["name"] as String in favoriteAttractions) Color.Red else Color.White
                                    )
                                }
                                // Gidilenlere Kaydet Butonu
                                IconButton(
                                    onClick = {
                                        if (selectedAttraction!!["name"] as String in visitedAttractions) {
                                            visitedAttractions.remove(selectedAttraction!!["name"] as String)
                                            Toast.makeText(context, "Gidilenlerden kaldırıldı", Toast.LENGTH_SHORT).show()
                                        } else {
                                            visitedAttractions.add(selectedAttraction!!["name"] as String)
                                            Toast.makeText(context, "Gidilenlere eklendi", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (selectedAttraction!!["name"] as String in visitedAttractions) {
                                            Icons.Default.CheckBox
                                        } else {
                                            Icons.Default.CheckBoxOutlineBlank
                                        },
                                        contentDescription = "Gidilenlere Kaydet",
                                        tint = if (selectedAttraction!!["name"] as String in visitedAttractions) Color.Green else Color.White
                                    )
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = selectedAttraction!!["name"] as String,
                            style = MaterialTheme.typography.headlineMedium.copy(color = Color.Black),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = selectedAttraction!!["description"] as String,
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                        )
                    }
                }
            }
        }
    }
