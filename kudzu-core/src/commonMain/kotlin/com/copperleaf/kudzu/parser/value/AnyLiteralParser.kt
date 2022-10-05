package com.copperleaf.kudzu.parser.value

import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.parser.wrapped.WrappedParser
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser

@Suppress("UNCHECKED_CAST")
public class AnyLiteralParser : WrappedParser<ValueNode<Any>>(
    FlatMappedParser(
        ExactChoiceParser(
            BooleanLiteralParser(),
            DoubleLiteralParser(),
            IntLiteralParser(),
            StringLiteralParser(),
            CharLiteralParser(),
        )
    ) { it.node as ValueNode<Any> }
)
