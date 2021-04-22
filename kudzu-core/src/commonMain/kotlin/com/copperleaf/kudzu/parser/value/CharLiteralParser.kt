package com.copperleaf.kudzu.parser.value

import com.copperleaf.kudzu.node.chars.CharNode
import com.copperleaf.kudzu.node.choice.ChoiceNode
import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.chars.AnyCharParser
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.chars.EscapedCharParser
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.mapped.MappedParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

@ExperimentalStdlibApi
class CharLiteralParser : Parser<ValueNode<Char>> {
    private val parser by lazy {
        MappedParser(
            SequenceParser(
                CharInParser('\''),
                ExactChoiceParser(
                    EscapedCharParser(),
                    AnyCharParser(),
                ),
                CharInParser('\''),
            ),
        ) {
            val (_, c, _) = it.children
            val choiceNode = c as ChoiceNode

            (choiceNode.node as CharNode).char
        }
    }

    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<ValueNode<Char>>> = parser.parse
}
