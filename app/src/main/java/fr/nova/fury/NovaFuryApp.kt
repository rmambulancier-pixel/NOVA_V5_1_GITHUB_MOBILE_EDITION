package fr.nova.fury

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.concurrent.Executor

enum class Screen { HOME, MISSIONS, BUSINESS, WATCH, MONEY, BRAIN, FOCUS, SETTINGS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaFuryApp() {
    val context = LocalContext.current
    val store = remember { NovaStore(context) }
    var state by remember { mutableStateOf(NovaState()) }
    LaunchedEffect(Unit) { store.flow.collect { state = it } }

    var screen by rememberSaveable { mutableStateOf(Screen.HOME) }
    var drawer by remember { mutableStateOf(false) }

    val colors = if (Build.VERSION.SDK_INT >= 31 && !state.dark)
        dynamicLightColorScheme(context)
    else if (Build.VERSION.SDK_INT >= 31)
        dynamicDarkColorScheme(context)
    else if (state.dark)
        darkColorScheme(primary = Color(0xFFB8C4FF))
    else
        lightColorScheme(primary = Color(0xFF2643C7))

    fun update(block: (NovaState) -> NovaState) {
        state = block(state)
        store.save(state)
    }

    MaterialTheme(colorScheme = colors) {
        val drawerState = rememberDrawerState(if (drawer) DrawerValue.Open else DrawerValue.Closed)
        LaunchedEffect(drawer) {
            if (drawer) drawerState.open() else drawerState.close()
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text("NOVA", Modifier.padding(24.dp), fontSize = 36.sp, fontWeight = FontWeight.Black)
                    Text("FURY V5 • PIXEL EDITION", Modifier.padding(horizontal = 24.dp), style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(14.dp))
                    val entries = listOf(
                        Screen.HOME to "Alphonse",
                        Screen.MISSIONS to "Priority Radar",
                        Screen.BUSINESS to "Business Hunter",
                        Screen.WATCH to "Watch Lab",
                        Screen.MONEY to "Money OS",
                        Screen.BRAIN to "Second Brain",
                        Screen.FOCUS to "Focus Mode",
                        Screen.SETTINGS to "Settings"
                    )
                    entries.forEach { (target, label) ->
                        NavigationDrawerItem(
                            label = { Text(label) },
                            selected = screen == target,
                            onClick = { screen = target; drawer = false },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(title(screen), fontWeight = FontWeight.Black) },
                        navigationIcon = {
                            IconButton(onClick = { drawer = true }) {
                                Icon(Icons.Default.Menu, null)
                            }
                        }
                    )
                }
            ) { padding ->
                AnimatedContent(
                    targetState = screen,
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    label = "nova_screen"
                ) { current ->
                    when (current) {
                        Screen.HOME -> Home(state, update = ::update, onGo = { screen = it })
                        Screen.MISSIONS -> Missions(state, update = ::update)
                        Screen.BUSINESS -> Business(state, update = ::update)
                        Screen.WATCH -> WatchLab(state, update = ::update)
                        Screen.MONEY -> Money(state, update = ::update)
                        Screen.BRAIN -> Brain(state, update = ::update)
                        Screen.FOCUS -> Focus()
                        Screen.SETTINGS -> Settings(state, update = ::update)
                    }
                }
            }
        }
    }
}

private fun title(s: Screen) = when (s) {
    Screen.HOME -> "ALPHONSE"
    Screen.MISSIONS -> "PRIORITY RADAR"
    Screen.BUSINESS -> "BUSINESS HUNTER"
    Screen.WATCH -> "WATCH LAB"
    Screen.MONEY -> "MONEY OS"
    Screen.BRAIN -> "SECOND BRAIN"
    Screen.FOCUS -> "FOCUS MODE"
    Screen.SETTINGS -> "SETTINGS"
}

