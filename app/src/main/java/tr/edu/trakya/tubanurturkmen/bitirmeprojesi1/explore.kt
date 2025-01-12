package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1
import android.R.attr
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.R.attr.maxLines
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map

@Composable
fun ExploreScreen(
    navController: NavController,
    placeViewModel: PlaceViewModel = viewModel(),
    categoryViewModel: ExploreViewModel = viewModel(),
    visitedPlaceViewModel: VisitedPlaceViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel(),
    commentViewModel: CommentViewModel = viewModel(),
    placeId: String? = null // PlaceId parametresi burada tanımlandı, nullable olacak
){

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
    var isExpanded by remember { mutableStateOf(false) }
    val searchedPlaces = places.filter { it.placeName.contains(searchQuery, ignoreCase = true) }
    // Eğer placeId parametresi varsa, o id'ye göre mekan araması yap
    if (placeId != null) {
        selectedAttraction = places.find { it.placeId.toString() == placeId }
    }
    fun getDrawableResourceByPlaceName(placeName: String): Int {
        return when (placeName.lowercase()) {
            "saray muhallebicisi" -> R.drawable.saray1
            "gülhane parkı"->R.drawable.gulhane1
            "yıldız parkı"->R.drawable.yildiz1
            "mandabatmaz"->R.drawable.mandabatmaz1
            "yerebatan sarnıcı"->R.drawable.yerebatan
            "sultanahmet camii"->R.drawable.sultanahmet
            "kız kulesi"->R.drawable.kiz1
            "vialand tema park"->R.drawable.vialand1
            "arkeoloji müzesi"->R.drawable.arkeoloji1
            "ayasofya camii"->R.drawable.ayasofya1
            "balat"->R.drawable.balat1
            "binbirdirek sarnıcı"->R.drawable.binbirdirek1
            "beylerbeyi sarayı"->R.drawable.beylerbeyi1
            "pierre loti tepesi" -> R.drawable.pierre1
            "madame tussauds müzesi" -> R.drawable.madame1
            "çamlıca kulesi" -> R.drawable.camlica1
            "büyükada"->R.drawable.buyukada1
            "çemberlitaş"->R.drawable.cemberlitas1
            "eminönü"->R.drawable.eminonu1
            "emirgan korusu"->R.drawable.emirgan4
            "galata kulesi"->R.drawable.galata1
            "gülhane parkı"->R.drawable.gulhane1
            "topkapı sarayı"->R.drawable.topkapi1
            "yeni cami"->R.drawable.yeni
            "haydarpaşa tren garı"->R.drawable.haydarpasa1
            "istanbul akvaryum"->R.drawable.istakvaryum1
            "kapalıçarşı"->R.drawable.kapali1
            "süleymaniye cami"->R.drawable.suleymaniye1
            "rumeli hisarı"->R.drawable.rumeli1
            "ortaköy cami"->R.drawable.ortakoy1
            "dolmabahçe sarayı"->R.drawable.dolmabahce1
            "rahmi koç müzesi"->R.drawable.rahmi1
            "pera palace otel"->R.drawable.pera1
            "pelit çikolata müzesi" -> R.drawable.pelit1
            "nusr-et restoran " -> R.drawable.nusret1
            "taksim meydanı" -> R.drawable.taksim2
            "kariye camii (eski chora kilisesi)" -> R.drawable.kariye1

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
                            .padding(bottom = 50.dp) // BottomNavigationBar alanı
                            .verticalScroll(scrollState) // Dikey kaydırmayı etkinleştir
                    ) {
                        Box(modifier = Modifier.height(280.dp).fillMaxWidth()) {
                            Image(
                                painter = painterResource(id = R.drawable.istanbul),
                                contentDescription = "Istanbul Overview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Arama çubuğu
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.8f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
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
                            // Alt bilgi kısmı
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomStart)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.9f)
                                            )
                                        )
                                    )
                                    .padding(23.dp)
                            ) {
                                Column {
                                    Text(
                                        text = if (isExpanded) {
                                            "İstanbul, Asya ve Avrupa’yı birleştiren, zengin tarihi ve büyüleyici atmosferiyle eşsiz bir metropoldür. Roma,Bizans ve Osmanlı gibi imparatorluklara başkentlik yapan şehir,tarihi yapılarla geçmişin izlerini günümüze taşır. Doğu ve Batı’nın ruhunu barındıran İstanbul,her köşesinde farklı bir hikâye sunar."
                                        } else {
                                            "İstanbul, Asya ve Avrupa’yı birleştiren, zengin tarihi ve büyüleyici atmosferiyle eşsiz bir metropoldür. Roma, Bizans ve Osmanlı gibi imparatorluklara başkentlik yapan şehir, tarihi yapılarla geçmişin izlerini günümüze taşır."
                                        },
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            color = Color.White
                                        ),
                                        maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                                        overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (isExpanded) "Daha az göster" else "Daha fazla oku",
                                        style = TextStyle(
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = TextDecoration.Underline
                                        ),
                                        modifier = Modifier.clickable {
                                            isExpanded = !isExpanded
                                        }
                                    )
                                }
                            }}
                        if (searchQuery.isNotEmpty()) {
                            LazyRow(modifier = Modifier.padding(16.dp)) {
                                if (searchedPlaces.isEmpty()) {
                                    // Arama sonucunda eşleşme bulunamadığında gösterilecek mesaj
                                    item {
                                        Text(
                                            "Aramaya uygun sonuç bulunamadı",
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                } else {
                                    items(searchedPlaces) { attraction ->
                                        var isFavorite by remember { mutableStateOf(false) }
                                        var isVisited by remember { mutableStateOf(false) }
                                        val context = LocalContext.current

                                        // Favori ve ziyaret durumu kontrolü
                                        LaunchedEffect(attraction.placeId) {
                                            favoriteViewModel.fetchUserFavorites { favorites, _ ->
                                                if (favorites != null) {
                                                    isFavorite = favorites.any { it.placeId == attraction.placeId }
                                                }
                                            }

                                            visitedPlaceViewModel.fetchUserVisitedPlaces { visitedPlaces, _ ->
                                                if (visitedPlaces != null) {
                                                    isVisited = visitedPlaces.any { it.placeId == attraction.placeId }
                                                }
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .width(180.dp) // Sabit genişlik
                                                .height(240.dp) // Sabit yükseklik
                                                .padding(end = 16.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color.LightGray)
                                                .clickable { selectedAttraction = attraction }
                                        ) {
                                            // Mekan Görseli
                                            Image(
                                                painter = painterResource(
                                                    id = getDrawableResourceByPlaceName(attraction.placeName)
                                                ),
                                                contentDescription = attraction.placeName,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(16.dp)),
                                                contentScale = ContentScale.Crop
                                            )

                                            // Favori ve gidilenler ikonları
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .align(Alignment.TopEnd)
                                                    .padding(8.dp)
                                            ) {
                                                Row(horizontalArrangement = Arrangement.End) {
                                                    // Favori İkonu
                                                    IconButton(onClick = {
                                                        if (isFavorite) {
                                                            favoriteViewModel.deleteFavorite(attraction.placeId) { success, message ->
                                                                if (success) {
                                                                    isFavorite = false
                                                                    Toast.makeText(
                                                                        context,
                                                                        "${attraction.placeName} favorilerden çıkarıldı.",
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
                                                            favoriteViewModel.addFavorite(attraction.placeId) { success, message ->
                                                                if (success) {
                                                                    isFavorite = true
                                                                    Toast.makeText(
                                                                        context,
                                                                        "${attraction.placeName} favorilere eklendi.",
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
                                                    IconButton(onClick = {
                                                        if (isVisited) {
                                                            visitedPlaceViewModel.deleteVisitedPlace(attraction.placeId) { success, message ->
                                                                if (success) {
                                                                    isVisited = false
                                                                    Toast.makeText(
                                                                        context,
                                                                        "${attraction.placeName} gidilenlerden çıkarıldı.",
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
                                                            visitedPlaceViewModel.addVisitedPlace(attraction.placeId) { success, message ->
                                                                if (success) {
                                                                    isVisited = true
                                                                    Toast.makeText(
                                                                        context,
                                                                        "${attraction.placeName} gidilenlere kaydedildi.",
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

                                            // Mekan adı ve kategori bilgisi
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .align(Alignment.BottomStart)
                                                    .background(
                                                        Brush.verticalGradient(
                                                            colors = listOf(
                                                                Color.Transparent,
                                                                Color.Black.copy(alpha = 0.8f)
                                                            )
                                                        )
                                                    )
                                                    .padding(16.dp)
                                            ) {
                                                Column {
                                                    Text(
                                                        text = attraction.placeName,
                                                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = "${attraction.placeType.placeTypeName}",
                                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f)),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        else if (searchQuery.isEmpty()) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = "Önerilenler",
                                    style = MaterialTheme.typography.headlineSmall.copy(color = Color.Black),
                                    modifier = Modifier.padding(16.dp)
                                )

                                when {
                                    suggestedPlaces.isNotEmpty() -> {
                                        LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
                                            items(suggestedPlaces) { place ->
                                                var isFavorite by remember { mutableStateOf(false) }
                                                var isVisited by remember { mutableStateOf(false) }

                                                // Favoriler ve gidilenler durumunu dinamik kontrol et
                                                LaunchedEffect(place.placeId) {
                                                    favoriteViewModel.fetchUserFavorites { favorites, error ->
                                                        if (favorites != null) {
                                                            isFavorite = favorites.any { it.placeId == place.placeId }
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                error ?: "Favoriler alınırken hata oluştu.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }

                                                    visitedPlaceViewModel.fetchUserVisitedPlaces { visitedPlaces, error ->
                                                        if (visitedPlaces != null) {
                                                            isVisited = visitedPlaces.any { it.placeId == place.placeId }
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                error ?: "Ziyaret edilenler alınırken hata oluştu.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .width(220.dp)
                                                        .height(280.dp)
                                                        .padding(end = 16.dp)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(Color.LightGray)
                                                        .clickable { selectedAttraction = place }
                                                ) {
                                                    Image(
                                                        painter = painterResource(
                                                            id = getDrawableResourceByPlaceName(place.placeName)
                                                        ),
                                                        contentDescription = place.placeName,
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(RoundedCornerShape(16.dp)),
                                                        contentScale = ContentScale.Crop
                                                    )

                                                    // Favoriler ve gidilenler ikonları
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .align(Alignment.TopEnd)
                                                            .padding(8.dp)
                                                    ) {
                                                        Row(horizontalArrangement = Arrangement.End) {
                                                            // Favori İkonu
                                                            IconButton(onClick = {
                                                                if (isFavorite) {
                                                                    favoriteViewModel.deleteFavorite(place.placeId) { success, message ->
                                                                        if (success) {
                                                                            isFavorite = false
                                                                            Toast.makeText(
                                                                                context,
                                                                                "${place.placeName} favorilerden çıkarıldı.",
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
                                                                    favoriteViewModel.addFavorite(place.placeId) { success, message ->
                                                                        if (success) {
                                                                            isFavorite = true
                                                                            Toast.makeText(
                                                                                context,
                                                                                "${place.placeName} favorilere eklendi.",
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
                                                            IconButton(onClick = {
                                                                if (isVisited) {
                                                                    visitedPlaceViewModel.deleteVisitedPlace(place.placeId) { success, message ->
                                                                        if (success) {
                                                                            isVisited = false
                                                                            Toast.makeText(
                                                                                context,
                                                                                "${place.placeName} gidilenlerden çıkarıldı.",
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
                                                                    visitedPlaceViewModel.addVisitedPlace(place.placeId) { success, message ->
                                                                        if (success) {
                                                                            isVisited = true
                                                                            Toast.makeText(
                                                                                context,
                                                                                "${place.placeName} gidilenlere kaydedildi.",
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

                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .align(Alignment.BottomStart)
                                                            .background(
                                                                Brush.verticalGradient(
                                                                    colors = listOf(
                                                                        Color.Transparent,
                                                                        Color.Black.copy(alpha = 0.8f)
                                                                    )
                                                                )
                                                            )
                                                            .padding(16.dp)
                                                    ) {
                                                        Column {
                                                            Text(
                                                                text = place.placeName,
                                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                                    color = Color.White,
                                                                    fontWeight = FontWeight.Bold
                                                                ),
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                            Spacer(modifier = Modifier.height(4.dp))
                                                            Text(
                                                                text = "${place.rating} ★",
                                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                                    color = Color.White
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else -> {
                                        Text(
                                            text = "Öneri yok",
                                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }

                            }
                            // Kategoriler için Tablar
                            Text(
                                text = "Keşfedin",
                                style = MaterialTheme.typography.headlineSmall.copy(color = Color.Black),
                                modifier = Modifier.padding(16.dp)
                            )
                            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                                items(categories) { category ->
                                    Button(
                                        onClick = { selectedCategory = category },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (category == selectedCategory) Color(0xFF2196F3) else Color.White,
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
                                            "Eşleşen öge yok",
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                } else {
                                    items(filteredAttractions) { attraction ->
                                        var isFavorite by remember { mutableStateOf(false) }
                                        var isVisited by remember { mutableStateOf(false) }

                                        // Dinamik kontrol
                                        LaunchedEffect(attraction.placeId) {
                                            favoriteViewModel.fetchUserFavorites { favorites, error ->
                                                if (favorites != null) {
                                                    isFavorite = favorites.any { it.placeId == attraction.placeId }
                                                }
                                            }

                                            visitedPlaceViewModel.fetchUserVisitedPlaces { visitedPlaces, error ->
                                                if (visitedPlaces != null) {
                                                    isVisited = visitedPlaces.any { it.placeId == attraction.placeId }
                                                }
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .width(180.dp) // Sabit genişlik
                                                .height(240.dp) // Sabit yükseklik
                                                .padding(end = 16.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color.LightGray)
                                                .clickable { selectedAttraction = attraction }
                                        ) {
                                            Image(
                                                painter = painterResource(
                                                    id = getDrawableResourceByPlaceName(attraction.placeName)
                                                ),
                                                contentDescription = attraction.placeName,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(16.dp)),
                                                contentScale = ContentScale.Crop
                                            )

                                            // Favori ve gidilen ikonları
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .align(Alignment.TopEnd)
                                                    .padding(8.dp)
                                            ) {
                                                Row(horizontalArrangement = Arrangement.End) {
                                                    // Favori İkonu
                                                    IconButton(onClick = {
                                                        if (isFavorite) {
                                                            favoriteViewModel.deleteFavorite(attraction.placeId) { success, _ ->
                                                                if (success) isFavorite = false
                                                            }
                                                        } else {
                                                            favoriteViewModel.addFavorite(attraction.placeId) { success, _ ->
                                                                if (success) isFavorite = true
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
                                                    IconButton(onClick = {
                                                        if (isVisited) {
                                                            visitedPlaceViewModel.deleteVisitedPlace(attraction.placeId) { success, _ ->
                                                                if (success) isVisited = false
                                                            }
                                                        } else {
                                                            visitedPlaceViewModel.addVisitedPlace(attraction.placeId) { success, _ ->
                                                                if (success) isVisited = true
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

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .align(Alignment.BottomStart)
                                                    .background(
                                                        Brush.verticalGradient(
                                                            colors = listOf(
                                                                Color.Transparent,
                                                                Color.Black.copy(alpha = 0.8f)
                                                            )
                                                        )
                                                    )
                                                    .padding(12.dp)
                                            ) {
                                                Column {
                                                    Text(
                                                        text = attraction.placeName,
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }}}}}
        else {
            val scrollState = rememberScrollState()
            val commentsState = remember { mutableStateOf<List<CommentDto>?>(null) }
            var rating by remember { mutableStateOf(0f) }
            var comment by remember { mutableStateOf("") }
            val maxHeight = 650.dp
            val minHeight = 10.dp
            val density = LocalDensity.current // LocalDensity ile DP'den PX'e dönüştürme
            val imageHeight = with(density) {
                val maxPx = maxHeight.toPx()
                val minPx = minHeight.toPx()
                derivedStateOf {
                    val scrollOffset = scrollState.value.toFloat()
                    (maxPx - scrollOffset.coerceIn(0f, maxPx - minPx)).toDp()
                }.value
            }

            Box(modifier = Modifier.fillMaxSize()) {
                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Resim Bölümü
                    Box(
                        modifier = Modifier
                            .height(imageHeight)
                            .fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(
                                id = selectedAttraction?.let { getDrawableResourceByPlaceName(it.placeName) }
                                    ?: R.drawable.istanbul // Default image
                            ),
                            contentDescription = "Selected Place Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.1f)) // blur
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
                            IconButton(
                                onClick = {
                                    if (placeId != null) {
                                        navController.popBackStack()
                                    } else {
                                        selectedAttraction = null
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp) // Beyaz arka planın boyutunu küçültmek için boyut tanımlandı
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Geri Git",
                                    tint = Color.White, // Beyaz renk
                                    modifier = Modifier.size(36.dp) // İkon boyutunu büyütmek için
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
                    Column (Modifier.padding(15.dp)){
                        Spacer(modifier = Modifier.height(8.dp))

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
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = selectedAttraction?.placeAddress.orEmpty(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
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

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null // Removes ripple effect
                                ) {
                                    val placeId = selectedAttraction?.placeId.toString()
                                    navController.navigate("map/$placeId")
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Go to Map",
                                modifier = Modifier.size(24.dp) // İkon boyutu
                            )
                            Text(
                                text = "Haritada Göster",
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    }
                    selectedAttraction?.let { attraction ->
                        val placeId = attraction.placeId
                        var isCommentSectionVisible by remember { mutableStateOf(false) } // Yorum alanı görünürlük durumu
                        // Fetch Comments
                        commentViewModel.getPlaceComments(placeId) { comments, _ ->
                            commentsState.value = comments
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween // Bileşenleri sola ve sağa yayar
                        ) {
                        Text(text="Yorumlar",
                            style = MaterialTheme.typography.headlineMedium
                        )                        // Yorum Bölümü Açma / Kapatma FAB
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {

                                FloatingActionButton(
                                    onClick = { isCommentSectionVisible = !isCommentSectionVisible },
                                    containerColor = Color(0xFF3F51B5),
                                    contentColor = Color.White,
                                    modifier = Modifier.size(45.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isCommentSectionVisible) Icons.Default.Close else Icons.Default.Add,
                                        contentDescription = if (isCommentSectionVisible) "Yorumu Kapat" else "Yorum Yap"
                                    )
                                }
                            }
                            FloatingActionButton(
                                onClick = { isCommentSectionVisible = !isCommentSectionVisible },
                                containerColor = Color(0xFF0571C7),
                                contentColor = Color.White,
                                modifier = Modifier.size(45.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCommentSectionVisible) Icons.Default.Close else Icons.Default.Add,
                                    contentDescription = if (isCommentSectionVisible) "Yorumu Kapat" else "Yorum Yap"
                                )
                            }
                        }
                            Spacer(modifier = Modifier.height(8.dp))

                            // Yorum Alanı - Yalnızca görünürse çizilir
                    if (isCommentSectionVisible) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp)
                        ) {
                            // Star Rating Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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
                                label = { Text("Yorumunuz") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF0571C7),
                                    unfocusedBorderColor = Color.Gray,
                                    focusedContainerColor = Color(0xFFF5F5F5),
                                    unfocusedContainerColor = Color(0xFFF5F5F5)
                                ),
                                maxLines = 5
                            )

                            Spacer(modifier = Modifier.height(16.dp))

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
                                                isCommentSectionVisible = false // Yorum bölümü kapanır
                                            } else {
                                                Toast.makeText(context, "Failed to submit review: $errorMessage", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0571C7))

                            ) {
                                Text("Gönder", color = Color.White, fontSize = 16.sp)
                            }
                        }
                    }
                        // Display Comments
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ){
                    val comments = commentsState.value
                    if (comments.isNullOrEmpty()) {
                        Text(
                            text = "Henüz hiç yorum yok.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(comments) { comment ->
                                // selectedCommentId'yi bir state olarak tanımlayın
                                var selectedCommentId by remember { mutableStateOf<Int?>(null) }
                                var isView by remember { mutableStateOf(true) }

                                // Düzenlenmekte olan yorumun ID'sini kontrol et
                                val isEditingComment = selectedCommentId == comment.commentId
                                var selectedRating by remember { mutableStateOf(comment.rate.toFloat()) }
                                var editableCommentText by remember { mutableStateOf(comment.text ?: "") }
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF0F8FF)
                                    )

                                    ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp) // Profil boyutu
                                                .clip(CircleShape) // Yuvarlak şekil
                                                .background(Color(0xFFE8ECEF)) // Arka plan rengi
                                                .border(2.dp, Color(0xFF0571C7), CircleShape) // Kenarlık
                                        ) {
                                            val firstLetter = comment.createdBy?.firstOrNull()?.uppercaseChar() ?: "?"
                                            Text(
                                                text = firstLetter.toString(),
                                                fontSize = 20.sp,
                                                color = Color(0xFF0571C7),
                                                modifier = Modifier.align(Alignment.Center)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = comment.createdBy ?: "Unknown",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color =Color.Gray,
                                                fontWeight = FontWeight.Bold
                                            )

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
                                                                        selectedCommentId = null // Düzenleme sonrasında tıklanan yorumu sıfırla
                                                                        commentViewModel.getPlaceComments(placeId) { comments, _ ->
                                                                            commentsState.value = comments
                                                                        }

                                                                    } else {
                                                                        Toast.makeText(context, "Yorum güncellenemedi: $errorMessage", Toast.LENGTH_SHORT).show()
                                                                    }
                                                                }
                                                            },
                                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0571C7))
                                                        ) {
                                                            Text("Kaydet", color = Color.White)
                                                        }

                                                        TextButton(onClick = { selectedCommentId = null }) {
                                                            Text("İptal", color = Color.Black)
                                                        }
                                                    }
                                                }
                                            } else {
                                                // Görüntüleme Modu
                                                Text(
                                                    text = comment.text ?: "",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier.padding(vertical = 4.dp)
                                                )
                                                Row {
                                                    repeat(5) { index ->
                                                        Icon(
                                                            imageVector = if (index < comment.rate) Icons.Default.Star else Icons.Default.StarBorder,
                                                            contentDescription = null,
                                                            tint = if (index < comment.rate) Color(0xFFFFC107) else Color.LightGray
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                val formattedDate = formatDateTime(comment.createdOn)

                                                Text(
                                                    text = formattedDate,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.DarkGray
                                                )
                                            }
                                        }

                                        // Düzenleme ve Silme Butonları
                                        if (!isEditingComment) {
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
                                            if (comment.createdBy == userProfile.value?.userName) {
                                                Row {
                                                    // Edit button
                                                    IconButton(onClick = { selectedCommentId = comment.commentId; isView = false }) {
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
                                                                    // Deletion logic
                                                                    commentViewModel.deleteComment(comment.commentId) { _, errorMessage ->
                                                                        if (errorMessage == null) {
                                                                            Toast.makeText(context, "Yorum başarıyla silindi", Toast.LENGTH_SHORT).show()
                                                                            commentViewModel.getPlaceComments(placeId) { comments, _ ->
                                                                                commentsState.value = comments
                                                                            }
                                                                        } else {
                                                                            Toast.makeText(context, "Yorum silinirken bir hata oluştu: $errorMessage", Toast.LENGTH_SHORT).show()
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
                                            }

                                        }
                                    }}
                            }

                        }
                    }
                }



            }
        }}}}
}