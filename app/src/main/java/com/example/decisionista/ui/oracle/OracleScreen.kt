package com.example.decisionista.ui.oracle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionista.model.SavedDecision
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OracleScreen(
    onBack: () -> Unit,
    onNavigateToResult: (SavedDecision) -> Unit,
    modifier: Modifier
) {
    var currentProphecy by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    val prophecies = listOf(
        "Le stelle sussurrano che un grande cambiamento è in arrivo",
        "La fortuna sorriderà a chi osa fare il primo passo",
        "Un incontro inaspettato porterà nuove opportunità",
        "La pazienza sarà la tua alleata più preziosa oggi",
        "Una decisione coraggiosa aprirà porte mai immaginate",
        "L\'energia positiva che emanai attrarrà ciò che desideri",
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
            delay(2000) // Simulate oracle thinking time
            isGenerating = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔮 Oracolo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background // Fade to background
                        ),
                        radius = 1200f // Adjust radius for desired spread
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
                val infiniteTransition = rememberInfiniteTransition()
                val animatedScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.1f, // Slightly reduced scale for subtlety
                    animationSpec = infiniteRepeatable(
                        animation = tween(2500), // Slightly slower animation
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
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic
                    ),
                    color = MaterialTheme.colorScheme.primary, // Use theme color
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                AnimatedVisibility(
                    visible = isGenerating,
                    enter = fadeIn(animationSpec = tween(500)),
                    exit = fadeOut(animationSpec = tween(500))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer // Use theme color
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "✨ L\'oracolo sta consultando il destino... ✨",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    textAlign = TextAlign.Center,
                                    fontStyle = FontStyle.Italic
                                ),
                                color = MaterialTheme.colorScheme.onSecondaryContainer // Use theme color
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = currentProphecy.isNotEmpty() && !isGenerating,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 200)), // Slight delay
                    exit = fadeOut(animationSpec = tween(500))
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer // Use theme color
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🌟 Profezia del Destino 🌟",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f), // Use theme color
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Text(
                                text = currentProphecy,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    textAlign = TextAlign.Center,
                                    fontStyle = FontStyle.Italic,
                                    lineHeight = 24.sp
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer // Use theme color
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = generateProphecy,
                    enabled = !isGenerating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary // Use theme color
                    )
                ) {
                    Text(
                        text = if (isGenerating) "Consultando..." else "✨ Nuova Profezia ✨",
                        color = MaterialTheme.colorScheme.onPrimary // Use theme color
                    )
                }

                Text(
                    text = "Puoi anche scuotere il telefono per attivare l\'oracolo!",
                    style = MaterialTheme.typography.bodySmall.copy(
                        textAlign = TextAlign.Center
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), // Use theme color
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
