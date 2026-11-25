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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.zainkhalid91.motionicons.CartIcon
import io.github.zainkhalid91.motionicons.FilterSortIcon
import io.github.zainkhalid91.motionicons.GiftIcon
import io.github.zainkhalid91.motionicons.MailIcon
import io.github.zainkhalid91.motionicons.MotionIconStyle
import io.github.zainkhalid91.motionicons.PhoneIcon
import io.github.zainkhalid91.motionicons.ProvideMotionIconStyle
import io.github.zainkhalid91.motionicons.ReplyIcon
import io.github.zainkhalid91.motionicons.SyncIcon
import io.github.zainkhalid91.motionicons.ThumbUpIcon
import io.github.zainkhalid91.motionicons.TypingIcon
import io.github.zainkhalid91.motionicons.UploadIcon
import java.io.File
import kotlin.math.roundToInt

/*
 * The LinkedIn video for the latest batch, 1080 x 1350 (4:5, the tallest a feed shows in full).
 * Built for a phone with the sound off: the first frame is already moving under a bold headline
 * (it doubles as the thumbnail), text is large, each scene says one thing, story-style bars show
 * how long is left, and it ends on the install line and where to find the link.
 */

private class Scene(val seconds: Float, val chip: String?, val headline: String)

private val Scenes = listOf(
    Scene(3.0f, null, "Chat and shop icons\nthat actually move"),
    Scene(3.2f, "Messaging", "Mail opens.\nCalls hang up."),
    Scene(3.2f, "Shopping", "Items drop\ninto the cart"),
    Scene(3.2f, "Status", "Sync spins,\nthen settles"),
    Scene(4.4f, "In your app", "Drop them into\na real screen"),
    Scene(4.0f, null, "40 icons.\nFree and open source."),
)

private const val BatchId = "2026-W50"
private const val Version = "0.3.0"
private val Accent = Color(0xFF7C9CFF)

