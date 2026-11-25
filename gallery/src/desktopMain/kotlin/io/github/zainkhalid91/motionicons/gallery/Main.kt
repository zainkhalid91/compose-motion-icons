package io.github.zainkhalid91.motionicons.gallery

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Compose Motion Icons",
        state = rememberWindowState(width = 900.dp, height = 860.dp),
    ) {
        IconGallery()
    }
}
