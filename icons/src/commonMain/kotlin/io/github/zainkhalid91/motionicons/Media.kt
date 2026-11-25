package io.github.zainkhalid91.motionicons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt

/**
 * The play triangle splits down the middle and each half squares off into a pause bar.
 *
 * @param playing true shows pause (the action the button will take), false shows play.
 */
@Composable
public fun PlayPauseIcon(
    playing: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (playing) "Pause" else "Play",
) {
    val progress = animateToggle(playing, 420)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        // Each shape is four corners; the right half of the triangle has two corners on its tip.
        val left = path(0)
        quad(left, PlayLeft, PauseLeft, t)
        val right = path(1)
        quad(right, PlayRight, PauseRight, t)
        val squash = 1f - 0.12f * bump(t)
        scale(squash) {
            fillPath(left)
            strokePath(left)
            fillPath(right)
            strokePath(right)
        }
    }
}

private val PlayLeft = floatArrayOf(7f, 4.5f, 13f, 8f, 13f, 16f, 7f, 19.5f)
private val PlayRight = floatArrayOf(13f, 8f, 19f, 12f, 19f, 12f, 13f, 16f)
private val PauseLeft = floatArrayOf(6.5f, 4.5f, 9.5f, 4.5f, 9.5f, 19.5f, 6.5f, 19.5f)
private val PauseRight = floatArrayOf(14.5f, 4.5f, 17.5f, 4.5f, 17.5f, 19.5f, 14.5f, 19.5f)

private fun quad(path: Path, from: FloatArray, to: FloatArray, t: Float) {
    path.moveTo(lerp(from[0], to[0], t), lerp(from[1], to[1], t))
    for (i in 2 until 8 step 2) path.lineTo(lerp(from[i], to[i], t), lerp(from[i + 1], to[i + 1], t))
    path.close()
}

/**
 * A speaker whose sound waves grow and shrink with the volume. At zero the waves fold away
 * and an X draws in.
 *
 * @param level Volume from 0 to 1. Each third lights one more wave.
 * @param muted Shows the X whatever the level, so unmuting returns to the same level.
 */
@Composable
public fun VolumeIcon(
    level: Float,
    modifier: Modifier = Modifier,
    muted: Boolean = false,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (muted || level <= 0f) "Volume muted" else "Volume ${(level.coerceIn(0f, 1f) * 100).roundToInt()}%",
) {
    val shown = if (muted) 0f else level.coerceIn(0f, 1f)
    val progress = animateLevel(shown, 520)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val v = progress.value
        val speaker = path(0)
        speaker.moveTo(3.5f, 9f)
        speaker.lineTo(7.5f, 9f)
        speaker.lineTo(12f, 4.5f)
        speaker.lineTo(12f, 19.5f)
        speaker.lineTo(7.5f, 15f)
        speaker.lineTo(3.5f, 15f)
        speaker.close()
        strokePath(speaker)
        // Waves: each one grows from its centre as its third of the range fills.
        for (i in 0 until 3) {
            val k = easeOutBack(((v * 3f) - i).coerceIn(0f, 1f), 1.4f).coerceAtLeast(0f)
            if (k > 0f) {
                val r = 3.2f + i * 3f
                val sweep = 80f * k
                arc(12.5f, 12f, r, -sweep / 2f, sweep, alpha = k)
            }
        }
        val x = phase(1f - v * 6f, 0f, 1f)
        if (x > 0f) {
            val half = 2.5f * easeOutBack(x, 2f)
            line(18f - half, 12f - half, 18f + half, 12f + half)
            line(18f + half, 12f - half, 18f - half, 12f + half)
        }
    }
}

/**
 * A microphone that a slash cuts through when muted, with the capsule dipping as it's struck.
 *
 * @param muted true shows the slashed microphone.
 */
@Composable
public fun MicIcon(
    muted: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (muted) "Unmute microphone" else "Mute microphone",
) {
    val progress = animateToggle(muted, 440)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        layer {
            translate(0f, 1.2f * bump(t)) {
                val capsule = path(0)
                capsule.addRoundRect(RoundRect(9f, 2.5f, 15f, 14f, CornerRadius(3f, 3f)))
                strokePath(capsule)
            }
            arc(12f, 11f, 7f, 0f, 180f)
            line(12f, 18f, 12f, 21.5f)
            slash(t)
        }
    }
}
