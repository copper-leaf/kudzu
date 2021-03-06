package com.copperleaf.kudzu.parser.text

import com.copperleaf.kudzu.checkNotEmpty
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.text.TextNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.ParserResult

@ExperimentalStdlibApi
abstract class BaseTextParser(
    val isValidChar: (Int, Char) -> Boolean,
    val isValidText: (String) -> Boolean,
    val allowEmptyInput: Boolean
) : Parser<TextNode> {

    final override fun predict(input: ParserContext): Boolean {
        return if (input.isEmpty()) {
            allowEmptyInput
        } else {
            isValidChar(0, input.next())
        }
    }

    override val parse = DeepRecursiveFunction<ParserContext, ParserResult<TextNode>> { input ->
        if (!allowEmptyInput) {
            checkNotEmpty(input)
        }

        var remaining = input
        var nextChar: Char
        val tokenBuilder = StringBuilder()
        var index = 0

        while (!remaining.isEmpty()) {
            nextChar = remaining.next()
            if (!isValidChar(index, nextChar)) break

            tokenBuilder.append(nextChar)
            remaining = remaining.remaining()
            index++
        }

        val token = tokenBuilder.toString()

        if (!isValidText(token)) throw ParserException(
            "Unexpected text '$token' in ${this::class.simpleName}",
            this@BaseTextParser,
            input
        )

        TextNode(token, NodeContext(input, remaining)) to remaining
    }
}