@Composable
private fun Home(state: NovaState, update: ((NovaState) -> NovaState) -> Unit, onGo: (Screen) -> Unit) {
    val context = LocalContext.current
    val activity = context as Activity
    var voiceResult by remember { mutableStateOf("") }

    val voiceLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val text = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!text.isNullOrBlank()) {
            voiceResult = text
            val action = NovaEngine.parseCommand(text)
            when (action) {
                "MEMORY" -> update { it.copy(brain = (it.brain + text).takeLast(100)) }
                "FOCUS" -> onGo(Screen.FOCUS)
                "DEAL" -> onGo(Screen.BUSINESS)
                "WATCH" -> onGo(Screen.WATCH)
            }
        }
    }

    val health = NovaEngine.health(state)

    LazyColumn(
        Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("TON CENTRE DE COMMANDEMENT", fontSize = 27.sp, fontWeight = FontWeight.Black)
            Text("Une seule question : qu'est-ce qui te rapproche le plus de ton objectif maintenant ?")
        }

        item {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("NOVA SYSTEM HEALTH", fontWeight = FontWeight.Bold)
                    Text("$health / 100", fontSize = 56.sp, fontWeight = FontWeight.Black)
                    LinearProgressIndicator(progress = { health / 100f }, Modifier.fillMaxWidth())
                }
            }
        }

        item {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Text("ALPHONSE DÉCIDE", fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(6.dp))
                    Text(NovaEngine.nextAction(state), fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            launchVoice(activity, voiceLauncher)
                        },
                        Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Mic, null)
                        Spacer(Modifier.width(8.dp))
                        Text("PARLER À ALPHONSE")
                    }
                    if (voiceResult.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text("« $voiceResult »", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        item { Text("ACCÈS RAPIDE", fontWeight = FontWeight.Black) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Quick("Business", Icons.Default.Storefront, Modifier.weight(1f)) { onGo(Screen.BUSINESS) }
                Quick("Watch Lab", Icons.Default.Watch, Modifier.weight(1f)) { onGo(Screen.WATCH) }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Quick("Focus", Icons.Default.CenterFocusStrong, Modifier.weight(1f)) { onGo(Screen.FOCUS) }
                Quick("Brain", Icons.Default.Psychology, Modifier.weight(1f)) { onGo(Screen.BRAIN) }
            }
        }

        item { Text("DERNIÈRES MÉMOIRES", fontWeight = FontWeight.Black) }
        items(state.brain.takeLast(5).reversed()) { memory ->
            Card(Modifier.fillMaxWidth()) { Text(memory, Modifier.padding(14.dp)) }
        }
    }
}

@Composable
private fun Quick(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, mod: Modifier, click: () -> Unit) {
    ElevatedCard(mod) {
        Column(Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, Modifier.size(30.dp))
            Spacer(Modifier.height(6.dp))
            Text(label, fontWeight = FontWeight.Bold)
            TextButton(onClick = click) { Text("OPEN") }
        }
    }
}

@Composable
private fun Missions(state: NovaState, update: ((NovaState) -> NovaState) -> Unit) {
    var dialog by remember { mutableStateOf(false) }

    LazyColumn(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Button(onClick = { dialog = true }, Modifier.fillMaxWidth()) { Text("NOUVELLE MISSION") } }
        items(state.missions.sortedByDescending(NovaEngine::missionScore)) { m ->
            val score = NovaEngine.missionScore(m)
            ElevatedCard(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = m.done,
                        onCheckedChange = { checked ->
                            update { s -> s.copy(missions = s.missions.map { if (it.id == m.id) it.copy(done = checked) else it }) }
                        }
                    )
                    Column(Modifier.weight(1f)) {
                        Text(m.title, fontWeight = FontWeight.Black)
                        Text("${m.area} • impact ${m.impact} • effort ${m.effort} • urgence ${m.urgency}")
                        LinearProgressIndicator(progress = { score / 100f }, Modifier.fillMaxWidth())
                    }
                    Spacer(Modifier.width(10.dp))
                    AssistChip(onClick = {}, label = { Text("$score") })
                }
            }
        }
    }

    if (dialog) MissionDialog(
        close = { dialog = false },
        add = { m -> update { it.copy(missions = it.missions + m) }; dialog = false }
    )
}

