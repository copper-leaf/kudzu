package com.copperleaf.kudzu.parser.value

import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.mapped.MappedParser
import com.copperleaf.kudzu.parser.text.LiteralTokenParser

@ExperimentalStdlibApi
class BooleanLiteralParser : Parser<ValueNode<Boolean>> {
    private val parser by lazy {
        MappedParser(
            ExactChoiceParser(
                LiteralTokenParser("true"),
                LiteralTokenParser("false"),
            )
        ) { it.text.toBoolean() }
    }

    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<ValueNode<Boolean>>> = parser.parse
}
