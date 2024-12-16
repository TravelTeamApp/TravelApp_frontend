package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.osmdroid.util.GeoPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.util.MapPlace


data class AddPlaceTypeResponse(
    val message: String,
    val addedPlaceTypes: List<String>,
    val existingPlaceTypes: List<String>
)

class SharedViewModel : ViewModel() {
    private val _selectedInterests = MutableStateFlow<List<String>>(emptyList())
    val selectedInterests: StateFlow<List<String>> = _selectedInterests

    fun updateSelectedInterests(interests: List<String>) {
        _selectedInterests.value = interests
    }
    fun addUserInterests(placeTypeNames: List<String>) {
        val api = RetrofitClient.apiService
        val request = UserPlaceTypeDto(placeTypeNames)

        api.addUserPlaceTypes(request).enqueue(object : retrofit2.Callback<AddPlaceTypeResponse> {
            override fun onResponse(
                call: Call<AddPlaceTypeResponse>,
                response: retrofit2.Response<AddPlaceTypeResponse>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.let {
                        println("Added: ${it.addedPlaceTypes}")
                        println("Existing: ${it.existingPlaceTypes}")
                    }
                } else {
                    println("Error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<AddPlaceTypeResponse>, t: Throwable) {
                println("Request failed: ${t.message}")
            }
        })
    }

}

class PlaceViewModel : ViewModel() {
    // State'ler: List of places, loading state, and error message
    private val _places = mutableStateOf<List<PlaceDto>>(emptyList())
    val places: State<List<PlaceDto>> get() = _places
    private val _mapPlaces = mutableStateOf<List<MapPlace>>(emptyList())
    val mapPlaces: State<List<MapPlace>> get() = _mapPlaces
    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> get() = _loading
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> get() = _errorMessage
    private val _suggestedPlaces = mutableStateOf<List<PlaceDto>>(emptyList())
    val suggestedPlaces: State<List<PlaceDto>> get() = _suggestedPlaces
    init {
        fetchPlaces()
    }
    fun fetchPlaces() {
        _loading.value = true  // API çağrısı başladığında loading state'i true
        _errorMessage.value = null  // Hata mesajını sıfırla
        RetrofitClient.apiService.getAllPlaces().enqueue(object : Callback<List<PlaceDto>> {
            override fun onResponse(call: Call<List<PlaceDto>>, response: Response<List<PlaceDto>>) {
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
            override fun onFailure(call: Call<List<PlaceDto>>, t: Throwable) {
                _loading.value = false  // Hata durumunda loading state'i false
                _errorMessage.value = "Network Error: ${t.message}"
                Log.e("Network Error", "Error: ${t.message}")
            }
        })

    }

    fun getPlaceTypesByUserId(callback: (List<UserPlaceTypeDto>?) -> Unit) {
        RetrofitClient.apiService.getPlaceTypesByUserId().enqueue(object :
            Callback<List<UserPlaceTypeDto>> {
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
        RetrofitClient.apiService.getPlacesByUserPlaceTypes().enqueue(object :
            Callback<List<PlaceDto>> {
            override fun onResponse(call: Call<List<PlaceDto>>, response: Response<List<PlaceDto>>) {
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
            override fun onFailure(call: Call<List<PlaceDto>>, t: Throwable) {
                _loading.value = false
                _errorMessage.value = "Network Error: ${t.message}"
                Log.e("Network Error", "Error: ${t.message}")
            }
        })
    }



}

class ExploreViewModel : ViewModel() {
    private val _categories = mutableStateOf<List<PlaceTypeDto>>(emptyList())
    val categories: State<List<PlaceTypeDto>> = _categories
    init {
        fetchCategories()
    }
    private fun fetchCategories() {
        RetrofitClient.apiService.getAllPlaceTypes().enqueue(object : Callback<List<PlaceTypeDto>> {
            override fun onResponse(call: Call<List<PlaceTypeDto>>, response: Response<List<PlaceTypeDto>>) {
                if (response.isSuccessful) {
                    response.body()?.let { placeTypes ->
                        _categories.value = placeTypes

                        Log.d("ExploreViewModel", "Categories: ${placeTypes.joinToString { it.placeTypeName }}")
                    }
                } else {
                    android.util.Log.e("API Error", "Error: ${response.code()} - ${response.message()}")
                }
            }
            override fun onFailure(call: Call<List<PlaceTypeDto>>, t: Throwable) {
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
        RetrofitClient.apiService.getUserVisitedPlaces().enqueue(object :
            Callback<List<VisitedPlaceDto>> {
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
    fun getPlaceComments(placeId: Int, callback: (List<CommentDto>?, String?) -> Unit) {
        RetrofitClient.apiService.getCommentsByPlaceId(placeId).enqueue(object : Callback<List<CommentDto>> {
            override fun onResponse(call: Call<List<CommentDto>>, response: Response<List<CommentDto>>) {
                if (response.isSuccessful) {
                    // Eğer başarılıysa, yorumları callback'e gönder
                    callback(response.body(), null)
                } else {
                    // Eğer başarısızsa, null verisi ve hata mesajı gönder
                    callback(null, "Yorumlar alınırken bir hata oluştu.")
                }
            }

            override fun onFailure(call: Call<List<CommentDto>>, t: Throwable) {
                // Bağlantı hatası durumunda callback'e null ve hata mesajı gönder
                callback(null, "Bir hata oluştu: ${t.message}")
            }
        })
    }

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
    // Create a new comment
    fun createComment(placeId: Int, content: String, rate: Int, callback: (CommentDto?, String?) -> Unit) {
        val createCommentRequest = CreateCommentDto(content, rate)
        RetrofitClient.apiService.createComment(placeId, createCommentRequest).enqueue(object :
            Callback<CommentDto> {
            override fun onResponse(
                call: Call<CommentDto>,
                response: Response<CommentDto>
            ) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Hata: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CommentDto>, t: Throwable) {
                callback(null, "İstek başarısız: ${t.message}")
            }
        })
    }
    fun updateComment(id: Int, updateCommentRequest: UpdateCommentRequestDto, callback: (CommentResponse?, String?) -> Unit) {
        RetrofitClient.apiService.updateComment(id, updateCommentRequest).enqueue(object : Callback<CommentResponse> {
            override fun onResponse(call: Call<CommentResponse>, response: Response<CommentResponse>) {
                if (response.isSuccessful) {
                    // Yorum güncelleme başarılıysa, güncellenmiş yorumu callback'e gönder
                    callback(response.body(), null)
                } else {
                    // Yorum güncellenirken bir hata oluşursa, null verisi ve hata mesajı gönder
                    callback(null, "Yorum güncellenirken bir hata oluştu.")
                }
            }

            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                // Bağlantı hatası durumunda callback'e null ve hata mesajı gönder
                callback(null, "Bir hata oluştu: ${t.message}")
            }
        })
    }
    fun deleteComment(commentId: Int, callback: (CommentDto?, String?) -> Unit) {
        RetrofitClient.apiService.deleteComment(commentId).enqueue(object : Callback<CommentDto> {
            override fun onResponse(call: Call<CommentDto>, response: Response<CommentDto>) {
                if (response.isSuccessful) {
                    // Başarılı bir şekilde silindiyse, silinen yorumu callback'e gönder
                    callback(response.body(), null)
                } else {
                    // Başarısız bir durumda hata mesajını döndür
                    callback(null, "Yorum silinirken bir hata oluştu: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CommentDto>, t: Throwable) {
                // Bağlantı hatası durumunda callback'e hata mesajını döndür
                callback(null, "Bir hata oluştu: ${t.message}")
            }
        })
    }

}