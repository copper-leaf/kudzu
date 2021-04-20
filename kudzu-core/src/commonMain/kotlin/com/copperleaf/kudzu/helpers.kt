package com.copperleaf.kudzu

import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException

@ExperimentalStdlibApi
fun Parser<*>.checkNotEmpty(input: ParserContext) {
    if (input.isEmpty()) throw ParserException("unexpected end of input", this, input)
}

fun Char.isLetter(): Boolean = KudzuPlatform.isLetter(this)
fun Char.isDigit(): Boolean = KudzuPlatform.isDigit(this)
fun Char.isLetterOrDigit(): Boolean = KudzuPlatform.isLetterOrDigit(this)
