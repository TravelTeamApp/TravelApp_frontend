package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

data class FavoriteDto(
        val favoriteId: Int,
        val placeId: Int,
        val placeName: String?,
        val placeAddress: String?,
        val description: String?,
        val rating: Double?,
        val placeType: PlaceTypeDto?, // Her mekanın türü (opsiyonel)
        val comments: List<CommentDto>?, // Yorumlar
        val userName: String? // Kullanıcı adı
)

data class CommentDto(
        val commentId: Int,
        val placeId: Int?,
        val placeName: String?,
        val text: String?,
        val createdOn: String,
        val createdBy: String?,
        val userId: Int?,
        val rate:Int
)

data class CommentsResponse(
        val comments: List<CommentDto>
)

data class VisitedPlaceDto(
        val visitedPlaceId: Int,
        val placeId: Int,
        val placeName: String?,
        val placeAddress: String?,
        val description: String?,
        val rating: Double?,
        val placeType: PlaceTypeDto?, // Mekan türü
        val comments: List<CommentDto>?, // Mekan yorumları
        val userName: String? // Ziyaret eden kullanıcı
)

data class UserPlaceTypeDto(
        val placeTypeNames: List<String> // Mekan türü adlarını içeren liste
)

data class PlaceDto(
        val placeId: Int,
        val placeName: String,
        val placeAddress: String,
        val description: String,
        val rating: Double,
        val latitude:Double,
        val longitude:Double,
        val placeType: PlaceTypeDto,
        val comments: List<CommentDto>
)

data class PlaceTypeDto(
        val id: Int,
        val placeTypeName: String
)

data class CreateCommentDto(
        val text: String,
        val rate: Int
)

data class RegisterResponse(
        val error: String? = null,
        val message: String? = null,
        val token: String? = null
)

data class RegisterRequest(
        val userName: String,
        val email: String,
        val password: String,
        val tckimlik: String
)

data class LoginResponse(
        val token: String?,
        val message: String,
        val error: String? = null,

        )

data class LoginRequest(
        val email: String,
        val password: String,
        )

data class ForgotPasswordResponse(val tckimlik: String, val message:String)

data class UserProfileResponse(
        val id: Int,
        val email: String,
        val userName: String,
        val score: Int
)

data class CommentResponse(
        val id: Int?,
        val content: String?,
        val userId: Int?,
        val createdAt: String,
        val updatedAt: String
)
data class UpdateCommentRequestDto(
        val text: String, // Yorum metni
        val rate: Int? // Yorum puanı (isteğe bağlı)
)
