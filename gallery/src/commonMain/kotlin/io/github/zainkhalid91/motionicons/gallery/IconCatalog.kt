package io.github.zainkhalid91.motionicons.gallery

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.zainkhalid91.motionicons.AddCloseIcon
import io.github.zainkhalid91.motionicons.BellIcon
import io.github.zainkhalid91.motionicons.BookmarkIcon
import io.github.zainkhalid91.motionicons.CheckboxIcon
import io.github.zainkhalid91.motionicons.CopyIcon
import io.github.zainkhalid91.motionicons.DownloadIcon
import io.github.zainkhalid91.motionicons.EyeIcon
import io.github.zainkhalid91.motionicons.GridListIcon
import io.github.zainkhalid91.motionicons.HeartIcon
import io.github.zainkhalid91.motionicons.LockIcon
import io.github.zainkhalid91.motionicons.MenuCloseIcon
import io.github.zainkhalid91.motionicons.MicIcon
import io.github.zainkhalid91.motionicons.PlayPauseIcon
import io.github.zainkhalid91.motionicons.SearchCloseIcon
import io.github.zainkhalid91.motionicons.SendIcon
import io.github.zainkhalid91.motionicons.StarIcon
import io.github.zainkhalid91.motionicons.ThemeIcon
import io.github.zainkhalid91.motionicons.TrashIcon
import io.github.zainkhalid91.motionicons.VolumeIcon
import io.github.zainkhalid91.motionicons.WifiIcon

/**
 * One icon in the gallery. [steps] is how many states it cycles through when tapped: 2 for
 * on/off icons, more for levels. Step 0 is the resting state.
 */
public class IconEntry(
    public val id: Int,
    public val name: String,
    public val category: String,
    public val batch: String,
    public val steps: Int = 2,
    public val content: @Composable (step: Int, modifier: Modifier) -> Unit,
)

/** Every icon, in the order it was added. ICONS.md must list the same ids and names. */
public object IconCatalog {
    private const val W48 = "2026-W48"

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
    )
}
