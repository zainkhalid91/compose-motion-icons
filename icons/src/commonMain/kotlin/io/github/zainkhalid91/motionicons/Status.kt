package io.github.zainkhalid91.motionicons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.Dp
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * A bell that rings (swinging, with its clapper lagging behind) as a badge pops onto its corner.
 * The badge cuts a clean gap out of the bell, so it reads on any background.
 *
 * @param badge true shows the badge. Turning it on rings the bell.
 * @param badgeColor Badge fill.
 */
@Composable
public fun BellIcon(
    badge: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    badgeColor: Color = MotionIconColors.Badge,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (badge) "Notifications, new" else "Notifications",
) {
    val progress = animateToggle(badge, 420)
    val ring = animateFlourish(badge, 1000)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val f = ring.value
        val swing = if (f < 1f) 18f * sin(f * PI.toFloat() * 5f) * (1f - f) else 0f
        layer {
            rotate(swing, 12f, 3.5f) {
                val bell = path(0)
                bell.moveTo(6.5f, 17.5f)
                bell.lineTo(6.5f, 11f)
                bell.cubicTo(6.5f, 7.4f, 8.9f, 5f, 12f, 5f)
                bell.cubicTo(15.1f, 5f, 17.5f, 7.4f, 17.5f, 11f)
                bell.lineTo(17.5f, 17.5f)
                strokePath(bell)
                line(4.5f, 17.5f, 19.5f, 17.5f)
                line(12f, 3f, 12f, 5f)
                // The clapper hangs from the rim and swings against the bell.
                rotate(-swing * 0.9f, 12f, 17.5f) { arc(12f, 18.6f, 2.2f, 20f, 140f) }
            }
            if (t > 0f) {
                val s = easeOutBack(t, 2.4f).coerceAtLeast(0f)
                clearCircle(18f, 6f, 4.8f * s.coerceAtMost(1f))
                circle(18f, 6f, 3.3f * s, badgeColor, fill = true)
            }
        }
    }
}

/**
 * Wi-Fi arcs that light up one by one as the signal grows and dim as it drops. Turning Wi-Fi off
 * slashes through them.
 *
 * @param strength Signal from 0 to 1. Each third lights one more arc.
 * @param enabled false dims every arc and draws the slash.
 */
@Composable
public fun WifiIcon(
    strength: Float,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (enabled) "Wi-Fi, ${(strength.coerceIn(0f, 1f) * 3).roundToInt()} of 3 bars" else "Wi-Fi off",
) {
    val level = animateLevel(if (enabled) strength.coerceIn(0f, 1f) else 0f, 600)
    val off = animateToggle(!enabled, 420)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val v = level.value
        layer {
            circle(12f, 19.5f, 1.3f, fill = true)
            for (i in 0 until 3) {
                val lit = ((v * 3f) - i).coerceIn(0f, 1f)
                val r = 4.6f + i * 4f
                // A lit arc swells slightly as it switches on.
                val swell = 0.6f * bump(lit)
                arc(12f, 19.5f, r + swell, 225f, 90f, alpha = 0.22f + 0.78f * lit)
            }
            slash(off.value)
        }
    }
}

/**
 * An arrow that drops into its tray, then folds into a tick. Screen readers hear "Downloaded".
 *
 * @param done true shows the tick.
 * @param doneColor Tick colour when done.
 */
@Composable
public fun DownloadIcon(
    done: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    doneColor: Color = MotionIconColors.Success,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (done) "Downloaded" else "Download",
) {
    val progress = animateToggle(done, 720)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription, live = true) {
        val t = progress.value
        val give = 1.2f * bump(phase(t, 0.25f, 0.6f))
        translate(0f, give) {
            val tray = path(0)
            tray.moveTo(4f, 15f)
            tray.lineTo(4f, 19.5f)
            tray.lineTo(20f, 19.5f)
            tray.lineTo(20f, 15f)
            strokePath(tray)
        }
        val drop = phase(t, 0f, 0.45f)
        val top = lerp(3.5f, 13.5f, drop * drop)
        line(12f, top, 12f, 14f, alpha = 1f - drop)
        val m = easeOutBack(phase(t, 0.35f, 1f), 1.6f)
        val c = lerp(color, doneColor, phase(t, 0.35f, 0.8f))
        val head = path(1)
        head.moveTo(lerp(7f, 7f, m), lerp(9f, 11f, m))
        head.lineTo(lerp(12f, 10.5f, m), lerp(14f, 14.8f, m))
        head.lineTo(lerp(17f, 17.5f, m), lerp(9f, 7.2f, m))
        strokePath(head, c)
    }
}

/**
 * Two sheets that slide together, round off into a badge and get a tick. The classic "copied"
 * moment for code blocks, links and share sheets. Screen readers hear "Copied".
 *
 * @param copied true shows the tick.
 * @param doneColor Badge and tick colour when copied.
 */
@Composable
public fun CopyIcon(
    copied: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    doneColor: Color = MotionIconColors.Success,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (copied) "Copied" else "Copy",
) {
    val progress = animateToggle(copied, 620)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription, live = true) {
        val t = progress.value
        val slide = easeInOut(phase(t, 0f, 0.45f))
        if (slide < 1f) {
            translate(4f * slide, 4f * slide) {
                val back = path(0)
                back.moveTo(4f, 15.5f)
                back.lineTo(4f, 6f)
                back.quadraticTo(4f, 4f, 6f, 4f)
                back.lineTo(15.5f, 4f)
                strokePath(back, alpha = 1f - slide)
            }
        }
        val k = easeOutBack(phase(t, 0.25f, 0.8f), 1.3f)
        val c = lerp(color, doneColor, phase(t, 0.3f, 0.7f))
        val r = lerp(2f, 8.5f, k.coerceIn(0f, 1f))
        val front = path(1)
        front.addRoundRect(RoundRect(lerp(8f, 3.5f, k), lerp(8f, 3.5f, k), lerp(20f, 20.5f, k), lerp(20f, 20.5f, k), CornerRadius(r, r)))
        strokePath(front, c)
        polyline(CopyTick, easeInOut(phase(t, 0.5f, 1f)), c)
    }
}

private val CopyTick = floatArrayOf(8f, 12.3f, 11f, 15.3f, 16.5f, 9f)

/**
 * A rounded box that fills with a squeeze while its tick draws in.
 *
 * @param checked true shows the filled, ticked box.
 * @param checkColor Tick colour. Unspecified (the default) cuts the tick out of the fill, so the
 *   background shows through and it works on any colour.
 */
@Composable
public fun CheckboxIcon(
    checked: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    checkColor: Color = Color.Unspecified,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (checked) "Checked" else "Not checked",
) {
    val progress = animateToggle(checked, 380)
    val squeeze = animateFlourish(checked, 420)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val box = path(0)
        box.addRoundRect(RoundRect(3.5f, 3.5f, 20.5f, 20.5f, CornerRadius(4.5f, 4.5f)))
        scale(1f - 0.14f * bump(squeeze.value)) {
            layer {
                strokePath(box)
                val fill = easeInOut(phase(t, 0f, 0.55f))
                if (fill > 0f) scale(fill) { fillPath(box) }
                val tick = easeInOut(phase(t, 0.3f, 1f))
                if (checkColor.isSpecified) {
                    polyline(CheckTick, tick, checkColor)
                } else {
                    polyline(CheckTick, tick, Color.Black, blendMode = BlendMode.Clear)
                }
            }
        }
    }
}

private val CheckTick = floatArrayOf(7.5f, 12.3f, 10.6f, 15.4f, 16.5f, 9f)
