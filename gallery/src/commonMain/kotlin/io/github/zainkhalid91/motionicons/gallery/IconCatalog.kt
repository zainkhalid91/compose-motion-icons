package io.github.zainkhalid91.motionicons.gallery

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.zainkhalid91.motionicons.AddCloseIcon
import io.github.zainkhalid91.motionicons.ApertureIcon
import io.github.zainkhalid91.motionicons.BellIcon
import io.github.zainkhalid91.motionicons.BookmarkIcon
import io.github.zainkhalid91.motionicons.CameraFlipIcon
import io.github.zainkhalid91.motionicons.CastIcon
import io.github.zainkhalid91.motionicons.CheckboxIcon
import io.github.zainkhalid91.motionicons.CopyIcon
import io.github.zainkhalid91.motionicons.DownloadIcon
import io.github.zainkhalid91.motionicons.EqualizerIcon
import io.github.zainkhalid91.motionicons.EyeIcon
import io.github.zainkhalid91.motionicons.FullscreenIcon
import io.github.zainkhalid91.motionicons.GridListIcon
import io.github.zainkhalid91.motionicons.HeartIcon
import io.github.zainkhalid91.motionicons.LockIcon
import io.github.zainkhalid91.motionicons.MenuCloseIcon
import io.github.zainkhalid91.motionicons.MicIcon
import io.github.zainkhalid91.motionicons.PipIcon
import io.github.zainkhalid91.motionicons.PlayPauseIcon
import io.github.zainkhalid91.motionicons.RecordIcon
import io.github.zainkhalid91.motionicons.RepeatIcon
import io.github.zainkhalid91.motionicons.SearchCloseIcon
import io.github.zainkhalid91.motionicons.SendIcon
import io.github.zainkhalid91.motionicons.ShuffleIcon
import io.github.zainkhalid91.motionicons.SpeedIcon
import io.github.zainkhalid91.motionicons.StarIcon
import io.github.zainkhalid91.motionicons.ThemeIcon
import io.github.zainkhalid91.motionicons.TrashIcon
import io.github.zainkhalid91.motionicons.VolumeIcon
import io.github.zainkhalid91.motionicons.WifiIcon

/**
 * One icon in the gallery. [steps] is how many states it cycles through when tapped: 2 for
 * on/off icons, more for levels. Step 0 is the resting state. [loops] lists steps where the icon
 * keeps moving (an equalizer while playing), which tests can't expect to settle.
 */
public class IconEntry(
    public val id: Int,
    public val name: String,
    public val category: String,
    public val batch: String,
    public val steps: Int = 2,
    public val loops: Set<Int> = emptySet(),
    public val content: @Composable (step: Int, modifier: Modifier) -> Unit,
)

private val SpeedSteps = floatArrayOf(0.25f, 0.5f, 0.75f, 1f)

/** Every icon, in the order it was added. ICONS.md must list the same ids and names. */
public object IconCatalog {
    private const val W48 = "2026-W48"
    private const val W49 = "2026-W49"

    public val entries: List<IconEntry> = listOf(
        IconEntry(1, "MenuCloseIcon", "Navigation", W48) { s, m -> MenuCloseIcon(open = s == 1, modifier = m) },
        IconEntry(2, "PlayPauseIcon", "Media", W48) { s, m -> PlayPauseIcon(playing = s == 1, modifier = m) },
        IconEntry(3, "HeartIcon", "Social", W48) { s, m -> HeartIcon(liked = s == 1, modifier = m) },
        IconEntry(4, "BellIcon", "Status", W48) { s, m -> BellIcon(badge = s == 1, modifier = m) },
        IconEntry(5, "LockIcon", "System", W48) { s, m -> LockIcon(locked = s == 0, modifier = m) },
        IconEntry(6, "EyeIcon", "System", W48) { s, m -> EyeIcon(visible = s == 0, modifier = m) },
        IconEntry(7, "ThemeIcon", "System", W48) { s, m -> ThemeIcon(dark = s == 1, modifier = m) },
        IconEntry(8, "SearchCloseIcon", "Navigation", W48) { s, m -> SearchCloseIcon(active = s == 1, modifier = m) },
        IconEntry(9, "CheckboxIcon", "Status", W48) { s, m -> CheckboxIcon(checked = s == 1, modifier = m) },
        IconEntry(10, "BookmarkIcon", "Social", W48) { s, m -> BookmarkIcon(saved = s == 1, modifier = m) },
        IconEntry(11, "VolumeIcon", "Media", W48, steps = 4) { s, m -> VolumeIcon(level = (3 - s) / 3f, modifier = m) },
        IconEntry(12, "WifiIcon", "Status", W48, steps = 4) { s, m -> WifiIcon(strength = (3 - s) / 3f, enabled = s != 3, modifier = m) },
        IconEntry(13, "MicIcon", "Media", W48) { s, m -> MicIcon(muted = s == 1, modifier = m) },
        IconEntry(14, "AddCloseIcon", "Navigation", W48) { s, m -> AddCloseIcon(open = s == 1, modifier = m) },
        IconEntry(15, "CopyIcon", "Status", W48) { s, m -> CopyIcon(copied = s == 1, modifier = m) },
        IconEntry(16, "DownloadIcon", "Status", W48) { s, m -> DownloadIcon(done = s == 1, modifier = m) },
        IconEntry(17, "SendIcon", "Social", W48) { s, m -> SendIcon(sent = s == 1, modifier = m) },
        IconEntry(18, "StarIcon", "Social", W48) { s, m -> StarIcon(starred = s == 1, modifier = m) },
        IconEntry(19, "TrashIcon", "System", W48) { s, m -> TrashIcon(open = s == 1, modifier = m) },
        IconEntry(20, "GridListIcon", "Navigation", W48) { s, m -> GridListIcon(grid = s == 0, modifier = m) },
        IconEntry(21, "RecordIcon", "Media", W49) { s, m -> RecordIcon(recording = s == 1, modifier = m) },
        IconEntry(22, "RepeatIcon", "Media", W49, steps = 3) { s, m -> RepeatIcon(enabled = s > 0, one = s == 2, modifier = m) },
        IconEntry(23, "ShuffleIcon", "Media", W49) { s, m -> ShuffleIcon(on = s == 1, modifier = m) },
        IconEntry(24, "FullscreenIcon", "Media", W49) { s, m -> FullscreenIcon(fullscreen = s == 1, modifier = m) },
        IconEntry(25, "CameraFlipIcon", "Camera", W49) { s, m -> CameraFlipIcon(front = s == 1, modifier = m) },
        IconEntry(26, "SpeedIcon", "Media", W49, steps = 4) { s, m -> SpeedIcon(level = SpeedSteps[s], modifier = m) },
        IconEntry(27, "PipIcon", "Media", W49) { s, m -> PipIcon(pip = s == 1, modifier = m) },
        IconEntry(28, "CastIcon", "Media", W49) { s, m -> CastIcon(connected = s == 1, modifier = m) },
        IconEntry(29, "EqualizerIcon", "Media", W49, loops = setOf(1)) { s, m -> EqualizerIcon(playing = s == 1, modifier = m) },
        IconEntry(30, "ApertureIcon", "Camera", W49) { s, m -> ApertureIcon(open = s == 0, modifier = m) },
    )
}
