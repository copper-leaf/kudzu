package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.choice.ChoiceNNode
import com.copperleaf.kudzu.node.expression.PostfixOperatorNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.wrapped.WrappedParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

/**
 * The parser for a level of combined [Operator.Postfix] operators of the same precedence.
 */
public class PostfixOperatorParser(
    private val operator: Parser<ChoiceNNode>,
    private val operand: Parser<Node>
) : WrappedParser<PostfixOperatorNode>({
    FlatMappedParser(
        SequenceParser(
            operand,
            ManyParser(operator),
        )
    ) { (nodeContext, operandNode, manyOperatorsNode) ->
        val operatorNodes = manyOperatorsNode
            .nodeList
            .map { it.node }

        PostfixOperatorNode(
            operandNode,
            operatorNodes,
            nodeContext
        )
    }
})
