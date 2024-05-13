plugins {
    id("copper-leaf-base")
    id("copper-leaf-android-library")
    id("copper-leaf-targets")
    id("copper-leaf-kotest")
    id("copper-leaf-lint")
    id("copper-leaf-publish")
}

android {
    namespace = "com.copperleaf.kudzu"
}

kotlin {
    sourceSets {
        // Common Sourcesets
        val commonMain by getting {
            dependencies { }
        }
        val commonTest by getting {
            dependencies { }
        }

        // plain JVM Sourcesets
        val jvmMain by getting {
            dependencies { }
        }
        val jvmTest by getting {
            dependencies { }
        }

        // Android JVM Sourcesets
        val androidMain by getting {
            dependencies { }
        }
        val androidUnitTest by getting {
            dependencies { }
        }

        // JS Sourcesets
        val jsMain by getting {
            dependencies { }
        }
        val jsTest by getting {
            dependencies { }
        }

        // iOS Sourcesets
        val iosMain by getting {
            dependencies { }
        }
        val iosTest by getting {
            dependencies { }
        }

        // WASM JS Sourcesets
        val wasmJsMain by getting {
            dependencies { }
        }
        val wasmJsTest by getting {
            dependencies { }
        }
    }
}
