package io.github.zainkhalid91.motionicons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Defaults shared by every icon below [ProvideMotionIconStyle]. Each icon also takes these as
 * parameters, which win over the style.
 *
 * @param color Stroke colour. With Material, pass `MaterialTheme.colorScheme.onSurface` or `LocalContentColor.current`.
 * @param size Icon size. Icons draw on a 24-unit grid, so 24.dp is one unit per dp.
 * @param strokeWidth Line weight at [size]. 2.dp at 24.dp matches Lucide and Material Symbols Rounded.
 * @param speed Multiplies how fast transitions run. 2f is twice as fast, 0.5f half as fast.
 */
@Immutable
public class MotionIconStyle(
    public val color: Color = Color(0xFF1F2937),
    public val size: Dp = 24.dp,
    public val strokeWidth: Dp = 2.dp,
    public val speed: Float = 1f,
) {
    init {
        require(speed > 0f) { "speed must be positive, was $speed" }
    }

    public fun copy(
        color: Color = this.color,
        size: Dp = this.size,
        strokeWidth: Dp = this.strokeWidth,
        speed: Float = this.speed,
    ): MotionIconStyle = MotionIconStyle(color, size, strokeWidth, speed)

    override fun equals(other: Any?): Boolean = other is MotionIconStyle &&
        color == other.color && size == other.size && strokeWidth == other.strokeWidth && speed == other.speed

    override fun hashCode(): Int =
        ((color.hashCode() * 31 + size.hashCode()) * 31 + strokeWidth.hashCode()) * 31 + speed.hashCode()

    override fun toString(): String = "MotionIconStyle(color=$color, size=$size, strokeWidth=$strokeWidth, speed=$speed)"
}

/** The style icons read their defaults from. Set it with [ProvideMotionIconStyle]. */
public val LocalMotionIconStyle: ProvidableCompositionLocal<MotionIconStyle> = staticCompositionLocalOf { MotionIconStyle() }

/** Sets the default colour, size, stroke and speed for every icon in [content]. */
@Composable
public fun ProvideMotionIconStyle(style: MotionIconStyle, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalMotionIconStyle provides style, content = content)
}

internal val LocalFrozenIcons = staticCompositionLocalOf { false }

/**
 * Makes every icon in [content] jump straight to its new state, with no transition and no
 * flourish. Use it in screenshot tests (Paparazzi, Roborazzi, Compose UI tests) so a test never
 * captures an icon halfway through a morph:
 *
 * ```
 * paparazzi.snapshot { FreezeMotionIcons { PlayerScreen(playing = true) } }
 * ```
 */
@Composable
public fun FreezeMotionIcons(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalFrozenIcons provides true, content = content)
}
