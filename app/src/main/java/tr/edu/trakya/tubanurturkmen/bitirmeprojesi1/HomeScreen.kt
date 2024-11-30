package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp



import SessionCookieJar
import android.util.Log

import androidx.compose.material.icons.filled.ArrowBack
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.mutableStateOf
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun HomeScreen(navController: NavController) {
    // Geri tuşuna basıldığında login ekranına yönlendirme
    BackHandler {
        navController.navigate("login") {
            popUpTo("home") { inclusive = true }
        }
    }

    // Kullanıcı bilgilerini state olarak saklayalım
    val userProfile = remember { mutableStateOf<UserProfileResponse?>(null) }
    val context = LocalContext.current

    // API çağrısını başlatıyoruz
    LaunchedEffect(Unit) {
        RetrofitClient.apiService.getUserProfile().enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                if (response.isSuccessful) {
                    userProfile.value = response.body() // Kullanıcı profilini state'e aktar
                } else {
                    Toast.makeText(context, "Hata: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                Toast.makeText(context, "İstek başarısız: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Geri butonunu sağ üst köşeye ekleyelim
        IconButton(
            onClick = {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp) // Butona biraz padding ekledik
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack, // Material Icons ile geri ok simgesi
                contentDescription = "Geri Git"
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Home sayfası içeriği
            if (userProfile.value != null) {
                Column(
                    modifier = Modifier.padding(16.dp), // Tüm öğelere kenarlık ekliyoruz
                    horizontalAlignment = Alignment.CenterHorizontally, // Ortalıyoruz
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Öğeler arasında boşluk bırakıyoruz
                ) {
                    Text(
                        text = "Hoşgeldiniz, ${userProfile.value?.userName}!",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = "Email: ${userProfile.value?.email}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                }
            } else {
                Text(
                    text = "Kullanıcı bilgileri yükleniyor...",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

    }}