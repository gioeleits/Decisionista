package com.example.decisionista
import com.example.decisionista.navigation.AppNavHost
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.decisionista.navigation.AppNavHost
import com.example.decisionista.ui.theme.DecisionistaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // gestisce meglio status bar / navigation bar
        setContent {
            DecisionistaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}

// ============================
// Preview funzionante
// ============================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainActivityPreview() {
    DecisionistaTheme {
        val navController = rememberNavController()
        AppNavHost(navController = navController)
    }
}