@Composable
private fun Business(state: NovaState, update: ((NovaState) -> NovaState) -> Unit) {
    var dialog by remember { mutableStateOf(false) }

    LazyColumn(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("CHASSE AUX OPPORTUNITÉS", fontSize = 25.sp, fontWeight = FontWeight.Black)
            Text("NOVA ne te dit pas seulement combien tu gagnes. Il t'aide à éliminer les mauvaises opportunités.")
        }
        item { Button(onClick = { dialog = true }, Modifier.fillMaxWidth()) { Text("ANALYSER UNE OPPORTUNITÉ") } }
        items(state.deals.sortedByDescending(NovaEngine::dealScore)) { d ->
            val score = NovaEngine.dealScore(d)
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(d.title, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Text("Score NOVA : $score / 100", fontWeight = FontWeight.Bold)
                    LinearProgressIndicator(progress = { score / 100f }, Modifier.fillMaxWidth())
                    Text("Profit : %.2f € • ROI : %.1f%%".format(d.profit, d.roi))
                    Text("Achat %.2f € → Vente %.2f €".format(d.buy, d.sell))
                    Text("Risque ${d.risk}/100 • Confiance ${d.confidence}/100 • Demande ${d.demand}/100")
                }
            }
        }
    }

    if (dialog) DealDialog(
        close = { dialog = false },
        add = { d -> update { it.copy(deals = it.deals + d) }; dialog = false }
    )
}

@Composable
private fun WatchLab(state: NovaState, update: ((NovaState) -> NovaState) -> Unit) {
    var dialog by remember { mutableStateOf(false) }

    LazyColumn(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("WATCH LAB // FURY", fontSize = 25.sp, fontWeight = FontWeight.Black)
            Text("Ton laboratoire de concepts. Le but : éliminer les idées faibles avant de dépenser de l'argent.")
        }
        item { Button(onClick = { dialog = true }, Modifier.fillMaxWidth()) { Text("CRÉER UN CONCEPT") } }
        items(state.watches.sortedByDescending(NovaEngine::watchScore)) { w ->
            val score = NovaEngine.watchScore(w)
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(w.name, fontSize = 21.sp, fontWeight = FontWeight.Black)
                    Text("${w.movement} • ${w.caseDesign}")
                    Text(w.dial)
                    Text("Prix cible %.2f € • Coût %.2f €".format(w.targetPrice, w.estimatedCost))
                    Text("Score concept : $score / 100", fontWeight = FontWeight.Bold)
                    LinearProgressIndicator(progress = { score / 100f }, Modifier.fillMaxWidth())
                    Text("Originalité ${w.originality} • ADN ${w.brandFit} • Faisabilité ${w.feasibility}")
                }
            }
        }
    }

    if (dialog) WatchDialog(
        close = { dialog = false },
        add = { w -> update { it.copy(watches = it.watches + w) }; dialog = false }
    )
}

@Composable
private fun Money(state: NovaState, update: ((NovaState) -> NovaState) -> Unit) {
    var dialog by remember { mutableStateOf(false) }
    val income = state.cash.filter { it.income }.sumOf { it.amount }
    val expense = state.cash.filterNot { it.income }.sumOf { it.amount }

    LazyColumn(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Metric("Entrées", "%.0f€".format(income), Modifier.weight(1f))
                Metric("Sorties", "%.0f€".format(expense), Modifier.weight(1f))
                Metric("Solde", "%.0f€".format(income - expense), Modifier.weight(1f))
            }
        }
        item { Button(onClick = { dialog = true }, Modifier.fillMaxWidth()) { Text("AJOUTER UN FLUX") } }
        items(state.cash.reversed()) { c ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column { Text(c.title, fontWeight = FontWeight.Bold); Text(c.category) }
                    Text((if (c.income) "+" else "-") + "%.2f €".format(c.amount), fontWeight = FontWeight.Black)
                }
            }
        }
    }

    if (dialog) CashDialog(
        close = { dialog = false },
        add = { c -> update { it.copy(cash = it.cash + c) }; dialog = false }
    )
}

