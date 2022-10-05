package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.choice.ChoiceNNode
import com.copperleaf.kudzu.node.expression.BinaryOperationNode
import com.copperleaf.kudzu.node.expression.InfixrOperatorNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.wrapped.WrappedParser
import com.copperleaf.kudzu.parser.lazy.LazyParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.maybe.MaybeParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

/**
 * The parser for a level of combined [Operator.Infixr] operators of the same precedence.
 */

public class InfixrOperatorParser(
    private val operator: Parser<ChoiceNNode>,
    private val operand: Parser<Node>
) : WrappedParser<InfixrOperatorNode>({
    val impl = LazyParser<InfixrOperatorNode>()
    val lazy = SequenceParser(
        operand,
        MaybeParser(
            SequenceParser(
                operator,
                impl,
            )
        ),
    )

    impl uses FlatMappedParser(lazy) { (nodeContext, startOperandNode, maybeBinaryOperationNodes) ->
        val binaryOperationNode = maybeBinaryOperationNodes
            .node
            ?.let {
                val (operatorNode, operandNode) = it.children
                val choiceOperatorNode: ChoiceNNode = operatorNode as ChoiceNNode
                BinaryOperationNode(
                    choiceOperatorNode.node,
                    operandNode,
                    it.context
                )
            }

        InfixrOperatorNode(
            startOperandNode,
            binaryOperationNode,
            nodeContext
        )
    }

    impl
})
