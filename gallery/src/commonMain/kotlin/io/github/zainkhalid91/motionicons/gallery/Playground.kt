package io.github.zainkhalid91.motionicons.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.zainkhalid91.motionicons.MotionIconStyle
import io.github.zainkhalid91.motionicons.ProvideMotionIconStyle
import kotlinx.coroutines.delay

// The website's dark palette, so the playground sits in the page without a seam.
private val Surface = Color(0xFF181818)
private val Raised = Color(0xFF1F1F1F)
private val Hover = Color(0xFF272727)
private val Line = Color(0xFF272727)
private val Code = Color(0xFF000000)
private val Faint = Color(0xFF9B9B9B)
private val Accent = Color(0xFF7C9CFF)

private val Strokes = listOf(1.5f to "Thin", 2f to "Regular", 2.5f to "Bold")
private val PlaygroundSpeeds = listOf(0.5f to "0.5x", 1f to "1x", 2f to "2x")

/**
 * The interactive playground on the website. Search and filter the icons, hover one to preview
 * its next state, click it to open it in the inspector, where every state, colour, stroke and speed
 * can be tried and the matching Kotlin copied.
 *
 * @param onCopy Puts text on the clipboard. The web page passes the browser's clipboard.
 */
@Composable
public fun Playground(onCopy: (String) -> Unit, modifier: Modifier = Modifier) {
    val steps = remember { mutableStateListOf(*Array(IconCatalog.entries.size) { 0 }) }
    var selected by remember { mutableStateOf(IconCatalog.entries.first { it.name == "HeartIcon" }) }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<String?>(null) }
    var color by remember { mutableStateOf(GalleryPalette.first()) }
    var stroke by remember { mutableStateOf(2f) }
    var speed by remember { mutableStateOf(1f) }
    val ordered = remember { IconCatalog.grouped.flatMap { it.second } }
    val visible = ordered.filter { e ->
        (category == null || e.category == category) &&
            (query.isBlank() || e.name.contains(query.trim(), ignoreCase = true) || e.category.contains(query.trim(), ignoreCase = true))
    }
    val select: (IconEntry) -> Unit = { selected = it }
    val inspector: @Composable (Modifier, Boolean) -> Unit = { m, compact ->
        Inspector(
            entry = selected,
            steps = steps,
            color = color,
            onColor = { color = it },
            stroke = stroke,
            onStroke = { stroke = it },
            speed = speed,
            onSpeed = { speed = it },
            onCopy = onCopy,
            compact = compact,
            modifier = m,
        )
    }
    ProvideMotionIconStyle(MotionIconStyle(color = color, strokeWidth = stroke.dp, speed = speed)) {
        BoxWithConstraints(modifier.fillMaxSize().background(Surface)) {
            if (maxWidth >= 820.dp) {
                Row(Modifier.fillMaxSize()) {
                    Browser(visible, steps, selected, query, { query = it }, category, { category = it }, select, Modifier.weight(1f).fillMaxHeight())
                    Box(Modifier.width(1.dp).fillMaxHeight().background(Line))
                    inspector(Modifier.width(360.dp).fillMaxHeight(), false)
                }
            } else {
                Column(Modifier.fillMaxSize()) {
                    inspector(Modifier.fillMaxWidth(), true)
                    Box(Modifier.fillMaxWidth().height(1.dp).background(Line))
                    Browser(visible, steps, selected, query, { query = it }, category, { category = it }, select, Modifier.weight(1f).fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun Browser(
    visible: List<IconEntry>,
    steps: SnapshotStateList<Int>,
    selected: IconEntry,
    query: String,
    onQuery: (String) -> Unit,
    category: String?,
    onCategory: (String?) -> Unit,
    onSelect: (IconEntry) -> Unit,
    modifier: Modifier,
) {
    Column(modifier.padding(16.dp)) {
        SearchField(query, onQuery)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pill("All ${IconCatalog.entries.size}", category == null) { onCategory(null) }
            for ((name, group) in IconCatalog.grouped) {
                if (group.isNotEmpty()) Pill("$name ${group.size}", category == name) { onCategory(if (category == name) null else name) }
            }
        }
        Spacer(Modifier.height(16.dp))
        if (visible.isEmpty()) {
            Column(Modifier.fillMaxWidth().padding(top = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                BasicText("No icon matches “${query.trim()}”", style = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium))
                Spacer(Modifier.height(8.dp))
                BasicText("Try cart, play or heart, or clear the filters.", style = TextStyle(color = Faint, fontSize = 14.sp))
                Spacer(Modifier.height(16.dp))
                Pill("Clear filters", true) {
                    onQuery("")
                    onCategory(null)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(96.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                items(visible, key = { it.id }) { entry ->
                    Tile(entry, steps, entry.id == selected.id) { onSelect(entry) }
                }
            }
        }
    }
}

/** Hovering previews the next state; clicking selects the icon and moves it to that state. */
@Composable
private fun Tile(entry: IconEntry, steps: SnapshotStateList<Int>, selected: Boolean, onSelect: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    // After a click the icon is already in the previewed state, so the preview waits for the
    // pointer to leave before it arms again.
    var armed by remember { mutableStateOf(true) }
    LaunchedEffect(hovered) { if (!hovered) armed = true }
    val index = entry.id - 1
    val shown = if (hovered && armed) (steps[index] + 1) % entry.steps else steps[index]
    Column(
        Modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(if (hovered) Hover else Raised, RoundedCornerShape(12.dp))
            .border(1.dp, if (selected) Accent else Line, RoundedCornerShape(12.dp))
            .hoverable(interaction)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(interaction, indication = null, role = Role.Button) {
                steps[index] = (steps[index] + 1) % entry.steps
                armed = false
                onSelect()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        entry.content(shown, Modifier.size(32.dp))
        Spacer(Modifier.height(12.dp))
        BasicText(entry.name.removeSuffix("Icon"), style = TextStyle(color = if (selected) Color.White else Faint, fontSize = 12.sp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Inspector(
    entry: IconEntry,
    steps: SnapshotStateList<Int>,
    color: Color,
    onColor: (Color) -> Unit,
    stroke: Float,
    onStroke: (Float) -> Unit,
    speed: Float,
    onSpeed: (Float) -> Unit,
    onCopy: (String) -> Unit,
    compact: Boolean,
    modifier: Modifier,
) {
    val index = entry.id - 1
    val step = steps[index]
    val advance = { steps[index] = (step + 1) % entry.steps }
    Column(modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
        if (compact) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Stage(entry, step, 112.dp, 64.dp, advance)
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) { Heading(entry) }
            }
        } else {
            Stage(entry, step, 200.dp, 96.dp, advance)
            Spacer(Modifier.height(16.dp))
            Heading(entry)
        }
        Spacer(Modifier.height(16.dp))
        Label("State")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (s in 0 until entry.steps) {
                Pill(IconText.state(entry.name, s), s == step, mono = true) { steps[index] = s }
            }
        }
        Spacer(Modifier.height(16.dp))
        Label("Style")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            for (c in GalleryPalette) {
                Box(
                    Modifier
                        .size(24.dp)
                        .background(c, CircleShape)
                        .border(2.dp, if (c == color) Color.White else Color.Transparent, CircleShape)
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable(role = Role.RadioButton) { onColor(c) },
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for ((value, label) in Strokes) Pill(label, stroke == value) { onStroke(value) }
            for ((value, label) in PlaygroundSpeeds) Pill(label, speed == value) { onSpeed(value) }
        }
        Spacer(Modifier.height(16.dp))
        CodeBlock(code(entry, step, color, stroke, speed), onCopy)
    }
}

@Composable
private fun Stage(entry: IconEntry, step: Int, box: Dp, icon: Dp, onTap: () -> Unit) {
    Box(
        Modifier
            .then(if (box == 200.dp) Modifier.fillMaxWidth() else Modifier.width(box))
            .height(box)
            .background(Raised, RoundedCornerShape(16.dp))
            .border(1.dp, Line, RoundedCornerShape(16.dp))
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(remember { MutableInteractionSource() }, indication = null, role = Role.Button, onClick = onTap),
        contentAlignment = Alignment.Center,
    ) {
        entry.content(step, Modifier.size(icon))
        // The compact stage is too small for the hint without it touching the icon.
        if (box == 200.dp) BasicText(
            "Click to change state",
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
            style = TextStyle(color = Faint, fontSize = 12.sp),
        )
    }
}

@Composable
private fun Heading(entry: IconEntry) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        BasicText(entry.name, style = TextStyle(color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.width(8.dp))
        BasicText(
            entry.category,
            modifier = Modifier.background(Accent.copy(alpha = 0.16f), RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 2.dp),
            style = TextStyle(color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Medium),
            softWrap = false,
            maxLines = 1,
        )
    }
    Spacer(Modifier.height(4.dp))
    val motion = IconText.motion[entry.name].orEmpty()
    BasicText(motion.replaceFirstChar { it.uppercase() } + ".", style = TextStyle(color = Faint, fontSize = 14.sp, lineHeight = 20.sp))
}

@Composable
private fun Label(text: String) {
    BasicText(text, modifier = Modifier.padding(bottom = 8.dp), style = TextStyle(color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium))
}

@Composable
private fun Pill(label: String, selected: Boolean, mono: Boolean = false, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    BasicText(
        label,
        modifier = Modifier
            .background(if (selected) Color.White else if (hovered) Hover else Raised, RoundedCornerShape(50))
            .border(1.dp, if (selected) Color.White else Line, RoundedCornerShape(50))
            .hoverable(interaction)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(interaction, indication = null, role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        style = TextStyle(
            color = if (selected) Color.Black else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default,
        ),
    )
}

@Composable
private fun SearchField(query: String, onQuery: (String) -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Raised, RoundedCornerShape(12.dp))
            .border(1.dp, Line, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (query.isEmpty()) BasicText("Search ${IconCatalog.entries.size} icons", style = TextStyle(color = Faint, fontSize = 14.sp))
        BasicTextField(
            value = query,
            onValueChange = onQuery,
            singleLine = true,
            textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
            cursorBrush = SolidColor(Accent),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CodeBlock(code: String, onCopy: (String) -> Unit) {
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(1600)
            copied = false
        }
    }
    Box(
        Modifier
            .fillMaxWidth()
            .background(Code, RoundedCornerShape(12.dp))
            .border(1.dp, Line, RoundedCornerShape(12.dp))
            .padding(12.dp),
    ) {
        BasicText(code, style = TextStyle(color = Color(0xFFB4C6FF), fontSize = 12.sp, lineHeight = 18.sp, fontFamily = FontFamily.Monospace))
        Box(Modifier.align(Alignment.TopEnd)) {
            Pill(if (copied) "Copied" else "Copy", copied) {
                onCopy(code)
                copied = true
            }
        }
    }
}

/** The Kotlin for what the inspector shows, with only the arguments that differ from defaults. */
internal fun code(entry: IconEntry, step: Int, color: Color, stroke: Float, speed: Float): String {
    val args = mutableListOf(IconText.state(entry.name, step))
    if (color != GalleryPalette.first()) {
        args += "color = Color(0x" + color.toArgb().toUInt().toString(16).uppercase().padStart(8, '0') + ")"
    }
    if (stroke != 2f) args += "strokeWidth = ${stroke}.dp"
    val call = entry.name + "(\n" + args.joinToString("") { "    $it,\n" } + ")"
    return if (speed == 1f) call else "ProvideMotionIconStyle(MotionIconStyle(speed = ${speed}f)) {\n" +
        call.lines().joinToString("\n") { "    $it" } + "\n}"
}
