package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5000/api/User/"


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // apiService tanımlanıyor.
    val apiService: AuthService = retrofit.create(AuthService::class.java)

}
