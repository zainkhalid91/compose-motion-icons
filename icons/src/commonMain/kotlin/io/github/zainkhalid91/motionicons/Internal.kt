package io.github.zainkhalid91.motionicons

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

/** Icons draw on a 24 x 24 unit grid, like Material and Lucide. */
internal const val Grid = 24f

internal val Standard: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

/**
 * Animates from the current value to [target]. The first composition starts at [target], so an
 * icon that appears already liked or playing never animates on its own. Interrupting a transition
 * reverses it from where it is, taking only the time the remaining distance needs.
 */
@Composable
internal fun animateLevel(target: Float, durationMillis: Int, easing: Easing = Standard): Animatable<Float, AnimationVector1D> {
    val speed = LocalMotionIconStyle.current.speed
    val frozen = LocalFrozenIcons.current
    val anim = remember { Animatable(target) }
    LaunchedEffect(target, frozen) {
        if (frozen) {
            anim.snapTo(target)
        } else {
            val distance = abs(target - anim.value).coerceAtMost(1f)
            val millis = (durationMillis * distance / speed).roundToInt().coerceAtLeast(1)
            anim.animateTo(target, tween(millis, easing = easing))
        }
    }
    return anim
}

@Composable
internal fun animateToggle(on: Boolean, durationMillis: Int, easing: Easing = Standard): Animatable<Float, AnimationVector1D> =
    animateLevel(if (on) 1f else 0f, durationMillis, easing)

/**
 * A one-shot flourish (a burst, a ring, a bounce) that runs from 0 to 1 when [trigger] turns on,
 * and sits at 1 otherwise. It never fires on the first composition, so a list of already-liked
 * items doesn't burst as it scrolls into view.
 */
@Composable
internal fun animateFlourish(trigger: Boolean, durationMillis: Int): Animatable<Float, AnimationVector1D> {
    val speed = LocalMotionIconStyle.current.speed
    val frozen = LocalFrozenIcons.current
    val anim = remember { Animatable(1f) }
    val previous = remember { booleanArrayOf(trigger) }
    LaunchedEffect(trigger, frozen) {
        val fire = trigger && !previous[0]
        previous[0] = trigger
        if (fire && !frozen) {
            anim.snapTo(0f)
            anim.animateTo(1f, tween((durationMillis / speed).roundToInt().coerceAtLeast(1), easing = LinearEasing))
        } else {
            anim.snapTo(1f)
        }
    }
    return anim
}

/**
 * The box every icon draws in. Drawing reads animation values inside [onDraw] only, so a running
 * transition redraws the icon without recomposing it.
 */
@Composable
internal fun IconCanvas(
    modifier: Modifier,
    size: Dp,
    color: Color,
    strokeWidth: Dp,
    contentDescription: String?,
    live: Boolean = false,
    onDraw: IconScope.() -> Unit,
) {
    val scope = remember { IconScope() }
    val semantics = if (contentDescription == null) {
        Modifier
    } else {
        Modifier.semantics {
            this.contentDescription = contentDescription
            role = Role.Image
            if (live) liveRegion = LiveRegionMode.Polite
        }
    }
    Spacer(
        modifier
            .then(semantics)
            .size(size)
            .drawWithCache {
                val strokePx = strokeWidth.toPx()
                onDrawBehind { scope.run(this, color, strokePx, onDraw) }
            },
    )
}

/**
 * Drawing helpers in grid units: (0, 0) is the top-left of the 24 x 24 grid and (24, 24) the
 * bottom-right, whatever the icon's real size. Paths and the stroke are reused between frames.
 */
