package io.github.zainkhalid91.motionicons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * A red record dot that squares off into a stop button with a quarter turn, sending a ripple
 * out through its ring when recording starts.
 *
 * @param recording true shows the stop square.
 * @param activeColor Dot and square colour.
 */
@Composable
public fun RecordIcon(
    recording: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    activeColor: Color = MotionIconColors.Badge,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (recording) "Stop recording" else "Record",
) {
    val progress = animateToggle(recording, 460)
    val ripple = animateFlourish(recording, 800)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val f = ripple.value
        circle(12f, 12f, 9.5f)
        if (f < 1f) circle(12f, 12f, 9.5f + 3f * f, activeColor, alpha = 1f - f)
        val k = easeOutBack(t, 1.3f)
        val half = lerp(4.8f, 3.6f, k)
        val r = lerp(4.8f, 1.2f, t)
        rotate(90f * easeInOut(t)) {
            val dot = path(0)
            dot.addRoundRect(RoundRect(12f - half, 12f - half, 12f + half, 12f + half, CornerRadius(r, r)))
            fillPath(dot, activeColor)
        }
    }
}

/**
 * Two repeat arrows that chase each other half way round the loop and light up when repeat turns
 * on. Repeat-one pops a "1" into the middle.
 *
 * @param enabled false dims the icon.
 * @param one true repeats the current track and shows the "1".
 */
@Composable
public fun RepeatIcon(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    one: Boolean = false,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = when {
        !enabled -> "Repeat off"
        one -> "Repeat one"
        else -> "Repeat all"
    },
) {
    val progress = animateToggle(enabled, 620)
    val single = animateToggle(enabled && one, 380)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val alpha = lerp(0.4f, 1f, t)
        rotate(180f * easeInOut(t)) {
            val loop = path(0)
            loop.moveTo(3f, 11f)
            loop.lineTo(3f, 10f)
            loop.cubicTo(3f, 7.8f, 4.8f, 6f, 7f, 6f)
            loop.lineTo(20.5f, 6f)
            loop.moveTo(17f, 2.5f)
            loop.lineTo(20.5f, 6f)
            loop.lineTo(17f, 9.5f)
            loop.moveTo(21f, 13f)
            loop.lineTo(21f, 14f)
            loop.cubicTo(21f, 16.2f, 19.2f, 18f, 17f, 18f)
            loop.lineTo(3.5f, 18f)
            loop.moveTo(7f, 14.5f)
            loop.lineTo(3.5f, 18f)
            loop.lineTo(7f, 21.5f)
            strokePath(loop, alpha = alpha)
        }
        val o = single.value
        if (o > 0f) {
            scale(easeOutBack(o, 2f).coerceAtLeast(0f)) {
                val digit = path(1)
                digit.moveTo(10.6f, 10.4f)
                digit.lineTo(12f, 9.3f)
                digit.lineTo(12f, 14.7f)
                strokePath(digit, alpha = o)
            }
        }
    }
}

/**
 * Two parallel arrows whose ends swap places, bending into a crossing shuffle.
 *
 * @param on true shows the crossed shuffle arrows.
 */
@Composable
public fun ShuffleIcon(
    on: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (on) "Shuffle on" else "Shuffle off",
) {
    val progress = animateToggle(on, 560)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val k = easeOutBack(t, 1.4f)
        val alpha = lerp(0.4f, 1f, t)
        for (i in 0 until 2) {
            val y0 = if (i == 0) 7f else 17f
            val y1 = lerp(y0, if (i == 0) 17f else 7f, k)
            val p = path(i)
            p.moveTo(3f, y0)
            p.lineTo(6f, y0)
            p.cubicTo(11f, y0, 12f, y1, 17f, y1)
            p.lineTo(20.5f, y1)
            p.moveTo(17.5f, y1 - 3f)
            p.lineTo(20.5f, y1)
            p.lineTo(17.5f, y1 + 3f)
            strokePath(p, alpha = alpha)
        }
    }
}

/**
 * Four corner brackets whose corners slide inward along the diagonals while their arms stay put,
 * turning "enter fullscreen" into "exit fullscreen".
 *
 * @param fullscreen true shows the exit (inward) corners.
 */
@Composable
public fun FullscreenIcon(
    fullscreen: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (fullscreen) "Exit fullscreen" else "Enter fullscreen",
) {
    val progress = animateToggle(fullscreen, 480)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        // The whole icon breathes out then in, like a window resizing.
        scale(1f + 0.12f * bump(t)) {
            for (i in 0 until 4) {
                val sx = if (i % 2 == 0) -1f else 1f
                val sy = if (i < 2) -1f else 1f
                val k = easeOutBack(phase(t, i * 0.06f, 0.82f + i * 0.06f), 1.6f)
                // Corner vertex moves between the outer corner and 4 units in on each axis.
                val d = lerp(9f, 4f, k)
                val p = path(i)
                p.moveTo(12f + sx * 4f, 12f + sy * 9f)
                p.lineTo(12f + sx * d, 12f + sy * d)
                p.lineTo(12f + sx * 9f, 12f + sy * 4f)
                strokePath(p)
            }
        }
    }
}

