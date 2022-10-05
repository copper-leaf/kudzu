package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NonTerminalNode
import com.copperleaf.kudzu.node.choice.ChoiceNNode
import com.copperleaf.kudzu.node.expression.BinaryOperationNode
import com.copperleaf.kudzu.node.expression.InfixOperatorNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.wrapped.WrappedParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.maybe.MaybeParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

/**
 * The parser for a level of combined [Operator.Infix] operators of the same precedence.
 */
public class InfixOperatorParser(
    public val operator: Parser<ChoiceNNode>,
    public val operand: Parser<Node>
) : WrappedParser<InfixOperatorNode>(
    FlatMappedParser(
        SequenceParser(
            operand,
            MaybeParser(
                ManyParser(
                    SequenceParser(
                        operator,
                        operand,
                    )
                )
            )
        )
    ) { (nodeContext, startOperandNode, maybeManyBinaryOperationNodes) ->
        val binaryOperationNodes = maybeManyBinaryOperationNodes
            .node
            ?.children
            ?.map {
                val (operatorNode, operandNode) = (it as NonTerminalNode).children
                val choiceOperatorNode: ChoiceNNode = operatorNode as ChoiceNNode
                BinaryOperationNode(
                    choiceOperatorNode.node,
                    operandNode,
                    nodeContext
                )
            } ?: emptyList()

        InfixOperatorNode(
            startOperandNode,
            binaryOperationNodes,
            nodeContext
        )
    }
)
