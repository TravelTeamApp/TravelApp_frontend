package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import java.text.SimpleDateFormat
import java.util.Locale
import android.annotation.SuppressLint
import android.text.format.DateUtils.formatDateTime
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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
            ProfileScreenContent(userProfile.value!!, navController=navController)
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
fun ProfileScreenContent(userProfile: UserProfileResponse,navController: NavController) {
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
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .height(2.dp)
                        .background(Color(0xFF1E88E5)) // Mavi çizgi sadece seçili tab'da
                )
            }
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (selectedTabIndex == index) Color(0xFF1E88E5) else Color.Gray // Seçilen tab'da mavi, diğerlerinde gri
                        )
                    },
                    text = {
                        Text(
                            text = tab.title,
                            color = if (selectedTabIndex == index) Color(0xFF1E88E5) else Color.Gray // Seçilen tab'da mavi, diğerlerinde gri
                        )
                    },
                    modifier = Modifier.background(Color.White) // Arka plan beyaz
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Tab içerikleri
        when (selectedTabIndex) {
            0 -> UserCommentsSection(navController=navController)
            1 -> BadgesSection(score = userProfile.score)
            2 -> FavoritesSection(navController = navController)
            3 -> VisitedPlacesSection(navController = navController)
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
            .height(280.dp)
    ) {
        // Arka plan görseli
        Image(
            painter = painterResource(id = R.drawable.profil2), // Drawable görsel
            contentDescription = "Profile Background",
            contentScale = ContentScale.Crop, // Görselin boyutlandırılması
            modifier = Modifier
                .fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f)) // blur
        )
        Column(
            modifier = Modifier
                .fillMaxWidth() // Tüm genişliği kaplamasını sağlar
                .wrapContentHeight(), // Yükseklik içeriğe göre ayarlanır
            horizontalAlignment = Alignment.CenterHorizontally // İçerikleri yatayda ortalar
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(), // Tüm ekranı kaplar
                contentAlignment = Alignment.TopCenter // İçeriği üstten ortalamaya hizalar
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp), // Resmi daha aşağı kaydırmak için üstten boşluk
                    horizontalAlignment = Alignment.CenterHorizontally // İçeriği yatayda ortalar
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF0F8FF)),
                        contentAlignment = Alignment.Center // Box içeriğini ortalar
                    ) {
                        Text(
                            firstLetter.toString(),
                            fontSize = 48.sp, // Harfin büyüklüğü
                            color = Color(0xFF1E88E5) // Harf rengi
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp)) // Resim ve metin arasına boşluk
                    Text(
                        text = userName,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold, // Kalın yazı stili
                        color = Color.White
                    )

            PlaceTypesSection()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Score $score",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}}}

