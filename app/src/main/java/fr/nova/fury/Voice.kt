package fr.nova.fury

import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import java.util.Locale

fun launchVoice(
    launcher: ActivityResultLauncher<Intent>,
    onUnavailable: (() -> Unit)? = null
) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.FRENCH.toLanguageTag())
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Parle à Alphonse...")
    }

    try {
        launcher.launch(intent)
    } catch (_: Exception) {
        onUnavailable?.invoke()
    }
}
