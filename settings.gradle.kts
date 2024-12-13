rootProject.name = "kudzu"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

val conventionDir = "./gradle-convention-plugins"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("$conventionDir/gradle/conventionLibs.versions.toml"))
        }
    }
}

includeBuild(conventionDir)

include(":kudzu-core")
include(":docs")
