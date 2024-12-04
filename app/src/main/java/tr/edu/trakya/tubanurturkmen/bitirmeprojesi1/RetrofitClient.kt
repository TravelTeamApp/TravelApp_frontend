package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import SessionCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5000/api/"

    // Cookie yönetimi için SessionCookieJar kullanılıyor.
    private val cookieJar = SessionCookieJar()

    // OkHttpClient, Cookie yönetimini destekleyecek şekilde ayarlanıyor.
    private val okHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .build()

    // Retrofit, OkHttpClient ile yapılandırılıyor.
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient) // OkHttpClient'ı Retrofit'e ekliyoruz.
        .build()

    // AuthService arayüzü oluşturuluyor.
    val apiService: AuthService = retrofit.create(AuthService::class.java)
}