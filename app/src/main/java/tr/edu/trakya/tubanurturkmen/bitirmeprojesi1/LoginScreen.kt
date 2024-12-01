package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1
import androidx.compose.ui.res.painterResource
import com.google.gson.stream.JsonReader
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.input.pointer.pointerInput


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.viewinterop.AndroidView

// Android SDK ve sistem bileşenleri
import android.net.Uri
import android.util.Log
import android.widget.Toast

// Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Media3 (ExoPlayer)
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

// Navigation
import androidx.navigation.NavController

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val context = LocalContext.current
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }

    // ExoPlayer oluşturuluyor
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val videoUri =
                Uri.parse("android.resource://${context.packageName}/raw/manzara")
            val mediaItem = MediaItem.fromUri(videoUri)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            volume = 0f

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        seekTo(0)
                        playWhenReady = true
                    }
                }
            })
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Arka plan video oynatıcı
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Hoşgeldiniz",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 36.sp,
                        letterSpacing = 1.5.sp,
                        color = Color.White
                    )
                )

                // Email alanı
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                // Şifre alanı
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Şifre") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (isPasswordVisible) "Şifreyi gizle" else "Şifreyi göster",
                            modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }
                        )
                    }
                )

                Text(
                    text = "Şifremi Unuttum",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp, fontWeight = FontWeight.ExtraBold),
                    color = Color.White,
                    modifier = Modifier
                        .clickable { navController.navigate("forgotPassword") }
                        .align(Alignment.Start)
                )

                // Eğer yükleme yapılırken buton, kullanıcıyı bilgilendirecek şekilde değiştirilebilir.
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                } else {
                    // Giriş Butonu
                    Button(
                        onClick = {
                            if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
                                isLoading = true

                                // RetrofitClient üzerinden apiService'e erişim
                                val loginRequest = LoginRequest(email.value, password.value)

                                RetrofitClient.apiService.login(loginRequest)
                                    .enqueue(object : retrofit2.Callback<LoginResponse> {
                                        override fun onResponse(
                                            call: Call<LoginResponse>,
                                            response: Response<LoginResponse>
                                        ) {
                                            isLoading = false
                                            if (response.isSuccessful) {
                                                // Başarılı giriş işlemi
                                                val token = response.body()
                                                Log.d(
                                                    "LoginSuccess",
                                                    "Giriş Başarılı: Kullanıcı Token'ı: $token"
                                                )  // Başarılı giriş logu
                                                if (token != null) {
                                                    // Token ile işlemler
                                                    Toast.makeText(
                                                        context,
                                                        "Giriş Başarılı",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    // Başarıyla giriş yaptıktan sonra Home sayfasına yönlendir
                                                    navController.navigate("hobies") // Home ekranına yönlendirme

                                                    // Token'ı saklama veya diğer işlemler yapılabilir
                                                }
                                            } else {
                                                val errorBody = response.errorBody()?.string()
                                                val errorCode = response.code()
                                                Log.e(
                                                    "LoginError",
                                                    "Hata Kodu: $errorCode, Hata Mesajı: $errorBody"
                                                )
                                                Toast.makeText(
                                                    context,
                                                    "Giriş Başarısız: $errorBody",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<LoginResponse>,
                                            t: Throwable
                                        ) {
                                            isLoading = false
                                            // Bağlantı hatası durumunda
                                            Log.e("LoginError", "Bağlantı Hatası: ${t.message}")
                                            Toast.makeText(
                                                context,
                                                "Bağlantı Hatası: ${t.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            } else {
                                Toast.makeText(
                                    context,
                                    "Lütfen tüm alanları doldurun",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isHovered) Color(0xFF091057) else Color(0xFF0D92F4), // Hover ve normal renkler
                            contentColor = Color.White
                        ),
                    ) {
                        Text(text = "Giriş Yap")
                    }

                    TextButton(
                        onClick = { navController.navigate("register") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Hesabınız yok mu? Kayıt olun", color = Color.White)
                    }
                }
            }
        }
    }
}