internal class IconScope {
    lateinit var draw: DrawScope
        private set
    var color: Color = Color.Black
        private set
    var strokeWidth: Float = 2f
        private set
    var stroke: Stroke = Stroke(2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        private set
    private var wideStroke: Stroke = Stroke(6f, cap = StrokeCap.Round)
    private val paths = Array(6) { Path() }
    private val linePath = Path()
    val layerPaint: Paint = Paint()

    fun run(scope: DrawScope, color: Color, strokePx: Float, block: IconScope.() -> Unit) {
        draw = scope
        this.color = color
        val unit = min(scope.size.width, scope.size.height) / Grid
        if (unit <= 0f) return
        val width = strokePx / unit
        if (width != strokeWidth) {
            strokeWidth = width
            stroke = Stroke(width, cap = StrokeCap.Round, join = StrokeJoin.Round)
            wideStroke = Stroke(width * 3f, cap = StrokeCap.Round)
        }
        scope.translate((scope.size.width - Grid * unit) / 2f, (scope.size.height - Grid * unit) / 2f) {
            scale(unit, unit, pivot = Offset.Zero) { block() }
        }
    }

    /** A cleared, reusable path. Each index is a separate path, so up to six can be alive at once. */
    fun path(index: Int = 0): Path = paths[index].apply { rewind() }

    // Lines go through drawPath with our own Stroke rather than drawLine: drawLine shares a cached
    // paint with drawPath, and on Skia it left state behind that changed the next frame's joins.
    fun line(x1: Float, y1: Float, x2: Float, y2: Float, c: Color = color, alpha: Float = 1f) {
        if (alpha <= 0f) return
        linePath.rewind()
        linePath.moveTo(x1, y1)
        linePath.lineTo(x2, y2)
        draw.drawPath(linePath, c, alpha.coerceAtMost(1f), stroke)
    }

    fun circle(cx: Float, cy: Float, r: Float, c: Color = color, alpha: Float = 1f, fill: Boolean = false) {
        if (alpha <= 0f || r <= 0f) return
        draw.drawCircle(c, r, Offset(cx, cy), alpha.coerceAtMost(1f), if (fill) Fill else stroke)
    }

    fun arc(cx: Float, cy: Float, r: Float, startDeg: Float, sweepDeg: Float, c: Color = color, alpha: Float = 1f) {
        if (alpha <= 0f || r <= 0f || sweepDeg == 0f) return
        draw.drawArc(c, startDeg, sweepDeg, false, Offset(cx - r, cy - r), Size(2 * r, 2 * r), alpha.coerceAtMost(1f), stroke)
    }

    fun strokePath(path: Path, c: Color = color, alpha: Float = 1f, blendMode: BlendMode = BlendMode.SrcOver) {
        if (alpha > 0f) draw.drawPath(path, c, alpha.coerceAtMost(1f), stroke, blendMode = blendMode)
    }

    fun fillPath(path: Path, c: Color = color, alpha: Float = 1f) {
        if (alpha > 0f) draw.drawPath(path, c, alpha.coerceAtMost(1f), Fill)
    }

    inline fun rotate(degrees: Float, px: Float = 12f, py: Float = 12f, block: () -> Unit) {
        if (degrees == 0f) block() else draw.rotate(degrees, Offset(px, py)) { block() }
    }

    inline fun translate(dx: Float, dy: Float, block: () -> Unit) {
        if (dx == 0f && dy == 0f) block() else draw.translate(dx, dy) { block() }
    }

    inline fun scale(sx: Float, sy: Float = sx, px: Float = 12f, py: Float = 12f, block: () -> Unit) {
        if (sx == 1f && sy == 1f) block() else draw.scale(sx, sy, Offset(px, py)) { block() }
    }

    /** Draws [block] into its own layer, so [clearCircle] and [clearLine] cut holes in it only. */
    inline fun layer(block: () -> Unit) {
        val canvas = draw.drawContext.canvas
        canvas.saveLayer(Rect(-4f, -4f, Grid + 4f, Grid + 4f), layerPaint)
        block()
        canvas.restore()
    }

    fun clearCircle(cx: Float, cy: Float, r: Float) {
        if (r > 0f) draw.drawCircle(Color.Black, r, Offset(cx, cy), blendMode = BlendMode.Clear)
    }

    /** Clears a line three strokes wide, the gap around a slash. */
    fun clearLine(x1: Float, y1: Float, x2: Float, y2: Float) {
        linePath.rewind()
        linePath.moveTo(x1, y1)
        linePath.lineTo(x2, y2)
        draw.drawPath(linePath, Color.Black, style = wideStroke, blendMode = BlendMode.Clear)
    }

    /**
     * Strokes the first [t] (0 to 1) of the polyline through [points] (x0, y0, x1, y1, ...),
     * measured by length, so a tick draws itself in at an even speed.
     */
    fun polyline(
        points: FloatArray,
        t: Float,
        c: Color = color,
        alpha: Float = 1f,
        index: Int = 5,
        blendMode: BlendMode = BlendMode.SrcOver,
    ) {
        if (t <= 0f || alpha <= 0f) return
        var total = 0f
        for (i in 2 until points.size step 2) total += dist(points[i - 2], points[i - 1], points[i], points[i + 1])
        var remaining = total * t.coerceAtMost(1f)
        val p = path(index)
        p.moveTo(points[0], points[1])
        for (i in 2 until points.size step 2) {
            val len = dist(points[i - 2], points[i - 1], points[i], points[i + 1])
            if (remaining >= len) {
                p.lineTo(points[i], points[i + 1])
                remaining -= len
            } else {
                val k = if (len == 0f) 0f else remaining / len
                p.lineTo(lerp(points[i - 2], points[i], k), lerp(points[i - 1], points[i + 1], k))
                break
            }
        }
        strokePath(p, c, alpha, blendMode)
    }

    /**
     * A diagonal slash from top-left to bottom-right that draws in with [t], cutting a gap through
     * whatever was drawn before it in the same [layer] (the "off" mark on eye, mic and Wi-Fi icons).
     */
    fun slash(t: Float, c: Color = color) {
        if (t <= 0f) return
        val x2 = lerp(3f, 21f, t)
        clearLine(3f, 3f, x2, x2)
        line(3f, 3f, x2, x2, c)
    }
}

private fun dist(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    val dx = x2 - x1
    val dy = y2 - y1
    return sqrt(dx * dx + dy * dy)
}

internal fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t

internal fun lerp(a: Color, b: Color, t: Float): Color {
    val k = t.coerceIn(0f, 1f)
    return Color(lerp(a.red, b.red, k), lerp(a.green, b.green, k), lerp(a.blue, b.blue, k), lerp(a.alpha, b.alpha, k))
}

/** Where [t] is between [start] and [end], clamped to 0..1. Used to stage parts of a transition. */
internal fun phase(t: Float, start: Float, end: Float): Float = ((t - start) / (end - start)).coerceIn(0f, 1f)

internal fun easeOutBack(x: Float, overshoot: Float = 1.7f): Float {
    val k = x - 1f
    return 1f + (overshoot + 1f) * k * k * k + overshoot * k * k
}

internal fun easeInOut(x: Float): Float = x * x * (3f - 2f * x)

/** A bump that rises from 0 to 1 and back over 0..1. */
internal fun bump(x: Float): Float = if (x <= 0f || x >= 1f) 0f else 4f * x * (1f - x)
