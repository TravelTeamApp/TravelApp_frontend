package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
// Media3 (ExoPlayer)
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
@androidx.media3.common.util.UnstableApi
@Composable
fun TravelogScreen(navController: NavController) {
    val context = LocalContext.current
    var isHoveredLogin by remember { mutableStateOf(false) }
    var isHoveredRegister by remember { mutableStateOf(false) }


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

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
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
        // Welcome Text
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Travelog",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(4f, 4f),
                        blurRadius = 8f
                    )
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Unutulmaz anılar için hazır mısınız?",
                fontSize = 18.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        // White Box at the Bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f) // Beyaz kutunun yüksekliği (ekranın %20'si)
                .align(Alignment.BottomCenter)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp) // Yuvarlak üst köşeler
                )
                .padding(16.dp) // İçerik kenar boşluğu
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                // Sign in Button
                Button(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .pointerInput(Unit) {
                            // Hover için fare hareketini yakala
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    isHoveredLogin = event.changes.any { it.pressed }
                                }
                            }
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isHoveredLogin) Color(0xFF1C28E0) else Color(0xFF117ED0), // Hover ve normal renkler
                        contentColor = Color.White
                    ),
                ) {
                    Text(text = "Giriş Yap", color = Color.White, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Create an account Button
                Button(
                    onClick = { navController.navigate("register") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .pointerInput(Unit) {
                            // Hover için fare hareketini yakala
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    isHoveredRegister = event.changes.any { it.pressed }
                                }
                            }
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isHoveredRegister) Color(0xFF1C28E0) else Color(0xFF117ED0), // Hover ve normal renkler
                        contentColor = Color.White
                    ),
                ) {
                    Text(text = "Kayıt Ol", color = Color.White, fontSize = 20.sp)
                }
            }
        }
    }
}