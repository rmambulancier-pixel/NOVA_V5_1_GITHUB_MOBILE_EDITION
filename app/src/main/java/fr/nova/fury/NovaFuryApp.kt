package fr.nova.fury

import android.app.Activity
import android.content.Intent
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
import fr.nova.fury.ui.theme.NovaTheme
import fr.nova.fury.ui.home.HomeScreen

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

    val colors = if (android.os.Build.VERSION.SDK_INT >= 31 && !state.dark)
        dynamicLightColorScheme(context)
    else if (android.os.Build.VERSION.SDK_INT >= 31)
        dynamicDarkColorScheme(context)
    else if (state.dark)
        darkColorScheme(primary = Color(0xFFB8C4FF))
    else
        lightColorScheme(primary = Color(0xFF2643C7))

    fun update(block: (NovaState) -> NovaState) {
        state = block(state)
        store.save(state)
    }

    NovaTheme {
        val drawerState = rememberDrawerState(if (drawer) DrawerValue.Open else DrawerValue.Closed)
        LaunchedEffect(drawer) {
            if (drawer) drawerState.open() else drawerState.close()
        }

        // Create a voice launcher here and handle results so HomeScreen can just launch
        val activity = context as Activity
        val voiceLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val text = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!text.isNullOrBlank()) {
                val action = NovaEngine.parseCommand(text)
                when (action) {
                    "MEMORY" -> update { it.copy(brain = (it.brain + text).takeLast(100)) }
                    "FOCUS" -> screen = Screen.FOCUS
                    "DEAL" -> screen = Screen.BUSINESS
                    "WATCH" -> screen = Screen.WATCH
                    else -> Unit
                }
            }
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
                        Screen.HOME -> HomeScreen(state, update = ::update, onGo = { screen = it }, activity = activity, voiceLauncher = voiceLauncher)
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
