package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ui.navigation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon

import androidx.compose.material3.MaterialTheme
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // BottomNavigation için state tutmak
    val currentRoute = remember { mutableStateOf("login") }

    // Bottom Navigation Bar'ın hangi ekranlarda gizleneceği
    val hideBottomNavRoutes = listOf("login", "register", "hobies","forgotPassword")
    val showBottomNav = !hideBottomNavRoutes.contains(currentRoute.value)

    // Ekran içeriği ve yerleşim düzeni
    Box(modifier = Modifier.fillMaxSize()) {
        // NavHost
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomNav) 56.dp else 0.dp) // BottomNavigationBar için padding ekleniyor
        ) {
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
            composable("home") {
                currentRoute.value = "home"
                HomeScreen(navController)
            }
            composable("forgotPassword") {
                currentRoute.value = "forgotPassword"
                ForgotPasswordScreen(navController)
            }
            composable("explore") {
                currentRoute.value = "explore"
                ExploreScreen(navController)
            }
            composable("profile") {
                currentRoute.value = "profile"
                ProfileScreen(navController, sharedViewModel = SharedViewModel())
            }
        }

        // Bottom Navigation Bar
        if (showBottomNav) {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(4.dp),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(24.dp) // Varsayılan boyut
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
                            modifier = Modifier.size(24.dp) // Varsayılan boyut
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
            }
        }
    }
}