/** Renders the video frame by frame. Usage: batchreel <outDir>. tools/make-media.sh encodes it. */
fun main(args: Array<String>) {
    val out = File(args.getOrElse(0) { "build/batchreel" }).apply { mkdirs() }
    out.listFiles()?.forEach { it.delete() }
    val total = (Scenes.sumOf { it.seconds.toDouble() } * Fps).roundToInt()
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
    while (index < Scenes.size - 1 && t >= start + Scenes[index].seconds) {
        start += Scenes[index].seconds
        index++
    }
    val s = t - start
    val scene = Scenes[index]
    // Each scene's text eases in over its first quarter second, except the first: frame one is
    // the thumbnail, so the hook headline is there from the start.
    val enter = if (index == 0) 1f else (s / 0.25f).coerceIn(0f, 1f)
    val last = index == Scenes.lastIndex
    ProvideMotionIconStyle(MotionIconStyle(color = GalleryPalette[0])) {
        Column(Modifier.fillMaxSize().background(Background).padding(horizontal = 22.dp, vertical = 18.dp)) {
            StoryBars(index, s / scene.seconds)
            Spacer(Modifier.height(12.dp))
            BasicText("Compose Motion Icons", style = TextStyle(color = Muted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold))
            Spacer(Modifier.height(10.dp))
            Box(Modifier.height(24.dp)) {
                if (scene.chip != null) {
                    BasicText(
                        scene.chip,
                        modifier = Modifier
                            .alpha(enter)
                            .background(Accent.copy(alpha = 0.16f), RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        style = TextStyle(color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold),
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            BasicText(
                scene.headline,
                modifier = Modifier.alpha(enter).padding(top = (6f * (1f - enter)).dp).height(72.dp),
                style = TextStyle(color = Color.White, fontSize = 27.sp, lineHeight = 34.sp, fontWeight = FontWeight.ExtraBold),
            )
            Spacer(Modifier.height(12.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(if (last) 164.dp else 200.dp)
                    .background(Card, RoundedCornerShape(24.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center,
            ) {
                key(index) {
                    when (index) {
                        0 -> Hook(s)
                        1 -> Messaging(s)
                        2 -> Shopping(s)
                        3 -> Status(s)
                        4 -> ChatScreen(s)
                        else -> AllForty(s)
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Footer(index)
        }
    }
}

/** One bar per scene, like stories: done, filling, waiting. */
@Composable
private fun StoryBars(index: Int, k: Float) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in Scenes.indices) {
            Box(Modifier.weight(1f).height(3.dp).background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(2.dp))) {
                val fill = when {
                    i < index -> 1f
                    i == index -> k.coerceIn(0f, 1f)
                    else -> 0f
                }
                if (fill > 0f) Box(Modifier.fillMaxWidth(fill).height(3.dp).background(Color.White, RoundedCornerShape(2.dp)))
            }
        }
    }
}

@Composable
private fun Footer(index: Int) {
    if (index == Scenes.lastIndex) {
        Column {
            BasicText(
                "implementation(\"com.github.zainkhalid91.\n    compose-motion-icons:icons:$Version\")",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF12151B), RoundedCornerShape(12.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                style = TextStyle(color = Color(0xFFB4C6FF), fontSize = 10.5.sp, fontFamily = FontFamily.Monospace),
            )
            Spacer(Modifier.height(12.dp))
            BasicText(
                "Tap them live in your browser: link in the comments",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center),
            )
        }
    } else {
        BasicText(
            "Pure Compose  ·  No Lottie  ·  Android, desktop, web",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(color = Muted, fontSize = 11.sp, textAlign = TextAlign.Center),
        )
    }
}

/** The ten new icons, all switching at once in a quick ripple: motion on the very first frame. */
@Composable
private fun Hook(s: Float) {
    val batch = IconCatalog.entries.filter { it.batch == BatchId }
    Column(verticalArrangement = Arrangement.spacedBy(26.dp)) {
        for (row in batch.chunked(5)) {
            Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                for (entry in row) {
                    val n = batch.indexOf(entry)
                    // Already mid-change at t = 0, so the thumbnail isn't a still grid.
                    val on = s >= n * 0.06f - 0.2f && s < 1.7f + n * 0.06f
                    entry.content(if (on) entry.steps - 1 else 0, Modifier.size(40.dp))
                }
            }
        }
    }
}

@Composable
private fun Labeled(label: String, size: Dp = 58.dp, content: @Composable (Modifier) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        content(Modifier.size(size))
        Spacer(Modifier.height(12.dp))
        BasicText(label, style = TextStyle(color = Muted, fontSize = 11.sp))
    }
}

@Composable
private fun Messaging(s: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
        Labeled("Mail") { m -> MailIcon(s >= 0.3f, m.tap(s, 0.3f)) }
        Labeled("Phone") { m -> PhoneIcon(s >= 0.8f && s < 2.4f, m.tap(s, 0.8f, 2.4f)) }
        Labeled("Typing") { m -> TypingIcon(s >= 1.2f, m) }
        Labeled("Reply") { m -> ReplyIcon(s >= 1.7f, m.tap(s, 1.7f)) }
    }
}

@Composable
private fun Shopping(s: Float) {
    val count = when {
        s >= 1.5f -> 3
        s >= 0.9f -> 2
        s >= 0.3f -> 1
        else -> 0
    }
    Row(horizontalArrangement = Arrangement.spacedBy(34.dp), verticalAlignment = Alignment.CenterVertically) {
        Labeled("Cart", 66.dp) { m -> CartIcon(count, m.tap(s, 0.3f, 0.9f, 1.5f)) }
        Labeled("FilterSort", 66.dp) { m -> FilterSortIcon(s >= 2.0f, m.tap(s, 2.0f)) }
        Labeled("Gift", 66.dp) { m -> GiftIcon(s >= 2.4f, m.tap(s, 2.4f)) }
    }
}

@Composable
private fun Status(s: Float) {
    val progress = when {
        s >= 2.0f -> 1f
        s >= 1.3f -> 0.75f
        s >= 0.6f -> 0.4f
        else -> 0f
    }
    Row(horizontalArrangement = Arrangement.spacedBy(34.dp), verticalAlignment = Alignment.CenterVertically) {
        Labeled("Sync", 66.dp) { m -> SyncIcon(s >= 0.2f && s < 1.9f, m.tap(s, 0.2f, 1.9f)) }
        Labeled("Upload", 66.dp) { m -> UploadIcon(progress, m) }
        Labeled("ThumbUp", 66.dp) { m -> ThumbUpIcon(s >= 2.3f, m.tap(s, 2.3f)) }
    }
}

/** A small chat screen where the icons do their jobs: typing, a reply, a like, a call. */
@Composable
private fun ChatScreen(s: Float) {
    val bubble = Color(0xFF232733)
    val mine = Color(0xFF3B4C86)
    Column(
        Modifier
            .width(286.dp)
            .height(180.dp)
            .background(Background, RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(22.dp).background(Accent, RoundedCornerShape(50)))
            Spacer(Modifier.width(8.dp))
            BasicText("Sam", style = TextStyle(color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold))
            Spacer(Modifier.weight(1f))
            PhoneIcon(s >= 3.3f, Modifier.size(20.dp).tap(s, 3.3f))
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicText(
                "Lunch tomorrow?",
                modifier = Modifier.background(bubble, RoundedCornerShape(12.dp)).padding(horizontal = 10.dp, vertical = 6.dp),
                style = TextStyle(color = Color.White, fontSize = 11.sp),
            )
            Spacer(Modifier.width(6.dp))
            ThumbUpIcon(s >= 1.0f, Modifier.size(18.dp).tap(s, 1.0f))
        }
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth().height(30.dp), contentAlignment = Alignment.CenterEnd) {
            if (s >= 2.3f) {
                BasicText(
                    "Yes! 1pm works",
                    modifier = Modifier.background(mine, RoundedCornerShape(12.dp)).padding(horizontal = 10.dp, vertical = 6.dp),
                    style = TextStyle(color = Color.White, fontSize = 11.sp),
                )
            } else {
                Box(Modifier.background(bubble, RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                    TypingIcon(s >= 0.2f && s < 2.3f, Modifier.size(22.dp))
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            Modifier.fillMaxWidth().background(bubble, RoundedCornerShape(12.dp)).padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ReplyIcon(s >= 1.6f, Modifier.size(18.dp).tap(s, 1.6f))
            Spacer(Modifier.width(8.dp))
            BasicText("Message", style = TextStyle(color = Muted, fontSize = 11.sp))
            Spacer(Modifier.weight(1f))
            MailIcon(s >= 2.8f, Modifier.size(18.dp).tap(s, 2.8f))
        }
    }
}

/** Every icon, grouped as in the gallery, rippling on and off in reading order. */
@Composable
private fun AllForty(s: Float) {
    var order = 0
    val all = IconCatalog.grouped.flatMap { it.second }
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        for (row in all.chunked(8)) {
            Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                for (entry in row) entry.content(rippleStep(entry, s, 0.2f, 2.2f, order++), Modifier.size(22.dp))
            }
        }
    }
}
