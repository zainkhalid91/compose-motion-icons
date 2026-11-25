import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    jvm("desktop") {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }

    // The live gallery on GitHub Pages: ./gradlew :gallery:wasmJsBrowserDistribution
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("gallery")
        browser { commonWebpackConfig { outputFileName = "gallery.js" } }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":icons"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
        }
        getByName("desktopMain").dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            // Virtual time for the recorders, so transitions follow the video's clock.
            implementation(libs.kotlinx.coroutines.test)
        }
        getByName("desktopTest").dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

val desktopMain = kotlin.jvm("desktop").compilations["main"]

// ./gradlew :gallery:recordFrames renders every icon, the grid and the before/after demo to build/frames.
tasks.register<JavaExec>("recordFrames") {
    description = "Renders each icon frame by frame to build/frames"
    classpath(desktopMain.output.allOutputs, desktopMain.runtimeDependencyFiles)
    mainClass.set("io.github.zainkhalid91.motionicons.gallery.RecordKt")
    args(layout.buildDirectory.dir("frames").get().asFile.absolutePath)
}

// ./gradlew :gallery:recordDemoReel renders the 1080 x 1350 launch video to build/demoreel.
tasks.register<JavaExec>("recordDemoReel") {
    description = "Renders the launch video frame by frame to build/demoreel"
    classpath(desktopMain.output.allOutputs, desktopMain.runtimeDependencyFiles)
    mainClass.set("io.github.zainkhalid91.motionicons.gallery.DemoReelKt")
    args(layout.buildDirectory.dir("demoreel").get().asFile.absolutePath)
}

// ./gradlew :gallery:recordBatchReel renders the latest batch's video to build/batchreel.
tasks.register<JavaExec>("recordBatchReel") {
    description = "Renders the latest batch video frame by frame to build/batchreel"
    classpath(desktopMain.output.allOutputs, desktopMain.runtimeDependencyFiles)
    mainClass.set("io.github.zainkhalid91.motionicons.gallery.BatchReelKt")
    args(layout.buildDirectory.dir("batchreel").get().asFile.absolutePath)
}

// The page shows real download progress. It needs each .wasm file's size up front, because
// a compressed response doesn't say how many bytes it will unpack to.
// The page also shows every icon's GIF straight away, grouped by category, while the wasm loads.
tasks.named("wasmJsBrowserDistribution") {
    val dist = layout.buildDirectory.dir("dist/wasmJs/productionExecutable").get().asFile
    val media = rootProject.layout.projectDirectory.dir("docs/media").asFile
    doLast {
        val preview = File(dist, "preview").apply { mkdirs() }
        media.listFiles { f -> f.name.endsWith("Icon.gif") || f.name == "icons.json" }.orEmpty()
            .forEach { it.copyTo(File(preview, it.name), overwrite = true) }
        File(media, "og.png").copyTo(File(dist, "og.png"), overwrite = true)
        val wasm = dist.listFiles { f -> f.extension == "wasm" }.orEmpty().sortedBy { it.name }
        File(dist, "sizes.json").writeText(wasm.joinToString(",", "{", "}") { "\"${it.name}\":${it.length()}" })
    }
}

compose.desktop {
    application {
        mainClass = "io.github.zainkhalid91.motionicons.gallery.MainKt"
    }
}

tasks.named<Test>("desktopTest") {
    maxHeapSize = "2g"
}
