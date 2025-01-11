package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import java.text.SimpleDateFormat
import java.util.Locale
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.R.drawable.profile


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
                navController.navigateUp() // Navigates to the previous screen
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {

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
        TabItem("Gidilenler", Icons.Default.Place),


        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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

        Spacer(modifier = Modifier.height(5.dp))

        // Tab içerikleri
        when (selectedTabIndex) {
            0 -> UserCommentsSection()
            1 -> BadgesSection(score = userProfile.score)
            2 -> FavoritesSection()
            3 -> VisitedPlacesSection()
        }
    }
}

data class TabItem(val title: String, val icon: ImageVector)

@SuppressLint("SuspiciousIndentation")
@Composable
fun TopSection(userName: String, score: Int) {
    val firstLetter = userName.firstOrNull()?.toUpperCase() ?: ""
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        // Background image
        Image(
            painter = painterResource(id = profile), // Drawable image
            contentDescription = "Profile Background",
            contentScale = ContentScale.Crop, // Resize the image
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Restrict height of background
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)) // Rounded corners
        )

        // Profile details
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 150.dp, start = 16.dp), // Push the content down below the image
            horizontalAlignment = Alignment.Start
        ) {
            // Profile picture
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Color(0xFFE8ECEF), CircleShape)
            ) {
                Text(
                    firstLetter.toString(),
                    fontSize = 48.sp, // Font size of the letter
                    color = Color(0xFF377A8D), // Letter color
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(16.dp)) // Adjust spacing between items

            // User Name
            Text(
                text = userName,
                fontSize = 24.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold, // Bold font style
                color =Color(0xFF377A8D)// Changed to black for visibility
            )

            Row(
                modifier = Modifier
                    .padding(top = 8.dp) // Add padding if needed
                    .align(Alignment.Start), // Align the Row to the start
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Traveler Text
                Text(
                    text = "Traveler  /",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray // Subtle text color
                )

                Spacer(modifier = Modifier.width(8.dp)) // Add space between Traveler and Score

                Text(
                    text = "Score $score",
                    fontSize = 16.sp, // Adjust font size
                    fontWeight = FontWeight.Bold,
                    color =Color(0xFFF9D131)
                )



            }


            Spacer(modifier = Modifier.height(15.dp)) // Add space before PlaceTypesSection

            // Place types section
            PlaceTypesSection()
        }
    }
}

