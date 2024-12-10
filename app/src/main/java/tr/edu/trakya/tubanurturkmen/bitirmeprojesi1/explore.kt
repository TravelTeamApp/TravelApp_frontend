package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1
import android.util.Log
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
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
data class Place(
    val placeId: Int,
    val placeName: String,
    val placeAddress: String,
    val description: String,
    val rating: Int,
    val placeType: PlaceType,
    val comments: List<Comment>
)
// Data model
data class PlaceType(
    val placeTypeId: Int,
    val placeTypeName: String
)
data class Comment(
    val commentId: Int,
    val text: String,
    val createdBy: String,
    val createdOn: String
)
// DTO for creating a comment
data class CreateCommentDto(
    val text: String
)
class PlaceViewModel : ViewModel() {
    // State'ler: List of places, loading state, and error message
    private val _places = mutableStateOf<List<Place>>(emptyList())
    val places: State<List<Place>> get() = _places
    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> get() = _loading
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> get() = _errorMessage
    private val _suggestedPlaces = mutableStateOf<List<Place>>(emptyList())
    val suggestedPlaces: State<List<Place>> get() = _suggestedPlaces
    init {
        fetchPlaces()
    }
    private fun fetchPlaces() {
        _loading.value = true  // API çağrısı başladığında loading state'i true
        _errorMessage.value = null  // Hata mesajını sıfırla
        RetrofitClient.apiService.getAllPlaces().enqueue(object : Callback<List<Place>> {
            override fun onResponse(call: Call<List<Place>>, response: Response<List<Place>>) {
                _loading.value = false  // API yanıtı alındığında loading state'i false
                if (response.isSuccessful) {
                    response.body()?.let { placesList ->
                        _places.value = placesList
                        // Mekanları log'la
                        Log.d("PlaceViewModel", "Places: ${placesList.joinToString { it.placeName }}")
                    }
                } else {
                    // API hatası durumunda
                    _errorMessage.value = "API Error: ${response.code()} - ${response.message()}"
                    Log.e("API Error", "Error: ${response.code()} - ${response.message()}")
                }
            }
            override fun onFailure(call: Call<List<Place>>, t: Throwable) {
                _loading.value = false  // Hata durumunda loading state'i false
                _errorMessage.value = "Network Error: ${t.message}"
                Log.e("Network Error", "Error: ${t.message}")
            }
        })
    }
    fun getPlaceTypesByUserId(callback: (List<UserPlaceTypeDto>?) -> Unit) {
        RetrofitClient.apiService.getPlaceTypesByUserId().enqueue(object : Callback<List<UserPlaceTypeDto>> {
            override fun onResponse(
                call: Call<List<UserPlaceTypeDto>>,
                response: Response<List<UserPlaceTypeDto>>
            ) {
                if (response.isSuccessful) {
                    callback(response.body()) // API yanıtını başarılı bir şekilde aldık
                } else {
                    callback(null) // Hata durumunda null dönüyoruz
                }
            }
            override fun onFailure(call: Call<List<UserPlaceTypeDto>>, t: Throwable) {
                callback(null) // Hata durumunda null dönüyoruz
            }
        })
    }
    fun fetchPlacesByUserPlaceTypes() {
        _loading.value = true
        _errorMessage.value = null
        RetrofitClient.apiService.getPlacesByUserPlaceTypes().enqueue(object : Callback<List<Place>> {
            override fun onResponse(call: Call<List<Place>>, response: Response<List<Place>>) {
                _loading.value = false

                if (response.isSuccessful) {
                    response.body()?.let { suggestedList ->
                        _suggestedPlaces.value = suggestedList
                        Log.d("PlaceViewModel", "Suggested Places: ${suggestedList.joinToString { it.placeName }}")
                    }
                } else {
                    _errorMessage.value = "API Error: ${response.code()} - ${response.message()}"
                    Log.e("API Error", "Error: ${response.code()} - ${response.message()}")
                }
            }
            override fun onFailure(call: Call<List<Place>>, t: Throwable) {
                _loading.value = false
                _errorMessage.value = "Network Error: ${t.message}"
                Log.e("Network Error", "Error: ${t.message}")
            }
        })
    }
}
class ExploreViewModel : ViewModel() {
    private val _categories = mutableStateOf<List<PlaceType>>(emptyList())
    val categories: State<List<PlaceType>> = _categories
    init {
        fetchCategories()
    }
    private fun fetchCategories() {
        RetrofitClient.apiService.getAllPlaceTypes().enqueue(object : Callback<List<PlaceType>> {
            override fun onResponse(call: Call<List<PlaceType>>, response: Response<List<PlaceType>>) {
                if (response.isSuccessful) {
                    response.body()?.let { placeTypes ->
                        _categories.value = placeTypes
                        // categories listesini log yazdır
                        Log.d("ExploreViewModel", "Categories: ${placeTypes.joinToString { it.placeTypeName }}")
                    }
                } else {
                    android.util.Log.e("API Error", "Error: ${response.code()} - ${response.message()}")
                }
            }
            override fun onFailure(call: Call<List<PlaceType>>, t: Throwable) {
                android.util.Log.e("Network Error", "Error: ${t.message}")
            }
        })
    }
}
class VisitedPlaceViewModel() : ViewModel() {
    // Ziyaret edilen yer ekleme
    fun addVisitedPlace(placeId: Int, callback: (Boolean, String) -> Unit) {
        RetrofitClient.apiService.addVisitedPlace(placeId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback(true, "Başarılı")
                } else {
                    callback(false, "Hata: ${response.code()} - ${response.message()}")
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback(false, "İstek başarısız: ${t.message}")
            }
        })
    }
    // Ziyaret edilen yer silme
    fun deleteVisitedPlace(placeId: Int, callback: (Boolean, String) -> Unit) {
        RetrofitClient.apiService.deleteVisitedPlace(placeId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback(true, "Başarılı")
                } else {
                    callback(false, "Hata: ${response.code()} - ${response.message()}")
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback(false, "İstek başarısız: ${t.message}")
            } }) }
    fun fetchUserVisitedPlaces(callback: (List<VisitedPlaceDto>?, String?) -> Unit) {
        RetrofitClient.apiService.getUserVisitedPlaces().enqueue(object : Callback<List<VisitedPlaceDto>> {
            override fun onResponse(
                call: Call<List<VisitedPlaceDto>>,
                response: Response<List<VisitedPlaceDto>>
            ) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Hata: ${response.code()} - ${response.message()}")
                }
            }
            override fun onFailure(call: Call<List<VisitedPlaceDto>>, t: Throwable) {
                callback(null, "İstek başarısız: ${t.message}")
            } }) } }
