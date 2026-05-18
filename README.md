# AireTV

Android TV app for watching Paraguayan free-to-air TV channels. Built with Kotlin and Jetpack Compose for TV, with Media3 (ExoPlayer) for HLS playback.

Channel discovery is delegated to [airetvgo](https://github.com/elvus/airetvgo) — a Go library, consumed here as an Android `.aar` produced via `gomobile bind`, that scrapes the live stream metadata.

## Features

- D-pad / remote-friendly UI with a side channel list and a fullscreen player.
- Auto-fullscreen after a few seconds of inactivity; overlay with channel info.
- HLS playback with auto-quality selection (1080p / 720p / 360p variants exposed by `airetvgo`).
- In-memory channel cache with manual refresh; cache is cleared on `onStop`.
- Leanback launcher entry — installs as a TV app on Android TV / Fire TV / leanback emulators.

## Requirements

- Android Studio (Koala or newer) with AGP 8.11+
- JDK 17
- Android SDK 35 (compile) / 21+ (min)
- An Android TV device or emulator (`Android TV (1080p)` AVD works)

## Project layout

```
app/
  libs/airetvgo.aar          # prebuilt Go library (channel scraper)
  libs/airetvgo-sources.jar  # Java sources for the AAR
  src/main/java/com/airetv/app/
    MainActivity.kt          # entry point; wires ViewModel -> PlayerScreen
    AireTVApp.kt
    data/
      repository/ChannelRepository.kt   # thin wrapper over Airetvgo
      api/                              # Retrofit scaffolding (unused in main flow)
      model/Channel.kt, ChannelList.kt
    ui/
      ChannelViewModel.kt
      player/PlayerScreen.kt, ChannelSidebar.kt, PlayerOverlay.kt
      browse/                # ChannelCard, FeaturedChannel, BrowseScreen
      components/LoadingIndicator.kt
      theme/
```

## How it works

`ChannelRepository` calls into the `airetvgo` AAR directly (no network layer on the app side):

```kotlin
import airetvgo.Airetvgo

Airetvgo.getChannels()      // cached JSON, or fresh scrape if empty
Airetvgo.refreshChannels()  // force refresh
Airetvgo.cleanCache()       // called from MainActivity.onStop()
```

The JSON is parsed with Gson into `ChannelList` / `Channel`, exposed through `ChannelViewModel`, and rendered by `PlayerScreen` which drives an `ExoPlayer` instance with the selected channel's `stream_url`.

## Building

1. Make sure `app/libs/airetvgo.aar` exists. To rebuild it from source:

   ```sh
   git clone https://github.com/elvus/airetvgo.git
   cd airetvgo
   go install golang.org/x/mobile/cmd/gomobile@latest
   gomobile init
   gomobile bind -target=android -o airetvgo.aar .
   ```

   Copy the resulting `airetvgo.aar` (and optionally `airetvgo-sources.jar`) into `app/libs/`.

2. Build and install:

   ```sh
   ./gradlew :app:installDebug
   ```

   Then launch **AireTV** from the leanback launcher on your TV device/emulator.

## Controls

- **D-pad up/down** — move through the channel list
- **D-pad center / Enter** — enter fullscreen on the focused channel; press again to bring the sidebar back
- **D-pad up/down (fullscreen)** — exit fullscreen back to the sidebar
- **Back** — exit fullscreen; press **Back** twice on the sidebar to quit

## Notes

- `data/api/RetrofitClient.kt` and `ChannelApi` are scaffolding for an alternative HTTP backend (`http://10.0.2.2:8080/`) and are not wired into the active flow — the app uses the AAR. Remove or repurpose as needed.
- The manifest declares `usesCleartextTraffic="true"` because some upstream HLS sources are served over HTTP.
- UI strings are in Spanish.
