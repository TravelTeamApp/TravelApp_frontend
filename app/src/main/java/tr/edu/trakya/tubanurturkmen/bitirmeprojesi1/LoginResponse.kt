package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

data class LoginResponse(
    val token: String?,
    val message: String,
    val error: String? = null,

    )
