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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.zainkhalid91.motionicons.BellIcon
import io.github.zainkhalid91.motionicons.CopyIcon
import io.github.zainkhalid91.motionicons.DownloadIcon
import io.github.zainkhalid91.motionicons.EyeIcon
import io.github.zainkhalid91.motionicons.FreezeMotionIcons
import io.github.zainkhalid91.motionicons.HeartIcon
import io.github.zainkhalid91.motionicons.LockIcon
import io.github.zainkhalid91.motionicons.MenuCloseIcon
import io.github.zainkhalid91.motionicons.MotionIconStyle
import io.github.zainkhalid91.motionicons.PlayPauseIcon
import io.github.zainkhalid91.motionicons.ProvideMotionIconStyle
import io.github.zainkhalid91.motionicons.StarIcon
import io.github.zainkhalid91.motionicons.ThemeIcon
import io.github.zainkhalid91.motionicons.VolumeIcon
import java.io.File
import kotlin.math.roundToInt

// Scene lengths in seconds: hook, grid, four close-ups, real screen, code, outro.
private val Scenes = floatArrayOf(4.4f, 5.0f, 2.3f, 2.3f, 2.3f, 2.3f, 5.4f, 3.6f, 3.6f)

private val White = GalleryPalette[0]
private val Grey = Color(0xFF6B7280)

/**
 * Renders the 1080 x 1350 (4:5) launch video, frame by frame, from the real library on a virtual
 * clock. Usage: demoreel <outDir>. tools/make-media.sh turns the frames into docs/media/demo-reel.mp4.
 */
fun main(args: Array<String>) {
    val out = File(args.getOrElse(0) { "build/demoreel" }).apply { mkdirs() }
    val total = (Scenes.sum() * Fps).roundToInt()
    val time = mutableFloatStateOf(0f)
    VirtualScene(1080, 1350, 3f) { Reel(time.floatValue) }.use { scene ->
        for (i in 0 until total) {
            time.floatValue = i / Fps.toFloat()
            scene.render(i * 1_000_000_000L / Fps).writePng(File(out, "%04d.png".format(i)))
        }
    }
    println("Wrote $total frames to ${out.absolutePath}")
}

@Composable
private fun Reel(t: Float) {
    var start = 0f
    var index = 0
    while (index < Scenes.size - 1 && t >= start + Scenes[index]) {
        start += Scenes[index]
        index++
    }
    val s = t - start
    val title: String
    val code: String
    val stage: @Composable () -> Unit
    when (index) {
        0 -> {
            title = "Most apps just swap icons"
            code = "// Same state change, two results"
            stage = { SwapScene(s) }
        }
        1 -> {
            title = "20 icons, one line each"
            code = "MenuCloseIcon(open = drawerOpen)"
            stage = { GridScene(s) }
        }
        2 -> {
            title = "Likes that pop and sparkle"
            code = "HeartIcon(liked = isLiked)"
            stage = { PairScene(s, { on, m -> HeartIcon(on, m) }, { on, m -> StarIcon(on, m) }) }
        }
        3 -> {
            title = "Bells ring. Locks spring open."
            code = "BellIcon(badge = hasUnread)"
            stage = { PairScene(s, { on, m -> BellIcon(on, m) }, { on, m -> LockIcon(!on, m) }) }
        }
        4 -> {
            title = "The sun rolls into a moon"
            code = "ThemeIcon(dark = isDarkTheme)"
            stage = { PairScene(s, { on, m -> ThemeIcon(on, m) }, { on, m -> EyeIcon(!on, m) }) }
        }
        5 -> {
            title = "Arrows drop into a tick"
            code = "DownloadIcon(done = isSaved)"
            stage = { PairScene(s, { on, m -> DownloadIcon(on, m) }, { on, m -> CopyIcon(on, m) }) }
        }
        6 -> {
            title = "Drops into real screens"
            code = "PlayPauseIcon(playing)   VolumeIcon(level)"
            stage = { PlayerScene(s) }
        }
        7 -> {
            title = "Pass a state. It animates."
            code = "FreezeMotionIcons { Screen() }  // for tests"
            stage = { CodeScene(s) }
        }
        else -> {
            title = "Free and open source"
            code = "implementation(\"com.github.zainkhalid91.\n    compose-motion-icons:icons:0.1.0\")"
            stage = { OutroScene(s) }
        }
    }
    ProvideMotionIconStyle(MotionIconStyle(color = White)) {
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
                // A fresh composition per scene, so each one starts from its first frame.
                key(index) { stage() }
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
            BasicText(
                "Pure Compose  ·  No Lottie  ·  Android, desktop, web",
                style = TextStyle(color = Muted, fontSize = 12.sp),
            )
        }
    }
}

/** A soft ring where a finger tapped, fading out over 0.4 s. */
private fun Modifier.tap(t: Float, vararg at: Float): Modifier = drawWithContent {
    drawContent()
    for (a in at) {
        val k = (t - a) / 0.4f
        if (k in 0f..1f) drawCircle(Color.White.copy(alpha = 0.3f * (1f - k)), radius = size.minDimension * (0.35f + 0.45f * k))
    }
}