@Composable
fun UserCommentsSection(commentViewModel: CommentViewModel = viewModel()) {
    val context = LocalContext.current

    // Durumlar
    var comments by remember { mutableStateOf<List<CommentDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Yorumları backend'den çek
    LaunchedEffect(Unit) {
        commentViewModel.fetchUserComments { fetchedComments, error ->
            if (fetchedComments != null) {
                comments = fetchedComments
                isLoading = false
            } else {
                errorMessage = error
                isLoading = false
            }
        }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(
                text = "Hata: $errorMessage",
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else if (comments.isEmpty()) {
            Text(
                text = "Henüz yorum yapmadınız.",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                items(comments) { comment ->
                    UserCommentItem(comment = comment)
                }
            }
        }
    }
}

@Composable
fun UserCommentItem(comment: CommentDto) {
    val formattedDate = formatDateTime(comment.createdOn)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(android.graphics.Color.parseColor("#f9fcfd")))
                .border(1.dp, Color(android.graphics.Color.parseColor("#459ab2")))
                .padding(14.dp)


        ) {
            // Adı
            Text(
                text = comment.placeName ?: "Anonim",

                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Mekan Adı
            Text(
                text = comment.createdBy ?: "Anonim",

                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Yorum Metni
            Text(
                text = comment.text ?: "Yorum yok",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Tarih
            Text(
                text = "Tarih: $formattedDate",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Rating (Star icons)
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < comment.rate) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Rating Star",
                        tint = if (index < comment.rate) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}






fun formatDateTime(isoDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
        val date = inputFormat.parse(isoDate)
        date?.let { outputFormat.format(it) } ?: "Tarih Bilinmiyor"
    } catch (e: Exception) {
        "Tarih Bilinmiyor"
    }
}

@Composable
fun PlaceTypesSection(placeViewModel: PlaceViewModel = viewModel()) {
    val context = LocalContext.current

    // Durumlar
    var placeTypes by remember { mutableStateOf<List<UserPlaceTypeDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Mekan türlerini backend'den çek
    LaunchedEffect(Unit) {
        placeViewModel.getPlaceTypesByUserId { fetchedPlaceTypes ->
            if (fetchedPlaceTypes != null) {
                placeTypes = fetchedPlaceTypes
                isLoading = false
            } else {
                errorMessage = "Mekan türleri yüklenirken bir hata oluştu."
                isLoading = false
            }
        }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()

    ) {

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(
                text = "Hata: $errorMessage",
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else if (placeTypes.isEmpty()) {
            Text(
                text = "Henüz mekan türü seçmediniz.",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()) // Tüm butonları yatay kaydırılabilir yap
                ,
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Butonlar arasında boşluk
            ) {
                placeTypes.forEach { placeType ->
                    placeType.placeTypeNames.forEach { placeTypeName ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color.White, RoundedCornerShape(12.dp))
                                .background(Color(0xFF81AAB7))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = placeTypeName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BadgesSection(score: Int) {
    val badges = listOf(
        Badge("Amatör", R.drawable.neww, 1),
        Badge("Kaşif", R.drawable.ic_explorer, 5),
        Badge("Maceracı", R.drawable.maps, 100),
        Badge("Koleksiyoncu", R.drawable.collector, 200),
        Badge("Gezgin", R.drawable.maceraci, 300),
        Badge("Öncü", R.drawable.light, 400),
        Badge("Fatih", R.drawable.adventurer, 500),
        Badge("Yön Bulucu", R.drawable.search, 700),
        Badge("Dünya Turu", R.drawable.language, 800)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
            items(badges.chunked(3)) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    row.forEach { badge ->
                        BadgeItem(badge = badge, isUnlocked = score >= badge.requiredScore)
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeItem(badge: Badge, isUnlocked: Boolean) {
    val context = LocalContext.current
    val tooltipText = "Bu rozeti kazanmak için skorunuz ${badge.requiredScore} olmalı"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        val badgeColor = if (isUnlocked) Color(0xFFFFD700) else Color(0xFFB0BEC5) // Altın veya gri
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(badgeColor)
                .clickable(enabled = !isUnlocked) {
                    // Show tooltip if badge is locked
                    if (!isUnlocked) {
                        Toast.makeText(
                            context,
                            tooltipText,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = badge.iconRes),
                contentDescription = badge.name,
                modifier = Modifier.size(50.dp)
            )
        }
        Text(
            text = badge.name,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}


data class Badge(val name: String, val iconRes: Int, val requiredScore: Int)


@Composable
fun FavoritesSection(favoriteViewModel: FavoriteViewModel = viewModel() ) {
    val context = LocalContext.current

    // Favori mekanların durumu
    var favoritePlaces by remember { mutableStateOf<List<FavoriteDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Favori mekanları backend'den çek
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

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(
                text = "Hata: $errorMessage",
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else if (favoritePlaces.isEmpty()) {
            Text(
                text = "Henüz favori mekanınız yok.",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                items(favoritePlaces) { place ->
                    FavoritePlaceItem(place = place)
                }
            }
        }
    }
}

@Composable
fun FavoritePlaceItem(place: FavoriteDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),

        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background( Color(0xFFDFF6FC))
                .padding(16.dp)
        ) {
            Text(
                text = place.placeName ?: "Bilinmeyen Mekan",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0C657C)
            )
            Text(
                text = place.placeAddress ?: "Adres bilgisi yok",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = place.description ?: "Açıklama yok",
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 8.dp)
            )
            place.rating?.let {
                Text(
                    text = "Puan: $it",
                    fontSize = 14.sp,
                    color = Color(0xFFF9D131),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}


@Composable
fun VisitedPlacesSection(visitedPlaceViewModel: VisitedPlaceViewModel = viewModel()) {
    val context = LocalContext.current

    // Durumlar
    var visitedPlaces by remember { mutableStateOf<List<VisitedPlaceDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Ziyaret edilen mekanları backend'den çek
    LaunchedEffect(Unit) {
        visitedPlaceViewModel.fetchUserVisitedPlaces { places, error ->
            if (places != null) {
                visitedPlaces = places
                isLoading = false
            } else {
                errorMessage = error
                isLoading = false
            }
        }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {


        Spacer(modifier = Modifier.height(10.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(
                text = "Hata: $errorMessage",
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else if (visitedPlaces.isEmpty()) {
            Text(
                text = "Henüz ziyaret ettiğiniz bir yer yok.",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                items(visitedPlaces) { place ->
                    VisitedPlaceItem(place = place)
                }
            }
        }
    }
}

@Composable
fun VisitedPlaceItem(place: VisitedPlaceDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFDFF6FC))
                .padding(16.dp)
        ) {
            Text(
                text = place.placeName ?: "Bilinmeyen Mekan",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0C657C)
            )
            Text(
                text = place.placeAddress ?: "Adres bilgisi yok",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = place.description ?: "Açıklama yok",
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 8.dp)
            )
            place.rating?.let {
                Text(
                    text = "Puan: $it",
                    fontSize = 14.sp,
                    color = Color(0xFFF9D131),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}