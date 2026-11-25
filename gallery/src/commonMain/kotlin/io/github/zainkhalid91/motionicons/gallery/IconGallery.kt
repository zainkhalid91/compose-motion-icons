package io.github.zainkhalid91.motionicons.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.zainkhalid91.motionicons.MotionIconStyle
import io.github.zainkhalid91.motionicons.ProvideMotionIconStyle
import kotlinx.coroutines.delay

internal val Background = Color(0xFF0F1115)
internal val Card = Color(0xFF171A21)
internal val CardBorder = Color(0xFF252A34)
internal val Muted = Color(0xFF8A90A0)

/** Icon colours the gallery offers. The first is the default. */
public val GalleryPalette: List<Color> = listOf(
    Color(0xFFF1F3F7),
    Color(0xFF7C9CFF),
    Color(0xFF34D399),
    Color(0xFFFBBF24),
    Color(0xFFF472B6),
)

private val Speeds = listOf(0.5f to "0.5x", 1f to "1x", 2f to "2x")

// The order icons appear on screen: grouped by category.
private val ReadingOrder: List<IconEntry> = IconCatalog.grouped.flatMap { it.second }

/** The whole gallery: tap an icon to change its state, or let them all play. */
@Composable
public fun IconGallery(modifier: Modifier = Modifier) {
    val steps = remember { mutableStateListOf(*Array(IconCatalog.entries.size) { 0 }) }
    var color by remember { mutableStateOf(GalleryPalette.first()) }
    var speed by remember { mutableStateOf(1f) }
    var autoplay by remember { mutableStateOf(true) }
    if (autoplay) {
        LaunchedEffect(Unit) {
            var tick = 0
            while (true) {
                delay(220)
                // One icon at a time, in reading order, so the grid ripples rather than flashing.
                val entry = ReadingOrder[tick % ReadingOrder.size]
                val i = entry.id - 1
                steps[i] = (steps[i] + 1) % entry.steps
                tick++
            }
        }
    }
    ProvideMotionIconStyle(MotionIconStyle(color = color, speed = speed)) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(112.dp),
            modifier = modifier.fillMaxSize().background(Background),
            contentPadding = PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Header(
                    color = color,
                    onColor = { color = it },
                    speed = speed,
                    onSpeed = { speed = it },
                    autoplay = autoplay,
                    onAutoplay = { autoplay = it },
                )
            }
            for ((category, group) in IconCatalog.grouped) {
                if (group.isEmpty()) continue
                item(key = "header-$category", span = { GridItemSpan(maxLineSpan) }) {
                    CategoryHeader(category, group.size)
                }
                items(group, key = { it.id }) { entry ->
                    val index = entry.id - 1
                    IconCard(entry, steps[index]) {
                        autoplay = false
                        steps[index] = (steps[index] + 1) % entry.steps
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                BasicText(
                    "implementation(\"com.github.zainkhalid91.compose-motion-icons:icons:0.3.0\")",
                    modifier = Modifier.padding(top = 12.dp),
                    style = TextStyle(color = Muted, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Header(
    color: Color,
    onColor: (Color) -> Unit,
    speed: Float,
    onSpeed: (Float) -> Unit,
    autoplay: Boolean,
    onAutoplay: (Boolean) -> Unit,
) {
    Column(Modifier.padding(bottom = 8.dp)) {
        BasicText("Compose Motion Icons", style = TextStyle(color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(6.dp))
        BasicText(
            "${IconCatalog.entries.size} icons that animate between states. Pure Compose, no Lottie. Tap any icon.",
            style = TextStyle(color = Muted, fontSize = 14.sp),
        )
        Spacer(Modifier.height(16.dp))
        // Wraps on phones: the gallery also runs on the web.
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            for (c in GalleryPalette) {
                Box(
                    Modifier
                        .size(26.dp)
                        .background(c, CircleShape)
                        .border(2.dp, if (c == color) Color.White else Color.Transparent, CircleShape)
                        .clickable(role = Role.RadioButton) { onColor(c) },
                )
            }
            Spacer(Modifier.size(4.dp))
            for ((value, label) in Speeds) Chip(label, speed == value) { onSpeed(value) }
            Spacer(Modifier.size(4.dp))
            Chip(if (autoplay) "Pause demo" else "Play demo", autoplay) { onAutoplay(!autoplay) }
        }
    }
}

@Composable
private fun CategoryHeader(name: String, count: Int) {
    Row(Modifier.padding(top = 14.dp, bottom = 2.dp), verticalAlignment = Alignment.Bottom) {
        BasicText(name, style = TextStyle(color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.size(8.dp))
        BasicText("$count", style = TextStyle(color = Muted, fontSize = 13.sp))
    }
}

@Composable
private fun Chip(label: String, selected: Boolean, onClick: () -> Unit) {
    BasicText(
        label,
        modifier = Modifier
            .background(if (selected) CardBorder else Color.Transparent, RoundedCornerShape(50))
            .border(1.dp, CardBorder, RoundedCornerShape(50))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        style = TextStyle(color = if (selected) Color.White else Muted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
    )
}

@Composable
private fun IconCard(entry: IconEntry, step: Int, onTap: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .widthIn(min = 120.dp)
            .background(Card, RoundedCornerShape(18.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .clickable(remember { MutableInteractionSource() }, indication = null, role = Role.Button, onClick = onTap)
            .padding(vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        entry.content(step, Modifier.size(48.dp))
        Spacer(Modifier.height(14.dp))
        BasicText(entry.name.removeSuffix("Icon"), style = TextStyle(color = Muted, fontSize = 12.sp))
    }
}
