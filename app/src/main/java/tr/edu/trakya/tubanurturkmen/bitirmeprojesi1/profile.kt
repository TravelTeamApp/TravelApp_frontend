package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ProfileScreen(navController: NavController,sharedViewModel: SharedViewModel) {
    val selectedInterests by sharedViewModel.selectedInterests.collectAsState()
    BackHandler {
        navController.navigate("login") {
            popUpTo("home") { inclusive = true }
        }
    }

    val userProfile = remember { mutableStateOf<UserProfileResponse?>(null) }
    val context = LocalContext.current

    // API çağrısı
    LaunchedEffect(Unit) {
        RetrofitClient.apiService.getUserProfile().enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                if (response.isSuccessful) {
                    userProfile.value = response.body()
                } else {
                    Toast.makeText(context, "Hata: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                Toast.makeText(context, "İstek başarısız: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Geri butonu
        IconButton(
            onClick = {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Geri Git"
            )
        }

        if (userProfile.value != null) {
            ProfileScreenContent(userProfile.value!!)
        } else {
            Text(
                text = "Kullanıcı bilgileri yükleniyor...",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun ProfileScreenContent(userProfile: UserProfileResponse) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(
        TabItem("Yorumlar", Icons.Default.Comment),
        TabItem("Rozetler", Icons.Default.Star),
        TabItem("Favoriler", Icons.Default.Favorite),
        TabItem("Gidilenler", Icons.Default.Place)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD))
    ) {
        // Dinamik isimle TopSection
        TopSection(userName = userProfile.userName, score = userProfile.score)
        Divider(color = Color.Black, thickness = 0.5.dp)

        // TabRow
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.background(Color.White),
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title
                        )
                    },
                    text = {
                        Text(text = tab.title)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab içerikleri
        when (selectedTabIndex) {
            0 -> ReviewsSection(reviews = listOf("Review 1 text", "Review 2 text", "Review 3 text"))
            1 -> BadgesSection()
            2 -> FavoritesSection()
            3 -> VisitedSection()
        }
    }
}

data class TabItem(val title: String, val icon: ImageVector)

@Composable
fun TopSection(userName: String, score: Int) {
    val firstLetter = userName?.firstOrNull()?.toUpperCase() ?: ""
       Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF64B5F6), Color(0xFF1E88E5))
                ),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) { Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                ) {
            Text(
                firstLetter.toString(),
                fontSize = 48.sp, // Harfin büyüklüğü
                color = Color(0xFF1E88E5), // Harf rengi
                modifier = Modifier.align(Alignment.Center)
            )
        }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$userName",
                fontSize = 22.sp,
                fontFamily = FontFamily.SansSerif,
                color = Color.Black
            )
            Text(
                text = "Traveler",
                fontSize = 16.sp,
                color = Color.Black
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Badges",
                fontSize = 20.sp,
                color = Color.Black
            )
            Text(
                text = "Score $score",
                fontSize = 20.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun ReviewsSection(reviews: List<String>) {
    Text(
        text = "Yorumlar",
        fontSize = 20.sp,
        color = Color.Black,
        modifier = Modifier
            .padding(start = 14.dp, top = 10.dp)
    )
    LazyColumn(
        modifier = Modifier.padding(top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(reviews) { reviewText ->
            ReviewCard(reviewText)
        }
    }
}

@Composable
fun ReviewCard(reviewText: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.pull),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = reviewText,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun BadgesSection() {
    Text(
        text = "Rozetleriniz",
        fontSize = 20.sp,
        color = Color.Black,
        modifier = Modifier
            .padding(start = 14.dp, top = 13.dp)
    )
    // Rozetlerinizi gösterecek bir alan
}

@Composable
fun FavoritesSection() {
    Text(
        text = "Favoriler",
        fontSize = 20.sp,
        color = Color.Black,
        modifier = Modifier
            .padding(start = 14.dp, top = 13.dp)
    )
    Spacer(modifier = Modifier.height(10.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray) // Placeholder for favorites carousel
    )
}

@Composable
fun VisitedSection() {
    Text(
        text = "Gidilen Yerler",
        fontSize = 20.sp,
        color = Color.Black,
        modifier = Modifier
            .padding(start = 14.dp, top = 13.dp)
    )
    // Gidilen yerlerin listeleneceği bir alan
}
