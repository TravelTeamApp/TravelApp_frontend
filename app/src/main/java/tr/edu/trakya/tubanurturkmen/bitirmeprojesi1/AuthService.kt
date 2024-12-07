package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.Path

interface AuthService {

    // Kullanıcı giriş işlemi
    @POST("User/login")  // Bu, BASE_URL'in sonuna eklenir, yani: http://localhost:5000/api/User/login
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    // Kullanıcı kayıt işlemi
    @POST("User/register")  // Yine BASE_URL'in sonuna eklenir, yani: http://localhost:5000/api/User/register
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    // Şifre unutma işlemi
    @POST("User/forgot-password")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    // Kullanıcı profil bilgilerini almak için
    @GET("User/profile")
    fun getUserProfile(): Call<UserProfileResponse>

    // Kullanıcının mekan türlerini eklemek için
    @POST("UserPlaceType/add-by-names")  // BASE_URL'in sonuna eklenir, yani: http://10.0.2.2:5000/api/User/add-by-names
    fun addUserPlaceTypes(
        @Body userPlaceTypeDto: UserPlaceTypeDto
    ): Call<AddPlaceTypeResponse>

    // Tüm mekan türlerini getiren metod
    @GET("UserPlaceType/all")  // Backend URL'ini buraya ekleyin
    fun getAllPlaceTypes(): Call<List<PlaceType>>

    @GET("place")  // Bu URL backend'deki endpoint'e bağlıdır, örneğin: http://localhost:5000/api/places/all
    fun getAllPlaces(): Call<List<Place>> // Tüm mekanları bir liste olarak alacak

    // Yorum eklemek için
    @POST("place/{placeId}/comments")
    fun addComment(
        @Path("placeId") placeId: Int,
        @Body commentDto: CreateCommentDto
    ): Call<Comment>

    // Kullanıcının ziyaret ettiği bir mekanı eklemek için
    @POST("visitedPlace")
    fun addVisitedPlace(
        @Path("placeId") placeId: Int,
    ): Call<Unit>

    @DELETE("visitedPlace")
    fun deleteVisitedPlace(
        @Path("placeId") placeId: Int,
    ): Call<Unit>



}