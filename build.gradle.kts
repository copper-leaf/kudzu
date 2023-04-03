plugins {
    `copper-leaf-base`
    `copper-leaf-version`
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.13.0"
    `copper-leaf-sonatype`
}

apiValidation {
    ignoredProjects.addAll(
        listOf(
            "docs",
        )
    )
}