@Composable
private fun SwapScene(s: Float) {
    val on = s >= 0.5f && s < 2.7f
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        SwapPanel("Swapping icons", Grey, frozen = true, on = on)
        SwapPanel("Motion Icons", White, frozen = false, on = on)
    }
}

@Composable
private fun SwapPanel(label: String, color: Color, frozen: Boolean, on: Boolean) {
    Column(
        Modifier.width(140.dp).height(200.dp).background(Background, RoundedCornerShape(18.dp)).padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicText(label, style = TextStyle(color = color, fontSize = 13.sp, fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.height(22.dp))
        val icons: @Composable () -> Unit = {
            ProvideMotionIconStyle(MotionIconStyle(color = color)) {
                Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(30.dp)) {
                        MenuCloseIcon(on, Modifier.size(38.dp))
                        PlayPauseIcon(on, Modifier.size(38.dp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(30.dp)) {
                        HeartIcon(on, Modifier.size(38.dp))
                        EyeIcon(!on, Modifier.size(38.dp))
                    }
                }
            }
        }
        if (frozen) FreezeMotionIcons(icons) else icons()
    }
}

@Composable
private fun GridScene(s: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        for (row in IconCatalog.entries.chunked(5)) {
            Row(horizontalArrangement = Arrangement.spacedBy(26.dp)) {
                for (entry in row) entry.content(rippleStep(entry, s, 0.3f, 2.7f), Modifier.size(32.dp))
            }
        }
    }
}

@Composable
private fun PairScene(
    s: Float,
    first: @Composable (Boolean, Modifier) -> Unit,
    second: @Composable (Boolean, Modifier) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(56.dp), verticalAlignment = Alignment.CenterVertically) {
        first(s >= 0.35f, Modifier.size(88.dp).tap(s, 0.35f))
        second(s >= 0.75f, Modifier.size(88.dp).tap(s, 0.75f))
    }
}

@Composable
private fun PlayerScene(s: Float) {
    val playing = s >= 0.6f
    val liked = s >= 1.6f
    val level = when {
        s < 2.6f -> 1f
        s < 3.2f -> 0.34f
        s < 4.4f -> 0f
        else -> 0.67f
    }
    Column(Modifier.width(272.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Column(Modifier.fillMaxWidth().background(Background, RoundedCornerShape(18.dp)).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(48.dp).background(
                        Brush.linearGradient(listOf(Color(0xFF7C9CFF), Color(0xFFF472B6))),
                        RoundedCornerShape(10.dp),
                    ),
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    BasicText("Midnight Drive", style = TextStyle(color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold))
                    BasicText("Compose FM", style = TextStyle(color = Muted, fontSize = 12.sp))
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                HeartIcon(liked, Modifier.size(30.dp).tap(s, 1.6f))
                Box(Modifier.size(52.dp).background(Color.White, RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
                    PlayPauseIcon(playing, Modifier.size(26.dp).tap(s, 0.6f), color = Background)
                }
                VolumeIcon(level, Modifier.size(30.dp).tap(s, 2.6f, 3.2f, 4.4f))
            }
        }
        val shown = s >= 3.8f
        Row(
            Modifier.fillMaxWidth().background(Background, RoundedCornerShape(14.dp)).padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                if (shown) "compose-rocks" else "•••••••••••••",
                modifier = Modifier.weight(1f),
                style = TextStyle(color = Color.White, fontSize = 15.sp, fontFamily = FontFamily.Monospace),
            )
            EyeIcon(shown, Modifier.size(26.dp).tap(s, 3.8f))
        }
    }
}

@Composable
private fun CodeScene(s: Float) {
    val liked = s >= 0.5f && s < 2.0f || s >= 2.6f
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(22.dp)) {
        BasicText(
            "var liked by remember {\n  mutableStateOf(false)\n}\n\nHeartIcon(\n  liked = liked,\n  modifier = Modifier\n    .clickable {\n      liked = !liked\n    }\n)",
            style = TextStyle(color = Color(0xFFB4C6FF), fontSize = 11.sp, fontFamily = FontFamily.Monospace),
        )
        HeartIcon(liked, Modifier.size(76.dp).tap(s, 0.5f, 2.0f, 2.6f))
    }
}

@Composable
private fun OutroScene(s: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            for (entry in IconCatalog.entries.take(6)) {
                entry.content(rippleStep(entry, s, 0.2f, 2.2f), Modifier.size(30.dp))
            }
        }
        Spacer(Modifier.height(22.dp))
        BasicText("github.com/zainkhalid91", style = TextStyle(color = Muted, fontSize = 14.sp))
        BasicText("compose-motion-icons", style = TextStyle(color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(14.dp))
        BasicText(
            "Try every icon in your browser",
            style = TextStyle(color = Color(0xFF34D399), fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
        )
    }
}
