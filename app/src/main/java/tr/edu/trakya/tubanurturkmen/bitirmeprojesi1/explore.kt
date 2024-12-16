package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun ExploreScreen(

    navController: NavController,
    placeViewModel: PlaceViewModel = viewModel(),
    categoryViewModel: ExploreViewModel = viewModel(),
    visitedPlaceViewModel: VisitedPlaceViewModel = viewModel(), // Eklenen ViewModel
    favoriteViewModel: FavoriteViewModel = viewModel(), // Eklenen ViewModel
    commentViewModel: CommentViewModel = viewModel()
) {

    val places by placeViewModel.places
    val categories by categoryViewModel.categories
    val context = LocalContext.current
    if (places.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    // Seçili kategori kontrolü
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: PlaceTypeDto(1, "Restaurant")) }
    var selectedAttraction by remember { mutableStateOf<PlaceDto?>(null) }
    var favoriteAttractions by remember { mutableStateOf(mutableSetOf<String>()) }
    var isDarkMode by remember { mutableStateOf(false) }
    var visitedAttractions by remember { mutableStateOf(mutableSetOf<String>()) }
    val backgroundColor = if (isDarkMode) Color.Black else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
    val topAttractions = places
    val suggestedPlaces by placeViewModel.suggestedPlaces
    val isLoading by placeViewModel.loading
    val errorMessage by placeViewModel.errorMessage

    val searchedPlaces = places.filter { it.placeName.contains(searchQuery, ignoreCase = true) }
    fun getDrawableResourceByPlaceName(placeName: String): Int {
        return when (placeName.lowercase()) {
            "saray muhallebicisi" -> R.drawable.saray
            "gülhane parkı"->R.drawable.gulhane
            "yıldız parkı"->R.drawable.yildiz
            "mandabatmaz"->R.drawable.mandabatmaz
            "yerebatan sarnıcı"->R.drawable.yerebatan
            "sultanahmet camii"->R.drawable.sultanahmet
            "taksim meydanı"->R.drawable.taksim
            "kız kulesi"->R.drawable.kiz
            //"çamlıca tepesi"->R.drawable.camlica
            //"pierre loti tepesi" -> R.drawable.pierre
            //"madame tussauds müzesi" -> R.drawable.madame
            //"miniatürk" -> R.drawable.miniaturk
            //"çamlıca kulesi" -> R.drawable.camlica
            //"pelit çikolata müzesi" -> R.drawable.cikolata
            "nusr-et restoran " -> R.drawable.nusret
            //"kariye camii (eski chora kilisesi)" -> R.drawable.kariye

            else -> R.drawable.istanbul // Varsayılan görsel
        }
    }
    LaunchedEffect(Unit) {
        placeViewModel.fetchPlacesByUserPlaceTypes()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        if (selectedAttraction == null) {
            Column {
                Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 56.dp) // BottomNavigationBar alanı
                            .verticalScroll(scrollState) // Dikey kaydırmayı etkinleştir
                    ) {
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
                                        .background(
                                            Color.White.copy(alpha = 0.8f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
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
                                        textStyle = TextStyle(
                                            color = Color.Black,
                                            fontSize = 16.sp
                                        ),
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
                                text = "İstanbul, Asya ve Avrupa’yı birleştiren, zengin tarihi ve büyüleyici atmosferiyle eşsiz bir metropoldür. Roma, Bizans ve Osmanlı gibi imparatorluklara başkentlik yapan şehir, Sultanahmet’teki Ayasofya, Topkapı Sarayı ve Sultanahmet Camii gibi tarihi yapılarla geçmişin izlerini günümüze taşır. İstanbul Boğazı’nda bir yürüyüş veya tekne turu, şehrin doğal güzelliklerini ve siluetini keşfetmek için harika bir fırsattır. Doğu ve Batı’nın ruhunu barındıran İstanbul, her köşesinde farklı bir hikâye sunar.",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    color = Color.Black
                                ))
                        }
                        if (searchQuery.isNotEmpty()) {
                            LazyRow(modifier = Modifier.padding(16.dp)) {
                                if (searchedPlaces.isEmpty()) {
                                    // No results found for the search
                                    item {
                                        Text(
                                            "No attractions found for this search.",
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                } else {
                                    items(searchedPlaces) { attraction ->
                                        Card(
                                            modifier = Modifier
                                                .width(200.dp)
                                                .padding(8.dp)
                                                .clickable { selectedAttraction = attraction },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color.Gray.copy(alpha = 0.3f)
                                            )
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Image(
                                                    painter = painterResource(id = getDrawableResourceByPlaceName(attraction.placeName)), // Fixed
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(120.dp), // Example height
                                                    contentScale = ContentScale.Crop
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = attraction.placeName,
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        color = Color.Black
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 8.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (searchQuery.isEmpty()) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = "Suggested Attractions",
                                    style = MaterialTheme.typography.headlineSmall.copy(color = Color.Black),
                                    modifier = Modifier.padding(16.dp)
                                )

                                when {
                                    suggestedPlaces.isNotEmpty() -> { // Önerilen mekanlar varsa göster
                                        LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
                                            items(suggestedPlaces) { place ->
                                                Card(
                                                    modifier = Modifier
                                                        .width(200.dp)
                                                        .padding(end = 16.dp)
                                                        .clickable { selectedAttraction = place },
                                                    shape = RoundedCornerShape(12.dp),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = Color.Gray.copy(alpha = 0.3f)
                                                    )
                                                ) {
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Image(
                                                            painter = painterResource(
                                                                id = getDrawableResourceByPlaceName(
                                                                    place.placeName
                                                                )
                                                            ),
                                                            contentDescription = place.placeName,
                                                            modifier = Modifier
                                                                .height(120.dp)
                                                                .fillMaxWidth(),
                                                            contentScale = ContentScale.Crop
                                                        )
                                                        Text(
                                                            text = place.placeName,
                                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                                color = Color.Black
                                                            ),
                                                            modifier = Modifier.padding(horizontal = 8.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    else -> { // Önerilen mekan yoksa bir mesaj göster
                                        Text(
                                            text = "No suggestions available",
                                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }
                            // Kategoriler için Tablar
                            Text(
                                text = "Top Attractions",
                                style = MaterialTheme.typography.headlineSmall.copy(color = Color.Black),
                                modifier = Modifier.padding(16.dp)
                            )

                            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                                items(categories) { category ->
                                    Button(
                                        onClick = { selectedCategory = category },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (category == selectedCategory) Color(
                                                0xFF2196F3
                                            ) else Color.White,
                                            contentColor = if (category == selectedCategory) Color.White else Color.Gray
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text(text = category.placeTypeName)
                                    }
                                }
                            }
                            LazyRow(modifier = Modifier.padding(16.dp)) {
                                val filteredAttractions =
                                    places.filter { it.placeType.placeTypeName == selectedCategory.placeTypeName }

                                if (filteredAttractions.isEmpty()) {
                                    // Eşleşen öğe yoksa, mesaj göster
                                    item {
                                        Text(
                                            "No attractions found for this category.",
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                } else {
                                    items(filteredAttractions) { attraction ->
                                        Card(
                                            modifier = Modifier
                                                .width(220.dp) // Sabit genişlik
                                                .height(300.dp) // Sabit yükseklik
                                                .padding(8.dp)
                                                .clickable { selectedAttraction = attraction },
                                            shape = RoundedCornerShape(16.dp), // Köşeler yuvarlatılmış
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color.Gray.copy(alpha = 0.15f) // Daha hafif bir gri
                                            )
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(12.dp)
                                            ) {
                                                // Mekanın görseli
                                                Image(
                                                    painter = painterResource(
                                                        id = getDrawableResourceByPlaceName(
                                                            attraction.placeName
                                                        )
                                                    ),
                                                    contentDescription = attraction.placeName,
                                                    modifier = Modifier
                                                        .height(200.dp) // Görsel için sabit yükseklik
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(12.dp)), // Görsel için köşeleri yuvarlatma
                                                    contentScale = ContentScale.Crop
                                                )

                                                Spacer(modifier = Modifier.height(8.dp)) // Görsel ile metin arasındaki boşluk
                                                Text(
                                                    text = attraction.placeName,
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        color = Color.Black,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 8.dp)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }}
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }
        else {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.White)
            ) {
                // Resim Bölümü
                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(
                            id = selectedAttraction?.let { getDrawableResourceByPlaceName(it.placeName) }
                                ?: R.drawable.istanbul // Default image
                        ),
                        contentDescription = "Selected Place Image", // Add meaningful content description
                        modifier = Modifier.fillMaxSize(), // Add a modifier to specify the size or alignment
                        contentScale = ContentScale.Crop // Optionally set the scale for the image
                    )

                    // Geri ve diğer ikon butonlar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.TopStart),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Geri Butonu
                        IconButton(
                            onClick = { selectedAttraction = null },
                            modifier = Modifier
                                .background(
                                    Color.White, // Arka plan rengini beyaz yaptık
                                    shape = RoundedCornerShape(50.dp) // Oval bir şekil için köşe yarıçapını artırdık
                                )
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Geri Dön",
                                tint = Color.Black // İkon rengini siyah yaptık
                            )
                        }

                        // Favorilere Ekle ve Gidilenlere Kaydet İkonları
                        Row {
                            // Favori İkonu
                            var isFavorite by remember { mutableStateOf(false) }

                            LaunchedEffect(selectedAttraction) {
                                selectedAttraction?.placeName?.let { placeName ->
                                    favoriteViewModel.fetchUserFavorites { favorites, error ->
                                        if (favorites != null) {
                                            // Favorilerde mi kontrolü
                                            isFavorite = favorites.any { it.placeName == placeName }
                                        } else {
                                            // Hata durumunda mesaj gösterilebilir
                                            Toast.makeText(
                                                context,
                                                error ?: "Favoriler alınırken hata oluştu.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }

                            IconButton(onClick = {
                                selectedAttraction?.let { attraction ->
                                    val placeName = attraction.placeName
                                    val placeId = attraction.placeId

                                    if (isFavorite) {
                                        favoriteViewModel.deleteFavorite(placeId) { success, message ->
                                            if (success) {
                                                // Favorilerden çıkarıldıktan sonra listeyi güncelle
                                                isFavorite = false
                                                Toast.makeText(
                                                    context,
                                                    "$placeName favorilerden çıkarıldı.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Hata: $message",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } else {
                                        favoriteViewModel.addFavorite(placeId) { success, message ->
                                            if (success) {
                                                // Favorilere eklendikten sonra listeyi güncelle
                                                isFavorite = true
                                                Toast.makeText(
                                                    context,
                                                    "$placeName favorilere eklendi.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Hata: $message",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                            }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (isFavorite) R.drawable.favorite_filled else R.drawable.favorite_outline
                                    ),
                                    contentDescription = "Favorilere Ekle",
                                    tint = Color.White
                                )
                            }

                            // Gidilenler İkonu
                            var isVisited by remember { mutableStateOf(false) }

                            LaunchedEffect(selectedAttraction) {
                                selectedAttraction?.placeName?.let { placeName ->
                                    visitedPlaceViewModel.fetchUserVisitedPlaces { visitedPlaces, error ->
                                        if (visitedPlaces != null) {
                                            // Ziyaret edilenler listesinde mi kontrolü
                                            isVisited =
                                                visitedPlaces.any { it.placeName == placeName }
                                        } else {
                                            // Hata durumunda mesaj gösterilebilir
                                            Toast.makeText(
                                                context,
                                                error ?: "Ziyaret edilenler alınırken hata oluştu.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }

                            IconButton(onClick = {
                                selectedAttraction?.let { attraction ->
                                    val placeName = attraction.placeName
                                    val placeId = attraction.placeId

                                    if (isVisited) {
                                        visitedPlaceViewModel.deleteVisitedPlace(placeId) { success, message ->
                                            if (success) {
                                                // Gidilenlerden çıkarıldıktan sonra listeyi güncelle
                                                isVisited = false
                                                Toast.makeText(
                                                    context,
                                                    "$placeName gidilenlerden çıkarıldı.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Hata: $message",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } else {
                                        visitedPlaceViewModel.addVisitedPlace(placeId) { success, message ->
                                            if (success) {
                                                // Gidilenlere eklendikten sonra listeyi güncelle
                                                isVisited = true
                                                Toast.makeText(
                                                    context,
                                                    "$placeName gidilenlere kaydedildi.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Hata: $message",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                            }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (isVisited) R.drawable.visited_filled else R.drawable.visited_outline
                                    ),
                                    contentDescription = "Gidilenlere Kaydet",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
                var selectedTabIndex by remember { mutableStateOf(0) }
                val commentsState = remember { mutableStateOf<List<CommentDto>?>(null) }
                var rating by remember { mutableStateOf(0f) }
                var comment by remember { mutableStateOf("") }

                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    // Tab Row for switching between "Details" and "Comments"
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color.White, // TabRow background color
                        contentColor = Color.Black // Text color for selected tab
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            selectedContentColor = Color.Black,
                            unselectedContentColor = Color.Gray
                        ) {
                            Text("Detaylar", modifier = Modifier.padding(16.dp), color = Color.Black)
                        }
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            selectedContentColor = Color.Black,
                            unselectedContentColor = Color.Gray
                        ) {
                            Text("Yorumlar", modifier = Modifier.padding(16.dp), color = Color.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))


                    when (selectedTabIndex) {
                        0 -> { // Details Tab
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = selectedAttraction?.placeName.orEmpty(),
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = selectedAttraction?.placeAddress.orEmpty(),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                // Star Rating Section
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Ensure rating is valid and not null. If null, set it to 0.0
                                    val rating = (selectedAttraction?.rating ?: 0.0).coerceIn(0.0, 5.0) // Clamp to a valid range between 0.0 and 5.0

                                    val fullStars = rating.toInt()
                                    val hasHalfStar = rating % 1 >= 0.5

                                    // Full stars
                                    repeat(fullStars) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Full Star",
                                            tint = Color(0xFFFFD700) // Gold color
                                        )
                                    }

                                    // Half star
                                    if (hasHalfStar) {
                                        Icon(
                                            imageVector = Icons.Default.StarHalf,
                                            contentDescription = "Half Star",
                                            tint = Color(0xFFFFD700) // Gold color
                                        )
                                    }

                                    // Empty stars (to fill up to 5 stars)
                                    repeat(5 - fullStars - if (hasHalfStar) 1 else 0) {
                                        Icon(
                                            imageVector = Icons.Default.StarOutline,
                                            contentDescription = "Empty Star",
                                            tint = Color.Gray
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Ensure rating is properly formatted to 1 decimal place and avoid invalid format exceptions
                                    val formattedRating = try {
                                        String.format("%.1f", rating) // Format to 1 decimal place
                                    } catch (e: Exception) {
                                        "0.0" // Fallback to a default value if formatting fails
                                    }

                                    Text(
                                        text = formattedRating, // Display the formatted rating
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }


                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = selectedAttraction?.description.orEmpty(),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                        1 -> { // Comments Tab
                            Column(modifier = Modifier.fillMaxSize()) {
                                selectedAttraction?.let { attraction ->
                                    val placeId = attraction.placeId

                                    // Fetch Comments
                                    commentViewModel.getPlaceComments(placeId) { comments, _ ->
                                        commentsState.value = comments
                                    }

                                    // Display Comments
                                    Box(modifier = Modifier.weight(1f)) {
                                        when {
                                            commentsState.value != null -> {
                                                LazyColumn(
                                                    contentPadding = PaddingValues(16.dp),
                                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                                ) {
                                                    items(commentsState.value!!) { comment ->
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(8.dp),
                                                            verticalAlignment = Alignment.Top
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.AccountCircle,
                                                                contentDescription = null,
                                                                modifier = Modifier
                                                                    .size(40.dp)
                                                                    .padding(end = 8.dp),
                                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                            )
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = comment.createdBy
                                                                        ?: "Unknown",
                                                                    style = MaterialTheme.typography.bodyMedium,
                                                                    color = MaterialTheme.colorScheme.onSurface,
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                                Text(
                                                                    text = comment.text ?: "",
                                                                    style = MaterialTheme.typography.bodyMedium,
                                                                    modifier = Modifier.padding(
                                                                        vertical = 4.dp
                                                                    )
                                                                )
                                                                Row {
                                                                    repeat(5) { index ->
                                                                        Icon(
                                                                            imageVector = if (index < comment.rate) Icons.Default.Star else Icons.Default.StarBorder,
                                                                            contentDescription = null,
                                                                            tint = if (index < comment.rate) Color(
                                                                                0xFFFFC107
                                                                            ) else MaterialTheme.colorScheme.onSurfaceVariant
                                                                        )
                                                                    }
                                                                }
                                                                Spacer(
                                                                    modifier = Modifier.height(
                                                                        8.dp
                                                                    )
                                                                )
                                                                Text(
                                                                    text = comment.createdOn,
                                                                    style = MaterialTheme.typography.bodySmall,
                                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                )
                                                            }
                                                            IconButton(onClick = {
                                                                // Yorum düzenleme fonksiyonu
                                                                // editComment(comment)
                                                            }) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Edit,
                                                                    contentDescription = "Edit Comment",
                                                                    tint = MaterialTheme.colorScheme.primary
                                                                )
                                                            }
                                                        }
                                                        Divider(
                                                            color = MaterialTheme.colorScheme.outline,
                                                            thickness = 1.dp
                                                        )
                                                    }
                                                }
                                            }
                                            else -> {
                                                Text(
                                                    text = "Henüz hiç yorum yok.",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            }
                                        }
                                    }
                                    // Add Comment Section
                                    var comment by remember { mutableStateOf("") }
                                    var rating by remember { mutableStateOf(0f) }

                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Yorum Yapın", style = MaterialTheme.typography.headlineSmall)
                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Star Rating Row
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            repeat(5) { index ->
                                                IconButton(onClick = { rating = (index + 1).toFloat() }) {
                                                    Icon(
                                                        imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                                                        contentDescription = null,
                                                        tint = if (index < rating) Color(0xFFFFC107) else Color.Gray
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Styled TextField with Rounded Corners
                                        OutlinedTextField(
                                            value = comment,
                                            onValueChange = { comment = it },
                                            label = { Text("Your comment") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(150.dp)
                                                .padding(vertical = 8.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFF3F51B5),
                                                unfocusedBorderColor = Color.Gray,
                                                focusedContainerColor = Color(0xFFF5F5F5),
                                                unfocusedContainerColor = Color(0xFFF5F5F5)
                                            ),
                                            maxLines = 5
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Submit Button
                                        Button(
                                            onClick = {
                                                selectedAttraction?.let { attraction ->
                                                    val placeId = attraction.placeId
                                                    commentViewModel.createComment(
                                                        placeId = placeId,
                                                        content = comment,
                                                        rate = rating.toInt()
                                                    ) { createdComment, errorMessage ->
                                                        if (createdComment != null) {
                                                            Toast.makeText(context, "Review submitted successfully", Toast.LENGTH_SHORT).show()
                                                            // Reset text field and rating
                                                            comment = ""
                                                            rating = 0f
                                                        }
                                                        else {
                                                            Toast.makeText(context, "Failed to submit review: $errorMessage", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
                                        ) {
                                            Text("Yorum Yap", color = Color.White, fontSize = 16.sp)
                                        }
                                    }
                                }}}}}}
        }
    }
}
