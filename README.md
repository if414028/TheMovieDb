# TheMovieDb

A simple Android app for browsing movies using the TMDB API. Built as part of an Android developer assessment.

## Features

- Browse official movie genres.
- Discover movies by genre with endless scrolling.
- View movie details, including synopsis, rating, and release date.
- Read user reviews with endless scrolling.
- Watch YouTube trailers or open them in YouTube.
- Handle loading, empty results, and errors with retry actions.

## Tech stack

Kotlin, Jetpack Compose, MVVM, Hilt, Retrofit, OkHttp, Kotlin Serialization, Coroutines, StateFlow, Navigation Compose, Paging 3, and Coil.

The UI uses ViewModels for state management and a repository for fetching and mapping API data. Trailers play through a WebView using the YouTube embedded player.

## Run the app

1. Clone this repository and open it in Android Studio.
2. Use JDK 17 and install Android SDK 36.
3. Get an **API Read Access Token** from [TMDB](https://www.themoviedb.org/settings/api).
4. Add it to the root `local.properties` file, keeping the existing `sdk.dir` entry:

   ```properties
   TMDB_READ_ACCESS_TOKEN=your_read_access_token
   ```

5. Sync Gradle and run the app on a device or emulator with Android 7.0 (API 24) or newer.

Use the long read access token without the `Bearer` prefix. Keep `local.properties` out of Git.

To build a debug APK:

```bash
./gradlew :app:assembleDebug
```

The APK will be in `app/build/outputs/apk/debug/`.

## Tests

Tests cover ViewModel states, pagination, response parsing, trailer selection, navigation, and empty/error/retry scenarios. They use fake repository data and MockWebServer instead of calling the live TMDB API.

Run local tests:

```bash
./gradlew :app:testDebugUnitTest
```

Run Compose UI tests with an emulator or device connected:

```bash
./gradlew :app:connectedDebugAndroidTest
```

## Notes

The app requires an internet connection and does not store movies for offline browsing. Trailer playback depends on YouTube and the device's WebView. Playback position resets when the player is recreated.

## Credits

Movie data and images are provided by [TMDB](https://www.themoviedb.org/).

This product uses the TMDB API but is not endorsed or certified by TMDB.
