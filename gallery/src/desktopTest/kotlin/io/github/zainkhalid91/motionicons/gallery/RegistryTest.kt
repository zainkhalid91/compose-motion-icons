package io.github.zainkhalid91.motionicons.gallery

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Keeps ICONS.md, the catalog and the library sources in step, so an icon can't be added twice
 * or shipped without being registered.
 */
class RegistryTest {
    // Public composables in :icons that are not icons themselves.
    private val helpers = setOf("ProvideMotionIconStyle", "FreezeMotionIcons")

    private val root = generateSequence(File("").absoluteFile) { it.parentFile }
        .first { File(it, "ICONS.md").exists() }

    // Rows look like: | 3 | `HeartIcon` | Social | ... |
    private val registry: List<Pair<Int, String>> = File(root, "ICONS.md").readLines()
        .mapNotNull { Regex("""^\|\s*(\d+)\s*\|\s*`?(\w+)`?\s*\|""").find(it) }
        .map { it.groupValues[1].toInt() to it.groupValues[2] }

    @Test
    fun idsAndNamesAreUnique() {
        val entries = IconCatalog.entries
        assertEquals(entries.size, entries.map { it.id }.toSet().size, "duplicate id in IconCatalog")
        assertEquals(entries.size, entries.map { it.name.lowercase() }.toSet().size, "duplicate name in IconCatalog")
    }

    @Test
    fun idsAreSequential() {
        assertEquals((1..IconCatalog.entries.size).toList(), IconCatalog.entries.map { it.id })
    }

    @Test
    fun registryMatchesCatalog() {
        assertEquals(IconCatalog.entries.map { it.id to it.name }, registry, "ICONS.md rows must match IconCatalog, in order")
    }

    @Test
    fun everyLibraryIconIsRegistered() {
        val sources = File(root, "icons/src/commonMain/kotlin").walk().filter { it.extension == "kt" }
        val public = sources.flatMap { file ->
            Regex("""@Composable\s+public fun (\w+)\(""").findAll(file.readText()).map { it.groupValues[1] }
        }.filterNot { it in helpers }.toSet()
        assertTrue(public.isNotEmpty(), "found no public icons; did the source layout move?")
        val missing = public - registry.map { it.second }.toSet()
        if (missing.isNotEmpty()) fail("Public icons missing from ICONS.md: $missing")
    }
}
