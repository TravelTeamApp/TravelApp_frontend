package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.LoginScreen
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.RegisterScreen
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.HomeScreen
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ForgotPasswordScreen
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.Hobies

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        composable("hobies") {
            Hobies(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("home") { HomeScreen(navController) } // Home ekranını ekledik
        composable("forgotPassword") {
            ForgotPasswordScreen(navController) // Add this route for ForgotPasswordScreen
        }
    }
}
