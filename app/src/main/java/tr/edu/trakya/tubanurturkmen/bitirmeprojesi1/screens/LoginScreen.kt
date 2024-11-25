package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val context = LocalContext.current
    var isPasswordVisible by remember { mutableStateOf(false) }

    // ExoPlayer'ı başlat ve video dosyasını ayarla
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val videoUri = Uri.parse("android.resource://${context.packageName}/raw/manzara") // Video dosyanızın yolu
            val mediaItem = MediaItem.fromUri(videoUri)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            volume = 0f // Arka planda sessiz oynatmak için

            // Sonsuz döngü için Listener ekle
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        seekTo(0) // Videonun başına git
                        playWhenReady = true // Oynatmayı tekrar başlat
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
                    useController = false // Kontrolleri gizle
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM // Ekranı tam olarak kapla
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Ön plandaki kullanıcı arayüzü
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Hoşgeldiniz", style = MaterialTheme.typography.headlineLarge, color = Color.White)

                Spacer(modifier = Modifier.height(32.dp))

                // Email TextField
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFF3E5F5),
                        cursorColor = Color(0xFF1A237E),
                        focusedIndicatorColor = Color(0xFF5C6BC0),
                        unfocusedIndicatorColor = Color(0xFF9FA8DA)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password TextField
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Şifre") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                            modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFF3E5F5),
                        cursorColor = Color(0xFF1A237E),
                        focusedIndicatorColor = Color(0xFF5C6BC0),
                        unfocusedIndicatorColor = Color(0xFF9FA8DA)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (validateLogin(email.value, password.value)) {
                            Toast.makeText(context, "Giriş Başarılı", Toast.LENGTH_SHORT).show()
                            navController.navigate("citySelector") // CitySelector ekranına geçiş
                        } else {
                            Toast.makeText(context, "Hatalı Giriş", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0))
                ) {
                    Text(text = "Giriş", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Hesabınız yok mu? Hemen Kaydol",
                    color = Color(0xFF3949AB),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.clickable { /* Kayıt ekranına geçiş */ }
                )
            }
        }
    }

    // ExoPlayer kaynağını serbest bırak
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}

private fun validateLogin(email: String, password: String): Boolean {
    return email == "test@gmail.com" && password == "password123"
}
