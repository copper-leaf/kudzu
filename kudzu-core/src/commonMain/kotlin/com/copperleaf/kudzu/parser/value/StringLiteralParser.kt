package com.copperleaf.kudzu.parser.value

import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.parser.ParseFunction
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.chars.CharNotInParser
import com.copperleaf.kudzu.parser.chars.EscapedCharParser
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.mapped.MappedParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

public class StringLiteralParser(private val delimiter: Char = '"') : Parser<ValueNode<String>> {
    private val parser by lazy {
        MappedParser(
            SequenceParser(
                CharInParser(delimiter),
                ManyParser(
                    ExactChoiceParser(
                        EscapedCharParser(),
                        CharNotInParser(delimiter),
                    )
                ),
                CharInParser(delimiter),
            ),
            remapErrors = { _, _ ->
                ParserException(
                    "Expected string literal delimited by $delimiter",
                    this@StringLiteralParser,
                    this
                )
            }
        ) { (_, _, manyNode, _) ->
            manyNode.text
        }
    }

    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override val parse: ParseFunction<ValueNode<String>> = parser.parse
}
