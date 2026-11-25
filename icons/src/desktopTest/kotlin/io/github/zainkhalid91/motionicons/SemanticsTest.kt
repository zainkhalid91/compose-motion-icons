package io.github.zainkhalid91.motionicons

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SemanticsTest {
    @Test
    fun descriptionFollowsTheState() = runComposeUiTest {
        var liked by mutableStateOf(false)
        setContent { HeartIcon(liked = liked) }
        onNodeWithContentDescription("Like").assertExists()
        liked = true
        onNodeWithContentDescription("Unlike").assertExists()
    }

    @Test
    fun nullDescriptionIsDecorative() = runComposeUiTest {
        setContent { HeartIcon(liked = true, contentDescription = null) }
        onAllNodesWithContentDescription("Unlike").assertCountEquals(0)
        onAllNodesWithContentDescription("Like").assertCountEquals(0)
    }

    @Test
    fun resultIconsAnnounceThemselves() = runComposeUiTest {
        setContent { CopyIcon(copied = true) }
        onNodeWithContentDescription("Copied")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.LiveRegion))
    }

    @Test
    fun levelIconsDescribeTheirLevel() = runComposeUiTest {
        setContent {
            VolumeIcon(level = 0.5f)
            WifiIcon(strength = 1f)
        }
        onNodeWithContentDescription("Volume 50%").assertExists()
        onNodeWithContentDescription("Wi-Fi, 3 of 3 bars").assertExists()
    }
}
