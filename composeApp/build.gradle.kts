import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.0.0"
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.animation)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
            implementation("sh.calvin.reorderable:reorderable:2.4.0")
            // lyrics
            implementation("io.ktor:ktor-client-core:2.3.7")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")

            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            // Lecture tags audio (MP3, FLAC, etc.)
            implementation("net.jthink:jaudiotagger:3.0.1")
            // Lecture audio VLCJ
            implementation("uk.co.caprica:vlcj:4.8.2")
            //drag & drop
            implementation("sh.calvin.reorderable:reorderable:2.4.0")
            //lyrics
            implementation("io.ktor:ktor-client-okhttp:2.3.7")
        }
    }
}

android {
    namespace = "com.kenemi.kenemimusic"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.kenemi.kenemimusic"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.kenemi.kenemimusic.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KenemiMusic"
            packageVersion = "1.0.0"
            description = "Lecteur de musique KenemiMusic"
            copyright = "© 2025 kevinwg02"
            vendor = "kevinwg02"

            windows {
                iconFile.set(project.file("src/jvmMain/resources/KM-icon.ico"))
                menuGroup = "KenemiMusic"
                perUserInstall = true
                upgradeUuid = "GENERE-UN-UUID-SUR-uuidgenerator.net"
            }
            linux {
                iconFile.set(project.file("src/jvmMain/resources/KM-icon.png"))
                packageName = "kenemimusic"
            }
        }
    }
}