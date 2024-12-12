package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController

import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ui.navigation.AppNavigation
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ui.theme.LearningMapTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView


class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = "tr.edu.trakya.tubanurturkmen.bitirmeprojesi1"
        // Log the current user agent value to the Logcat
        Log.d("UserAgent", "User Agent: ${Configuration.getInstance().userAgentValue}")
        // Harita bileşenini başlatıyoruz

        // UI'yı Composable olarak yapılandırıyoruz
        setContent {
            LearningMapTheme {
                // Navigasyonu yönet
                AppNavigation()
            }
        }
    }
}

