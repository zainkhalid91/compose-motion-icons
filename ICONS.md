# Icon registry

Every icon in the library, in the order it was added. This file is the source of truth for
"have we done this already?". A test (`gallery/src/desktopTest/.../RegistryTest.kt`) fails the
build if it drifts from `IconCatalog` or from the public functions in `:icons`.

## Rules for adding an icon

1. Read the **Motion signature** column first. A new icon must differ in its *motion idea*, not
   only its picture. "HeartIcon but a thumb" is a new picture with the same motion; give it a
   motion of its own, or don't add it.
2. Every icon takes its state as a parameter (`liked`, `open`, `level`) and animates whenever it
   changes. It starts in that state with no animation, reverses cleanly when interrupted, honours
   `FreezeMotionIcons` and reduced motion, and has a state-aware `contentDescription`.
3. Draw on the 24 x 24 grid with the shared stroke, so icons sit together at any size.
4. Add the next number. Numbers are permanent; never reuse one, even if an icon is removed
   (mark it ~~struck through~~ instead).
5. Add the row here, the entry in `IconCatalog` and the function in `icons/src/commonMain`.
   `./gradlew :gallery:desktopTest` checks all three agree and renders every state.

## Shipped

| # | Name | Category | Motion signature | Batch |
|---|------|----------|------------------|-------|
| 1 | `MenuCloseIcon` | Navigation | three lines slide together, cross into an X while the whole icon turns 90 degrees | 2026-W48 |
| 2 | `PlayPauseIcon` | Media | triangle splits down the middle, each half squares into a pause bar, with a squash | 2026-W48 |
| 3 | `HeartIcon` | Social | fills from the centre, squashes and pops, ring of 8 sparks flies out | 2026-W48 |
| 4 | `BellIcon` | Status | bell swings with a lagging clapper as a badge pops on and cuts its own gap | 2026-W48 |
| 5 | `LockIcon` | System | shackle springs up and tilts open on its hinge; clunks down when locked | 2026-W48 |
| 6 | `EyeIcon` | System | eye squints as a slash cuts through; on reopening the pupil glances side to side | 2026-W48 |
| 7 | `ThemeIcon` | System | sun pulls in its rays and swells while a shadow slides across, leaving a moon | 2026-W48 |
| 8 | `SearchCloseIcon` | Navigation | lens unwinds, handle stretches into one stroke of an X, second stroke draws across | 2026-W48 |
| 9 | `CheckboxIcon` | Status | box squeezes, fills from the centre, tick draws in cut out of the fill | 2026-W48 |
| 10 | `BookmarkIcon` | Social | ribbon fills from the top and stretches down like it dropped into place | 2026-W48 |
| 11 | `VolumeIcon` | Media | waves grow and fold one per third of the level; at zero an X pops in | 2026-W48 |
| 12 | `WifiIcon` | Status | arcs light up one by one with a swell as the signal grows; off slashes through | 2026-W48 |
| 13 | `MicIcon` | Media | slash cuts through the microphone while the capsule dips under the hit | 2026-W48 |
| 14 | `AddCloseIcon` | Navigation | plus spins past 45 degrees with an overshoot and settles as an X, arms snapping shorter | 2026-W48 |
| 15 | `CopyIcon` | Status | two sheets slide together, round off into a badge and get a tick | 2026-W48 |
| 16 | `DownloadIcon` | Status | arrow drops into the tray (tray gives), then folds into a tick | 2026-W48 |
| 17 | `SendIcon` | Social | paper plane flies off the top-right, a tick draws in; a new plane glides back from the bottom-left | 2026-W48 |
| 18 | `StarIcon` | Social | spins one point round with overshoot, fills, sparkles shed from its tips | 2026-W48 |
| 19 | `TrashIcon` | System | lid flips up on its left hinge with a bounce | 2026-W48 |
| 20 | `GridListIcon` | Navigation | four tiles stretch into four list rows one after another | 2026-W48 |
| 21 | `RecordIcon` | Media | red dot squares off into a stop button with a quarter turn; a ripple leaves the ring | 2026-W49 |
| 22 | `RepeatIcon` | Media | arrows chase each other half way round the loop and light up; repeat-one pops a 1 in | 2026-W49 |
| 23 | `ShuffleIcon` | Media | two parallel arrows swap their ends and bend into a crossing, with overshoot | 2026-W49 |
| 24 | `FullscreenIcon` | Media | corner vertices slide in along the diagonals while the arms stay put; the icon breathes | 2026-W49 |
| 25 | `CameraFlipIcon` | Camera | camera squeezes flat as if turning round while its inner arrows spin half a turn | 2026-W49 |
| 26 | `SpeedIcon` | Media | gauge needle swings to the level with overshoot, filling the dial behind it | 2026-W49 |
| 27 | `PipIcon` | Media | inner window shrinks into the bottom-right corner and solidifies | 2026-W49 |
| 28 | `CastIcon` | Media | screen floods with colour from the corner while the waves ripple out in turn | 2026-W49 |
| 29 | `EqualizerIcon` | Media | four bars dance on their own rhythms while playing, sink to rest when paused (loops) | 2026-W49 |
| 30 | `ApertureIcon` | Camera | six blades twist and close towards the centre, and open again | 2026-W49 |

## Backlog (to reach 50)

Ideas not built yet, grouped so each weekly batch has a theme. Check the table above before
picking one.

- **Media controls (more):** skip next/previous (triangles slide through), rewind/forward 10 s
  (arrow circles with the number), subtitles (lines type into the box).
- **Messaging:** mail (envelope flap opens, letter rises), attach (paperclip bends), thumbs-up
  (thumb lifts with a wobble), reply (arrow curls back), typing (bubble with a tick).
- **Commerce:** cart (item drops in, count badge bumps), refresh to done (arrows spin, then a
  tick), filter to sort (funnel splits into lines), upload (arrow leaves the tray), coupon cut.
- **Devices:** battery (level fills, bolt when charging), bluetooth on/off, flashlight beam,
  airplane mode, cellular bars.
- **Editing:** pencil to save tick, link/unlink (chain breaks apart), pin (pushes in), zoom in/out
  (lens with plus morphing to minus), undo/redo (arrow swings).
- **Navigation:** back arrow to menu (drawer arrow), chevron expand/collapse, home tab fill,
  settings gear (turns and settles), profile add (person with a plus popping in).
