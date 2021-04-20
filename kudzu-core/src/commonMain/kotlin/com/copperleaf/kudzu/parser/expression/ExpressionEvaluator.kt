package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.expression.InfixOperatorNode
import com.copperleaf.kudzu.node.expression.InfixrOperatorNode
import com.copperleaf.kudzu.node.expression.PostfixOperatorNode
import com.copperleaf.kudzu.node.expression.PrefixOperatorNode
import com.copperleaf.kudzu.node.mapped.ValueNode

@ExperimentalStdlibApi
class ExpressionEvaluator<T: Any>(
    private vararg val operators: Operator<T>,
) {
    fun evaluateExpression(node: Node): T {
        return when (node) {
            is InfixOperatorNode -> node.evaluate()
            is InfixrOperatorNode -> node.evaluate()
            is PrefixOperatorNode -> node.evaluate()
            is PostfixOperatorNode -> node.evaluate()
            is ValueNode<*> -> node.value as T
            else -> error("Unknown expression node")
        }
    }

    private fun InfixOperatorNode.evaluate(): T {
        var result = evaluateExpression(leftOperand)

        for (node in operationNodes) {
            val rightOperatorResult = evaluateExpression(node.operand)
            result = node.operator.text.applyBinary(result, rightOperatorResult)
        }

        return result
    }

    private fun InfixrOperatorNode.evaluate(): T {
        var result = evaluateExpression(leftOperand)

        if (operation != null) {
            val rightOperationResult = evaluateExpression(operation.operand)
            result = operation.operator.text.applyBinary(result, rightOperationResult)
        }

        return result
    }

    private fun PrefixOperatorNode.evaluate(): T {
        var result = evaluateExpression(operand)

        for (node in operatorNodes) {
            result = node.text.applyUnary(result)
        }

        return result
    }

    private fun PostfixOperatorNode.evaluate(): T {
        TODO()
    }

    private fun String.applyUnary(node: T): T {
        return operators
            .asSequence()
            .filterIsInstance<Operator.UnaryOperator<T>>()
            .first { it.name == this@applyUnary }
            .applyFn(node)
    }

    private fun String.applyBinary(left: T, right: T): T {
        return operators
            .asSequence()
            .filterIsInstance<Operator.BinaryOperator<T>>()
            .first { it.name == this@applyBinary }
            .applyFn(left, right)
    }
}
