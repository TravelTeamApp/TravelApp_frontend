package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun HomeScreen(navController: NavController) {
    // Geri tuşuna basıldığında login ekranına yönlendirme
    BackHandler {
        navController.navigate("login") {
            popUpTo("home") { inclusive = true }
        }
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

        // Home sayfası içeriği
        Text(
            text = "Hoşgeldiniz, Home Sayfası!",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
