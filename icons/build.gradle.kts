import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
    `maven-publish`
}

kotlin {
    explicitApi()
    // Kotlin 2.1 metadata and stdlib keep the library usable from apps on Kotlin 2.0 and newer.
    coreLibrariesVersion = "2.1.20"
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_1)
        apiVersion.set(KotlinVersion.KOTLIN_2_1)
    }

    android {
        namespace = "io.github.zainkhalid91.motionicons"
        compileSdk = 36
        minSdk = 23
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }

    jvm("desktop") {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    sourceSets {
        commonMain.dependencies {
            api(libs.lib.compose.runtime)
            api(libs.lib.compose.foundation)
            api(libs.lib.compose.ui)
            implementation(libs.lib.compose.animation)
        }
        // Wasm klibs must match the compiler's stdlib, so the 2.1 pin above applies to JVM and Android only.
        wasmJsMain.dependencies {
            implementation(kotlin("stdlib", libs.versions.kotlin.get()))
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        getByName("desktopTest").dependencies {
            implementation(libs.lib.compose.ui.test)
            implementation(compose.desktop.currentOs)
        }
    }
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            name.set("Compose Motion Icons")
            description.set("Icons that animate between states, written in pure Compose.")
            url.set("https://github.com/zainkhalid91/compose-motion-icons")
            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
        }
    }
}
