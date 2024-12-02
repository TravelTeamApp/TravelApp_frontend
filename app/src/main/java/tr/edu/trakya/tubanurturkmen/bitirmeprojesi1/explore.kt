package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

@Composable
fun ExploreScreen(navController: NavController) {
    // Mekanlar listesi
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

    var selectedAttraction by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isDarkMode by remember { mutableStateOf(false) }

    // Dark mode background
    val backgroundColor = if (isDarkMode) Color.Black else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        if (selectedAttraction != null) {
            // Seçili mekanın detay ekranı
            Column(
                modifier = Modifier.fillMaxSize().background(backgroundColor)
            ) {
                val imageId = selectedAttraction?.get("imageId") as Int
                val name = selectedAttraction?.get("name") as String
                val description = selectedAttraction?.get("description") as String

                Image(
                    painter = painterResource(id = imageId),
                    contentDescription = name,
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge.copy(color = textColor),
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(color = textColor),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { selectedAttraction = null },
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
                ) {
                    Text(text = "Back")
                }
            }
        } else {
            Column {
                // İstanbul Bilgi ve Resim Bölümü
                Box(modifier = Modifier.height(200.dp).fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.istanbul), // İstanbul fotoğrafı
                        contentDescription = "Istanbul Overview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    )
                    Text(
                        text = "ISTANBUL",
                        style = MaterialTheme.typography.headlineLarge.copy(color = Color.White),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }


                Text(
                    text = "İstanbul hakkında bilgi\nHagia Sophia, now a museum, was originally built as a cathedral in 537 AD and later became a mosque.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                    modifier = Modifier.padding(16.dp).background(Color.Gray.copy(alpha = 0.7f))
                        .padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))


                // Ana ekran
                Column(
                    modifier = Modifier.fillMaxSize().background(backgroundColor)
                ) {
                    // Arama çubuğu
                    var searchQuery by remember { mutableStateOf("") }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color.Gray.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.weight(1f).padding(8.dp),
                                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Search...",
                                            style = TextStyle(color = Color.White.copy(alpha = 0.5f))
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Önerilen mekanlar
                    Text(
                        text = "Top Attractions",
                        style = MaterialTheme.typography.titleLarge.copy(color = textColor),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    LazyRow(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        items(attractions) { attraction ->
                            Card(
                                modifier = Modifier
                                    .width(200.dp)
                                    .padding(end = 16.dp)
                                    .clickable { selectedAttraction = attraction },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Gray.copy(
                                        alpha = 0.3f
                                    )
                                )
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
                }
            }

            // Dark Mode düğmesi
            FloatingActionButton(
                onClick = { isDarkMode = !isDarkMode },
                containerColor = if (isDarkMode) Color.Gray else Color.DarkGray,
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            ) {
                Text(text = if (isDarkMode) "☀" else "🌙", color = Color.White)
            }
        }
    }
}