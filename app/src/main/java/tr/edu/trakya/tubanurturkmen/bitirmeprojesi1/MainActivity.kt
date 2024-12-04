package tr.edu.trakya.tubanurturkmen.bitirmeprojesi1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import tr.edu.trakya.tubanurturkmen.bitirmeprojesi1.ui.theme.BitirmeProjesi1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BitirmeProjesi1Theme {
                ExploreScreen() // Login ve Register ekranları arasında yönlendirme yapılacak
            }
        }
    }
}

