package com.example.decisionista.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SplashScreen(onComplete: () -> Unit) {
    var isAnimating by remember { mutableStateOf(true) }
    // val transition = rememberInfiniteTransition() // For sparkles that don't depend on isAnimating

    LaunchedEffect(Unit) {
        delay(3000) // Duration of splash screen animations
        isAnimating = false
        delay(500) // Duration for fade out before completing
        onComplete()
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
                    radius = 1000f // Adjust radius for desired spread
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
                enter = scaleIn(animationSpec = tween(1000)) + fadeIn(animationSpec = tween(1000)),
                exit = scaleOut(animationSpec = tween(500)) + fadeOut(animationSpec = tween(500))
            ) {
                Text(
                    text = "🔮",
                    fontSize = 80.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            AnimatedVisibility(
                visible = isAnimating,
                enter = fadeIn(animationSpec = tween(1000, delayMillis = 500)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "DECISIONISTA",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimary // Use theme color
                    )
                    Text(
                        text = "Il Mago delle Decisioni",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontStyle = FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), // Use theme color
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        // Sparkles animation - using infinite transition for continuous effect while splash is visible
        val infiniteTransition = rememberInfiniteTransition()
        if (isAnimating) { // Only show sparkles during the initial animation phase
            repeat(20) { index ->
                val randomX = remember { (Random.nextFloat() - 0.5f) * 2 }
                val randomY = remember { (Random.nextFloat() - 0.5f) * 2 }
                val sparkleAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000 + Random.nextInt(0, 500) + index * 100),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Text(
                    text = "✨",
                    fontSize = (10 + Random.nextInt(0, 10)).sp,
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = sparkleAlpha),
                    modifier = Modifier
                        .fillMaxSize() // Fill the Box
                        .wrapContentSize(Alignment.Center) // Center the sparkle text itself
                        .offset(
                            x = (randomX * 150).dp, // Spread sparkles across a certain range
                            y = (randomY * 300).dp
                        )
                )
            }
        }
    }
}
