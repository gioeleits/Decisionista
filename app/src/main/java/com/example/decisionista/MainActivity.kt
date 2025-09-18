// MainActivity.kt
package com.example.decisionista

import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlin.random.Random

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecisionistaApp() {
    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
    var userEmail by remember { mutableStateOf("") }
    var isLoggedIn by remember { mutableStateOf(false) }
    var decisions by remember { mutableStateOf(listOf<SavedDecision>()) }
    var currentOptions by remember { mutableStateOf(listOf<String>()) }
    var selectedMethod by remember { mutableStateOf(DecisionMethod.RANDOM) }

    when (currentScreen) {
        Screen.SPLASH -> SplashScreen { currentScreen = Screen.LOGIN }
        Screen.LOGIN -> LoginScreen(
            onLogin = { email ->
                userEmail = email
                isLoggedIn = true
                currentScreen = Screen.HOME
            },
            onRegister = { currentScreen = Screen.REGISTER },
            onGuest = {
                isLoggedIn = true
                currentScreen = Screen.HOME
            }
        )
        Screen.REGISTER -> RegisterScreen(
            onRegister = { email ->
                userEmail = email
                isLoggedIn = true
                currentScreen = Screen.HOME
            },
            onBack = { currentScreen = Screen.LOGIN }
        )
        Screen.HOME -> HomeScreen(
            onStartDecision = { currentScreen = Screen.OPTIONS_INPUT },
            onNavigate = { screen -> currentScreen = screen }
        )
        Screen.OPTIONS_INPUT -> OptionsInputScreen(
            options = currentOptions,
            onOptionsChange = { currentOptions = it },
            onNext = { currentScreen = Screen.METHOD_SELECTION },
            onBack = { currentScreen = Screen.HOME }
        )
        Screen.METHOD_SELECTION -> MethodSelectionScreen(
            selectedMethod = selectedMethod,
            onMethodSelect = { selectedMethod = it },
            onLaunch = { currentScreen = Screen.RITUAL },
            onBack = { currentScreen = Screen.OPTIONS_INPUT }
        )
        Screen.RITUAL -> RitualScreen(
            method = selectedMethod,
            options = currentOptions,
            onComplete = { result ->
                val decision = SavedDecision(
                    id = decisions.size,
                    options = currentOptions,
                    result = result,
                    method = selectedMethod,
                    timestamp = System.currentTimeMillis()
                )
                currentScreen = Screen.RESULT
            }
        )
        Screen.RESULT -> ResultScreen(
            result = currentOptions.randomOrNull() ?: "",
            method = selectedMethod,
            onRetry = { currentScreen = Screen.RITUAL },
            onSave = { result ->
                val decision = SavedDecision(
                    id = decisions.size,
                    options = currentOptions,
                    result = result,
                    method = selectedMethod,
                    timestamp = System.currentTimeMillis()
                )
                decisions = decisions + decision
            },
            onHome = { currentScreen = Screen.HOME }
        )
        Screen.GLIMMERIO -> GlimmerioScreen(
            decisions = decisions,
            onDecisionSelect = { decision ->
                currentOptions = decision.options
                selectedMethod = decision.method
                currentScreen = Screen.METHOD_SELECTION
            },
            onDelete = { decision ->
                decisions = decisions.filter { it.id != decision.id }
            },
            onBack = { currentScreen = Screen.HOME }
        )
        Screen.ORACLE -> OracleScreen(
            onBack = { currentScreen = Screen.HOME }
        )
        Screen.PROFILE -> ProfileScreen(
            userEmail = userEmail,
            onLogout = {
                isLoggedIn = false
                currentScreen = Screen.LOGIN
            },
            onBack = { currentScreen = Screen.HOME }
        )
    }
}

