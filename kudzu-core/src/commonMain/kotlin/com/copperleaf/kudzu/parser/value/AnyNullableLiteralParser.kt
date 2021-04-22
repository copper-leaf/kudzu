package com.copperleaf.kudzu.parser.value

import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser

@ExperimentalStdlibApi
@Suppress("UNCHECKED_CAST")
class AnyNullableLiteralParser : Parser<ValueNode<Any?>> {
    private val parser by lazy {
        FlatMappedParser(
            ExactChoiceParser(
                NullLiteralParser(),
                BooleanLiteralParser(),
                DoubleLiteralParser(),
                IntLiteralParser(),
            )
        ) { it.node as ValueNode<Any?> }
    }

    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<ValueNode<Any?>>> = parser.parse
}
