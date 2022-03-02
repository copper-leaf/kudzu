plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.0.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    implementation("org.jlleitschuh.gradle:ktlint-gradle:10.2.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
}
