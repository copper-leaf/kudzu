plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.4.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.20")
    implementation("org.jlleitschuh.gradle:ktlint-gradle:11.3.1")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
}
