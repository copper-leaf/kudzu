package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.choice.ChoiceNode
import com.copperleaf.kudzu.node.expression.BinaryOperationNode
import com.copperleaf.kudzu.node.expression.InfixOperatorNode
import com.copperleaf.kudzu.node.many.ManyNode
import com.copperleaf.kudzu.node.maybe.MaybeNode
import com.copperleaf.kudzu.node.sequence.SequenceNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.maybe.MaybeParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

@ExperimentalStdlibApi
@Suppress("UNCHECKED_CAST")
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

        FlatMappedParser(impl) { sequenceNode ->
            val (startOperandNode, maybeManyBinaryOperationNodes) = sequenceNode.children
            val binaryOperationNodes = (maybeManyBinaryOperationNodes as MaybeNode<ManyNode<SequenceNode>>)
                .node
                ?.children
                ?.map {
                    val (operatorNode, operandNode) = (it as SequenceNode).children
                    val choiceOperatorNode: ChoiceNode = operatorNode as ChoiceNode
                    BinaryOperationNode(
                        choiceOperatorNode.node,
                        operandNode,
                        sequenceNode.context
                    )
                } ?: emptyList()

            InfixOperatorNode(
                startOperandNode,
                binaryOperationNodes,
                sequenceNode.context
            )
        }
    }

    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<InfixOperatorNode>> = parser.parse
}