/**
 * A gauge whose needle swings to the playback speed with a little overshoot, filling the dial
 * up to it.
 *
 * @param level Speed from 0 (slowest) to 1 (fastest), mapped onto the dial.
 * @param label What screen readers hear after "Speed", for example "1.5x". Defaults to a percentage.
 */
@Composable
public fun SpeedIcon(
    level: Float,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    label: String = "${(level.coerceIn(0f, 1f) * 100).roundToInt()}%",
    contentDescription: String? = "Speed $label",
) {
    val progress = animateLevel(level.coerceIn(0f, 1f), 700) { x -> easeOutBack(x, 1.8f) }
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val v = progress.value
        val start = 150f
        val sweep = 240f
        val angle = start + sweep * v
        arc(12f, 13.5f, 9f, start, sweep, alpha = 0.3f)
        arc(12f, 13.5f, 9f, start, (sweep * v).coerceIn(0.5f, sweep))
        val a = (angle * PI / 180.0).toFloat()
        line(12f, 13.5f, 12f + 6f * cos(a), 13.5f + 6f * sin(a))
        circle(12f, 13.5f, 1.4f, fill = true)
    }
}

/**
 * A video frame whose inner window shrinks into the bottom-right corner for picture-in-picture,
 * and grows back to fill the frame.
 *
 * @param pip true shows the small corner window.
 */
@Composable
public fun PipIcon(
    pip: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (pip) "Exit picture-in-picture" else "Picture-in-picture",
) {
    val progress = animateToggle(pip, 520)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val k = easeOutBack(t, 1.2f)
        val frame = path(0)
        frame.addRoundRect(RoundRect(2.5f, 4.5f, 21.5f, 19.5f, CornerRadius(2.5f, 2.5f)))
        strokePath(frame)
        val inner = path(1)
        inner.addRoundRect(
            RoundRect(lerp(6f, 12.5f, k), lerp(8f, 12f, k), 18f, 16f, CornerRadius(1.2f, 1.2f)),
        )
        fillPath(inner, alpha = lerp(0.3f, 1f, t))
    }
}

/**
 * A cast icon whose screen floods with colour from the corner as it connects, while its waves
 * ripple out one after another.
 *
 * @param connected true shows the filled, connected screen.
 */
@Composable
public fun CastIcon(
    connected: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (connected) "Casting, stop" else "Cast",
) {
    val progress = animateToggle(connected, 640)
    val waves = animateFlourish(connected, 900)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription, live = true) {
        val t = progress.value
        val f = waves.value
        val screen = path(0)
        screen.moveTo(2.5f, 8f)
        screen.lineTo(2.5f, 6.5f)
        screen.quadraticTo(2.5f, 4f, 5f, 4f)
        screen.lineTo(19f, 4f)
        screen.quadraticTo(21.5f, 4f, 21.5f, 6.5f)
        screen.lineTo(21.5f, 17.5f)
        screen.quadraticTo(21.5f, 20f, 19f, 20f)
        screen.lineTo(14f, 20f)
        strokePath(screen)
        if (t > 0f) {
            layer {
                // The fill spreads as a circle from the corner the waves come from.
                val reach = 26f * easeInOut(t)
                val clip = path(1)
                clip.addOval(Rect(2.5f - reach, 20f - reach, 2.5f + reach, 20f + reach))
                draw.clipPath(clip) {
                    val fill = path(2)
                    fill.addRoundRect(RoundRect(5.5f, 7f, 18.5f, 17f, CornerRadius(1f, 1f)))
                    fillPath(fill)
                }
                // A clear gap around the waves keeps them readable on top of the fill.
                clearCircle(2.5f, 20f, 10.6f)
            }
        }
        circle(2.5f, 20f, 0.9f, fill = true)
        for (i in 0 until 2) {
            val r = 4.5f + i * 4f
            val pulse = if (f < 1f) bump(phase(f, i * 0.25f, 0.55f + i * 0.25f)) else 0f
            arc(2.5f, 20f, r + 1.2f * pulse, 270f, 90f)
        }
    }
}

/**
 * Equalizer bars that keep dancing while music plays and sink to rest when it stops. With
 * animations turned off in the system, the bars stand still.
 *
 * @param playing true makes the bars move.
 */
@Composable
public fun EqualizerIcon(
    playing: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (playing) "Playing" else "Paused",
) {
    val energy = animateToggle(playing, 420)
    val clock = rememberIconClock(playing)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val e = energy.value
        val s = clock.seconds
        for (i in 0 until 4) {
            val x = 5f + i * 4.67f
            // Each bar has its own rhythm, so the motion never lines up into a single wave.
            val wave = 0.5f + 0.5f * sin(s * EqSpeeds[i] + EqPhases[i])
            val h = EqRest[i] + e * (2f + 11f * wave)
            line(x, 20f, x, 20f - h)
        }
    }
}

private val EqRest = floatArrayOf(3f, 6f, 4.5f, 2.5f)
private val EqSpeeds = floatArrayOf(7.1f, 9.3f, 6.2f, 8.4f)
private val EqPhases = floatArrayOf(0f, 1.7f, 3.1f, 4.6f)
