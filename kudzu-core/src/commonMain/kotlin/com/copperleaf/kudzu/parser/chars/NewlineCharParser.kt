package com.copperleaf.kudzu.parser.chars

import com.copperleaf.kudzu.checkNotEmpty
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.chars.NewlineNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.ParserResult

/**
 * Consumes a single newline character (\n) or the combination of newline and carriage return (\r\n).
 *
 * Predicts true when:
 *   - the next character is \r or \n
 *
 * Parsing fails when:
 *   - there is no more input
 *   - the next input character is neither \r nor \n
 *   - the \r is not followed by \n
 */
class NewlineCharParser : Parser<NewlineNode> {
    override fun predict(input: ParserContext): Boolean {
        return input.validateNextChar { it == '\r' || it == '\n' }
    }

    override val parse = DeepRecursiveFunction<ParserContext, ParserResult<NewlineNode>> { input ->
        checkNotEmpty(input)

        val (char1, remaining1) = input.nextChar()

        if (char1 == '\n') {
            // just a single \n
            NewlineNode(NodeContext(input, remaining1)) to remaining1
        } else if (char1 == '\r') {
            if (remaining1.isEmpty()) {
                throw ParserException(
                    """\r must be followed by \n""",
                    this@NewlineCharParser,
                    input
                )
            }

            // we have \r, expect the next is \n
            val (char2, remaining2) = remaining1.nextChar()

            if (char2 == '\n') {
                NewlineNode(NodeContext(input, remaining2)) to remaining2
            } else {
                throw ParserException(
                    """\r must be followed by \n""",
                    this@NewlineCharParser,
                    input
                )
            }
        } else {
            throw ParserException(
                """Expected '\n' or '\r\n', got '$char1'""",
                this@NewlineCharParser,
                input
            )
        }
    }
}
