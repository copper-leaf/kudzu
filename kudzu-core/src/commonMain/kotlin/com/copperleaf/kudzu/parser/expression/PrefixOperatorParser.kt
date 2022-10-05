package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.choice.ChoiceNNode
import com.copperleaf.kudzu.node.expression.PrefixOperatorNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.wrapped.WrappedParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

/**
 * The parser for a level of combined [Operator.Prefix] operators of the same precedence.
 */
public class PrefixOperatorParser(
    private val operator: Parser<ChoiceNNode>,
    private val operand: Parser<Node>
) : WrappedParser<PrefixOperatorNode>({
    FlatMappedParser(
        SequenceParser(
            ManyParser(operator),
            operand
        )
    ) { (nodeContext, manyOperatorsNode, operandNode) ->
        val operatorNodes = manyOperatorsNode
            .nodeList
            .map { it.node }

        PrefixOperatorNode(
            operatorNodes,
            operandNode,
            nodeContext
        )
    }
})
