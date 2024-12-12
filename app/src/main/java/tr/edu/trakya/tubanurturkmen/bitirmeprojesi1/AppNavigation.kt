package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ui.navigation

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row

import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ExploreScreen
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.LoginScreen
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.RegisterScreen
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.HomeScreen
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ForgotPasswordScreen
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.HobiesScreen
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ProfileScreen
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.SharedViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

import org.osmdroid.views.MapView

import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.FinalLearningApp
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.setMapConfigurations


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            val defaultLocation = GeoPoint(41.008238, 28.978359)
            controller.setZoom(15.0)
            controller.setCenter(defaultLocation)
        }
    }


    val currentRoute = remember { mutableStateOf("login") }
    val hideBottomNavRoutes = listOf("login", "register", "hobies", "forgotPassword")
    val showBottomNav = !hideBottomNavRoutes.contains(currentRoute.value)

    // NavController'dan hedef değişikliklerini dinleme
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            currentRoute.value = destination.route ?: "login"
            if (destination.route == "map") {
                mapView.onResume() // Harita ekranına girişte çalıştır
            } else {
                mapView.onPause() // Harita ekranından çıkışta durdur
            }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomNav) 72.dp else 0.dp)
        ) {
            composable("login") { LoginScreen(navController) }
            composable("hobies") { HobiesScreen(navController, SharedViewModel()) }
            composable("register") { RegisterScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("forgotPassword") { ForgotPasswordScreen(navController) }
            composable("explore") { ExploreScreen(navController) }
            composable("profile") { ProfileScreen(navController, SharedViewModel()) }
            composable("map") {
                MapScreen(mapView = mapView) {
                    // Harita animasyonları için callback kullanılabilir
                }
            }
        }

        if (showBottomNav) {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .height(72.dp)
                    .align(Alignment.BottomCenter),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Profile",
                                modifier = Modifier.size(36.dp)
                            )
                        },
                        selected = currentRoute.value == "profile",
                        onClick = {
                            navController.navigate("profile") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        alwaysShowLabel = false
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Explore,
                                contentDescription = "Explore",
                                modifier = Modifier.size(36.dp)
                            )
                        },
                        selected = currentRoute.value == "explore",
                        onClick = {
                            navController.navigate("explore") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        alwaysShowLabel = false
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Place,
                                contentDescription = "Map",
                                modifier = Modifier.size(36.dp)
                            )
                        },
                        selected = currentRoute.value == "map",
                        onClick = {
                            navController.navigate("map") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    }
}

@Composable
fun MapScreen(
    mapView: MapView, // Harita bileşeni dışarıdan alınır
    onPlaceChangeAnimate: (() -> Unit) -> Unit // Animasyon tetikleme callback'i
) {
    val lifecycleOwner = LocalLifecycleOwner.current



    // Yaşam döngüsü olaylarını yönet
    DisposableEffect(mapView) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)

        // DisposableEffectResult döndürmek için cleanup işlemi
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    // Haritayı FinalLearningApp bileşenine gönder
    FinalLearningApp(
        mapView = mapView,
        onPlaceChangeAnimate = onPlaceChangeAnimate
    )
}
