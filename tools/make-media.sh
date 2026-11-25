#!/usr/bin/env bash
# Turns gallery/build/frames (./gradlew :gallery:recordFrames) into the README GIFs in docs/media,
# and gallery/build/demoreel (./gradlew :gallery:recordDemoReel) into docs/media/demo-reel.mp4.
# Needs ffmpeg on the PATH.
set -euo pipefail
cd "$(dirname "$0")/.."
frames=gallery/build/frames
out=docs/media
mkdir -p "$out"
palette="split[a][b];[a]palettegen=max_colors=128:stats_mode=diff[p];[b][p]paletteuse=dither=bayer:bayer_scale=4"
ffmpeg -loglevel error -y -framerate 30 -i "$frames/all/%03d.png" -vf "scale=800:-1:flags=lanczos,$palette" -loop 0 "$out/all.gif"
ffmpeg -loglevel error -y -framerate 30 -i "$frames/before-after/%03d.png" -vf "scale=620:-1:flags=lanczos,$palette" -loop 0 "$out/before-after.gif"
for dir in "$frames"/*Icon/; do
  name=$(basename "$dir")
  ffmpeg -loglevel error -y -framerate 30 -i "$dir/%03d.png" \
    -vf "scale=100:100:flags=lanczos,split[a][b];[a]palettegen=max_colors=64[p];[b][p]paletteuse" \
    -loop 0 "$out/$name.gif"
done
if [ -d gallery/build/demoreel ]; then
  ffmpeg -loglevel error -y -framerate 30 -i gallery/build/demoreel/%04d.png \
    -c:v libx264 -pix_fmt yuv420p -crf 18 -movflags +faststart "$out/demo-reel.mp4"
fi
echo "Media written to $out"
