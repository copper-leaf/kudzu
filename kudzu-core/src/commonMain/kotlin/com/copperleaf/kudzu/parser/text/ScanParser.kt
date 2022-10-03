package com.copperleaf.kudzu.parser.text

import com.copperleaf.kudzu.checkNotEmpty
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.text.TextNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.ParserResult

/**
 * Consume characters from the input until its parser predicts true. It requires that at least 1 character be able to be
 * consumed before [stoppingCondition] predicts true.
 *
 * The resulting [TextNode] contains all the characters that were consumed as a single text String. This Parser is
 * useful in doing find/replace operations on text, where the text to be replaced is a complex Parser that would be
 * overly cumbersome to implement using Regex.
 *
 * Predicts true when:
 *   - input is not empty
 *   - The input to its parser does not predict true
 *
 * Parsing stops when:
 *   - its parser predicts true
 *   - there is no more input remaining
 *
 * Parsing fails when:
 *   - its parser fails to parse
 */
class ScanParser(
    private val stoppingCondition: Parser<*>,
) : Parser<TextNode> {

    override fun predict(input: ParserContext): Boolean {
        return !input.isEmpty() && !stoppingCondition.predict(input)
    }

    override val parse = DeepRecursiveFunction<ParserContext, ParserResult<TextNode>> { input ->
        checkNotEmpty(input)

        var remaining = input
        val textBuilder = StringBuilder()

        while (!remaining.isEmpty() && !stoppingCondition.predict(remaining)) {
            val nextCharResult = remaining.nextChar()
            textBuilder.append(nextCharResult.first)
            remaining = nextCharResult.second
        }

        if (textBuilder.isEmpty()) {
            throw ParserException(
                "No text was scanned",
                this@ScanParser,
                input
            )
        } else {
            val nodeContext = NodeContext(input, remaining)
            TextNode(textBuilder.toString(), nodeContext) to remaining
        }
    }
}
