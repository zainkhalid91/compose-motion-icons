package io.github.zainkhalid91.motionicons

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private const val SyncDegreesPerSecond = 300f

/**
 * Two arrows chasing round a circle. They tighten and spin while syncing and, when it stops, finish the turn
 * they are on and settle with a small overshoot. With animations turned off in the system, the
 * arrows stand still.
 *
 * @param syncing true makes the arrows spin.
 */
@Composable
public fun SyncIcon(
    syncing: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (syncing) "Syncing" else "Sync",
) {
    val clock = rememberIconClock(syncing)
    val tight = animateToggle(syncing, 360)
    val settle = remember { Animatable(0f) }
    val speed = LocalMotionIconStyle.current.speed
    val frozen = LocalFrozenIcons.current
    LaunchedEffect(syncing, frozen) {
        if (syncing) {
            // Picking up mid-settle continues from where the arrows are.
            if (settle.value != 0f) {
                clock.seconds = settle.value / SyncDegreesPerSecond
                settle.snapTo(0f)
            }
        } else {
            val angle = (clock.seconds * SyncDegreesPerSecond) % 360f
            clock.seconds = 0f
            if (angle > 0f && !frozen) {
                settle.snapTo(angle)
                val millis = (900f * (360f - angle) / 360f / speed).roundToInt().coerceAtLeast(120)
                settle.animateTo(360f, tween(millis, easing = SettleEasing))
            }
            settle.snapTo(0f)
        }
    }
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val angle = if (settle.value != 0f) settle.value else clock.seconds * SyncDegreesPerSecond
        // While syncing the arcs pull back from their heads, opening wider gaps between them.
        val cut = 40f * tight.value
        rotate(angle) {
            arc(12f, 12f, 8f, 200f + cut, 120f - cut)
            arc(12f, 12f, 8f, 20f + cut, 120f - cut)
            for (k in 0 until 2) {
                val a = ((320f + 180f * k) * PI / 180.0).toFloat()
                val hx = 12f + 8f * cos(a)
                val hy = 12f + 8f * sin(a)
                val tx = -sin(a)
                val ty = cos(a)
                line(hx, hy, hx - 2.4f * tx + 2f * cos(a), hy - 2.4f * ty + 2f * sin(a))
                line(hx, hy, hx - 2.4f * tx - 2f * cos(a), hy - 2.4f * ty - 2f * sin(a))
            }
        }
    }
}

// Decelerates into the rest angle and overshoots it slightly before settling.
private val SettleEasing = CubicBezierEasing(0.2f, 0.9f, 0.3f, 1.15f)

/**
 * An upload arrow inside a progress ring. The ring fills with the progress while the arrow rises,
 * and at 100% the arrow hops and the ring pulses. Screen readers hear "Uploaded".
 *
 * @param progress From 0 to 1.
 * @param doneColor Ring colour at 100%.
 */
@Composable
public fun UploadIcon(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    doneColor: Color = MotionIconColors.Success,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = when {
        progress >= 1f -> "Uploaded"
        progress <= 0f -> "Upload"
        else -> "Uploading ${(progress * 100).roundToInt()}%"
    },
) {
    val p = progress.coerceIn(0f, 1f)
    val level = animateLevel(p, 700)
    val done = animateFlourish(p >= 1f, 620)
    val complete = animateToggle(p >= 1f, 400)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription, live = true) {
        val v = level.value
        val f = done.value
        val pulse = if (f < 1f) 1.1f * bump(phase(f, 0.3f, 1f)) else 0f
        val ringColor = lerp(color, doneColor, complete.value)
        circle(12f, 12f, 9.5f, alpha = 0.25f)
        arc(12f, 12f, 9.5f + pulse, -90f, 360f * v, ringColor)
        val hop = if (f < 1f) 2.5f * bump(phase(f, 0f, 0.6f)) else 0f
        translate(0f, -1.5f * v - hop) {
            line(12f, 16.5f, 12f, 8.5f)
            polyline(UploadHead, 1f, index = 4)
        }
    }
}

private val UploadHead = floatArrayOf(8.5f, 12f, 12f, 8.5f, 15.5f, 12f)
