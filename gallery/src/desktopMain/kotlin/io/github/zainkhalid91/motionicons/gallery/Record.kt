package io.github.zainkhalid91.motionicons.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.zainkhalid91.motionicons.EyeIcon
import io.github.zainkhalid91.motionicons.FreezeMotionIcons
import io.github.zainkhalid91.motionicons.HeartIcon
import io.github.zainkhalid91.motionicons.MenuCloseIcon
import io.github.zainkhalid91.motionicons.MotionIconStyle
import io.github.zainkhalid91.motionicons.PlayPauseIcon
import io.github.zainkhalid91.motionicons.ProvideMotionIconStyle
import java.io.File

internal const val Fps = 30

/** Renders the README media frame by frame. Usage: record <outDir>. tools/make-gifs.sh encodes them. */
fun main(args: Array<String>) {
    val out = File(args.getOrElse(0) { "build/frames" })
    for (entry in IconCatalog.entries) {
        record(File(out, entry.name), 200, 200, 3.2f) { t ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                entry.content(singleStep(entry, t), Modifier.size(56.dp))
            }
        }
    }
    record(File(out, "all"), 1600, gridHeightPx(), 5.0f) { t -> GridDemo(t) }
    record(File(out, "before-after"), 1240, 400, 3.6f) { t -> BeforeAfter(t) }
    File(out, "icons.json").writeText(IconCatalog.previewManifest())
    println("Wrote frames to ${out.absolutePath}")
}

internal fun record(dir: File, width: Int, height: Int, seconds: Float, content: @Composable (Float) -> Unit) {
    dir.mkdirs()
    val time = mutableFloatStateOf(0f)
    val frames = (seconds * Fps).toInt()
    VirtualScene(width, height, 2f) {
        ProvideMotionIconStyle(MotionIconStyle(color = GalleryPalette[0])) {
            Box(Modifier.fillMaxSize().background(Background)) { content(time.floatValue) }
        }
    }.use { scene ->
        for (i in 0 until frames) {
            time.floatValue = i / Fps.toFloat()
            scene.render(i * 1_000_000_000L / Fps).writePng(File(dir, "%03d.png".format(i)))
        }
    }
}

/**
 * The state of one icon at time [t] in a 3.2 s loop: on/off icons switch at 0.5 s and back at
 * 1.9 s; level icons step through every level and end where they started.
 */
internal fun singleStep(entry: IconEntry, t: Float): Int = if (entry.steps == 2) {
    if (t >= 0.5f && t < 1.9f) 1 else 0
} else {
    val k = ((t - 0.3f) / 0.75f).toInt() + 1
    if (t < 0.3f) 0 else k.coerceAtMost(entry.steps) % entry.steps
}

/** The state for the grid recording: a ripple on in reading order, then a ripple back off. */
internal fun rippleStep(entry: IconEntry, t: Float, on: Float, off: Float, order: Int = entry.id - 1): Int {
    val delay = order * 0.05f
    val active = t >= on + delay && t < off + delay
    // Level icons show their most different state: muted, Wi-Fi off.
    return if (active) entry.steps - 1 else 0
}

// The grid recording: every icon, grouped by category, eight to a row.
private const val GridColumns = 8
private const val GridCell = 86
private const val GridGap = 8
private const val GridPad = 24
private const val GridHeader = 34

private fun gridHeightPx(): Int {
    val groups = IconCatalog.grouped.filter { it.second.isNotEmpty() }
    val rows = groups.sumOf { (it.second.size + GridColumns - 1) / GridColumns }
    val dp = 2 * GridPad + groups.size * GridHeader + rows * (GridCell + GridGap)
    return dp * 2
}

@Composable
private fun GridDemo(t: Float) {
    var order = 0
    Column(Modifier.fillMaxSize().padding(GridPad.dp)) {
        for ((category, group) in IconCatalog.grouped) {
            if (group.isEmpty()) continue
            Box(Modifier.height(GridHeader.dp), contentAlignment = Alignment.BottomStart) {
                BasicText(
                    category,
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = TextStyle(color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                )
            }
            for (row in group.chunked(GridColumns)) {
                Row(Modifier.padding(bottom = GridGap.dp), horizontalArrangement = Arrangement.spacedBy(GridGap.dp)) {
                    for (entry in row) {
                        val index = order++
                        Column(
                            Modifier
                                .size(GridCell.dp)
                                .background(Card, RoundedCornerShape(16.dp))
                                .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            entry.content(rippleStep(entry, t, 0.4f, 2.5f, index), Modifier.size(34.dp))
                            Spacer(Modifier.height(9.dp))
                            BasicText(entry.name.removeSuffix("Icon"), style = TextStyle(color = Muted, fontSize = 10.sp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BeforeAfter(t: Float) {
    val on = t >= 0.6f && t < 2.2f
    Row(
        Modifier.fillMaxSize().padding(28.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Panel("Swapping two icons", Color(0xFF6B7280), frozen = true, on = on)
        Panel("Compose Motion Icons", GalleryPalette[0], frozen = false, on = on)
    }
}

@Composable
private fun Panel(title: String, color: Color, frozen: Boolean, on: Boolean) {
    Column(
        Modifier
            .width(272.dp)
            .height(144.dp)
            .background(Card, RoundedCornerShape(20.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicText(title, style = TextStyle(color = color, fontSize = 15.sp, fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.height(24.dp))
        val icons: @Composable () -> Unit = {
            ProvideMotionIconStyle(MotionIconStyle(color = color)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MenuCloseIcon(on, Modifier.size(44.dp))
                    PlayPauseIcon(on, Modifier.size(44.dp))
                    HeartIcon(on, Modifier.size(44.dp))
                    EyeIcon(!on, Modifier.size(44.dp))
                }
            }
        }
        if (frozen) FreezeMotionIcons(icons) else icons()
    }
}
