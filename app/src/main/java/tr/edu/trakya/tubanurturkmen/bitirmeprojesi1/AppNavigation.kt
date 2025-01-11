package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ui.navigation

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
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
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavType
import androidx.navigation.navArgument
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.FinalLearningApp
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.TravelogScreen


@OptIn(UnstableApi::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // BottomNavigation için state tutmak
    val currentRoute = remember { mutableStateOf("login") }

    // Bottom Navigation Bar'ın hangi ekranlarda gizleneceği
    val hideBottomNavRoutes = listOf("travelog","login", "register", "hobies","forgotPassword")
    val showBottomNav = !hideBottomNavRoutes.contains(currentRoute.value)

    // Ekran içeriği ve yerleşim düzeni
    Box(modifier = Modifier.fillMaxSize()) {
        // NavHost
        NavHost(
            navController = navController,
            startDestination = "travelog",
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomNav) 48.dp else 0.dp) // BottomNavigationBar için padding ekleniyor
        ) {
            composable("travelog") {
                currentRoute.value = "travelog"
                TravelogScreen(navController)
            }
            composable("login") {
                currentRoute.value = "login"
                LoginScreen(navController)
            }
            composable("hobies") {
                currentRoute.value = "hobies"
                HobiesScreen(navController, sharedViewModel = SharedViewModel())
            }
            composable("register") {
                currentRoute.value = "register"
                RegisterScreen(navController)
            }
            composable("forgotPassword") {
                currentRoute.value = "forgotPassword"
                ForgotPasswordScreen(navController)
            }

            composable("profile") {
                currentRoute.value = "profile"
                ProfileScreen(navController, sharedViewModel = SharedViewModel())
            }
            composable("explore") {
                currentRoute.value = "explore"
                ExploreScreen(navController)
            }
            composable("map") {
                currentRoute.value = "map"
                FinalLearningApp()
            }
            // Parametreli rota
            composable(
                route = "map/{placeId}",
                arguments = listOf(navArgument("placeId") {
                    type = NavType.StringType
                }) // `placeId` as parameter
            ) { backStackEntry ->
                currentRoute.value = "map"
                val placeId =
                    backStackEntry.arguments?.getString("placeId") // Retrieve the parameter

                FinalLearningApp(placeId = placeId) // Pass placeId to FinalLearningApp
            }
        }

            // Bottom Navigation Bar
        if (showBottomNav) {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
                    .height(72.dp)
                    .align(Alignment.BottomCenter),
                containerColor = MaterialTheme.colorScheme.surface
            )  {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                    // Simgeleri yatayda ortalar
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Profile",
                                modifier = Modifier.size(40.dp) // Varsayılan boyut
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
                                modifier = Modifier.size(40.dp) // Varsayılan boyut
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

