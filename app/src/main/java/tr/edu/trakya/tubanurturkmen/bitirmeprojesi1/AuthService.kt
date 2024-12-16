package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.PUT
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
    fun getAllPlaceTypes(): Call<List<PlaceTypeDto>>

    @GET("place")  // Bu URL backend'deki endpoint'e bağlıdır, örneğin: http://localhost:5000/api/places/all
    fun getAllPlaces(): Call<List<PlaceDto>> // Tüm mekanları bir liste olarak alacak


    @GET("visitedPlace")
    fun getUserVisitedPlaces(): Call<List<VisitedPlaceDto>>

    // Ziyaret edilen yeri ekleme
    @POST("visitedPlace")
    fun addVisitedPlace(
        @Query("placeId") placeId: Int
    ): Call<Void>

    // Ziyaret edilen yeri silme
    @DELETE("visitedPlace")
    fun deleteVisitedPlace(
        @Query("placeId") placeId: Int
    ): Call<Void>

    // Favori ekleme işlemi
    @POST("favorite")
    fun addFavorite(
        @Query("placeId") placeId: Int
    ): Call<Void>

    // Favori silme işlemi
    @DELETE("favorite")
    fun deleteFavorite(
        @Query("placeId") placeId: Int
    ): Call<Void>

    // Kullanıcının favorilerini alma
    @GET("favorite")
    fun getUserFavorites(): Call<List<FavoriteDto>>

    // Kullanıcının yorumlarını getir
    @GET("comment/users")
    fun getUserComments(): Call<List<CommentDto>>

    // Kullanıcının yorumlarını getir
    // Belirli bir mekana ait yorumları getir
    @GET("comment/place/{placeId}")
    fun getCommentsByPlaceId(@Path("placeId") placeId: Int): Call<List<CommentDto>>


    // Yeni bir yorum oluştur
    @POST("comment/{placeId}")
    fun createComment(
        @Path("placeId") placeId: Int,
        @Body createCommentRequest: CreateCommentDto
    ): Call<CommentDto>
    // Yorum güncelleme işlemi
    @PUT("Comment/{id}")  // URL: BASE_URL/Comment/{id}
    fun updateComment(
        @Path("id") id: Int,
        @Body updateCommentRequest: UpdateCommentRequestDto
    ): Call<CommentResponse>
    // Kullanıcıya ait mekan türlerini getirme
    @GET("UserPlaceType")
    fun getPlaceTypesByUserId(): Call<List<UserPlaceTypeDto>>

    @GET("Place/userplace")  // BASE_URL'in sonuna eklenir
    fun getPlacesByUserPlaceTypes(): Call<List<PlaceDto>>
}