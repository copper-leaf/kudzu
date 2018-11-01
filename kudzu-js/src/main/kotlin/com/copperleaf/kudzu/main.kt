package com.copperleaf.kudzu

import kotlin.browser.document

external val module: Module

external interface Module {
    val hot: Hot?
}

external interface Hot {
    val data: dynamic

    fun accept()
    fun accept(dependency: String, callback: () -> Unit)
    fun accept(dependencies: Array<String>, callback: (updated: Array<String>) -> Unit)

    fun dispose(callback: (data: dynamic) -> Unit)
}

external fun require(name: String): dynamic

fun main(args: Array<String>) {
    println("loaded from Kotlin")

    val state: dynamic = module.hot?.let { hot ->
        hot.accept()

        hot.dispose { data ->
            cleanup()
        }

        hot.data
    }

    if (document.body != null) {
        init()
    } else {
        cleanup()
        document.addEventListener("DOMContentLoaded", { init() })
    }
}

fun cleanup() {
    println("cleanup Kotlin app")
}

fun init() {
    println("init Kotlin app, yo")
}
