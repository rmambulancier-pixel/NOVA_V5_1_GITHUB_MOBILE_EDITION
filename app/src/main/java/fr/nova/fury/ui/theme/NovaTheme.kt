package fr.nova.fury.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val DarkColors = darkColorScheme(
    primary = NovaColors.ElectricBlue,
    onPrimary = NovaColors.OnPrimary,
    background = NovaColors.DeepBlack,
    surface = NovaColors.NightBlue,
    onSurface = NovaColors.Frost,
)

@Composable
fun NovaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography(), // placeholder — tu peux injecter NovaTypography ici
        shapes = Shapes(
            small = RoundedCornerShape(10.dp),
            medium = RoundedCornerShape(14.dp),
            large = RoundedCornerShape(20.dp)
        ),
        content = content
    )
}
