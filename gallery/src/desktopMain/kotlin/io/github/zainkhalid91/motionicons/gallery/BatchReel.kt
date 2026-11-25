package io.github.zainkhalid91.motionicons.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.zainkhalid91.motionicons.ApertureIcon
import io.github.zainkhalid91.motionicons.CameraFlipIcon
import io.github.zainkhalid91.motionicons.CastIcon
import io.github.zainkhalid91.motionicons.EqualizerIcon
import io.github.zainkhalid91.motionicons.FullscreenIcon
import io.github.zainkhalid91.motionicons.MotionIconStyle
import io.github.zainkhalid91.motionicons.PipIcon
import io.github.zainkhalid91.motionicons.PlayPauseIcon
import io.github.zainkhalid91.motionicons.ProvideMotionIconStyle
import io.github.zainkhalid91.motionicons.RecordIcon
import io.github.zainkhalid91.motionicons.RepeatIcon
import io.github.zainkhalid91.motionicons.ShuffleIcon
import io.github.zainkhalid91.motionicons.SpeedIcon
import java.io.File
import kotlin.math.roundToInt

// Scene lengths in seconds: player hook, the new ten, three close-ups, outro.
private val BatchScenes = floatArrayOf(4.2f, 4.4f, 2.3f, 2.3f, 2.3f, 3.6f)

private const val BatchName = "Media and camera"
private const val BatchId = "2026-W49"

/**
 * Renders the 1080 x 1350 video for the latest batch, frame by frame. Usage: batchreel <outDir>.
 * tools/make-media.sh turns the frames into docs/media/batch-reel.mp4.
 */
fun main(args: Array<String>) {
    val out = File(args.getOrElse(0) { "build/batchreel" }).apply { mkdirs() }
    val total = (BatchScenes.sum() * Fps).roundToInt()
    val time = mutableFloatStateOf(0f)
    VirtualScene(1080, 1350, 3f) { BatchReel(time.floatValue) }.use { scene ->
        for (i in 0 until total) {
            time.floatValue = i / Fps.toFloat()
            scene.render(i * 1_000_000_000L / Fps).writePng(File(out, "%04d.png".format(i)))
        }
    }
    println("Wrote $total frames to ${out.absolutePath}")
}

@Composable
private fun BatchReel(t: Float) {
    var start = 0f
    var index = 0
    while (index < BatchScenes.size - 1 && t >= start + BatchScenes[index]) {
        start += BatchScenes[index]
        index++
    }
    val s = t - start
    val batch = IconCatalog.entries.filter { it.batch == BatchId }
    val (title, code) = when (index) {
        0 -> "Your video player, animated" to "FullscreenIcon(fullscreen = isFullscreen)"
        1 -> "10 new icons: $BatchName" to "EqualizerIcon(playing = isPlaying)"
        2 -> "Recording starts with a ripple" to "RecordIcon(recording = isRecording)"
        3 -> "Shutters twist shut" to "ApertureIcon(open = isOpen)"
        4 -> "Casting floods the screen" to "CastIcon(connected = isCasting)"
        else -> "${IconCatalog.entries.size} icons now, 50 on the way" to "implementation(\"com.github.zainkhalid91.\n    compose-motion-icons:icons:0.2.0\")"
    }
    ProvideMotionIconStyle(MotionIconStyle(color = GalleryPalette[0])) {
        Column(
            Modifier.fillMaxSize().background(Background).padding(horizontal = 24.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BasicText("Compose Motion Icons", style = TextStyle(color = Muted, fontSize = 15.sp, fontWeight = FontWeight.SemiBold))
            Spacer(Modifier.height(8.dp))
            BasicText(title, style = TextStyle(color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(16.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Card, RoundedCornerShape(24.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center,
            ) {
                key(index) {
                    when (index) {
                        0 -> PlayerHook(s)
                        1 -> NewTen(s, batch)
                        2 -> PairScene(s, { on, m -> RecordIcon(on, m) }, { on, m -> EqualizerIcon(on, m) })
                        3 -> PairScene(s, { on, m -> ApertureIcon(!on, m) }, { on, m -> CameraFlipIcon(on, m) })
                        4 -> PairScene(s, { on, m -> CastIcon(on, m) }, { on, m -> PipIcon(on, m) })
                        else -> AllIcons(s)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            BasicText(
                code,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF12151B), RoundedCornerShape(12.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                style = TextStyle(color = Color(0xFFB4C6FF), fontSize = 10.5.sp, fontFamily = FontFamily.Monospace),
            )
            Spacer(Modifier.weight(1f))
            BasicText("Pure Compose  ·  No Lottie  ·  Android, desktop, web", style = TextStyle(color = Muted, fontSize = 12.sp))
        }
    }
}

/** A video player whose every control animates as it's tapped, one after another. */
@Composable
private fun PlayerHook(s: Float) {
    val playing = s >= 0.3f
    Column(
        Modifier
            .width(290.dp)
            .height(214.dp)
            .background(
                Brush.verticalGradient(listOf(Color(0xFF2B3A67), Color(0xFF0B0D12))),
                RoundedCornerShape(16.dp),
            )
            .padding(14.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            RecordIcon(s >= 3.2f, Modifier.size(24.dp).tap(s, 3.2f))
            CastIcon(s >= 2.4f, Modifier.size(24.dp).tap(s, 2.4f))
        }
        Spacer(Modifier.weight(1f))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(Modifier.size(54.dp).background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
                PlayPauseIcon(playing, Modifier.size(28.dp).tap(s, 0.3f))
            }
        }
        Spacer(Modifier.weight(1f))
        // A progress line that runs while playing.
        Box(Modifier.fillMaxWidth().height(3.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp))) {
            val k = if (playing) ((s - 0.3f) / 6f).coerceIn(0f, 1f) * 0.6f + 0.15f else 0.15f
            Box(Modifier.fillMaxWidth(k).height(3.dp).background(Color(0xFFF472B6), RoundedCornerShape(2.dp)))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            ShuffleIcon(s >= 0.9f, Modifier.size(24.dp).tap(s, 0.9f))
            RepeatIcon(s >= 1.3f, Modifier.size(24.dp).tap(s, 1.3f, 1.9f), one = s >= 1.9f)
            EqualizerIcon(playing, Modifier.size(24.dp))
            SpeedIcon(if (s >= 2.8f) 0.85f else 0.4f, Modifier.size(24.dp).tap(s, 2.8f))
            PipIcon(s >= 3.6f, Modifier.size(24.dp).tap(s, 3.6f))
            FullscreenIcon(s >= 1.6f, Modifier.size(24.dp).tap(s, 1.6f))
        }
    }
}

@Composable
private fun NewTen(s: Float, batch: List<IconEntry>) {
    Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
        for (row in batch.chunked(5)) {
            Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                for (entry in row) {
                    val n = batch.indexOf(entry)
                    val on = s >= 0.3f + n * 0.12f && s < 2.9f + n * 0.08f
                    entry.content(if (on) entry.steps - 1 else 0, Modifier.size(42.dp))
                }
            }
        }
    }
}

@Composable
private fun AllIcons(s: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        for (row in IconCatalog.entries.chunked(6)) {
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                for (entry in row) entry.content(rippleStep(entry, s, 0.2f, 2.3f), Modifier.size(26.dp))
            }
        }
    }
}
