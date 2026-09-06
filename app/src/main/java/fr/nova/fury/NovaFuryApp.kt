package fr.nova.fury

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.nova.fury.ui.home.HomeScreen
import fr.nova.fury.ui.watch.WatchPreview
import fr.nova.fury.ui.watch.ChoiceRow
import fr.nova.fury.ui.theme.NovaTheme
import kotlinx.coroutines.launch

enum class Screen { HOME, MISSIONS, BUSINESS, WATCH, MONEY, BRAIN, FOCUS, SETTINGS }

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
    var selectedId by remember { mutableStateOf(state.watches.firstOrNull()?.id) }
    val current = state.watches.firstOrNull { it.id == selectedId }
    var draft by remember(selectedId, state.watches) { mutableStateOf(current ?: WatchConcept(
        name = "NOVA CONCEPT", movement = "Automatique", caseDesign = "Visual V8", dial = "Minimal",
        targetPrice = 0.0, estimatedCost = 0.0, originality = 70, brandFit = 70, feasibility = 70
    )) }

    fun score(c: WatchConcept) = NovaEngine.watchScore(c)
    LazyColumn(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("WATCH LAB VISUAL // V8", fontSize = 25.sp, fontWeight = FontWeight.Black)
            Text("Configure ta montre en direct. NOVA garde tout en local et score chaque concept avant d'investir.")
        }
        item {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    WatchPreview(draft, Modifier.fillMaxWidth().height(330.dp))
                    Text("APERÇU V8 • ${draft.caseShape} • ${draft.caseSize} mm", fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            OutlinedTextField(draft.name, { draft = draft.copy(name = it) }, label = { Text("Nom du concept") }, modifier = Modifier.fillMaxWidth())
        }
        item { ChoiceRow("BOÎTIER", listOf("Round", "Square", "Cushion"), draft.caseShape) { draft = draft.copy(caseShape = it, caseDesign = it) } }
        item {
            Text("DIAMÈTRE : ${draft.caseSize} mm", style = MaterialTheme.typography.labelLarge)
            Slider(value = draft.caseSize.toFloat(), onValueChange = { draft = draft.copy(caseSize = it.toInt()) }, valueRange = 34f..48f, steps = 13)
        }
        item { ChoiceRow("CADRAN", listOf("Minimal", "Sunburst", "Diver", "Skeleton", "Vintage"), draft.dialStyle) { draft = draft.copy(dialStyle = it, dial = it) } }
        item { ChoiceRow("AIGUILLES", listOf("Dauphine", "Sword", "Mercedes"), draft.handStyle) { draft = draft.copy(handStyle = it) } }
        item { ChoiceRow("BRACELET", listOf("Leather", "Steel", "Rubber", "NATO"), draft.strapStyle) { draft = draft.copy(strapStyle = it) } }
        item { ChoiceRow("FINITIONS", listOf("Steel", "Gold", "Black"), draft.accentStyle) { draft = draft.copy(accentStyle = it) } }
        item {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("DESIGN SCORE NOVA : ${score(draft)} / 100", fontWeight = FontWeight.Black, fontSize = 20.sp)
                    LinearProgressIndicator(progress = { score(draft) / 100f }, Modifier.fillMaxWidth())
                    Text("Originalité ${draft.originality} • ADN ${draft.brandFit} • Faisabilité ${draft.feasibility}")
                }
            }
        }
        item {
            Button(onClick = {
                val saved = if (current == null) draft else draft.copy(id = current.id)
                update { st -> st.copy(watches = st.watches.filterNot { it.id == saved.id } + saved) }
                selectedId = saved.id
            }, modifier = Modifier.fillMaxWidth()) { Text(if (current == null) "SAUVEGARDER LE CONCEPT" else "METTRE À JOUR LE CONCEPT") }
        }
        if (state.watches.isNotEmpty()) {
            item { Text("CONCEPTS SAUVEGARDÉS", fontWeight = FontWeight.Black) }
            items(state.watches.sortedByDescending(::score)) { w ->
                ElevatedCard(Modifier.fillMaxWidth(), onClick = { selectedId = w.id }) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        WatchPreview(w, Modifier.size(88.dp))
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(w.name, fontWeight = FontWeight.Black)
                            Text("${w.caseShape} ${w.caseSize} mm • ${w.dialStyle} • ${w.strapStyle}")
                            Text("Score ${score(w)}/100")
                        }
                    }
                }
            }
        }
    }
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
    val context = LocalContext.current
    val router = remember { HybridAiRouter(context) }
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    LazyColumn(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("SECOND BRAIN", fontSize = 25.sp, fontWeight = FontWeight.Black)
            Text("Local par défaut. Le LLM distant intervient seulement selon ton mode et tes réglages.")
        }
        item {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ALPHONSE HYBRIDE", fontWeight = FontWeight.Black)
                    OutlinedTextField(question, { question = it }, Modifier.fillMaxWidth(), label = { Text("Demande à Alphonse") })
                    Button(onClick = {
                        if (question.isNotBlank() && !loading) scope.launch {
                            loading = true
                            val reply = router.ask(question, state)
                            answer = reply.text
                            source = reply.source
                            loading = false
                        }
                    }, Modifier.fillMaxWidth()) { Text(if (loading) "ANALYSE..." else "ANALYSER") }
                    if (answer.isNotBlank()) {
                        Text("SOURCE : $source", style = MaterialTheme.typography.labelSmall)
                        Text(answer)
                    }
                }
            }
        }
        item {
            OutlinedTextField(text, { text = it }, Modifier.fillMaxWidth().height(150.dp), label = { Text("Idée, décision, note...") })
            Button(onClick = {
                if (text.isNotBlank()) {
                    update { it.copy(brain = (it.brain + text).takeLast(100)) }
                    text = ""
                }
            }, Modifier.fillMaxWidth()) { Text("ENREGISTRER DANS LE CERVEAU") }
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
    val context = LocalContext.current
    val store = remember { NovaStore(context) }
    var llm by remember { mutableStateOf(LlmSettings()) }
    LaunchedEffect(Unit) { llm = store.loadLlmSettings() }
    LazyColumn(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("PIXEL SETTINGS", fontSize = 25.sp, fontWeight = FontWeight.Black) }
        item {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Mode sombre", Modifier.weight(1f)); Switch(state.dark, { v -> update { it.copy(dark = v) } })
                    }
                }
            }
        }
        item {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("IA HYBRIDE", fontWeight = FontWeight.Black)
                    Text("Local = instant et hors ligne. Distant = raisonnement ouvert. HYBRIDE choisit selon la demande et retombe sur le local en cas d'échec.", style = MaterialTheme.typography.bodySmall)
                    ChoiceRow("Mode", AiMode.entries.map { it.name }, llm.mode.name) { llm = llm.copy(mode = AiMode.valueOf(it)) }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Autoriser les requêtes distantes", Modifier.weight(1f))
                        Switch(llm.allowRemote, { llm = llm.copy(allowRemote = it) })
                    }
                    OutlinedTextField(llm.endpoint, { llm = llm.copy(endpoint = it) }, Modifier.fillMaxWidth(), label = { Text("Endpoint HTTPS compatible OpenAI") }, singleLine = true)
                    OutlinedTextField(llm.model, { llm = llm.copy(model = it) }, Modifier.fillMaxWidth(), label = { Text("Modèle") }, singleLine = true)
                    OutlinedTextField(llm.apiKey, { llm = llm.copy(apiKey = it) }, Modifier.fillMaxWidth(), label = { Text("Clé API") }, singleLine = true)
                    Text("NOVA V8 n'envoie aucune donnée à distance tant que l'autorisation est désactivée. Pour une version production, la clé doit idéalement être gérée via Android Keystore ou un backend proxy.", style = MaterialTheme.typography.bodySmall)
                    Button(onClick = { store.saveLlmSettings(llm) }, Modifier.fillMaxWidth()) { Text("ENREGISTRER L'ARCHITECTURE IA") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaFuryApp() {
    val context = LocalContext.current
    val store = remember { NovaStore(context) }
    var state by remember { mutableStateOf(NovaState()) }
    LaunchedEffect(Unit) { store.flow.collect { state = it } }

    var screen by rememberSaveable { mutableStateOf(Screen.HOME) }
    var drawer by remember { mutableStateOf(false) }

    var voiceResult by remember { mutableStateOf("") }
    val voiceLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val text = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!text.isNullOrBlank()) {
            voiceResult = text
            when (NovaEngine.parseCommand(text)) {
                "MEMORY" -> state = state.copy(brain = (state.brain + text).takeLast(100)).also { store.save(it) }
                "FOCUS" -> screen = Screen.FOCUS
                "DEAL" -> screen = Screen.BUSINESS
                "WATCH" -> screen = Screen.WATCH
            }
        }
    }

    fun updateState(block: (NovaState) -> NovaState) {
        state = block(state)
        store.save(state)
    }

    NovaTheme(darkTheme = state.dark) {
        val drawerState = rememberDrawerState(if (drawer) DrawerValue.Open else DrawerValue.Closed)
        LaunchedEffect(drawer) {
            if (drawer) drawerState.open() else drawerState.close()
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text("NOVA", Modifier.padding(24.dp), fontSize = 36.sp, fontWeight = FontWeight.Black)
                    Text("NOVA V8 • PERSONAL COMMAND CENTER", Modifier.padding(horizontal = 24.dp), style = MaterialTheme.typography.labelMedium)
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
                floatingActionButton = {
                    var quickOpen by remember { mutableStateOf(false) }
                    FloatingActionButton(onClick = { quickOpen = true }) { Icon(Icons.Default.Add, contentDescription = "Action rapide") }
                    if (quickOpen) {
                        AlertDialog(
                            onDismissRequest = { quickOpen = false },
                            title = { Text("QUE VEUX-TU FAIRE ?") },
                            text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { screen = Screen.BRAIN; quickOpen = false }, Modifier.fillMaxWidth()) { Text("🧠 CAPTURE RAPIDE") }
                                Button(onClick = { screen = Screen.MISSIONS; quickOpen = false }, Modifier.fillMaxWidth()) { Text("🎯 AJOUTER UNE MISSION") }
                                Button(onClick = { screen = Screen.BUSINESS; quickOpen = false }, Modifier.fillMaxWidth()) { Text("💰 ANALYSER UNE OPPORTUNITÉ") }
                                Button(onClick = { screen = Screen.WATCH; quickOpen = false }, Modifier.fillMaxWidth()) { Text("⌚ CRÉER UN CONCEPT") }
                                OutlinedButton(onClick = { quickOpen = false }, Modifier.fillMaxWidth()) { Text("FERMER") }
                            } },
                            confirmButton = {}
                        )
                    }
                },
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
                        Screen.HOME -> HomeScreen(
                            state = state,
                            update = { updateState(it) },
                            onGo = { screen = it },
                            voiceLauncher = voiceLauncher
                        )
                        Screen.MISSIONS -> Missions(state, update = { updateState(it) })
                        Screen.BUSINESS -> Business(state, update = { updateState(it) })
                        Screen.WATCH -> WatchLab(state, update = { updateState(it) })
                        Screen.MONEY -> Money(state, update = { updateState(it) })
                        Screen.BRAIN -> Brain(state, update = { updateState(it) })
                        Screen.FOCUS -> Focus()
                        Screen.SETTINGS -> Settings(state, update = { updateState(it) })
                    }
                }
            }
        }
    }
}
 
