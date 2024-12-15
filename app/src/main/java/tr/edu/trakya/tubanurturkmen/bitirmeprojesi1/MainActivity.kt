package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.osmdroid.config.Configuration
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ui.navigation.AppNavigation
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ui.theme.BitirmeProjesi1Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = "tr.edu.trakya.tubanurturkmen.bitirmeprojesi1"
        // Log the current user agent value to the Logcat
        Log.d("UserAgent", "User Agent: ${Configuration.getInstance().userAgentValue}")
        // Harita bileşenini başlatıyoruz
        enableEdgeToEdge()
        setContent {
            BitirmeProjesi1Theme {
                AppNavigation() // Login ve Register ekranları arasında yönlendirme yapılacak
            }
        }
    }
}
