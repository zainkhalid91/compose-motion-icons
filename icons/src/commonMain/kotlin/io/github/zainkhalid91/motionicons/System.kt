package io.github.zainkhalid91.motionicons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.unit.Dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A padlock whose shackle springs up and tilts open, and clunks shut when locked again.
 *
 * @param locked true shows the closed lock.
 */
@Composable
public fun LockIcon(
    locked: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (locked) "Unlock" else "Lock",
) {
    val progress = animateToggle(!locked, 560)
    val clunk = animateFlourish(locked, 320)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        translate(0f, 0.8f * bump(clunk.value)) {
            val lift = 2.6f * easeOutBack(phase(t, 0f, 0.6f), 2f)
            val tilt = -22f * easeInOut(phase(t, 0.35f, 1f))
            rotate(tilt, 8f, 11f) {
                translate(0f, -lift) {
                    // The left leg grows by the lift, so it stays in the body while the shackle rises.
                    val shackle = path(0)
                    shackle.moveTo(8f, 11f + lift)
                    shackle.lineTo(8f, 7.5f)
                    shackle.cubicTo(8f, 5.3f, 9.8f, 3.5f, 12f, 3.5f)
                    shackle.cubicTo(14.2f, 3.5f, 16f, 5.3f, 16f, 7.5f)
                    shackle.lineTo(16f, 11f)
                    strokePath(shackle)
                }
            }
            val body = path(1)
            body.addRoundRect(RoundRect(5f, 11f, 19f, 21f, CornerRadius(2.2f, 2.2f)))
            strokePath(body)
            circle(12f, 15.6f, 1.3f, fill = true)
            line(12f, 15.6f, 12f, 17.6f)
        }
    }
}

/**
 * An eye that squints as a slash cuts through it, and glances around when it opens again.
 * For show/hide password fields.
 *
 * @param visible true shows the open eye.
 */
@Composable
public fun EyeIcon(
    visible: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (visible) "Hide password" else "Show password",
) {
    val progress = animateToggle(!visible, 460)
    val glance = animateFlourish(visible, 900)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val f = glance.value
        layer {
            scale(1f, 1f - 0.4f * easeInOut(t)) {
                val eye = path(0)
                eye.moveTo(2f, 12f)
                eye.cubicTo(4.5f, 7.2f, 8f, 5f, 12f, 5f)
                eye.cubicTo(16f, 5f, 19.5f, 7.2f, 22f, 12f)
                eye.cubicTo(19.5f, 16.8f, 16f, 19f, 12f, 19f)
                eye.cubicTo(8f, 19f, 4.5f, 16.8f, 2f, 12f)
                eye.close()
                strokePath(eye)
            }
            val look = if (f < 1f) 2.2f * sin(f * 2f * PI.toFloat()) * (1f - f) else 0f
            circle(12f + look, 12f, 3f * (1f - 0.3f * t))
            slash(easeInOut(t))
        }
    }
}

/**
 * A sun that pulls in its rays and swells while a shadow slides across it, leaving a moon.
 * For light/dark theme switches.
 *
 * @param dark true shows the moon.
 */
@Composable
public fun ThemeIcon(
    dark: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (dark) "Switch to light theme" else "Switch to dark theme",
) {
    val progress = animateToggle(dark, 720)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val e = easeInOut(t)
        rotate(-40f * e) {
            val r = lerp(4.5f, 8.5f, easeOutBack(t, 1.2f))
            val body = path(0)
            body.addOval(Rect(12f - r, 12f - r, 12f + r, 12f + r))
            val cx = lerp(30f, 17f, e)
            val cy = lerp(-6f, 7.5f, e)
            val cr = lerp(4f, 6.6f, e)
            val shadow = path(1)
            shadow.addOval(Rect(cx - cr, cy - cr, cx + cr, cy + cr))
            val moon = path(2)
            moon.op(body, shadow, PathOperation.Difference)
            strokePath(moon)
            val rays = 1f - phase(t, 0f, 0.5f)
            if (rays > 0f) {
                rotate(90f * t) {
                    for (i in 0 until 8) {
                        val a = (i * PI / 4).toFloat()
                        val inner = r + 2.6f
                        val outer = inner + 2.4f * rays
                        line(12f + cos(a) * inner, 12f + sin(a) * inner, 12f + cos(a) * outer, 12f + sin(a) * outer, alpha = rays)
                    }
                }
            }
        }
    }
}

/**
 * A bin whose lid flips up on its hinge. For delete buttons, drag-to-delete targets and
 * confirm-delete states.
 *
 * @param open true shows the lid raised.
 */
@Composable
public fun TrashIcon(
    open: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = "Delete",
) {
    val progress = animateToggle(open, 480)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val swing = easeOutBack(t, 2.2f)
        rotate(-28f * swing, 3.5f, 6.5f) {
            translate(0f, -1.2f * swing) {
                line(3.5f, 6.5f, 20.5f, 6.5f)
                val handle = path(0)
                handle.moveTo(9f, 6.5f)
                handle.lineTo(9f, 4.6f)
                handle.quadraticTo(9f, 3.5f, 10.1f, 3.5f)
                handle.lineTo(13.9f, 3.5f)
                handle.quadraticTo(15f, 3.5f, 15f, 4.6f)
                handle.lineTo(15f, 6.5f)
                strokePath(handle)
            }
        }
        val body = path(1)
        body.moveTo(5.5f, 6.5f)
        body.lineTo(6.5f, 19.4f)
        body.quadraticTo(6.6f, 21f, 8.2f, 21f)
        body.lineTo(15.8f, 21f)
        body.quadraticTo(17.4f, 21f, 17.5f, 19.4f)
        body.lineTo(18.5f, 6.5f)
        strokePath(body)
        line(10f, 10.5f, 10f, 17f)
        line(14f, 10.5f, 14f, 17f)
    }
}
