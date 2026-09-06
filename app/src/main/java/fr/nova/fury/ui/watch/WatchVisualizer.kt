package fr.nova.fury.ui.watch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import fr.nova.fury.WatchConcept
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WatchPreview(concept: WatchConcept, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f; val cy = size.height / 2f
        val scale = (concept.caseSize.coerceIn(34, 48) - 30) / 20f
        val r = size.minDimension * (0.30f + scale * 0.12f)
        val accent = when (concept.accentStyle) { "Gold" -> Color(0xFFC9A227); "Black" -> Color(0xFF202124); else -> Color(0xFFB9C1C9) }
        val strap = when (concept.strapStyle) { "Steel" -> Color(0xFF7E8790); "Rubber" -> Color(0xFF30343A); "NATO" -> Color(0xFF52606D); else -> Color(0xFF5A4034) }
        val dialColor = when (concept.dialStyle) { "Sunburst" -> Color(0xFF1F2937); "Skeleton" -> Color(0xFF111111); "Diver" -> Color(0xFF0F3D56); "Vintage" -> Color(0xFFBFA77A); else -> Color(0xFF101216) }
        drawRoundRect(strap, Offset(cx-r*0.34f, 0f), Size(r*0.68f, size.height), CornerRadius(r*0.12f, r*0.12f))
        when (concept.caseShape) {
            "Square" -> { drawRoundRect(accent, Offset(cx-r, cy-r), Size(r*2, r*2), CornerRadius(r*0.18f, r*0.18f)); drawRoundRect(Color(0xFF060708), Offset(cx-r*0.82f, cy-r*0.82f), Size(r*1.64f, r*1.64f), CornerRadius(r*0.14f, r*0.14f)) }
            "Cushion" -> { drawRoundRect(accent, Offset(cx-r, cy-r*0.86f), Size(r*2, r*1.72f), CornerRadius(r*0.42f, r*0.42f)); drawRoundRect(Color(0xFF060708), Offset(cx-r*0.82f, cy-r*0.70f), Size(r*1.64f, r*1.40f), CornerRadius(r*0.34f, r*0.34f)) }
            else -> { drawCircle(accent, r, Offset(cx, cy)); drawCircle(Color(0xFF060708), r*0.86f, Offset(cx, cy)) }
        }
        drawCircle(dialColor, r*0.77f, Offset(cx, cy))
        if (concept.dialStyle != "Minimal") for (i in 0 until 12) {
            val a = Math.toRadians((i * 30 - 90).toDouble()); val x1 = cx + cos(a).toFloat()*r*0.58f; val y1 = cy + sin(a).toFloat()*r*0.58f; val x2 = cx + cos(a).toFloat()*r*0.68f; val y2 = cy + sin(a).toFloat()*r*0.68f
            drawLine(accent, Offset(x1,y1), Offset(x2,y2), strokeWidth = if (i % 3 == 0) r*0.045f else r*0.022f)
        }
        if (concept.dialStyle == "Skeleton") { drawCircle(Color(0xFF777777), r*0.40f, Offset(cx, cy), style = Stroke(r*0.018f)); drawCircle(Color(0xFF777777), r*0.16f, Offset(cx, cy), style = Stroke(r*0.018f)) }
        fun hand(angle: Float, length: Float, width: Float) { rotate(angle, Offset(cx, cy)) { drawLine(accent, Offset(cx,cy), Offset(cx, cy-length), strokeWidth = width) } }
        when (concept.handStyle) { "Mercedes" -> { hand(35f, r*0.50f, r*0.05f); hand(145f, r*0.64f, r*0.035f) }; "Sword" -> { hand(35f, r*0.54f, r*0.075f); hand(145f, r*0.68f, r*0.055f) }; else -> { hand(35f, r*0.50f, r*0.045f); hand(145f, r*0.66f, r*0.03f) } }
        drawCircle(accent, r*0.06f, Offset(cx,cy))
    }
}

@Composable
fun ChoiceRow(label: String, options: List<String>, selected: String, onSelected: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { options.forEach { option -> FilterChip(selected = selected == option, onClick = { onSelected(option) }, label = { Text(option) }) } }
    }
}
