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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
            "ayasofya camii" -> R.drawable.ayasofya
            "galata kulesi" -> R.drawable.galata
            "topkapı sarayı" -> R.drawable.topkapi
            "dolmabahçe sarayı" -> R.drawable.dolmabahce
            "istanbul arkeoloji müzesi" -> R.drawable.arkeoloji
            "emirgan korusu" -> R.drawable.emirgan
            "pierre loti tepesi" -> R.drawable.pierre
            "madame tussauds müzesi" -> R.drawable.madame
            "miniatürk" -> R.drawable.miniaturk
            "çamlıca kulesi" -> R.drawable.camlica
            "pelit çikolata müzesi" -> R.drawable.cikolata
            "nusr-et steakhouse " -> R.drawable.nusret
            "kariye camii (eski chora kilisesi)" -> R.drawable.kariye
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
                                text = "Hagia Sophia, 537 yılında katedral olarak inşa edilmiş ve sonrasında cami olarak kullanılmıştır. Bugün bir müze olarak ziyaretçilerini ağırlamaktadır.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                            )

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
                            // LazyRow - Attraction items
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
                                                .width(200.dp)
                                                .padding(8.dp) // BottomNavigationBar alanı

                                                .clickable { selectedAttraction = attraction },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color.Gray.copy(
                                                    alpha = 0.3f
                                                )
                                            )
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(16.dp) // Adding padding for better spacing
                                            ) {
                                                // Image of the attraction
                                                Image(
                                                    painter = painterResource(
                                                        id = getDrawableResourceByPlaceName(attraction.placeName)
                                                    ),
                                                    contentDescription = attraction.placeName,
                                                    modifier = Modifier
                                                        .height(200.dp) // Adjusting the height for better appearance
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(16.dp)), // Adding rounded corners to the image
                                                    contentScale = ContentScale.Crop
                                                )

                                                Spacer(modifier = Modifier.height(16.dp)) // Adding some space between image and description
                                                Text(
                                                    text = attraction.placeName,
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        color = Color.Black
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 8.dp)
                                                )

                                                Spacer(modifier = Modifier.height(2.dp))
                                            }

                                        }
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
                                    Color.Gray.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Geri Dön",
                                tint = Color.White
                            )
                        }

                        // Favorilere Ekle ve Gidilenlere Kaydet İkonları
                        Row {
                            // Favori İkonu
                            var isFavorite by remember { mutableStateOf(false) }

                            LaunchedEffect(selectedAttraction) {
                                isFavorite = selectedAttraction?.placeName in favoriteAttractions
                            }

                            IconButton(onClick = {
                                selectedAttraction?.let { attraction ->
                                    val placeName = attraction.placeName
                                    val placeId = attraction.placeId

                                    if (favoriteAttractions.contains(placeName)) {
                                        favoriteViewModel.deleteFavorite(placeId) { success, message ->
                                            if (success) {
                                                favoriteAttractions.remove(placeName)
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
                                                favoriteAttractions.add(placeName)
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
                                isVisited = selectedAttraction?.placeName in visitedAttractions
                            }

                            IconButton(onClick = {
                                selectedAttraction?.let { attraction ->
                                    val placeName = attraction.placeName
                                    val placeId = attraction.placeId

                                    if (visitedAttractions.contains(placeName)) {
                                        visitedPlaceViewModel.deleteVisitedPlace(placeId) { success, message ->
                                            if (success) {
                                                visitedAttractions.remove(placeName)
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
                                                visitedAttractions.add(placeName)
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
                        TabRow(selectedTabIndex = selectedTabIndex) {
                            Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }) {
                                Text("Details", modifier = Modifier.padding(16.dp))
                            }
                            Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
                                Text("Comments", modifier = Modifier.padding(16.dp))
                            }
                        }

                        when (selectedTabIndex) {
                            0 -> { // Details Tab
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = selectedAttraction?.placeName.orEmpty(),
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = selectedAttraction?.description.orEmpty(),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.place),
                                            contentDescription = null,
                                            tint = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = selectedAttraction?.placeAddress.orEmpty(),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
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
                                                            Card(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                shape = RoundedCornerShape(12.dp),
                                                            ) {
                                                                Column(modifier = Modifier.padding(16.dp)) {
                                                                    comment.text?.let {
                                                                        Text(
                                                                            text = it,
                                                                            style = MaterialTheme.typography.bodyMedium,
                                                                            modifier = Modifier.padding(bottom = 8.dp)
                                                                        )
                                                                    }
                                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                                        repeat(5) { index ->
                                                                            Icon(
                                                                                imageVector = if (index < comment.rate) Icons.Default.Star else Icons.Default.StarBorder,
                                                                                contentDescription = null,
                                                                                tint = if (index < comment.rate) Color.Yellow else Color.Gray
                                                                            )
                                                                        }
                                                                    }
                                                                    Spacer(modifier = Modifier.height(8.dp))
                                                                    Text("Created by: ${comment.createdBy}")
                                                                    Text("Date: ${comment.createdOn}")
                                                                }
                                                            }
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
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text("Add Your Review", style = MaterialTheme.typography.headlineSmall)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                repeat(5) { index ->
                                                    IconButton(onClick = { rating = (index + 1).toFloat() }) {
                                                        Icon(
                                                            imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                                                            contentDescription = null,
                                                            tint = if (index < rating) Color.Yellow else Color.Gray
                                                        )
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            TextField(
                                                value = comment,
                                                onValueChange = { comment = it },
                                                label = { Text("Your comment") },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 8.dp),
                                                maxLines = 5
                                            )
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
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text("Submit Review")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }}}}
