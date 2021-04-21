package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.choice.ChoiceNode
import com.copperleaf.kudzu.node.expression.PrefixOperatorNode
import com.copperleaf.kudzu.node.many.ManyNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

/**
 * The parser for a level of combined [Operator.Prefix] operators of the same precedence.
 */
@ExperimentalStdlibApi
@Suppress("UNCHECKED_CAST")
class PrefixOperatorParser(
    private val operator: ExactChoiceParser,
    private val operand: Parser<Node>
) : Parser<PrefixOperatorNode> {

    private val parser: Parser<PrefixOperatorNode> by lazy {
        val impl = SequenceParser(
            ManyParser(operator),
            operand
        )

        FlatMappedParser(impl) { sequenceNode ->
            val (manyOperatorsNode, operandNode) = sequenceNode.children
            val operatorNodes = (manyOperatorsNode as ManyNode<ChoiceNode>)
                .nodeList
                .map { it.node }

            PrefixOperatorNode(
                operatorNodes,
                operandNode,
                sequenceNode.context
            )
        }
    }

    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<PrefixOperatorNode>> = parser.parse
}
