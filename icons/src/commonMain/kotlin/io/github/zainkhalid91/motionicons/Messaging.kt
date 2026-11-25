package io.github.zainkhalid91.motionicons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.sin

/**
 * An envelope whose flap folds up and back, then a letter rises out of its pocket.
 *
 * @param open true shows the open envelope with the letter out.
 */
@Composable
public fun MailIcon(
    open: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (open) "Close message" else "Open message",
) {
    val progress = animateToggle(open, 720)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val flap = easeInOut(phase(t, 0f, 0.55f))
        val out = phase(t, 0.4f, 1f)
        val rise = if (out > 0f) easeOutBack(out, 1.6f) else 0f
        layer {
            val apex = lerp(12.5f, 1.5f, flap)
            val f = path(0)
            f.moveTo(3f, 6.5f)
            f.lineTo(12f, apex)
            f.lineTo(21f, 6.5f)
            strokePath(f)
            if (out > 0f) {
                // The letter sits in front of the open flap, so it clears the flap behind it.
                val top = lerp(14.5f, 4.5f, rise)
                draw.drawRoundRect(Color.Black, Offset(6.5f, top), Size(11f, 12f), CornerRadius(1.2f, 1.2f), blendMode = BlendMode.Clear)
                val letter = path(1)
                letter.addRoundRect(RoundRect(6.5f, top, 17.5f, top + 12f, CornerRadius(1.2f, 1.2f)))
                strokePath(letter)
                line(9f, top + 3.2f, 15f, top + 3.2f)
                line(9f, top + 6.2f, 13f, top + 6.2f)
                // The pocket: everything below its V is the front of the envelope.
                val pocket = path(3)
                pocket.moveTo(2f, 9.5f)
                pocket.lineTo(12f, 15.5f)
                pocket.lineTo(22f, 9.5f)
                pocket.lineTo(22f, 28f)
                pocket.lineTo(2f, 28f)
                pocket.close()
                draw.drawPath(pocket, Color.Black, blendMode = BlendMode.Clear)
                polyline(PocketV, 1f, alpha = out, index = 4)
            }
            val body = path(2)
            body.moveTo(3f, 6.5f)
            body.lineTo(3f, 17.5f)
            body.quadraticTo(3f, 19.5f, 5f, 19.5f)
            body.lineTo(19f, 19.5f)
            body.quadraticTo(21f, 19.5f, 21f, 17.5f)
            body.lineTo(21f, 6.5f)
            strokePath(body)
            // Opened, the top edge is only the fold the flap turns on.
            line(3f, 6.5f, 21f, 6.5f, alpha = 1f - flap)
        }
    }
}

private val PocketV = floatArrayOf(3f, 10f, 12f, 15.5f, 21f, 10f)

/**
 * A phone handset that shakes as the call starts, then swings down into the red hang-up position.
 *
 * @param inCall true shows the hang-up handset.
 * @param endColor Handset colour during a call.
 */
@Composable
public fun PhoneIcon(
    inCall: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    endColor: Color = MotionIconColors.Badge,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (inCall) "End call" else "Call",
) {
    val progress = animateToggle(inCall, 620)
    val ring = animateFlourish(inCall, 520)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val f = ring.value
        val shake = if (f < 1f) 9f * sin(f * PI.toFloat() * 6f) * (1f - f) else 0f
        // The swing starts once the shake is under way, and overshoots into place.
        val swing = easeOutBack(phase(t, 0.2f, 1f), 1.4f)
        rotate(lerp(-135f, 0f, swing) + shake, 12f, 12.5f) {
            val h = path(0)
            h.moveTo(3f, 14.5f)
            h.lineTo(3f, 12.8f)
            h.quadraticTo(12f, 6.5f, 21f, 12.8f)
            h.lineTo(21f, 14.5f)
            h.quadraticTo(21f, 16f, 19.5f, 16f)
            h.lineTo(16.8f, 16f)
            h.quadraticTo(15.5f, 16f, 15.5f, 14.7f)
            h.lineTo(15.5f, 13.4f)
            h.quadraticTo(12f, 12.2f, 8.5f, 13.4f)
            h.lineTo(8.5f, 14.7f)
            h.quadraticTo(8.5f, 16f, 7.2f, 16f)
            h.lineTo(4.5f, 16f)
            h.quadraticTo(3f, 16f, 3f, 14.5f)
            h.close()
            val c = lerp(color, endColor, t)
            fillPath(h, c)
            strokePath(h, c)
        }
    }
}