@Composable
fun SplashScreen(onComplete: () -> Unit) {
    var isAnimating by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(3000)
        isAnimating = false
        delay(500)
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4A148C),
                        Color(0xFF1A237E),
                        Color(0xFF000051)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = isAnimating,
                enter = scaleIn(animationSpec = tween(1000)) + fadeIn(),
                exit = scaleOut(animationSpec = tween(500)) + fadeOut()
            ) {
                Text(
                    text = "🔮",
                    fontSize = 80.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            AnimatedVisibility(
                visible = isAnimating,

            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "DECISIONISTA",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 2.sp
                        )
                    )
                    Text(
                        text = "Il Mago delle Decisioni",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontStyle = FontStyle.Italic
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        // Sparkles animation
        repeat(20) { index ->
            val randomX = remember { Random.nextFloat() }
            val randomY = remember { Random.nextFloat() }
            val animatedAlpha by animateFloatAsState(
                targetValue = if (isAnimating) 1f else 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000 + index * 100),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Text(
                text = "✨",
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
                    .offset(
                        x = (randomX * 300).dp - 150.dp,
                        y = (randomY * 600).dp - 300.dp
                    )
                    .alpha(animatedAlpha)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLogin: (String) -> Unit,
    onRegister: () -> Unit,
    onGuest: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4A148C),
                        Color(0xFF1A237E)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🔮",
                    fontSize = 60.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Bentornato, Decisore!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A148C)
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email o Nickname") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password Magica") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                Button(
                    onClick = { onLogin(email) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A148C)
                    )
                ) {
                    Text("Entra nel Regno Magico", color = Color.White)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onRegister) {
                        Text("Registrati", color = Color(0xFF4A148C))
                    }

                    TextButton(onClick = onGuest) {
                        Text("Accesso Rapido", color = Color(0xFF4A148C))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegister: (String) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4A148C),
                        Color(0xFF1A237E)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🌟",
                    fontSize = 60.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Diventa un Decisore",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A148C)
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome Magico") },
                    leadingIcon = { Icon(Icons.Default.Face, null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password Magica") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                Button(
                    onClick = { onRegister(email) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A148C)
                    )
                ) {
                    Text("Inizia l'Avventura", color = Color.White)
                }

                TextButton(onClick = onBack) {
                    Text("← Torna al Login", color = Color(0xFF4A148C))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStartDecision: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🔮 ",
                            fontSize = 24.sp
                        )
                        Text(
                            "DECISIONISTA",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4A148C),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF4A148C)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, null, tint = Color.White) },
                    label = { Text("Home", color = Color.White) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        onNavigate(Screen.GLIMMERIO)
                    },
                    icon = { Icon(Icons.Default.Star, null, tint = Color.White) },
                    label = { Text("Glimmerio", color = Color.White) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        onNavigate(Screen.ORACLE)
                    },
                    icon = { Icon(Icons.Default.Favorite, null, tint = Color.White) },
                    label = { Text("Oracolo", color = Color.White) }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = {
                        selectedTab = 3
                        onNavigate(Screen.PROFILE)
                    },
                    icon = { Icon(Icons.Default.Person, null, tint = Color.White) },
                    label = { Text("Profilo", color = Color.White) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4A148C).copy(alpha = 0.1f),
                            Color(0xFF1A237E).copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Animated crystal ball
                val infiniteTransition = rememberInfiniteTransition()
                val animatedScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Text(
                    text = "🔮",
                    fontSize = 120.sp,
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .scale(animatedScale)
                )

                Text(
                    text = "Benvenuto nel Regno delle Decisioni",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A148C),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Il tuo mago personale ti aiuterà a scegliere\ncon saggezza e un pizzico di magia",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center,
                        color = Color(0xFF666666)
                    ),
                    modifier = Modifier.padding(bottom = 48.dp)
                )

                Button(
                    onClick = onStartDecision,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A148C)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "✨ ",
                            fontSize = 18.sp
                        )
                        Text(
                            "Inizia una Decisione",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = " ✨",
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsInputScreen(
    options: List<String>,
    onOptionsChange: (List<String>) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var newOption by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inserisci le Opzioni", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4A148C)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🌟 Aggiungi le tue opzioni",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A148C)
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newOption,
                            onValueChange = { newOption = it },
                            label = { Text("Nuova opzione") },
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                if (newOption.isNotBlank()) {
                                    onOptionsChange(options + newOption.trim())
                                    newOption = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4A148C)
                            )
                        ) {
                            Text("Aggiungi", color = Color.White)
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(options) { index, option ->
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A148C)
                                ),
                                modifier = Modifier.padding(end = 12.dp)
                            )

                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = {
                                    onOptionsChange(options.filterIndexed { i, _ -> i != index })
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Elimina",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onNext,
                enabled = options.size >= 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A148C)
                )
            ) {
                Text("Avanti - Scegli il Metodo", color = Color.White)
            }

            if (options.size < 2) {
                Text(
                    text = "Servono almeno 2 opzioni per decidere!",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MethodSelectionScreen(
    selectedMethod: DecisionMethod,
    onMethodSelect: (DecisionMethod) -> Unit,
    onLaunch: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scegli il Metodo Magico", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4A148C)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "🪄 Come vuoi che il mago decida?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A148C)
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(DecisionMethod.values()) { method ->
                    MethodCard(
                        method = method,
                        isSelected = selectedMethod == method,
                        onClick = { onMethodSelect(method) }
                    )
                }
            }

            Button(
                onClick = onLaunch,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A148C)
                )
            ) {
                Text(
                    text = "✨ Lancia la Decisione Magica ✨",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun MethodCard(
    method: DecisionMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                Color(0xFF4A148C).copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            BorderStroke(2.dp, Color(0xFF4A148C))
        else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = method.emoji,
                fontSize = 32.sp,
                modifier = Modifier.padding(end = 16.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = method.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFF4A148C) else Color.Black
                    )
                )
                Text(
                    text = method.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4A148C),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun RitualScreen(
    method: DecisionMethod,
    options: List<String>,
    onComplete: (String) -> Unit
) {
    var phase by remember { mutableStateOf(0) }
    val phrases = listOf(
        "🌟 Il mago sta consultando le stelle...",
        "🔮 La sfera magica si illumina...",
        "✨ Le energie si stanno allineando...",
        "🪄 Il destino sta per rivelarsi..."
    )

    LaunchedEffect(Unit) {
        for (i in 0..3) {
            phase = i
            delay(1500)
        }
        delay(1000)
        val result = options.random()
        onComplete(result)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4A148C),
                        Color(0xFF1A237E),
                        Color(0xFF000051)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated method emoji
            val infiniteTransition = rememberInfiniteTransition()
            val animatedRotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = LinearEasing)
                )
            )

            Text(
                text = method.emoji,
                fontSize = 100.sp,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .rotate(animatedRotation)
            )

            AnimatedVisibility(
                visible = phase < 4,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = if (phase < phrases.size) phrases[phase] else "🎯 La decisione è presa!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            // Sparkles around
            repeat(15) { index ->
                val randomOffset = remember {
                    Offset(
                        (Random.nextFloat() - 0.5f) * 600,
                        (Random.nextFloat() - 0.5f) * 600
                    )
                }

                val animatedAlpha by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800 + index * 200),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Text(
                    text = "✨",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .offset(randomOffset.x.dp, randomOffset.y.dp)
                        .alpha(animatedAlpha)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    result: String,
    method: DecisionMethod,
    onRetry: () -> Unit,
    onSave: (String) -> Unit,
    onHome: () -> Unit
) {
    var showWhyDialog by remember { mutableStateOf(false) }
    val magicalReasons = listOf(
        "Le stelle si sono allineate in modo perfetto per questa scelta",
        "L'energia cosmica ha guidato la decisione verso questa opzione",
        "Il vento del destino ha soffiato in questa direzione",
        "Gli spiriti saggi hanno sussurrato questo nome",
        "La magia antica ha rivelato questa verità nascosta",
        "Il cristallo della saggezza ha brillato per questa scelta"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("✨ Decisione Magica ✨", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4A148C)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4A148C).copy(alpha = 0.1f),
                            Color(0xFF1A237E).copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Animated result reveal
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(animationSpec = tween(1000)) + fadeIn()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4A148C)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎯",
                                fontSize = 60.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Text(
                                text = "Il Mago ha Deciso:",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = result,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }

                // Action buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A237E)
                            )
                        ) {
                            Icon(Icons.Default.Refresh, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Riprova", color = Color.White)
                        }

                        Button(
                            onClick = { onSave(result) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4A148C)
                            )
                        ) {
                            Icon(Icons.Default.Star, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Salva", color = Color.White)
                        }
                    }

                    OutlinedButton(
                        onClick = { showWhyDialog = true },
                        border = BorderStroke(1.dp, Color(0xFF4A148C))
                    ) {
                        Text("🔮 Perché questa scelta?", color = Color(0xFF4A148C))
                    }

                    TextButton(onClick = onHome) {
                        Text("← Torna alla Home", color = Color(0xFF4A148C))
                    }
                }
            }
        }
    }

    if (showWhyDialog) {
        AlertDialog(
            onDismissRequest = { showWhyDialog = false },
            title = {
                Text(
                    text = "🔮 Saggezza del Mago",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A148C)
                    )
                )
            },
            text = {
                Text(
                    text = magicalReasons.random(),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontStyle = FontStyle.Italic
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = { showWhyDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A148C)
                    )
                ) {
                    Text("Capisco", color = Color.White)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlimmerioScreen(
    decisions: List<SavedDecision>,
    onDecisionSelect: (SavedDecision) -> Unit,
    onDelete: (SavedDecision) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🌟 Glimmerio", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4A148C)
                )
            )
        }
    ) { paddingValues ->
        if (decisions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "📜",
                        fontSize = 80.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Il Glimmerio è vuoto",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A148C)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Inizia a prendere decisioni per riempirlo!",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(decisions.reversed()) { decision ->
                    DecisionCard(
                        decision = decision,
                        onClick = { onDecisionSelect(decision) },
                        onDelete = { onDelete(decision) }
                    )
                }
            }
        }
    }
}

