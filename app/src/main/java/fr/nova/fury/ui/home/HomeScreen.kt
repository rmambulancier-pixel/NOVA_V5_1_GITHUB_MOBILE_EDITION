package fr.nova.fury.ui.home

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nova.fury.NovaEngine
import fr.nova.fury.NovaState
import fr.nova.fury.Screen
import fr.nova.fury.launchVoice
import fr.nova.fury.ui.components.AlphonseMicButton
import fr.nova.fury.ui.components.NovaCard
import fr.nova.fury.ui.components.NovaModuleCard
import fr.nova.fury.ui.components.NovaStat
import fr.nova.fury.ui.theme.NovaColors

@Composable
fun HomeScreen(
    state: NovaState,
    update: ((NovaState) -> NovaState) -> Unit,
    onGo: (Screen) -> Unit,
    activity: Activity,
    voiceLauncher: ActivityResultLauncher<Intent>
) {
    var voiceResult by remember { mutableStateOf("") }
    val health = NovaEngine.health(state)
    var isListening by remember { mutableStateOf(false) }

    LazyColumn(
        Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Header
            Column {
                Text("NOVA", style = MaterialTheme.typography.displaySmall)
                Text("Alphonse\nTon assistant. Tes projets. Ton avenir.", style = MaterialTheme.typography.bodyMedium)
            }
        }

        item {
            // Nova System Health (large card)
            NovaCard(Modifier.fillMaxWidth()) {
                Column {
                    Text("NOVA SYSTEM HEALTH", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("$health / 100", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(8.dp))
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = health / 100f,
                        modifier = Modifier.fillMaxWidth(),
                        trackColor = NovaColors.NightBlue,
                        color = when {
                            health >= 75 -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                            health >= 40 -> androidx.compose.ui.graphics.Color(0xFFFFC107)
                            else -> androidx.compose.ui.graphics.Color(0xFFF44336)
                        }
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("NOVA analyse actuellement tes priorités.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        item {
            // Alphonse Decide card with mic button to the right
            NovaCard(Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("ALPHONSE DÉCIDE", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(NovaEngine.nextAction(state), style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(6.dp))
                        // Use the health as a score placeholder
                        Text("Score ${health}/100", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.width(12.dp))
                    AlphonseMicButton(isListening = isListening, onClick = {
                        isListening = true
                        launchVoice(activity, voiceLauncher)
                    })
                }
            }
        }

        item { Text("ACCÈS RAPIDE", fontWeight = androidx.compose.ui.text.font.FontWeight.Black) }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NovaModuleCard("Priority Radar", Icons.Default.PieChart, "Opportunités & veille") { onGo(Screen.MISSIONS) }
                Spacer(Modifier.width(10.dp))
                NovaModuleCard("Business Hunter", Icons.Default.ShoppingBag, "Business & revenus") { onGo(Screen.BUSINESS) }
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NovaModuleCard("Watch Lab", Icons.Default.Watch, "Design & horlogerie") { onGo(Screen.WATCH) }
                Spacer(Modifier.width(10.dp))
                NovaModuleCard("Money OS", Icons.Default.AttachMoney, "Finance personnelle") { onGo(Screen.MONEY) }
            }
        }

        item {
            // Stats row (compact)
            NovaCard(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NovaStat("✓ 12", "Tâches terminées")
                    NovaStat("📅 3", "À venir aujourd'hui")
                    NovaStat("📊 7", "Projets en cours")
                    NovaStat("⭐ 1", "Objectif prioritaire")
                }
            }
        }

        item {
            // Motivation area
            NovaCard(Modifier.fillMaxWidth()) {
                Column {
                    Text("« Un objectif sans plan n'est qu'un souhait. »", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(6.dp))
                    Surface(shape = MaterialTheme.shapes.medium, tonalElevation = 4.dp) {
                        Box(Modifier.height(120.dp).fillMaxWidth()) { /* image placeholder */ }
                    }
                }
            }
        }
    }
}
