package com.copperleaf.kudzu.parser.value

import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.chars.CharNotInParser
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.chars.EscapedCharParser
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.mapped.MappedParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

@ExperimentalStdlibApi
class StringLiteralParser : Parser<ValueNode<String>> {
    private val parser by lazy {
        MappedParser(
            SequenceParser(
                CharInParser('"'),
                ManyParser(
                    ExactChoiceParser(
                        EscapedCharParser(),
                        CharNotInParser('"'),
                    )
                ),
                CharInParser('"'),
            ),
        ) {
            val (_, manyNode, _) = it.children
            manyNode.text
        }
    }

    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<ValueNode<String>>> = parser.parse
}
