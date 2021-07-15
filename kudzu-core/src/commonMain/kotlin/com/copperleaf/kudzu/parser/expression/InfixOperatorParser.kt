package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NonTerminalNode
import com.copperleaf.kudzu.node.choice.ChoiceNode
import com.copperleaf.kudzu.node.expression.BinaryOperationNode
import com.copperleaf.kudzu.node.expression.InfixOperatorNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.maybe.MaybeParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

/**
 * The parser for a level of combined [Operator.Infix] operators of the same precedence.
 */
@ExperimentalStdlibApi

class InfixOperatorParser(
    val operator: ExactChoiceParser,
    val operand: Parser<Node>
) : Parser<InfixOperatorNode> {

    private val parser: Parser<InfixOperatorNode> by lazy {
        val impl = SequenceParser(
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

        FlatMappedParser(impl) { (nodeContext, startOperandNode, maybeManyBinaryOperationNodes) ->
            val binaryOperationNodes = maybeManyBinaryOperationNodes
                .node
                ?.children
                ?.map {
                    val (operatorNode, operandNode) = (it as NonTerminalNode).children
                    val choiceOperatorNode: ChoiceNode = operatorNode as ChoiceNode
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
    }

    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<InfixOperatorNode>> = parser.parse
}
