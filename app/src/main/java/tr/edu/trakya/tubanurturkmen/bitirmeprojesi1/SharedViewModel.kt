package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call



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