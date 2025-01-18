package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

        // Splash ekranını göstermek için bir layout ayarla
        setContentView(R.layout.activity_splash)
        Configuration.getInstance().setUserAgentValue("tr.edu.trakya.tubanurturkmen.bitirmeprojesi1")
        // Splash ekranından MainActivity'ye geçişi zamanla
        Handler(Looper.getMainLooper()).postDelayed({
            setTheme(R.style.Theme_BitirmeProjesi1) // Uygulama temasını ayarla
            setContentView(R.layout.activity_main) // MainActivity layout'u ayarla
            enableEdgeToEdge()
            setContent {
                BitirmeProjesi1Theme {
                    AppNavigation() // Login ve Register ekranları arasında yönlendirme yapılacak
                }
            }
        }, 4000) // 4 saniye bekleme süresi
    }
}