class FavoriteViewModel() : ViewModel() {
    // Favori ekleme metodu
    fun addFavorite(placeId: Int, callback: (Boolean, String) -> Unit) {
        RetrofitClient.apiService.addFavorite(placeId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Favori ekleme başarılı
                    callback(true, "Favori başarıyla eklendi.")
                } else {
                    // Hata durumu
                    callback(false, "Hata: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // İstek başarısız oldu
                callback(false, "İstek başarısız: ${t.message}")
            } }) }
    // Favori silme metodu
    fun deleteFavorite(placeId: Int, callback: (Boolean, String) -> Unit) {
        RetrofitClient.apiService.deleteFavorite(placeId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Favori silme başarılı
                    callback(true, "Favori başarıyla kaldırıldı.")
                } else {
                    // Hata durumu
                    callback(false, "Hata: ${response.code()} - ${response.message()}")
                } }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                // İstek başarısız oldu
                callback(false, "İstek başarısız: ${t.message}")
            } }) }
    fun fetchUserFavorites(callback: (List<FavoriteDto>?, String?) -> Unit) {
        RetrofitClient.apiService.getUserFavorites().enqueue(object : Callback<List<FavoriteDto>> {
            override fun onResponse(
                call: Call<List<FavoriteDto>>,
                response: Response<List<FavoriteDto>>
            ) {
                if (response.isSuccessful) {
                    callback(response.body(), null) // Favoriler başarıyla alındı
                } else {
                    callback(null, "Hata: ${response.code()} - ${response.message()}") } }
            override fun onFailure(call: Call<List<FavoriteDto>>, t: Throwable) {
                callback(null, "İstek başarısız: ${t.message}")
            } }) } }
