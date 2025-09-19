// MainActivity.kt
package com.example.decisionista

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.decisionista.model.DecisionMethod
import com.example.decisionista.model.SavedDecision
import com.example.decisionista.model.Screen
import com.example.decisionista.ui.auth.LoginScreen
import com.example.decisionista.ui.auth.RegisterScreen
import com.example.decisionista.ui.decision.MethodSelectionScreen
import com.example.decisionista.ui.decision.OptionsInputScreen
import com.example.decisionista.ui.decision.ResultScreen
import com.example.decisionista.ui.decision.RitualScreen
import com.example.decisionista.ui.glimmerio.GlimmerioScreen
import com.example.decisionista.ui.home.HomeScreen
import com.example.decisionista.ui.oracle.OracleScreen
import com.example.decisionista.ui.profile.ProfileScreen
import com.example.decisionista.ui.splash.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DecisionistaTheme {
                DecisionistaApp()
            }
        }
    }
}

@Composable
fun AppNavigationBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val items = listOf(
        Screen.HOME,
        Screen.GLIMMERIO,
        Screen.ORACLE,
        Screen.PROFILE
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        items.forEach { screen ->
            val selected = currentScreen == screen
            val icon = when (screen) {
                Screen.HOME -> Icons.Filled.Home
                Screen.GLIMMERIO -> Icons.Filled.Star
                Screen.ORACLE -> Icons.Filled.Favorite
                Screen.PROFILE -> Icons.Filled.Person
                else -> Icons.Filled.Home // Should not happen for these items
            }
            val label = when (screen) {
                Screen.HOME -> "Home"
                Screen.GLIMMERIO -> "Glimmerio"
                Screen.ORACLE -> "Oracolo"
                Screen.PROFILE -> "Profilo"
                else -> ""
            }

            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = selected,
                onClick = { onNavigate(screen) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecisionistaApp() {
    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
    var userEmail by remember { mutableStateOf("") }
    var decisions by remember { mutableStateOf(listOf<SavedDecision>()) }
    var currentOptions by remember { mutableStateOf(listOf<String>()) }
    var selectedMethod by remember { mutableStateOf(DecisionMethod.RANDOM) }
    var currentDecisionResult by remember { mutableStateOf<String?>(null) }

    val mainNavScreens = listOf(Screen.HOME, Screen.GLIMMERIO, Screen.ORACLE, Screen.PROFILE)
    val showNavBar = currentScreen in mainNavScreens

    if (showNavBar) {
        Scaffold(
            bottomBar = {
                AppNavigationBar(
                    currentScreen = currentScreen,
                    onNavigate = { newScreen -> currentScreen = newScreen }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            // Pass innerPadding to the main screens so their own Scaffold's content respects it
            MainScreensContainer(
                currentScreen = currentScreen,
                contentPadding = innerPadding,
                userEmail = userEmail,
                decisions = decisions,
                onStartDecision = {
                    currentOptions = listOf()
                    selectedMethod = DecisionMethod.RANDOM
                    currentDecisionResult = null
                    currentScreen = Screen.OPTIONS_INPUT
                },
                onNavigateToResult = { decision -> // For Glimmerio to Result
                    currentOptions = decision.options
                    selectedMethod = decision.method
                    currentDecisionResult = decision.result
                    currentScreen = Screen.RESULT
                },
                onDeleteDecision = { decisionToDelete ->
                     decisions = decisions.filter { it.id != decisionToDelete.id }
                },
                onLogout = {
                    userEmail = ""
                    currentOptions = listOf()
                    selectedMethod = DecisionMethod.RANDOM
                    currentDecisionResult = null
                    decisions = listOf()
                    currentScreen = Screen.LOGIN
                },
                onBackFromSubScreen = { currentScreen = Screen.HOME } // Generic back for Glimmerio, Oracle, Profile
            )
        }
    } else {
        // Screens that take the full screen (Splash, Login, Decision Flow)
        NonMainScreensContainer(
            currentScreen = currentScreen,
            userEmail = userEmail,
            decisions = decisions,
            currentOptions = currentOptions,
            selectedMethod = selectedMethod,
            currentDecisionResult = currentDecisionResult,
            onUserEmailChange = { userEmail = it },
            onDecisionsChange = { decisions = it },
            onCurrentOptionsChange = { currentOptions = it },
            onSelectedMethodChange = { selectedMethod = it },
            onCurrentDecisionResultChange = { currentDecisionResult = it },
            onNavigate = { newScreen -> currentScreen = newScreen },
            onLoginSuccess = { email ->
                userEmail = email
                currentScreen = Screen.HOME
            },
            onRegisterSuccess = { email ->
                userEmail = email
                currentScreen = Screen.HOME
            },
             onGuestLogin = {
                userEmail = "Ospite"
                currentScreen = Screen.HOME
            }
        )
    }
}

@Composable
fun MainScreensContainer(
    currentScreen: Screen,
    contentPadding: PaddingValues, // Padding from the Scaffold with Nav Bar
    userEmail: String,
    decisions: List<SavedDecision>,
    onStartDecision: () -> Unit,
    onNavigateToResult: (SavedDecision) -> Unit,
    onDeleteDecision: (SavedDecision) -> Unit,
    onLogout: () -> Unit,
    onBackFromSubScreen: () -> Unit // Used by Glimmerio, Oracle, Profile to go "back" (which is Home)
) {
    // These screens will apply contentPadding to their own Scaffold's content area
    // For now, we assume they will manage their own TopAppBars.
    // Their `modifier` will be Modifier.fillMaxSize() to fill the area given by the outer Scaffold.
    // The `contentPadding` is for the *content* of their *own* Scaffolds.

    when (currentScreen) {
        Screen.HOME -> HomeScreen(
            // HomeScreen's own Scaffold will use this padding for its content
            // Modifier.fillMaxSize() will be applied to HomeScreen's Scaffold.
            // This requires HomeScreen to be refactored to accept contentPadding.
            modifier = Modifier.padding(contentPadding), //This modifier will be applied to the HomeScreen's Scaffold
            onStartDecision = onStartDecision
            // onNavigate from HomeScreen is now handled by AppNavigationBar
        )
        Screen.GLIMMERIO -> GlimmerioScreen(
            modifier = Modifier.padding(contentPadding),
            decisions = decisions,
            onDecisionSelect = onNavigateToResult,
            onDelete = onDeleteDecision,
            onBack = onBackFromSubScreen
        ) // GlimmerioScreen manages its own TopAppBar and Scaffold
        Screen.ORACLE -> OracleScreen(
            modifier = Modifier.padding(contentPadding),
            onBack = onBackFromSubScreen,
            onNavigateToResult = onNavigateToResult // Assuming Oracle might also navigate to a result-like screen or use SavedDecision
        ) // OracleScreen manages its own TopAppBar and Scaffold
        Screen.PROFILE -> ProfileScreen(
            modifier = Modifier.padding(contentPadding),
            userEmail = userEmail,
            onLogout = onLogout,
            onBack = onBackFromSubScreen,
            decisions = decisions,
            onNavigateToResult = onNavigateToResult // ProfileScreen might show a summary or allow revisiting decisions
        ) // ProfileScreen manages its own TopAppBar and Scaffold
        else -> { /* Should not happen as currentScreen is checked to be in mainNavScreens */ }
    }
}

@Composable
fun NonMainScreensContainer(
    currentScreen: Screen,
    userEmail: String,
    decisions: List<SavedDecision>,
    currentOptions: List<String>,
    selectedMethod: DecisionMethod,
    currentDecisionResult: String?,
    onUserEmailChange: (String) -> Unit,
    onDecisionsChange: (List<SavedDecision>) -> Unit,
    onCurrentOptionsChange: (List<String>) -> Unit,
    onSelectedMethodChange: (DecisionMethod) -> Unit,
    onCurrentDecisionResultChange: (String?) -> Unit,
    onNavigate: (Screen) -> Unit,
    onLoginSuccess: (String) -> Unit,
    onRegisterSuccess: (String) -> Unit,
    onGuestLogin: () -> Unit
) {
    when (currentScreen) {
        Screen.SPLASH -> SplashScreen {
            onNavigate(if (userEmail.isNotBlank()) Screen.HOME else Screen.LOGIN)
        }
        Screen.LOGIN -> LoginScreen(
            onLogin = onLoginSuccess,
            onRegister = { onNavigate(Screen.REGISTER) },
            onGuest = onGuestLogin
        )
        Screen.REGISTER -> RegisterScreen(
            onRegister = onRegisterSuccess,
            onBack = { onNavigate(Screen.LOGIN) }
        )
        Screen.OPTIONS_INPUT -> OptionsInputScreen(
            options = currentOptions,
            onOptionsChange = onCurrentOptionsChange,
            onNext = { onNavigate(Screen.METHOD_SELECTION) },
            onBack = { onNavigate(Screen.HOME) }
        )
        Screen.METHOD_SELECTION -> MethodSelectionScreen(
            selectedMethod = selectedMethod,
            onMethodSelect = onSelectedMethodChange,
            onLaunch = { onNavigate(Screen.RITUAL) },
            onBack = { onNavigate(Screen.OPTIONS_INPUT) }
        )
        Screen.RITUAL -> RitualScreen(
            method = selectedMethod,
            options = currentOptions,
            onComplete = { ritualResult ->
                onCurrentDecisionResultChange(ritualResult)
                onNavigate(Screen.RESULT)
            }
        )
        Screen.RESULT -> ResultScreen(
            result = currentDecisionResult ?: "Errore: Nessun risultato deciso",
            method = selectedMethod,
            onRetry = {
                onNavigate(Screen.RITUAL)
            },
            onSave = { resultToSave ->
                val decision = SavedDecision(
                    id = decisions.size + 1, // Basic increment, consider UUID for robustness
                    options = currentOptions,
                    result = resultToSave,
                    method = selectedMethod,
                    timestamp = System.currentTimeMillis()
                )
                onDecisionsChange(decisions + decision)
            },
            onHome = {
                onNavigate(Screen.HOME)
            }
        )
        else -> { /* Should not happen */ }
    }
}


@Composable
fun DecisionistaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF3F51B5),      // Indigo 500
            onPrimary = Color.White,
            secondary = Color(0xFF673AB7),    // Deep Purple 500
            onSecondary = Color.White,
            tertiary = Color(0xFFFFC107),     // Amber 500
            onTertiary = Color.Black,
            background = Color(0xFFFDFBFF),
            onBackground = Color(0xFF1B1B1F),
            surface = Color(0xFFFDFBFF),
            onSurface = Color(0xFF1B1B1F),
            surfaceVariant = Color(0xFFE0E0FC),
            onSurfaceVariant = Color(0xFF444458),
            primaryContainer = Color(0xFFC5CAE9), // Indigo 100 - For Nav Bar Background
            onPrimaryContainer = Color(0xFF1A237E), // Indigo 900 - For Nav Bar selected item
            secondaryContainer = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f) // For Nav Bar indicator
        ),
        content = content
    )
}

