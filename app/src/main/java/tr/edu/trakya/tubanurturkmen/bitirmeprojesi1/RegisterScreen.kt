package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1
import com.google.gson.stream.JsonReader
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import java.io.StringReader
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource

import androidx.compose.ui.unit.dp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource


import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*

import androidx.compose.ui.input.pointer.pointerInput


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val userName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val tckimlik = remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    val backgroundImage: Painter = painterResource(id = R.drawable.kuzguncuk)

    var isHovered by remember { mutableStateOf(false) } // Hover durumu
    BackHandler {
        navController.navigate("login") {
            popUpTo("home") { inclusive = true }
        }}
    fun validateEmail(email: String): String {
        return if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ""
        } else {
            "Geçerli bir email adresi girin."
        }
    }

    fun validatePassword(password: String): String {
        if (password.length < 6) return "Şifre en az 6 karakter olmalıdır."
        if (!password.any { it.isUpperCase() }) return "Şifre en az bir büyük harf içermelidir."
        if (!password.any { it.isLowerCase() }) return "Şifre en az bir küçük harf içermelidir."
        if (!password.any { "!@#$%^&*()_+[]{}|;:,.<>?/".contains(it) }) return "Şifre en az bir özel karakter içermelidir."
        return ""
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Arka plan resmi
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Resmi tam ekran doldur
        )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f)) // Yarı saydam siyah renk
            )
        // Geri Butonu (En üstte ve solda)
        IconButton(
            onClick = {
                // Travelog ekranına yönlendirme
                navController.navigate("travelog") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart) // En üst ve sol hizalama
                .padding(16.dp) // Dış boşluk
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Geri Git",
                tint = Color.White, // Beyaz renk
                modifier = Modifier.size(36.dp) // İkon boyutunu büyütmek için
            )
        }
            Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {

            // Kullanıcı Adı
            TextField(
                value = userName.value,
                onValueChange = { userName.value = it },
                label = { Text("Kullanıcı Adı") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            // Email
            TextField(
                value = email.value,
                onValueChange = {
                    email.value = it
                    emailError = validateEmail(it)
                },
                label = { Text("Email") },
                isError = emailError.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            if (emailError.isNotEmpty()) {
                Text(
                    text = emailError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // T.C. Kimlik No
            TextField(
                value = tckimlik.value,
                onValueChange = { tckimlik.value = it },
                label = { Text("T.C. Kimlik No") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                maxLines = 1
            )

            // Şifre
            TextField(
                value = password.value,
                onValueChange = {
                    password.value = it
                    passwordError = validatePassword(it)
                },
                label = { Text("Şifre") },
                isError = passwordError.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Icon(
                        painter = if (isPasswordVisible) painterResource(id = R.drawable.baseline_visibility_24) else painterResource(id = R.drawable.baseline_visibility_off_24),
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }
                    )
                }
            )
            if (passwordError.isNotEmpty()) {
                Text(
                    text = passwordError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Kayıt Ol Butonu
            Button(
                onClick = {
                    if (email.value.isNotEmpty() &&
                        password.value.isNotEmpty() &&
                        userName.value.isNotEmpty() &&
                        tckimlik.value.length == 11 &&
                        emailError.isEmpty() &&
                        passwordError.isEmpty()
                    ) {
                        isLoading = true
                        val registerRequest = RegisterRequest(
                            userName = userName.value,
                            email = email.value,
                            password = password.value,
                            tckimlik = tckimlik.value
                        )

                        RetrofitClient.apiService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
                            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                                isLoading = false
                                Log.e("RegisterError", "Bağlantı Hatası: ${t.message}")
                                Toast.makeText(context, "Bağlantı hatası meydana geldi: ${t.message}", Toast.LENGTH_SHORT).show()
                            }

                            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                                isLoading = false
                                if (response.isSuccessful) {
                                    Log.d("RegisterSuccess", "Kayıt başarılı: ${response.body()}")
                                    Toast.makeText(context, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("hobies")
                                } else {
                                    val rawError = response.errorBody()?.string()
                                    Log.e("RegisterError", "Sunucudan Gelen Yanıt: $rawError")
                                    Toast.makeText(context, "Kayıt hatası: $rawError", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    } else {
                        Toast.makeText(context, "Lütfen tüm alanları doğru şekilde doldurun.", Toast.LENGTH_SHORT).show()
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        // Hover için fare hareketini yakala
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                isHovered = event.changes.any { it.pressed }
                            }
                        }
                    },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isHovered) Color(0xFF1C28E0) else Color(0xFF117ED0), // Hover ve normal renkler
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp) // Daha yumuşak kenarlar içi
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(
                        text = "Kayıt Ol",
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Text(
                text = "Hesabınız varsa giriş yapın",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp, fontWeight = FontWeight.ExtraBold),
                color = Color.White,
                modifier = Modifier
                    .clickable { navController.navigate("login") }
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