class CommentViewModel() : ViewModel() {
    fun fetchUserComments(callback: (List<CommentDto>?, String?) -> Unit) {
        RetrofitClient.apiService.getUserComments().enqueue(object : Callback<List<CommentDto>> {
            override fun onResponse(
                call: Call<List<CommentDto>>,
                response: Response<List<CommentDto>>
            ) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Hata: ${response.code()} - ${response.message()}") } }
            override fun onFailure(call: Call<List<CommentDto>>, t: Throwable) {
                callback(null, "İstek başarısız: ${t.message}")
            } }) }
    fun createComment(placeId: Int, content: String, callback: (CommentDto?, String?) -> Unit) {
        val createCommentRequest = CreateCommentDto(content)
        RetrofitClient.apiService.createComment(placeId, createCommentRequest).enqueue(object : Callback<CommentDto> {
            override fun onResponse(
                call: Call<CommentDto>,
                response: Response<CommentDto>
            ) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Hata: ${response.code()} - ${response.message()}") } }
            override fun onFailure(call: Call<CommentDto>, t: Throwable) {
                callback(null, "İstek başarısız: ${t.message}")
            }
        })
    }
}
@Composable
fun ExploreScreen(
    navController: NavController,
    placeViewModel: PlaceViewModel = viewModel(),
    categoryViewModel: ExploreViewModel = viewModel(),
    visitedPlaceViewModel: VisitedPlaceViewModel = viewModel(), // Eklenen ViewModel
    favoriteViewModel: FavoriteViewModel = viewModel() // Eklenen ViewModel
) {
    val places by placeViewModel.places
    val categories by categoryViewModel.categories
    val context = LocalContext.current
    // Yükleme durumu kontrolü
    if (places.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    // Seçili kategori kontrolü
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: PlaceType(1, "Restaurant")) }
    // Yükleme işlemi devam ederken, kategoriler ve çekilen verileri gözlemle
    var selectedAttraction by remember { mutableStateOf<Place?>(null) }
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
            "kapalıçarşı" -> R.drawable.kapali
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
                                text = "İstanbul hakkında bilgi:\nHagia Sophia, 537 yılında katedral olarak inşa edilmiş ve sonrasında cami olarak kullanılmıştır. Bugün bir müze olarak ziyaretçilerini ağırlamaktadır.",
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

                                                // Attraction name
                                                Text(
                                                    text = attraction.placeName,
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        color = Color.Black
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 8.dp)
                                                )

                                                Spacer(modifier = Modifier.height(8.dp)) // Adding space between name and description

                                                // Description of the attraction
                                                Text(
                                                    text = attraction.description,
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        color = Color.Gray
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 8.dp)
                                                )
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
        else{
            Column(modifier = Modifier.padding(16.dp).background(Color.White)) {

                Box(modifier = Modifier.height(200.dp).fillMaxWidth()) {
                    Image(
                        painter = painterResource(
                            id = selectedAttraction?.let { getDrawableResourceByPlaceName(it.placeName) }
                                ?: R.drawable.istanbul // Varsayılan görsel
                        ),
                        contentDescription = selectedAttraction?.placeName ?: "Istanbul",
                        modifier = Modifier
                            .fillMaxWidth() // Make the image take full width
                            .height(200.dp) // You can adjust the height as needed
                            .clip(RoundedCornerShape(16.dp)), // Rounded corners for the image
                        contentScale = ContentScale.Crop
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
                                    val placeId = attraction.placeId // `placeId` mevcutsa backend ile işlem yapabilirsiniz.
                                    if (favoriteAttractions.contains(placeName)) {
                                        // Backend'den favoriden çıkarma işlemi
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
                                        // Backend'e favori ekleme işlemi
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
                                    val placeId = attraction.placeId // Assuming `placeId` exists in the `selectedAttraction`

                                    if (visitedAttractions.contains(placeName)) {
                                        // Backend'de kaldırma işlemi
                                        visitedPlaceViewModel.deleteVisitedPlace(placeId) { success, message ->
                                            if (success) {
                                                // Yerel listeden çıkarma
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
                                        // Backend'e ekleme işlemi
                                        visitedPlaceViewModel.addVisitedPlace(placeId) { success, message ->
                                            if (success) {
                                                // Yerel listeye ekleme
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
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = selectedAttraction!!.placeName,
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Text(
                                    text = selectedAttraction!!.description,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }}
            }}