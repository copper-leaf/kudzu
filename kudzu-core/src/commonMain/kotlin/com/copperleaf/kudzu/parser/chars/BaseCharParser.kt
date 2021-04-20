package com.copperleaf.kudzu.parser.chars

import com.copperleaf.kudzu.checkNotEmpty
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.chars.CharNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.ParserResult

open class BaseCharParser(
    private val isValidChar: (Char) -> Boolean,
    private val validationFailedMessage: (Char) -> String,
) : Parser<CharNode>() {
    final override fun predict(input: ParserContext): Boolean {
        return input.isNotEmpty() && (runCatching { isValidChar(input.next()) }.getOrNull() ?: false)
    }

    @OptIn(ExperimentalStdlibApi::class)
    final override val parse = DeepRecursiveFunction<ParserContext, ParserResult<CharNode>> { input ->
        checkNotEmpty(input)

        val nextChar = input.next()
        val remaining = input.remaining()

        if (!isValidChar(nextChar)) throw ParserException(
            "char '$nextChar' ${validationFailedMessage(nextChar)}",
            this@BaseCharParser,
            input
        )

        CharNode(nextChar, NodeContext(input, remaining)) to remaining
    }
}
