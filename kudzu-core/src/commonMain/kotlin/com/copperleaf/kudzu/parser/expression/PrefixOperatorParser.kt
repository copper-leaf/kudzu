package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.choice.ChoiceNNode
import com.copperleaf.kudzu.node.expression.PrefixOperatorNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

/**
 * The parser for a level of combined [Operator.Prefix] operators of the same precedence.
 */
@ExperimentalStdlibApi

class PrefixOperatorParser(
    private val operator: Parser<ChoiceNNode>,
    private val operand: Parser<Node>
) : Parser<PrefixOperatorNode> {

    private val parser: Parser<PrefixOperatorNode> by lazy {
        val impl = SequenceParser(
            ManyParser(operator),
            operand
        )

        FlatMappedParser(impl) { (nodeContext, manyOperatorsNode, operandNode) ->
            val operatorNodes = manyOperatorsNode
                .nodeList
                .map { it.node }

            PrefixOperatorNode(
                operatorNodes,
                operandNode,
                nodeContext
            )
        }
    }

    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<PrefixOperatorNode>> = parser.parse
}