@Composable
fun DecisionCard(
    decision: SavedDecision,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = decision.method.emoji,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = decision.method.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A148C)
                            )
                        )
                    }

                    Text(
                        text = "Risultato: ${decision.result}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Opzioni: ${decision.options.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        ),
                        maxLines = 2
                    )
                }

                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Elimina",
                        tint = Color.Red
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Elimina Decisione") },
            text = { Text("Sei sicuro di voler eliminare questa decisione dal Glimmerio?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Elimina", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annulla")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OracleScreen(onBack: () -> Unit) {
    var currentProphecy by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    val prophecies = listOf(
        "Le stelle sussurrano che un grande cambiamento è in arrivo",
        "La fortuna sorriderà a chi osa fare il primo passo",
        "Un incontro inaspettato porterà nuove opportunità",
        "La pazienza sarà la tua alleata più preziosa oggi",
        "Una decisione coraggiosa aprirà porte mai immaginate",
        "L'energia positiva che emanai attrarrà ciò che desideri",
        "Un piccolo gesto di gentilezza avrà grandi conseguenze",
        "La risposta che cerchi si trova più vicina di quanto pensi",
        "Il destino ha in serbo per te una sorpresa meravigliosa",
        "La tua intuizione ti guiderà verso la scelta giusta"
    )

    val generateProphecy = {
        isGenerating = true
        currentProphecy = prophecies.random()
    }

    LaunchedEffect(isGenerating) {
        if (isGenerating) {
            delay(2000)
            isGenerating = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔮 Oracolo", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4A148C)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF4A148C).copy(alpha = 0.2f),
                            Color(0xFF1A237E).copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Animated crystal ball
                val infiniteTransition = rememberInfiniteTransition()
                val animatedScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3000),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Text(
                    text = "🔮",
                    fontSize = 120.sp,
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .scale(if (isGenerating) animatedScale else 1f)
                        .clickable { generateProphecy() }
                )

                Text(
                    text = "Tocca la sfera per una profezia del destino",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF4A148C),
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic
                    ),
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                AnimatedVisibility(
                    visible = isGenerating,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4A148C)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "✨ L'oracolo sta consultando il destino... ✨",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontStyle = FontStyle.Italic
                                )
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = currentProphecy.isNotEmpty() && !isGenerating,

                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4A148C)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🌟 Profezia del Destino 🌟",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Text(
                                text = currentProphecy,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontStyle = FontStyle.Italic,
                                    lineHeight = 24.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = generateProphecy,
                    enabled = !isGenerating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A148C)
                    )
                ) {
                    Text(
                        text = if (isGenerating) "Consultando..." else "✨ Nuova Profezia ✨",
                        color = Color.White
                    )
                }

                Text(
                    text = "Puoi anche scuotere il telefono per attivare l'oracolo!",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userEmail: String,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isDarkMode by remember { mutableStateOf(false) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("👤 Profilo", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4A148C)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Profile card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🧙‍♂️",
                        fontSize = 60.sp,
                        modifier = Modifier.padding(end = 20.dp)
                    )

                    Column {
                        Text(
                            text = "Benvenuto, Decisore!",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A148C)
                            )
                        )
                        if (userEmail.isNotEmpty()) {
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Settings
            Text(
                text = "⚙️ Impostazioni",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A148C)
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SettingsItem(
                icon = "🎨",
                title = "Modalità Scura",
                subtitle = "Attiva il tema scuro",
                isSwitch = true,
                switchValue = isDarkMode,
                onSwitchChange = { isDarkMode = it }
            )

            SettingsItem(
                icon = "🔊",
                title = "Suoni",
                subtitle = "Effetti sonori magici",
                isSwitch = true,
                switchValue = soundEnabled,
                onSwitchChange = { soundEnabled = it }
            )

            SettingsItem(
                icon = "📳",
                title = "Vibrazioni",
                subtitle = "Feedback tattile",
                isSwitch = true,
                switchValue = vibrationsEnabled,
                onSwitchChange = { vibrationsEnabled = it }
            )

            SettingsItem(
                icon = "🧙‍♂️",
                title = "Personalizza Mago",
                subtitle = "Cambia aspetto del tuo mago",
                onClick = { /* TODO: Implementare personalizzazione */ }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Icon(Icons.Default.ExitToApp, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", color = Color.White)
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Sei sicuro di voler uscire dal regno magico?") },
            confirmButton = {
                Button(
                    onClick = {
                        onLogout()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Logout", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Annulla")
                }
            }
        )
    }
}

