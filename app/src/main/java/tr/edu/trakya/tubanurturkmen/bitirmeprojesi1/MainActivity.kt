package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ui.theme.BitirmeProjesi1Theme
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.screens.CitySelector
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.screens.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BitirmeProjesi1Theme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(navController = navController)
                    }
                    composable("citySelector") {
                        CitySelector()
                    }
                }
            }
        }
    }
}
