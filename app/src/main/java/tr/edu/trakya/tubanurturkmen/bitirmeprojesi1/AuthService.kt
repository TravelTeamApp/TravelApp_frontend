package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST




interface AuthService {
    @POST("login")  // Bu, BASE_URL'in sonuna eklenir, yani: http://localhost:5000/api/User/login
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("register")  // Yine BASE_URL'in sonuna eklenir, yani: http://localhost:5000/api/User/register
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>
}
