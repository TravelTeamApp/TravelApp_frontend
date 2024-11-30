package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header


interface AuthService {
    @POST("login")  // Bu, BASE_URL'in sonuna eklenir, yani: http://localhost:5000/api/User/login
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("register")  // Yine BASE_URL'in sonuna eklenir, yani: http://localhost:5000/api/User/register
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>
    @POST("forgot-password")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    // Kullanıcı profil bilgilerini almak için
    @GET("profile")
    fun getUserProfile(): Call<UserProfileResponse>

}
