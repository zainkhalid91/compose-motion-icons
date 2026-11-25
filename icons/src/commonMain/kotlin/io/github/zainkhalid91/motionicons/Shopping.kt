package io.github.zainkhalid91.motionicons

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt

/**
 * A shopping cart. Each time the count goes up, an item drops into the basket, the cart dips
 * under its weight and the badge bumps. Up to three items show in the basket.
 *
 * @param count Items in the cart. 0 shows an empty cart with no badge.
 * @param badgeColor Badge fill.
 */
@Composable
public fun CartIcon(
    count: Int,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    badgeColor: Color = MotionIconColors.Badge,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = when (count) {
        0 -> "Cart, empty"
        1 -> "Cart, 1 item"
        else -> "Cart, $count items"
    },
) {
    val shown = count.coerceIn(0, 3)
    val items = animateLevel(shown.toFloat(), 360)
    val badge = animateToggle(count > 0, 360)
    val drop = animateIncrease(count, 760)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription, live = true) {
        val n = items.value
        val f = drop.value
        val dip = if (f < 1f) 1.2f * bump(phase(f, 0.45f, 0.85f)) else 0f
        layer {
            translate(0f, dip) {
                val cart = path(0)
                cart.moveTo(2f, 2.5f)
                cart.lineTo(4f, 2.5f)
                cart.lineTo(6.7f, 14.5f)
                cart.quadraticTo(7f, 16f, 8.7f, 16f)
                cart.lineTo(18.3f, 16f)
                cart.quadraticTo(19.9f, 16f, 20.3f, 14.5f)
                cart.lineTo(22f, 7.5f)
                cart.lineTo(5.1f, 7.5f)
                strokePath(cart)
                for (k in 0 until 3) {
                    val alpha = (n - k).coerceIn(0f, 1f)
                    if (alpha <= 0f) continue
                    // The newest item falls from above the cart and lands with a bounce.
                    val falling = k == shown - 1 && f < 1f
                    val y = if (falling) lerp(-9f, 0f, easeOutBack(phase(f, 0f, 0.55f), 1.2f)) else 0f
                    val x = 8.6f + k * 3.8f
                    val box = path(1 + k)
                    box.addRoundRect(RoundRect(x, 10f + y, x + 2.4f, 12.8f + y, CornerRadius(0.6f, 0.6f)))
                    fillPath(box, alpha = alpha)
                }
                circle(9f, 20.5f, 1.2f, fill = true)
                circle(18f, 20.5f, 1.2f, fill = true)
            }
            val b = badge.value
            if (b > 0f) {
                val pop = 1f + 0.4f * bump(phase(f, 0.5f, 1f))
                val s = easeOutBack(b, 2.4f).coerceAtLeast(0f) * pop
                clearCircle(19.5f, 4f, 4.4f * s.coerceAtMost(1.2f))
                circle(19.5f, 4f, 3f * s, badgeColor, fill = true)
            }
        }
    }
}

/**
 * A funnel that folds apart into three sort lines, longest first: filter becomes sort.
 *
 * @param sort true shows the sort lines.
 */
@Composable
public fun FilterSortIcon(
    sort: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (sort) "Sort" else "Filter",
) {
    val progress = animateToggle(sort, 640)
    val work = remember { Array(FunnelStrokes.size) { FloatArray(FunnelStrokes[it].size) } }
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        for (i in FunnelStrokes.indices) {
            // Top edge first, then the sides, so the funnel unfolds from the rim down.
            val k = easeOutBack(phase(t, i * 0.15f, 0.7f + i * 0.15f), 1.3f)
            val from = FunnelStrokes[i]
            val to = SortStrokes[i]
            val points = work[i]
            for (j in from.indices) points[j] = lerp(from[j], to[j], k)
            polyline(points, 1f, index = 3 + i)
        }
    }
}

private val FunnelStrokes = arrayOf(
    floatArrayOf(3f, 4f, 21f, 4f),
    floatArrayOf(21f, 4f, 14f, 12.5f, 14f, 19f),
    floatArrayOf(3f, 4f, 10f, 12.5f, 10f, 21f, 14f, 19f),
)
private val SortStrokes = arrayOf(
    floatArrayOf(3.5f, 6f, 20.5f, 6f),
    floatArrayOf(6.5f, 12f, 12f, 12f, 17.5f, 12f),
    floatArrayOf(9.5f, 18f, 11.17f, 18f, 12.83f, 18f, 14.5f, 18f),
)

/**
 * A gift box whose lid pops off and hangs tilted above it, the bow springing up, while light
 * spills out of the open box.
 *
 * @param open true shows the opened gift.
 */
@Composable
public fun GiftIcon(
    open: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (open) "Gift, opened" else "Gift",
) {
    val progress = animateToggle(open, 700)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val k = easeOutBack(t, 2f)
        val body = path(0)
        body.addRoundRect(RoundRect(5f, 12f, 19f, 21f, CornerRadius(1f, 1f)))
        strokePath(body)
        line(12f, 12f, 12f, 21f)
        // Rays spill out of the gap between the box and the lifted lid.
        val glow = phase(t, 0.35f, 1f)
        if (glow > 0f) {
            val l = 1.8f * glow
            line(12f, 10.6f, 12f, 10.6f - l, alpha = glow)
            line(8.6f, 10.8f, 8.6f - 0.8f * l, 10.8f - 0.75f * l, alpha = glow)
            line(15.4f, 10.8f, 15.4f + 0.8f * l, 10.8f - 0.75f * l, alpha = glow)
        }
        translate(0f, -3.5f * k) {
            rotate(-14f * k, 12f, 10f) {
                val lid = path(1)
                lid.addRoundRect(RoundRect(3.5f, 8.5f, 20.5f, 12f, CornerRadius(1f, 1f)))
                strokePath(lid)
                line(12f, 8.5f, 12f, 12f)
                val spring = 1f + 0.35f * bump(phase(t, 0.2f, 0.8f))
                scale(spring, spring, 12f, 8.5f) {
                    val bow = path(2)
                    bow.moveTo(12f, 8.5f)
                    bow.cubicTo(10f, 3.5f, 6f, 5.5f, 8.5f, 8.5f)
                    bow.moveTo(12f, 8.5f)
                    bow.cubicTo(14f, 3.5f, 18f, 5.5f, 15.5f, 8.5f)
                    strokePath(bow)
                }
            }
        }
    }
}

/**
 * A flourish that runs from 0 to 1 each time [count] goes up, and sits at 1 otherwise. It never
 * fires on the first composition.
 */
@Composable
internal fun animateIncrease(count: Int, durationMillis: Int): Animatable<Float, *> {
    val speed = LocalMotionIconStyle.current.speed
    val frozen = LocalFrozenIcons.current
    val anim = remember { Animatable(1f) }
    val previous = remember { intArrayOf(count) }
    LaunchedEffect(count, frozen) {
        val fire = count > previous[0]
        previous[0] = count
        if (fire && !frozen) {
            anim.snapTo(0f)
            anim.animateTo(1f, tween((durationMillis / speed).roundToInt().coerceAtLeast(1), easing = LinearEasing))
        } else {
            anim.snapTo(1f)
        }
    }
    return anim
}
