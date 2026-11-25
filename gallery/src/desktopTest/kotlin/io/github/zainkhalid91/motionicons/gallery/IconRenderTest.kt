package io.github.zainkhalid91.motionicons.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.zainkhalid91.motionicons.FreezeMotionIcons
import io.github.zainkhalid91.motionicons.MotionIconStyle
import io.github.zainkhalid91.motionicons.ProvideMotionIconStyle
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

/** Renders every icon in every state and checks the things a user would notice going wrong. */
class IconRenderTest {
    private val frameNanos = 1_000_000_000L / 60

    private fun pixels(image: Image): ByteArray = Bitmap.makeFromImage(image).readPixels()!!

    /** Renders [content] once, settled, on a plain background. */
    private fun still(content: @Composable () -> Unit): ByteArray =
        VirtualScene(96, 96, 2f) { Frame { FreezeMotionIcons(content) } }.use { pixels(it.render(0)) }

    @Composable
    private fun Frame(content: @Composable () -> Unit) {
        ProvideMotionIconStyle(MotionIconStyle(color = Color.White)) {
            Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) { content() }
        }
    }

    private fun ink(pixels: ByteArray): Int = pixels.count { it.toInt() and 0xFF > 40 }

    /**
     * Pixels that differ clearly between two renders. A path redrawn after many frames can land
     * an anti-aliased edge pixel or two differently from a fresh render, so a handful is "the same".
     */
    private fun changed(a: ByteArray, b: ByteArray): Int = (a.indices step 4).count { i ->
        (0 until 4).any { c -> kotlin.math.abs((a[i + c].toInt() and 0xFF) - (b[i + c].toInt() and 0xFF)) > 24 }
    }

    private fun same(a: ByteArray, b: ByteArray) = changed(a, b) <= 40

    /** Renders [frames] frames in a row, so animations see every frame like on a device. */
    private fun VirtualScene.play(from: Int, frames: Int): Image {
        var last: Image? = null
        for (i in from until from + frames) last = render(i * frameNanos)
        return last!!
    }

    @Test
    fun everyStateDrawsSomethingAndEveryChangeIsVisible() {
        for (entry in IconCatalog.entries) {
            val states = (0 until entry.steps).map { step -> still { entry.content(step, Modifier.size(40.dp)) } }
            states.forEachIndexed { step, p -> assertTrue(ink(p) > 200, "${entry.name} step $step is blank") }
            for (step in 1 until entry.steps) {
                assertTrue(changed(states[step], states[step - 1]) > 60, "${entry.name} looks the same at steps ${step - 1} and $step")
            }
        }
    }

    @Test
    fun iconsAppearInTheirStateWithoutAnimating() {
        // An already-liked heart in a list must not burst or fill as it scrolls into view.
        for (entry in IconCatalog.entries) {
            for (step in 0 until entry.steps) {
                if (step in entry.loops) continue
                val settled = still { entry.content(step, Modifier.size(40.dp)) }
                val first = VirtualScene(96, 96, 2f) { Frame { entry.content(step, Modifier.size(40.dp)) } }.use {
                    it.render(0)
                    pixels(it.render(frameNanos))
                }
                if (!same(first, settled)) fail("${entry.name} animates on its first frame at step $step")
            }
        }
    }

    @Test
    fun transitionsSettleOnTheTargetState() {
        for (entry in IconCatalog.entries) {
            for (target in 1 until entry.steps) {
                if (target in entry.loops) continue
                val step = mutableIntStateOf(0)
                val settled = still { entry.content(target, Modifier.size(40.dp)) }
                val end = VirtualScene(96, 96, 2f) { Frame { entry.content(step.intValue, Modifier.size(40.dp)) } }.use { scene ->
                    scene.render(0)
                    step.intValue = target
                    // Two seconds covers the longest transition and flourish.
                    pixels(scene.play(1, 120))
                }
                if (!same(end, settled)) fail("${entry.name} doesn't settle on step $target")
            }
        }
    }

    @Test
    fun transitionsMoveThroughInBetweenFrames() {
        // A transition that jumps straight to the end would look like a plain icon swap.
        for (entry in IconCatalog.entries) {
            val step = mutableIntStateOf(0)
            val start = still { entry.content(0, Modifier.size(40.dp)) }
            val end = still { entry.content(1, Modifier.size(40.dp)) }
            val middle = VirtualScene(96, 96, 2f) { Frame { entry.content(step.intValue, Modifier.size(40.dp)) } }.use { scene ->
                scene.render(0)
                step.intValue = 1
                pixels(scene.play(1, 8))
            }
            assertTrue(!same(middle, start) && !same(middle, end), "${entry.name} has no in-between frames")
        }
    }

    @Test
    fun loopingStatesKeepMovingAndStopWhenFrozen() {
        for (entry in IconCatalog.entries) {
            for (step in entry.loops) {
                val (a, b) = VirtualScene(96, 96, 2f) { Frame { entry.content(step, Modifier.size(40.dp)) } }.use { scene ->
                    pixels(scene.play(0, 20)) to pixels(scene.play(20, 7))
                }
                assertTrue(!same(a, b), "${entry.name} stands still at looping step $step")
                val (c, d) = VirtualScene(96, 96, 2f) {
                    Frame { FreezeMotionIcons { entry.content(step, Modifier.size(40.dp)) } }
                }.use { scene -> pixels(scene.play(0, 20)) to pixels(scene.play(20, 7)) }
                assertTrue(same(c, d), "${entry.name} keeps moving inside FreezeMotionIcons")
            }
        }
    }

    @Test
    fun freezeJumpsStraightToTheNewState() {
        for (entry in IconCatalog.entries) {
            val step = mutableIntStateOf(0)
            val settled = still { entry.content(1, Modifier.size(40.dp)) }
            val frozen = VirtualScene(96, 96, 2f) {
                Frame { FreezeMotionIcons { entry.content(step.intValue, Modifier.size(40.dp)) } }
            }.use { scene ->
                scene.render(0)
                step.intValue = 1
                pixels(scene.play(1, 2))
            }
            if (!same(frozen, settled)) fail("${entry.name} animates inside FreezeMotionIcons")
        }
    }
}
