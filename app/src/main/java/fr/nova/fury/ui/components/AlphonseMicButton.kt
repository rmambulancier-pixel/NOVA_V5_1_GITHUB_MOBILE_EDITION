package fr.nova.fury.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Mic
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nova.fury.ui.theme.NovaColors

@Composable
fun AlphonseMicButton(
    isListening: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition()
    val glow by transition.animateFloat(
        initialValue = if (isListening) 0.6f else 0.15f,
        targetValue = if (isListening) 1f else 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isListening) 800 else 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Button(
        onClick = onClick,
        modifier = modifier.size(84.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = NovaColors.ElectricBlue.copy(alpha = 0.12f)),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isListening) 18.dp else 6.dp),
        content = {
            Icon(imageVector = Icons.Filled.Mic, contentDescription = "Parler à Alphonse", tint = NovaColors.ElectricBlue)
        }
    )
}
