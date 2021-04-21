package com.copperleaf.kudzu.parser

interface ParserContext {

    val sourcePosition: SourcePosition

    fun isEmpty(): Boolean

    fun next(): Char

    fun remaining(): ParserContext

    companion object {
        fun fromString(input: String, skipWhitespace: Boolean = false): ParserContext {
            return ParserContextImpl(input, skipWhitespace = skipWhitespace)
        }
    }
}
