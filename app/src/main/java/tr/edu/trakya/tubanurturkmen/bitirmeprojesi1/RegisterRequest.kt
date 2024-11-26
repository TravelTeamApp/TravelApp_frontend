package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

data class RegisterRequest(
    val userName: String,
    val email: String,
    val password: String,
    val tckimlik: String
)