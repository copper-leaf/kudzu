plugins {
    `copper-leaf-android`
    `copper-leaf-targets`
    `copper-leaf-base`
    `copper-leaf-version`
    `copper-leaf-lint`
    `copper-leaf-publish`
}

description = "A monadic (I think...) recursive-descent parser written in Kotlin"

repositories {
    maven(url = "https://jitpack.io")
}

android {
    namespace = "com.copperleaf.kudzu"
}

kotlin {
    sourceSets {
        // Common Sourcesets
        val commonMain by getting {
            dependencies {
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        // plain JVM Sourcesets
        val jvmMain by getting {
            dependencies {
            }
        }
        val jvmTest by getting {
            dependsOn(commonTest)
            dependencies {
                implementation(kotlin("test-junit"))

                implementation("me.alllex.parsus:parsus-jvm:0.4.0")
                implementation("com.github.h0tk3y.betterParse:better-parse:0.4.4")
            }
        }

        // Android JVM Sourcesets
        val androidMain by getting {
            dependsOn(jvmMain)
            dependencies {
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        // JS Sourcesets
        val jsMain by getting {
            dependencies {
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        // iOS Sourcesets
        val iosMain by getting {
            dependencies { }
        }
        val iosTest by getting {
            dependencies { }
        }
    }
}
