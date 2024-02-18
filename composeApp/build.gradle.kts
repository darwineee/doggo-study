
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.sqlDelight)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting

        commonMain.configure {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.runtimeSaveable)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.animation)
                implementation(compose.animationGraphics)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.preview)
                @OptIn(ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                implementation(libs.letsplot.kernel)
                implementation(libs.letsplot.common)
                implementation(libs.letsplot.awt)
                implementation(libs.letsplot.compose)

                implementation(libs.kotlin.coroutine.core)
                implementation(libs.kotlin.serialization.core)

                implementation(libs.voyager.navigator)
                implementation(libs.voyager.navigator.bottomsheet)
                implementation(libs.voyager.navigator.tab)
                implementation(libs.voyager.screenModel)
                implementation(libs.voyager.transition)

                implementation(libs.sqldelight.adapter)
            }
        }
        
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlin.coroutine.swing)
            implementation(libs.sqldelight.jvmdriver)
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("data")
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            modules("java.instrument", "java.sql", "jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "DoggoStudy"
            packageVersion = "1.0.0"

            windows {
                shortcut = true
            }
        }

        buildTypes.release {
            proguard {
                configurationFiles.from("./proguard-rules.pro")
            }
        }
    }
}
