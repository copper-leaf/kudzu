package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.choice.ChoiceNode
import com.copperleaf.kudzu.node.expression.BinaryOperationNode
import com.copperleaf.kudzu.node.expression.InfixrOperatorNode
import com.copperleaf.kudzu.node.maybe.MaybeNode
import com.copperleaf.kudzu.node.sequence.SequenceNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.lazy.LazyParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.maybe.MaybeParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

/**
 * The parser for a level of combined [Operator.Infixr] operators of the same precedence.
 */
@ExperimentalStdlibApi
@Suppress("UNCHECKED_CAST")
class InfixrOperatorParser(
    private val operator: ExactChoiceParser,
    private val operand: Parser<Node>
) : Parser<InfixrOperatorNode> {

    private val parser: Parser<InfixrOperatorNode> by lazy {
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

        impl uses FlatMappedParser(lazy) { sequenceNode ->
            val (startOperandNode, maybeBinaryOperationNodes) = sequenceNode.children
            val binaryOperationNode = (maybeBinaryOperationNodes as MaybeNode<SequenceNode>)
                .node
                ?.let {
                    val (operatorNode, operandNode) = it.children
                    val choiceOperatorNode: ChoiceNode = operatorNode as ChoiceNode
                    BinaryOperationNode(
                        choiceOperatorNode.node,
                        operandNode,
                        it.context
                    )
                }

            InfixrOperatorNode(
                startOperandNode,
                binaryOperationNode,
                sequenceNode.context
            )
        }

        impl
    }

    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<InfixrOperatorNode>> = parser.parse
}
