package fr.nova.fury.ui.home

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nova.fury.*
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
            Column {
                Text("NOVA", style = MaterialTheme.typography.displaySmall)
                Text("Alphonse\nTon assistant. Tes projets. Ton avenir.", style = MaterialTheme.typography.bodyMedium)
            }
        }

        item {
            NovaCard(Modifier.fillMaxWidth()) {
                Column {
                    Text("NOVA SYSTEM HEALTH", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("$health / 100", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { health / 100f },
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
                        Text("Score $health/100", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.width(12.dp))
                    AlphonseMicButton(
                        isListening = isListening,
                        onClick = {
                            isListening = true
                            launchVoice(voiceLauncher) { isListening = false }
                        }
                    )
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
            val done = state.missions.count { it.done }
            val open = state.missions.count { !it.done }
            val topPriority = state.missions.filterNot { it.done }.maxByOrNull(NovaEngine::missionScore)
            NovaCard(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NovaStat("✓ $done", "Tâches terminées")
                    NovaStat("📊 $open", "Missions ouvertes")
                    NovaStat("🎯 ${state.deals.size}", "Opportunités suivies")
                    NovaStat(if (topPriority != null) "⭐ ${NovaEngine.missionScore(topPriority)}" else "⭐ —", "Score priorité n°1")
                }
            }
        }

        item {
            NovaCard(Modifier.fillMaxWidth()) {
                Text("« Un objectif sans plan n'est qu'un souhait. »", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

