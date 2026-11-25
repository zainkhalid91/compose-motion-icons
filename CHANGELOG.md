# Changelog

## 0.3.0 (2026-11-25)

10 more icons, 40 in all. Batch 3: messaging and shopping.

- Messaging: `MailIcon`, `PhoneIcon`, `TypingIcon` (loops while typing), `ReplyIcon` (reply-all)
- Shopping: `CartIcon` (takes a count; each new item drops in), `FilterSortIcon`, `GiftIcon`
- Status: `SyncIcon` (spins while syncing, settles when it stops), `UploadIcon` (progress ring)
- Social: `ThumbUpIcon`
- Icons are grouped by category in the gallery and the README.
- The web gallery shows every icon straight away as an animated preview, grouped by category,
  while the live version loads.

## 0.2.0 (2026-11-25)

10 more icons, 30 in all. Batch 2: media and camera.

- Playback: `RecordIcon`, `RepeatIcon` (with a repeat-one mode), `ShuffleIcon`, `FullscreenIcon`,
  `SpeedIcon`, `PipIcon`, `CastIcon`, `EqualizerIcon`
- Camera: `CameraFlipIcon`, `ApertureIcon`
- `EqualizerIcon` is the first looping icon: it moves while playing, stops under
  `FreezeMotionIcons` and when system animations are off.
- The web gallery shows real download progress while it loads.

## 0.1.0 (2026-11-25)

First release: 20 icons that animate between states.

- Navigation: `MenuCloseIcon`, `AddCloseIcon`, `SearchCloseIcon`, `GridListIcon`
- Media: `PlayPauseIcon`, `VolumeIcon`, `MicIcon`
- Social: `HeartIcon`, `StarIcon`, `BookmarkIcon`, `SendIcon`
- Status: `BellIcon`, `WifiIcon`, `DownloadIcon`, `CopyIcon`, `CheckboxIcon`
- System: `LockIcon`, `EyeIcon`, `ThemeIcon`, `TrashIcon`
- `MotionIconStyle` and `ProvideMotionIconStyle` for colour, size, stroke and speed.
- `FreezeMotionIcons` for screenshot tests.
- Android, desktop (JVM) and web (Wasm) targets. Live gallery on GitHub Pages.