@Composable
private fun Brain(state: NovaState, update: ((NovaState) -> NovaState) -> Unit) {
    var text by remember { mutableStateOf("") }
    LazyColumn(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("SECOND BRAIN", fontSize = 25.sp, fontWeight = FontWeight.Black)
            Text("Ici, tu vides ton cerveau. NOVA conserve la matière brute.")
        }
        item {
            OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth().height(150.dp), label = { Text("Idée, décision, note...") })
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        update { it.copy(brain = (it.brain + text).takeLast(100)) }
                        text = ""
                    }
                },
                Modifier.fillMaxWidth()
            ) { Text("ENREGISTRER DANS LE CERVEAU") }
        }
        items(state.brain.reversed()) { note ->
            ElevatedCard(Modifier.fillMaxWidth()) { Text(note, Modifier.padding(16.dp)) }
        }
    }
}

@Composable
private fun Focus() {
    var seconds by remember { mutableIntStateOf(25 * 60) }
    var running by remember { mutableStateOf(false) }

    LaunchedEffect(running) {
        while (running && seconds > 0) {
            kotlinx.coroutines.delay(1000)
            seconds--
        }
        if (seconds == 0) running = false
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.CenterFocusStrong, null, Modifier.size(76.dp))
        Text("DEEP FOCUS", fontSize = 30.sp, fontWeight = FontWeight.Black)
        Text("%02d:%02d".format(seconds / 60, seconds % 60), fontSize = 72.sp, fontWeight = FontWeight.Black)
        Button(onClick = { running = !running }, Modifier.fillMaxWidth()) { Text(if (running) "PAUSE" else "ENGAGER") }
        TextButton(onClick = { running = false; seconds = 25 * 60 }) { Text("RESET 25 MIN") }
    }
}

