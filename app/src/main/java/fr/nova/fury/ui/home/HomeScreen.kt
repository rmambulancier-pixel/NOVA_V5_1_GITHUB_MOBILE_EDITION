package fr.nova.fury.ui.home

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.nova.fury.*
import fr.nova.fury.launchVoice
import fr.nova.fury.ui.components.AlphonseMicButton
import fr.nova.fury.ui.components.NovaCard

@Composable
fun HomeScreen(
    state: NovaState,
    update: ((NovaState) -> NovaState) -> Unit,
    onGo: (Screen) -> Unit,
    voiceLauncher: ActivityResultLauncher<Intent>
) {
    val health = NovaEngine.health(state)
    val next = NovaEngine.nextAction(state)
    val open = state.missions.filterNot { it.done }.sortedByDescending(NovaEngine::missionScore)
    val top = open.firstOrNull()
    val income = state.cash.filter { it.income }.sumOf { it.amount }
    val expenses = state.cash.filterNot { it.income }.sumOf { it.amount }
    var listening by remember { mutableStateOf(false) }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text("BONJOUR, MICKAËL", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text("Ton cockpit est prêt.", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                Text("Une seule question : qu'est-ce qui mérite ton attention maintenant ?", style = MaterialTheme.typography.bodyMedium)
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("MAINTENANT", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    Text(next, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    if (top != null) Text("Priorité ${NovaEngine.missionScore(top)}/100 • ${top.area}")
                    Button(onClick = { onGo(Screen.MISSIONS) }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.PlayArrow, null)
                        Spacer(Modifier.width(8.dp)); Text("COMMENCER MAINTENANT")
                    }
                }
            }
        }

        item {
            NovaCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("ALPHONSE A REMARQUÉ", fontWeight = FontWeight.Black)
                        Text(
                            when {
                                open.isEmpty() -> "Toutes tes missions sont terminées. Tu peux planifier la prochaine étape."
                                state.deals.isEmpty() -> "Aucune opportunité n'est analysée. Une piste rentable peut être ta prochaine accélération."
                                state.watches.isEmpty() -> "Watch Lab attend encore un concept à transformer en projet concret."
                                else -> "Ton système est actif. Le meilleur levier reste : ${top?.title ?: next}"
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    AlphonseMicButton(isListening = listening, onClick = {
                        listening = true
                        launchVoice(voiceLauncher) { listening = false }
                    })
                }
            }
        }

        item { Text("AUJOURD'HUI", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("MISSIONS", "${open.size}", "à faire", Modifier.weight(1f))
                StatCard("BUSINESS", "${state.deals.size}", "pistes", Modifier.weight(1f))
                StatCard("SANTÉ", "$health", "/100", Modifier.weight(1f))
            }
        }

        item {
            NovaCard(Modifier.fillMaxWidth()) {
                Text("MONEY PULSE", fontWeight = FontWeight.Black)
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column { Text("Entrées"); Text("${"%.0f".format(income)} €", fontWeight = FontWeight.Black) }
                    Column(horizontalAlignment = Alignment.End) { Text("Sorties"); Text("${"%.0f".format(expenses)} €", fontWeight = FontWeight.Black) }
                }
                Spacer(Modifier.height(8.dp))
                Text("Solde suivi : ${"%.0f".format(income - expenses)} €", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            }
        }

        item { Text("ACCÈS DIRECT", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickAction("Priority Radar", "Décider quoi faire", Icons.Default.TrackChanges) { onGo(Screen.MISSIONS) }
                QuickAction("Business Hunter", "Évaluer une opportunité", Icons.Default.Search) { onGo(Screen.BUSINESS) }
                QuickAction("Watch Lab", "Créer et scorer un concept", Icons.Default.Watch) { onGo(Screen.WATCH) }
                QuickAction("Second Brain", "Capturer une idée ou parler à Alphonse", Icons.Default.Psychology) { onGo(Screen.BRAIN) }
            }
        }
    }
}

@Composable private fun StatCard(label: String, value: String, sub: String, modifier: Modifier) {
    ElevatedCard(modifier) { Column(Modifier.padding(12.dp)) { Text(label, style = MaterialTheme.typography.labelSmall); Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black); Text(sub, style = MaterialTheme.typography.labelSmall) } }
}

@Composable private fun QuickAction(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, click: () -> Unit) {
    ElevatedCard(onClick = click, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null); Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Black); Text(subtitle, style = MaterialTheme.typography.bodySmall) }; Icon(Icons.Default.ChevronRight, null)
        }
    }
}
