package io.github.zainkhalid91.motionicons.gallery

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

@JsFun("(text) => { if (navigator.clipboard) navigator.clipboard.writeText(text); }")
private external fun copyToClipboard(text: String)

/**
 * The website mounts the playground inside its #playground-app section. A page without that
 * element gets the full-screen gallery instead.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val host = document.getElementById("playground-app")
    if (host != null) {
        ComposeViewport(host) { Playground(onCopy = ::copyToClipboard) }
    } else {
        ComposeViewport(document.body!!) { IconGallery() }
    }
}
