package com.example.decisionista.ui.decision

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star // Assuming this was for Save, changed to Save icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionista.model.DecisionMethod // Import DecisionMethod model

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
                title = { Text("✨ Decisione Magica ✨") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background) // Use theme background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(animationSpec = tween(1000)) + fadeIn()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer // Use theme color
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = method.emoji, // Display method emoji
                                fontSize = 60.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Text(
                                text = "Il Mago ha Deciso:",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f), // Use theme color
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = result,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer // Use theme color
                            )
                        }
                    }
                }

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
                                containerColor = MaterialTheme.colorScheme.secondary // Use theme color
                            )
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Riprova")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Riprova", color = MaterialTheme.colorScheme.onSecondary) // Use theme color
                        }

                        Button(
                            onClick = { onSave(result) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary // Use theme color
                            )
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = "Salva") // Changed to Save icon
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Salva", color = MaterialTheme.colorScheme.onPrimary) // Use theme color
                        }
                    }

                    OutlinedButton(
                        onClick = { showWhyDialog = true },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary) // Use theme color
                    ) {
                        Text("🔮 Perché questa scelta?", color = MaterialTheme.colorScheme.primary) // Use theme color
                    }

                    TextButton(onClick = onHome) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Torna alla Home", color = MaterialTheme.colorScheme.primary) // Use theme color
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
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary // Use theme color
                )
            },
            text = {
                Text(
                    text = magicalReasons.random(),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontStyle = FontStyle.Italic
                    ),
                    color = MaterialTheme.colorScheme.onSurface // Use theme color
                )
            },
            confirmButton = {
                Button(
                    onClick = { showWhyDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary // Use theme color
                    )
                ) {
                    Text("Capisco", color = MaterialTheme.colorScheme.onPrimary) // Use theme color
                }
            },
            dismissButton = {
                TextButton(onClick = { showWhyDialog = false }) {
                    Text("Annulla", color = MaterialTheme.colorScheme.primary) // Use theme color
                }
            }
        )
    }
}