@Composable
fun UserCommentsSection(commentViewModel: CommentViewModel = viewModel(), navController: NavController) {
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
                    val formattedDate = formatDateTime(comment.createdOn)
                    val placeId = comment.placeId.toString()

                    var isEditingComment by remember { mutableStateOf(false) }
                    var editableCommentText by remember { mutableStateOf(comment.text ?: "") }
                    var selectedRating by remember { mutableStateOf(comment.rate.toFloat()) }

                    if (isEditingComment) {
                        // Düzenleme Modunda TextField ve Yıldız Değerlendirmesi
                        OutlinedTextField(
                            value = editableCommentText,
                            onValueChange = { editableCommentText = it },
                            label = { Text("Yorumunuzu Düzenleyin") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0571C7),
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5)
                            )
                        )
                        Spacer(modifier = Modifier.height(5.dp))

                        // Yıldız Derecelendirmesini Güncelleme
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { index ->
                                IconButton(onClick = { selectedRating = (index + 1).toFloat() }) {
                                    Icon(
                                        imageVector = if (index < selectedRating) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        tint = if (index < selectedRating) Color(0xFFFFC107) else Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Kaydet ve İptal Butonları
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Kaydet ve İptal Butonları
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = {
                                        commentViewModel.updateComment(
                                            id = comment.commentId,
                                            updateCommentRequest = UpdateCommentRequestDto(
                                                text = editableCommentText,
                                                rate = selectedRating.toInt()
                                            )
                                        ) { updatedComment, errorMessage ->
                                            if (updatedComment != null) {
                                                Toast.makeText(context, "Yorum başarıyla güncellendi", Toast.LENGTH_SHORT).show()

                                                // Yorumları yeniden fetch et

                                                commentViewModel.fetchUserComments { fetchedComments, error ->
                                                    if (fetchedComments != null) {
                                                        comments = fetchedComments
                                                        isLoading = false
                                                    }
                                                }


                                                isEditingComment = false // Düzenleme modunu kapat
                                            } else {
                                                Toast.makeText(context, "Yorum güncellenemedi: $errorMessage", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0571C7))
                                ) {
                                    Text("Kaydet", color = Color.White)
                                }

                                TextButton(onClick = { isEditingComment = false }) {
                                    Text("İptal", color = Color.Black)

                                }
                            }
                        }
                    }
                    else {
                        // Görüntüleme Görünümü
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { navController.navigate("explore/$placeId") },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF0F8FF)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Mekan Adı
                                Text(
                                    text = comment.placeName ?: "Anonim",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0277BD)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Yıldız Değerlendirme
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    repeat(5) { index ->
                                        Icon(
                                            imageVector = if (index < comment.rate) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = "Rating Star",
                                            tint = if (index < comment.rate) Color(0xFFFFD700) else Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                // Düzenle ve Sil Butonları
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Yorum Metni
                                    Text(
                                        text = comment.text ?: "Yorum yok",
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.weight(1f) // Butonlara yer bırakmak için esneklik
                                    )
                                    // Düzenle Butonu
                                    IconButton(onClick = { isEditingComment = true }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Comment",
                                            tint = Color(0xFF0571C7)
                                        )
                                    }

                                    // Silme Butonu ve Onay Diyaloğu
                                    var showDialog by remember { mutableStateOf(false) }

                                    IconButton(onClick = { showDialog = true }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Comment",
                                            tint = Color.Red
                                        )
                                    }

                                    if (showDialog) {
                                        AlertDialog(
                                            onDismissRequest = { showDialog = false },
                                            title = {
                                                Text(text = "Silmek istediğinizden emin misiniz?")
                                            },
                                            text = {
                                                Text(text = "Bu işlem geri alınamaz. Yorumu silmek istediğinizden emin misiniz?")
                                            },
                                            confirmButton = {
                                                TextButton(onClick = {
                                                    showDialog = false
                                                    commentViewModel.deleteComment(comment.commentId) { _, errorMessage ->
                                                        if (errorMessage == null) {
                                                            comments = comments.filter { it.commentId != comment.commentId }
                                                        } else {
                                                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                }) {
                                                    Text("Evet", color = Color.Red)
                                                }
                                            },
                                            dismissButton = {
                                                TextButton(onClick = { showDialog = false }) {
                                                    Text("Hayır")
                                                }
                                            }
                                        )
                                    }
                                }
                                // Tarih
                                Text(
                                    text = "$formattedDate",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }

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
            .padding(16.dp)
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
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Butonlar arasında boşluk
            ) {
                placeTypes.forEach { placeType ->
                    placeType.placeTypeNames.forEach { placeTypeName ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color.White, RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = placeTypeName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
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
fun FavoritesSection(favoriteViewModel: FavoriteViewModel = viewModel(),navController: NavController ) {
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
                    FavoritePlaceItem(place = place,navController=navController)
                }
            }
        }
    }
}

@Composable
fun FavoritePlaceItem(place: FavoriteDto, navController: NavController) {
    val placeId = place?.placeId.toString()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate("explore/$placeId")
            },
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F8FF)) // Beyaza yakın mavi
                .padding(16.dp)
        ) {
            Text(
                text = place.placeName ?: "Bilinmeyen Mekan",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0277BD) // Mavi başlık
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = place.placeAddress ?: "Adres bilgisi yok",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = place.description?.take(90) ?: "Açıklama yok", // 30 karakterle sınırlı açıklama
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            place.rating?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Puan: $it",
                    fontSize = 14.sp,
                    color = Color(0xFFFFD700)
                )
            }
        }
    }
}

@Composable
fun VisitedPlacesSection(visitedPlaceViewModel: VisitedPlaceViewModel = viewModel(),navController: NavController) {
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
                    VisitedPlaceItem(place = place,navController=navController)
                }
            }
        }
    }
}
@Composable
fun VisitedPlaceItem(place: VisitedPlaceDto, navController: NavController) {
    val placeId = place?.placeId.toString()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable {
                navController.navigate("explore/$placeId")
            },
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F8FF)) // Beyaza yakın mavi arka plan
                .padding(16.dp)
        ) {
            Text(
                text = place.placeName ?: "Bilinmeyen Mekan",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0277BD) // Mavi başlık rengi
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = place.placeAddress ?: "Adres bilgisi yok",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = place.description?.take(90)
                    ?: "Açıklama yok", // 30 karakterle sınırlı açıklama
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            place.rating?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Puan: $it",
                    fontSize = 14.sp,
                    color = Color(0xFFFFD700)
                )
            }
        }
    }
}