@Composable
private fun Settings(state: NovaState, update: ((NovaState) -> NovaState) -> Unit) {
    Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("PIXEL SETTINGS", fontSize = 25.sp, fontWeight = FontWeight.Black)
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Mode sombre", Modifier.weight(1f))
                    Switch(state.dark, { v -> update { it.copy(dark = v) } })
                }
                Spacer(Modifier.height(8.dp))
                Text("V5 est volontairement sans clé API : tu peux tester l'app immédiatement sur ton Pixel sans configurer un compte externe.", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun Metric(label: String, value: String, modifier: Modifier) {
    ElevatedCard(modifier) {
        Column(Modifier.padding(10.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun MissionDialog(close: () -> Unit, add: (Mission) -> Unit) {
    var title by remember { mutableStateOf("") }
    var impact by remember { mutableStateOf("4") }
    var effort by remember { mutableStateOf("3") }
    var urgency by remember { mutableStateOf("4") }

    AlertDialog(
        onDismissRequest = close,
        title = { Text("Nouvelle mission") },
        text = {
            Column {
                OutlinedTextField(title, { title = it }, label = { Text("Mission") })
                OutlinedTextField(impact, { impact = it }, label = { Text("Impact 1-5") })
                OutlinedTextField(effort, { effort = it }, label = { Text("Effort 1-5") })
                OutlinedTextField(urgency, { urgency = it }, label = { Text("Urgence 1-5") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                add(Mission(title = title, impact = impact.toIntOrNull() ?: 3, effort = effort.toIntOrNull() ?: 3, urgency = urgency.toIntOrNull() ?: 3))
            }) { Text("AJOUTER") }
        },
        dismissButton = { TextButton(onClick = close) { Text("ANNULER") } }
    )
}

@Composable
private fun DealDialog(close: () -> Unit, add: (Deal) -> Unit) {
    var title by remember { mutableStateOf("") }
    var buy by remember { mutableStateOf("") }
    var sell by remember { mutableStateOf("") }
    var fees by remember { mutableStateOf("0") }
    var ship by remember { mutableStateOf("0") }
    var risk by remember { mutableStateOf("30") }
    var confidence by remember { mutableStateOf("70") }
    var demand by remember { mutableStateOf("70") }

    AlertDialog(
        onDismissRequest = close,
        title = { Text("Business Hunter") },
        text = {
            LazyColumn {
                item { OutlinedTextField(title, { title = it }, label = { Text("Objet") }) }
                item { OutlinedTextField(buy, { buy = it }, label = { Text("Prix achat") }) }
                item { OutlinedTextField(sell, { sell = it }, label = { Text("Prix vente estimé") }) }
                item { OutlinedTextField(fees, { fees = it }, label = { Text("Frais") }) }
                item { OutlinedTextField(ship, { ship = it }, label = { Text("Port") }) }
                item { OutlinedTextField(risk, { risk = it }, label = { Text("Risque 0-100") }) }
                item { OutlinedTextField(confidence, { confidence = it }, label = { Text("Confiance 0-100") }) }
                item { OutlinedTextField(demand, { demand = it }, label = { Text("Demande 0-100") }) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                add(Deal(
                    title = title, buy = buy.toDoubleOrNull() ?: 0.0, sell = sell.toDoubleOrNull() ?: 0.0,
                    fees = fees.toDoubleOrNull() ?: 0.0, shipping = ship.toDoubleOrNull() ?: 0.0,
                    risk = risk.toIntOrNull() ?: 30, confidence = confidence.toIntOrNull() ?: 70, demand = demand.toIntOrNull() ?: 70
                ))
            }) { Text("ANALYSER") }
        },
        dismissButton = { TextButton(onClick = close) { Text("ANNULER") } }
    )
}

@Composable
private fun WatchDialog(close: () -> Unit, add: (WatchConcept) -> Unit) {
    var name by remember { mutableStateOf("") }
    var movement by remember { mutableStateOf("") }
    var caseD by remember { mutableStateOf("") }
    var dial by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var originality by remember { mutableStateOf("70") }
    var fit by remember { mutableStateOf("70") }
    var feasibility by remember { mutableStateOf("70") }

    AlertDialog(
        onDismissRequest = close,
        title = { Text("Nouveau concept") },
        text = {
            LazyColumn {
                item { OutlinedTextField(name, { name = it }, label = { Text("Nom du concept") }) }
                item { OutlinedTextField(movement, { movement = it }, label = { Text("Mouvement") }) }
                item { OutlinedTextField(caseD, { caseD = it }, label = { Text("Boîtier") }) }
                item { OutlinedTextField(dial, { dial = it }, label = { Text("Cadran") }) }
                item { OutlinedTextField(price, { price = it }, label = { Text("Prix cible") }) }
                item { OutlinedTextField(cost, { cost = it }, label = { Text("Coût estimé") }) }
                item { OutlinedTextField(originality, { originality = it }, label = { Text("Originalité") }) }
                item { OutlinedTextField(fit, { fit = it }, label = { Text("ADN marque") }) }
                item { OutlinedTextField(feasibility, { feasibility = it }, label = { Text("Faisabilité") }) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                add(WatchConcept(
                    name = name, movement = movement, caseDesign = caseD, dial = dial,
                    targetPrice = price.toDoubleOrNull() ?: 0.0, estimatedCost = cost.toDoubleOrNull() ?: 0.0,
                    originality = originality.toIntOrNull() ?: 70, brandFit = fit.toIntOrNull() ?: 70,
                    feasibility = feasibility.toIntOrNull() ?: 70
                ))
            }) { Text("CRÉER") }
        },
        dismissButton = { TextButton(onClick = close) { Text("ANNULER") } }
    )
}

@Composable
private fun CashDialog(close: () -> Unit, add: (CashFlow) -> Unit) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Général") }
    var income by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = close,
        title = { Text("Flux financier") },
        text = {
            Column {
                OutlinedTextField(title, { title = it }, label = { Text("Libellé") })
                OutlinedTextField(amount, { amount = it }, label = { Text("Montant") })
                OutlinedTextField(category, { category = it }, label = { Text("Catégorie") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Entrée d'argent")
                    Spacer(Modifier.width(12.dp))
                    Switch(income, { income = it })
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                add(CashFlow(title = title, amount = amount.toDoubleOrNull() ?: 0.0, income = income, category = category))
            }) { Text("AJOUTER") }
        },
        dismissButton = { TextButton(onClick = close) { Text("ANNULER") } }
    )
}
 
