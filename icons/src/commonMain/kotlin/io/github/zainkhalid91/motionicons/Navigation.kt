package io.github.zainkhalid91.motionicons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Three lines that slide together and cross into an X. For drawers and overflow menus.
 *
 * @param open true shows the X.
 */
@Composable
public fun MenuCloseIcon(
    open: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (open) "Close menu" else "Open menu",
) {
    val progress = animateToggle(open, 460)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val slide = easeInOut(phase(t, 0f, 0.5f))
        val turn = easeOutBack(phase(t, 0.4f, 1f), 1.2f)
        rotate(90f * turn) {
            val top = lerp(6f, 12f, slide)
            rotate(45f * turn, 12f, top) { line(4f, top, 20f, top) }
            val half = 8f * (1f - slide)
            line(12f - half, 12f, 12f + half, 12f, alpha = 1f - slide)
            val bottom = lerp(18f, 12f, slide)
            rotate(-45f * turn, 12f, bottom) { line(4f, bottom, 20f, bottom) }
        }
    }
}

/**
 * A plus that spins a quarter turn past 45 degrees and settles as an X. For floating action
 * buttons that open a menu, and add/cancel toggles.
 *
 * @param open true shows the X.
 */
@Composable
public fun AddCloseIcon(
    open: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (open) "Close" else "Add",
) {
    val progress = animateToggle(open, 420)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val turn = easeOutBack(t, 2.2f)
        // The arms dip in length mid-turn, which reads as a snap rather than a plain rotation.
        val arm = 7.5f - 1.5f * bump(t)
        rotate(135f * turn) {
            line(12f, 12f - arm, 12f, 12f + arm)
            line(12f - arm, 12f, 12f + arm, 12f)
        }
    }
}

/**
 * A magnifier whose lens unwinds while its handle stretches into one stroke of an X, then the
 * other stroke draws across. For search bars that expand and close.
 *
 * @param active true shows the X.
 */
@Composable
public fun SearchCloseIcon(
    active: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (active) "Close search" else "Search",
) {
    val progress = animateToggle(active, 560)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        val lens = phase(t, 0f, 0.55f)
        // The lens unwinds from the point where the handle leaves it.
        arc(10.5f, 10.5f, 6.5f, 45f + 360f * lens, 360f * (1f - lens))
        val stretch = easeInOut(phase(t, 0.2f, 0.75f))
        line(lerp(15.1f, 5f, stretch), lerp(15.1f, 5f, stretch), lerp(20f, 19f, stretch), lerp(20f, 19f, stretch))
        val cross = easeInOut(phase(t, 0.6f, 1f))
        if (cross > 0f) {
            val half = 7f * cross
            line(12f + half, 12f - half, 12f - half, 12f + half)
        }
    }
}

/**
 * Four tiles that stretch into four list rows one after another, and back. For grid/list view
 * switches.
 *
 * @param grid true shows the grid of tiles, false the list rows.
 */
@Composable
public fun GridListIcon(
    grid: Boolean,
    modifier: Modifier = Modifier,
    color: Color = LocalMotionIconStyle.current.color,
    size: Dp = LocalMotionIconStyle.current.size,
    strokeWidth: Dp = LocalMotionIconStyle.current.strokeWidth,
    contentDescription: String? = if (grid) "Switch to list view" else "Switch to grid view",
) {
    // 1 is the list, so the rows cascade in from the top.
    val progress = animateToggle(!grid, 620)
    IconCanvas(modifier, size, color, strokeWidth, contentDescription) {
        val t = progress.value
        for (i in 0 until 4) {
            val k = easeInOut(phase(t, i * 0.12f, 0.64f + i * 0.12f))
            val col = i % 2
            val row = i / 2
            // Grid tile.
            val gl = 4f + col * 10f
            val gt = 4f + row * 10f
            // List row: a short bullet line would read as two shapes, so each row is one bar.
            val ly = 5f + i * 4.67f
            val left = lerp(gl, 4f, k)
            val top = lerp(gt, ly, k)
            val right = lerp(gl + 6f, 20f, k)
            val bottom = lerp(gt + 6f, ly, k)
            val r = lerp(1.5f, 0f, k)
            val p = path(i)
            p.addRoundRect(RoundRect(left, top, right, bottom, CornerRadius(r, r)))
            strokePath(p)
        }
    }
}
