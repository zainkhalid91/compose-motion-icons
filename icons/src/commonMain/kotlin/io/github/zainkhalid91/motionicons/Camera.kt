package io.github.zainkhalid91.motionicons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * A camera that turns round on its vertical axis while the arrows inside it spin half a turn,
 * for switching between the front and back cameras.
 *
 * @param front true for the front (selfie) camera, shown with a lens dot. Each change flips the camera.
 */
@Composable
public fun CameraFlipIcon(
    front: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (front) "Switch to back camera" else "Switch to front camera",
) {
    val progress = animateToggle(front, 620)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        // A flat squeeze through the middle of the turn reads as the camera turning round.
        val turn = abs(cos(PI.toFloat() * t)).coerceAtLeast(0.12f)
        scale(turn, 1f) {
            val body = path(0)
            body.moveTo(4.5f, 6.5f)
            body.lineTo(8f, 6.5f)
            body.lineTo(9.5f, 4f)
            body.lineTo(14.5f, 4f)
            body.lineTo(16f, 6.5f)
            body.lineTo(19.5f, 6.5f)
            body.quadraticTo(21.5f, 6.5f, 21.5f, 8.5f)
            body.lineTo(21.5f, 17.5f)
            body.quadraticTo(21.5f, 19.5f, 19.5f, 19.5f)
            body.lineTo(4.5f, 19.5f)
            body.quadraticTo(2.5f, 19.5f, 2.5f, 17.5f)
            body.lineTo(2.5f, 8.5f)
            body.quadraticTo(2.5f, 6.5f, 4.5f, 6.5f)
            body.close()
            strokePath(body)
        }
        rotate(180f * easeOutBack(t, 1.3f), 12f, 13f) {
            arc(12f, 13f, 3.8f, 200f, 130f)
            arc(12f, 13f, 3.8f, 20f, 130f)
            // Arrowheads on the leading end of each arc.
            for (k in 0 until 2) {
                val a = ((330f + 180f * k) * PI / 180.0).toFloat()
                val hx = 12f + 3.8f * cos(a)
                val hy = 13f + 3.8f * sin(a)
                val tx = -sin(a)
                val ty = cos(a)
                line(hx, hy, hx - 1.8f * tx + 1.4f * cos(a), hy - 1.8f * ty + 1.4f * sin(a))
                line(hx, hy, hx - 1.8f * tx - 1.4f * cos(a), hy - 1.8f * ty - 1.4f * sin(a))
            }
        }
        // The front camera gets a lens dot, so the state reads at rest and not only mid-turn.
        circle(12f, 13f, 1.3f * easeOutBack(phase(t, 0.5f, 1f), 2f).coerceAtLeast(0f), fill = true)
    }
}

/**
 * A camera aperture whose six blades twist and close towards the centre, and open again.
 *
 * @param open true shows the wide-open aperture.
 */
@Composable
public fun ApertureIcon(
    open: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (open) "Close aperture" else "Open aperture",
) {
    val progress = animateToggle(open, 640)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val k = easeOutBack(t, 1.3f)
        circle(12f, 12f, 9.5f)
        // The blade edges are the sides of a hexagon extended to the rim. Shrinking the hexagon
        // while turning it closes the aperture.
        val hex = lerp(0.9f, 4.6f, k)
        val turn = lerp(-35f, 0f, k) * (PI / 180.0).toFloat()
        val rim = 9.5f
        for (i in 0 until 6) {
            val a0 = turn + i * PI.toFloat() / 3f
            val a1 = a0 + PI.toFloat() / 3f
            val vx = hex * cos(a0)
            val vy = hex * sin(a0)
            var dx = hex * cos(a1) - vx
            var dy = hex * sin(a1) - vy
            val len = sqrt(dx * dx + dy * dy)
            dx /= len
            dy /= len
            // Walk from the vertex along the edge direction until the line meets the rim.
            val b = vx * dx + vy * dy
            val c = vx * vx + vy * vy - rim * rim
            val s = -b + sqrt(b * b - c)
            line(12f + vx, 12f + vy, 12f + vx + dx * s, 12f + vy + dy * s)
        }
    }
}
