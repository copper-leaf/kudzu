package com.copperleaf.kudzu.parser.chars

import com.copperleaf.kudzu.checkNotEmpty
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.chars.CharNode
import com.copperleaf.kudzu.parser.ParseFunction
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.runParser

public abstract class BaseCharParser(
    private val isValidChar: (Char) -> Boolean,
    private val validationFailedMessage: (Char) -> String,
) : Parser<CharNode> {
    final override fun predict(input: ParserContext): Boolean {
        return input.validateNextChar(isValidChar)
    }

    override val parse: ParseFunction<CharNode> = runParser { input ->
        checkNotEmpty(input)

        val (nextChar, remaining) = input.nextChar()

        if (!isValidChar(nextChar)) throw ParserException(
            "char '$nextChar' ${validationFailedMessage(nextChar)}",
            this@BaseCharParser,
            input
        )

        CharNode(nextChar, NodeContext(input, remaining)) to remaining
    }
}
