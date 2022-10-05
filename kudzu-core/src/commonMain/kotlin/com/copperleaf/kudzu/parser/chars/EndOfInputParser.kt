package com.copperleaf.kudzu.parser.chars

import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.chars.EndOfInputNode
import com.copperleaf.kudzu.parser.ParseFunction
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.runParser

/**
 * Does not consume any input, but fails is the input is not empty.
 *
 * Predicts true when:
 *   - input is empty
 *
 * Parsing fails when:
 *   - There is more input to consume
 */
public class EndOfInputParser : Parser<EndOfInputNode> {
    override fun predict(input: ParserContext): Boolean {
        return input.isEmpty()
    }

    override val parse: ParseFunction<EndOfInputNode> = runParser { input ->
        if (input.isEmpty()) {
            EndOfInputNode(NodeContext(input, input)) to input
        } else {
            throw ParserException(
                "Expected end of input, but still had input remaining",
                this@EndOfInputParser,
                input
            )
        }
    }
}
