package io.github.zainkhalid91.motionicons.gallery

/**
 * Text the playground shows for each icon: what its motion does (the same words as ICONS.md,
 * which RegistryTest checks) and the Kotlin for each of its states.
 */
public object IconText {
    public val motion: Map<String, String> = mapOf(
        "MenuCloseIcon" to "three lines slide together, cross into an X while the whole icon turns 90 degrees",
        "PlayPauseIcon" to "triangle splits down the middle, each half squares into a pause bar, with a squash",
        "HeartIcon" to "fills from the centre, squashes and pops, ring of 8 sparks flies out",
        "BellIcon" to "bell swings with a lagging clapper as a badge pops on and cuts its own gap",
        "LockIcon" to "shackle springs up and tilts open on its hinge; clunks down when locked",
        "EyeIcon" to "eye squints as a slash cuts through; on reopening the pupil glances side to side",
        "ThemeIcon" to "sun pulls in its rays and swells while a shadow slides across, leaving a moon",
        "SearchCloseIcon" to "lens unwinds, handle stretches into one stroke of an X, second stroke draws across",
        "CheckboxIcon" to "box squeezes, fills from the centre, tick draws in cut out of the fill",
        "BookmarkIcon" to "ribbon fills from the top and stretches down like it dropped into place",
        "VolumeIcon" to "waves grow and fold one per third of the level; at zero an X pops in",
        "WifiIcon" to "arcs light up one by one with a swell as the signal grows; off slashes through",
        "MicIcon" to "slash cuts through the microphone while the capsule dips under the hit",
        "AddCloseIcon" to "plus spins past 45 degrees with an overshoot and settles as an X, arms snapping shorter",
        "CopyIcon" to "two sheets slide together, round off into a badge and get a tick",
        "DownloadIcon" to "arrow drops into the tray (tray gives), then folds into a tick",
        "SendIcon" to "paper plane flies off the top-right, a tick draws in; a new plane glides back from the bottom-left",
        "StarIcon" to "spins one point round with overshoot, fills, sparkles shed from its tips",
        "TrashIcon" to "lid flips up on its left hinge with a bounce",
        "GridListIcon" to "four tiles stretch into four list rows one after another",
        "RecordIcon" to "red dot squares off into a stop button with a quarter turn; a ripple leaves the ring",
        "RepeatIcon" to "arrows chase each other half way round the loop and light up; repeat-one pops a 1 in",
        "ShuffleIcon" to "two parallel arrows swap their ends and bend into a crossing, with overshoot",
        "FullscreenIcon" to "corner vertices slide in along the diagonals while the arms stay put; the icon breathes",
        "CameraFlipIcon" to "camera squeezes flat as if turning round while its inner arrows spin half a turn",
        "SpeedIcon" to "gauge needle swings to the level with overshoot, filling the dial behind it",
        "PipIcon" to "inner window shrinks into the bottom-right corner and solidifies",
        "CastIcon" to "screen floods with colour from the corner while the waves ripple out in turn",
        "EqualizerIcon" to "four bars dance on their own rhythms while playing, sink to rest when paused (loops)",
        "ApertureIcon" to "six blades twist and close towards the centre, and open again",
        "MailIcon" to "flap folds up and back, then a letter rises out of the pocket",
        "PhoneIcon" to "handset shakes, then swings down into the red hang-up position with overshoot",
        "ThumbUpIcon" to "thumb lifts and wobbles on its wrist while it fills from the bottom like a level",
        "TypingIcon" to "three dots pop into a bubble one by one, then bob in a wave while typing (loops)",
        "ReplyIcon" to "for reply-all a second arrowhead peels out from under the first, lagging behind",
        "CartIcon" to "an item drops into the basket per added count, the cart dips and the badge bumps",
        "SyncIcon" to "arrows tighten and spin while syncing; on stop they finish the turn and settle with overshoot (loops)",
        "FilterSortIcon" to "funnel unfolds from the rim down into three sort lines, longest first",
        "UploadIcon" to "progress ring fills round a rising arrow; at 100% the arrow hops and the ring pulses",
        "GiftIcon" to "lid pops off and hangs tilted, the bow springs and light spills out of the box",
    )

    /** The state arguments for [step], as they would appear in a call: "liked = true". */
    public fun state(name: String, step: Int): String = when (name) {
        "LockIcon" -> "locked = ${step == 0}"
        "EyeIcon" -> "visible = ${step == 0}"
        "GridListIcon" -> "grid = ${step == 0}"
        "ApertureIcon" -> "open = ${step == 0}"
        "VolumeIcon" -> "level = ${Levels[3 - step]}"
        "WifiIcon" -> if (step == 3) "strength = 0f, enabled = false" else "strength = ${Levels[3 - step]}"
        "RepeatIcon" -> when (step) {
            0 -> "enabled = false"
            1 -> "enabled = true"
            else -> "enabled = true, one = true"
        }
        "SpeedIcon" -> "level = ${listOf("0.25f", "0.5f", "0.75f", "1f")[step]}"
        "UploadIcon" -> "progress = ${listOf("0f", "0.4f", "0.75f", "1f")[step]}"
        "CartIcon" -> "count = $step"
        else -> "${Flags.getValue(name)} = ${step == 1}"
    }

    private val Levels = listOf("0f", "0.33f", "0.67f", "1f")

    private val Flags = mapOf(
        "MenuCloseIcon" to "open",
        "PlayPauseIcon" to "playing",
        "HeartIcon" to "liked",
        "BellIcon" to "badge",
        "ThemeIcon" to "dark",
        "SearchCloseIcon" to "active",
        "CheckboxIcon" to "checked",
        "BookmarkIcon" to "saved",
        "MicIcon" to "muted",
        "AddCloseIcon" to "open",
        "CopyIcon" to "copied",
        "DownloadIcon" to "done",
        "SendIcon" to "sent",
        "StarIcon" to "starred",
        "TrashIcon" to "open",
        "RecordIcon" to "recording",
        "ShuffleIcon" to "on",
        "FullscreenIcon" to "fullscreen",
        "CameraFlipIcon" to "front",
        "PipIcon" to "pip",
        "CastIcon" to "connected",
        "EqualizerIcon" to "playing",
        "MailIcon" to "open",
        "PhoneIcon" to "inCall",
        "ThumbUpIcon" to "liked",
        "TypingIcon" to "typing",
        "ReplyIcon" to "all",
        "SyncIcon" to "syncing",
        "FilterSortIcon" to "sort",
        "GiftIcon" to "open",
    )
}