/**
 * A thumbs-up that lifts and wobbles on its wrist while it fills from the bottom.
 *
 * @param liked true shows the filled thumb.
 * @param activeColor Fill and outline when liked.
 */
@Composable
public fun ThumbUpIcon(
    liked: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    activeColor: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (liked) "Remove like" else "Like",
) {
    val progress = animateToggle(liked, 520)
    val lift = animateFlourish(liked, 760)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val f = lift.value
        val angle = if (f < 1f) -20f * sin(f * PI.toFloat() * 2.5f) * (1f - f) else 0f
        val c = lerp(color, activeColor, t)
        rotate(angle, 4f, 21f) {
            val thumb = path(0)
            thumb.moveTo(15f, 5.9f)
            thumb.lineTo(14f, 10f)
            thumb.lineTo(19.8f, 10f)
            thumb.quadraticTo(22.2f, 10.3f, 21.75f, 12.6f)
            thumb.lineTo(19.4f, 20.6f)
            thumb.quadraticTo(18.9f, 22f, 17.5f, 22f)
            thumb.lineTo(4f, 22f)
            thumb.quadraticTo(2f, 22f, 2f, 20f)
            thumb.lineTo(2f, 12f)
            thumb.quadraticTo(2f, 10f, 4f, 10f)
            thumb.lineTo(6.8f, 10f)
            thumb.quadraticTo(8f, 10f, 8.55f, 8.9f)
            thumb.lineTo(12f, 2f)
            thumb.quadraticTo(15.6f, 2.4f, 15f, 5.9f)
            thumb.close()
            if (t > 0f) {
                layer {
                    // The fill rises from the wrist like a level, and the cuff line is cut out of it.
                    val top = lerp(22.5f, 1.5f, t)
                    draw.clipRect(0f, top, Grid, Grid) { fillPath(thumb, c) }
                    clearLine(7f, 10.5f, 7f, 21.5f)
                }
            }
            strokePath(thumb, c)
            line(7f, 10f, 7f, 22f, c)
        }
    }
}

/**
 * A chat bubble that pops in three dots one after another, which keep bobbing while someone types.
 * With animations turned off in the system, the dots stand still.
 *
 * @param typing true shows the bobbing dots.
 */
@Composable
public fun TypingIcon(
    typing: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (typing) "Typing" else "Message",
) {
    val show = animateToggle(typing, 480)
    val clock = rememberIconClock(typing)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val e = show.value
        val s = clock.seconds
        val bubble = path(0)
        bubble.moveTo(7f, 17.5f)
        bubble.lineTo(4f, 20.5f)
        bubble.lineTo(4f, 7f)
        bubble.quadraticTo(4f, 4f, 7f, 4f)
        bubble.lineTo(17f, 4f)
        bubble.quadraticTo(20f, 4f, 20f, 7f)
        bubble.lineTo(20f, 14.5f)
        bubble.quadraticTo(20f, 17.5f, 17f, 17.5f)
        bubble.close()
        strokePath(bubble)
        for (i in 0 until 3) {
            val pop = easeOutBack(phase(e, i * 0.2f, 0.6f + i * 0.2f), 2f).coerceAtLeast(0f)
            // Each dot hops in turn, a wave running left to right.
            val hop = 1.6f * max(0f, sin(s * 8f - i * 0.9f)) * e
            circle(8f + 4f * i, 10.75f - hop, 1.4f * pop, fill = true)
        }
    }
}

/**
 * A reply arrow. Switching to reply-all, a second arrowhead peels out from under the first.
 *
 * @param all true shows reply-all.
 */
@Composable
public fun ReplyIcon(
    all: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (all) "Reply all" else "Reply",
) {
    val progress = animateToggle(all, 560)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val shift = 3.5f * easeOutBack(t, 1.5f)
        // The second head leaves late and overshoots, so it reads as peeling off the first.
        val peel = shift - 3.5f * easeOutBack(phase(t, 0.25f, 1f), 1.8f)
        val alpha = phase(t, 0f, 0.25f)
        if (alpha > 0f) {
            translate(peel, 0f) { polyline(Chevron, 1f, alpha = alpha, index = 4) }
        }
        translate(shift, 0f) {
            polyline(Chevron, 1f)
            val tail = path(0)
            tail.moveTo(4.5f, 12f)
            tail.lineTo(12.5f, 12f)
            tail.quadraticTo(18f, 12f, 18f, 18f)
            strokePath(tail)
        }
    }
}

private val Chevron = floatArrayOf(9.5f, 7f, 4.5f, 12f, 9.5f, 17f)