@Composable
fun SettingsItem(
    icon: String,
    title: String,
    subtitle: String,
    isSwitch: Boolean = false,
    switchValue: Boolean = false,
    onSwitchChange: (Boolean) -> Unit = {},
    onClick: () -> Unit = {}
) {
    Card(
        onClick = if (!isSwitch) onClick else { {} },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 16.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    )
                )
            }

            if (isSwitch) {
                Switch(
                    checked = switchValue,
                    onCheckedChange = onSwitchChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF4A148C),
                        checkedTrackColor = Color(0xFF4A148C).copy(alpha = 0.5f)
                    )
                )
            } else {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }
    }
}

// Data classes e enum
enum class Screen {
    SPLASH, LOGIN, REGISTER, HOME, OPTIONS_INPUT,
    METHOD_SELECTION, RITUAL, RESULT, GLIMMERIO, ORACLE, PROFILE
}

enum class DecisionMethod(
    val title: String,
    val description: String,
    val emoji: String
) {
    RANDOM("Scelta Casuale", "Il mago lancia i dadi del destino", "🎲"),
    ELIMINATION("Eliminazione", "Elimina opzioni una alla volta", "⚡"),
    DUEL("Duello", "Le opzioni si sfidano a coppie", "⚔️"),
    WHEEL("Ruota Magica", "La ruota del destino decide", "🎡"),
    WEIGHTED("Scelta Ponderata", "Considera l'importanza di ogni opzione", "⚖️")
}

data class SavedDecision(
    val id: Int,
    val options: List<String>,
    val result: String,
    val method: DecisionMethod,
    val timestamp: Long
)

@Composable
fun DecisionistaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF4A148C),
            secondary = Color(0xFF1A237E),
            tertiary = Color(0xFF6A1B9A)
        ),
        content = content
    )
}