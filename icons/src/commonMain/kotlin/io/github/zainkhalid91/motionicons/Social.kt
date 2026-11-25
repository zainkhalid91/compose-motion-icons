package io.github.zainkhalid91.motionicons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A heart that fills from its centre, squashes, pops and throws out a ring of sparks when liked.
 * Unliking empties it quietly. Already-liked hearts never burst when they first appear.
 *
 * @param liked true shows the filled heart.
 * @param activeColor Fill and spark colour when liked.
 */
@Composable
public fun HeartIcon(
    liked: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    activeColor: Color = MotionIconColors.Heart,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (liked) "Unlike" else "Like",
) {
    val progress = animateToggle(liked, 360)
    val burst = animateFlourish(liked, 700)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val f = burst.value
        val heart = path(0)
        heart.moveTo(12f, 20.5f)
        heart.cubicTo(12f, 20.5f, 2.5f, 14.8f, 2.5f, 8.7f)
        heart.cubicTo(2.5f, 5.7f, 4.8f, 3.5f, 7.6f, 3.5f)
        heart.cubicTo(9.5f, 3.5f, 11.1f, 4.6f, 12f, 6.2f)
        heart.cubicTo(12.9f, 4.6f, 14.5f, 3.5f, 16.4f, 3.5f)
        heart.cubicTo(19.2f, 3.5f, 21.5f, 5.7f, 21.5f, 8.7f)
        heart.cubicTo(21.5f, 14.8f, 12f, 20.5f, 12f, 20.5f)
        heart.close()
        val pop = 1f - 0.2f * bump(phase(f, 0f, 0.3f)) + 0.14f * bump(phase(f, 0.25f, 0.65f))
        scale(pop) {
            strokePath(heart, lerp(color, activeColor, t))
            if (t > 0f) scale(easeOutBack(t, 1.2f).coerceAtMost(1.06f)) { fillPath(heart, activeColor) }
        }
        sparks(f, activeColor, 8, 9.5f, 13f, 12f, 12f)
    }
}

/**
 * A star that spins one point round, fills and sheds sparkles from its tips when starred.
 *
 * @param starred true shows the filled star.
 * @param activeColor Fill and sparkle colour when starred.
 */
@Composable
public fun StarIcon(
    starred: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    activeColor: Color = MotionIconColors.Star,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (starred) "Remove star" else "Star",
) {
    val progress = animateToggle(starred, 560)
    val burst = animateFlourish(starred, 700)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val star = StarPath
        rotate(72f * easeOutBack(t, 1.4f), 12f, 12.8f) {
            strokePath(star, lerp(color, activeColor, t))
            if (t > 0f) scale(easeOutBack(t, 1.4f).coerceAtMost(1.05f), py = 12.8f) { fillPath(star, activeColor) }
        }
        sparks(burst.value, activeColor, 5, 10.5f, 13.5f, 12f, 12.8f)
    }
}

/**
 * A bookmark that fills from the top like a ribbon dropping into place, with a little stretch.
 *
 * @param saved true shows the filled bookmark.
 */
@Composable
public fun BookmarkIcon(
    saved: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (saved) "Remove bookmark" else "Bookmark",
) {
    val progress = animateToggle(saved, 420)
    val drop = animateFlourish(saved, 520)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val mark = path(0)
        mark.moveTo(6f, 3.5f)
        mark.lineTo(18f, 3.5f)
        mark.lineTo(18f, 20.5f)
        mark.lineTo(12f, 16.5f)
        mark.lineTo(6f, 20.5f)
        mark.close()
        scale(1f, 1f + 0.12f * bump(drop.value), py = 3.5f) {
            strokePath(mark)
            if (t > 0f) {
                // The fill clip runs a little past the notch so the last frames close it cleanly.
                draw.clipRect(0f, 0f, Grid, lerp(2.5f, 21.5f, t)) { fillPath(mark) }
            }
        }
    }
}

/**
 * A paper plane that flies off the top-right corner and is replaced by a tick. Going back, a
 * new plane glides in from the bottom-left.
 *
 * @param sent true shows the tick.
 */
@Composable
public fun SendIcon(
    sent: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (sent) "Sent" else "Send",
) {
    val progress = animateToggle(sent, 760)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription, live = true) {
        val t = progress.value
        val away = phase(t, 0f, 0.55f)
        if (away < 1f) {
            val e = away * away
            // Leaving goes up and right; coming back arrives from the opposite corner.
            val dir = if (sent) 1f else -1f
            translate(16f * e * dir, -16f * e * dir) {
                scale(1f - 0.45f * e) {
                    val plane = path(0)
                    plane.moveTo(21.5f, 2.5f)
                    plane.lineTo(14.5f, 21.5f)
                    plane.lineTo(10.8f, 13.2f)
                    plane.lineTo(2.5f, 9.5f)
                    plane.close()
                    strokePath(plane, alpha = 1f - away)
                    line(21.5f, 2.5f, 10.8f, 13.2f, alpha = 1f - away)
                }
            }
        }
        polyline(Tick, easeInOut(phase(t, 0.5f, 1f)))
    }
}

/** The star never changes shape, so it is built once and shared. */
private val StarPath: Path by lazy {
    Path().apply {
        for (i in 0 until 10) {
            val r = if (i % 2 == 0) 9.8f else 4.2f
            val a = (-PI / 2 + i * PI / 5).toFloat()
            val x = 12f + r * cos(a)
            val y = 12.8f + r * sin(a)
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
}

private val Tick = floatArrayOf(5f, 12.5f, 10f, 17.5f, 19f, 7f)

/** A ring of short spark strokes flying outward over [f] (0 to 1); nothing when [f] is 1. */
internal fun IconScope.sparks(f: Float, c: Color, count: Int, from: Float, to: Float, cx: Float, cy: Float) {
    if (f >= 1f || f <= 0.12f) return
    val k = phase(f, 0.12f, 1f)
    val travel = 1f - (1f - k) * (1f - k)
    val outer = lerp(from, to, travel)
    val length = 2.2f * (1f - k)
    val alpha = 1f - phase(k, 0.5f, 1f)
    for (i in 0 until count) {
        val a = (-PI / 2 + i * 2 * PI / count + PI / count).toFloat()
        val dx = cos(a)
        val dy = sin(a)
        line(cx + dx * (outer - length), cy + dy * (outer - length), cx + dx * outer, cy + dy * outer, c, alpha)
    }
}

/** Accent colours used by the icons that light up. Each icon takes its own as a parameter too. */
public object MotionIconColors {
    public val Heart: Color = Color(0xFFF43F5E)
    public val Star: Color = Color(0xFFF59E0B)
    public val Badge: Color = Color(0xFFEF4444)
    public val Success: Color = Color(0xFF16A34A)
}
