package com.example.decisionista.ui.decision

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionista.model.DecisionMethod // Import DecisionMethod model
import kotlinx.coroutines.delay
import kotlin.random.Random

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
                        MaterialTheme.colorScheme.secondary, // Center color
                        MaterialTheme.colorScheme.primary,   // Middle color
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) // Outer color (darker primary)
                    ),
                    radius = 800f // Adjust radius as needed
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
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    color = MaterialTheme.colorScheme.onPrimary, // Text on primary/secondary
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
                    text = "✨", // Sparkle emoji
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = animatedAlpha), // Use tertiary for sparkles with animated alpha
                    modifier = Modifier
                        .offset(randomOffset.x.dp, randomOffset.y.dp)
                        .alpha(animatedAlpha) // Still use alpha modifier for smooth fade
                )
            }
        }
    }
}
