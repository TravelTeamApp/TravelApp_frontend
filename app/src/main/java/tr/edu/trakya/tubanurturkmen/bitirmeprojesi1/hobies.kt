package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

// Compose ve UI bileşenleri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

// Android SDK ve sistem bileşenleri
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

// Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight

// Media3 (ExoPlayer)
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

// Navigation
import androidx.navigation.NavController

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HobiesScreen(navController: NavController,sharedViewModel: SharedViewModel) {
    val interests = listOf(
        "Popüler Yerler",
        "Tarihi Mekanlar ve Anıtlar",
        "Mimari",
        "Müzeler",
        "Parklar ve Doğa",
        "Dini Yapılar",
        "Eğlence",
        "Mahalleler ve Pazarlar",
        "Manzara Noktaları",
        "Gizli Hazineler",
        "Aile Dostu",
        "Kültürel Simgeler"
    )

    val selectedInterests = remember { mutableStateListOf<String>() }
    val backgroundImage: Painter = painterResource(id = R.drawable.hobies)


    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize() // Arka plan resmini ekranın tamamına yay
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Gezmeye doyamadığınız yerleri bizimle paylaşın!",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), // Yazıyı kalın yapma
                color = Color.White,
                modifier = Modifier
                    .padding(top = 32.dp) // Yazıyı biraz daha aşağıya indirme
                    .padding(bottom = 8.dp)
            )

            Text(
                text = "İlgi alanlarınızı seçin (Birden fazla olabilir).",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxHeight(0.8f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(interests) { interest ->
                    val isSelected = interest in selectedInterests
                    Button(
                        onClick = {
                            if (isSelected) {
                                selectedInterests.remove(interest)
                            } else {
                                selectedInterests.add(interest)
                            }

                        },
                        colors = ButtonDefaults.buttonColors(
                            if (isSelected) Color.LightGray else Color.White,
                            if (isSelected) Color.White else Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(interest)
                    }
                }
            }

            Button(
                onClick = {
                    val selectedInterestsString = selectedInterests.joinToString(",")
                    sharedViewModel.updateSelectedInterests(selectedInterests)
                    navController.navigate("profile")
                },
                colors = ButtonDefaults.buttonColors(
                    Color.White, Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 16.dp)
                    .clickable {
                navController.navigate("profile")
            }
            ) {
                Text(text = "Hadi Başlayalım!")
            }
        }}}