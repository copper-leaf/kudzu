package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.expression.InfixOperatorNode
import com.copperleaf.kudzu.node.expression.InfixrOperatorNode
import com.copperleaf.kudzu.node.expression.PostfixOperatorNode
import com.copperleaf.kudzu.node.expression.PrefixOperatorNode
import com.copperleaf.kudzu.node.mapped.ValueNode

@ExperimentalStdlibApi
@Suppress("UNCHECKED_CAST")
internal class ExpressionEvaluatorImpl<T : Any>(
    private val operators: List<Operator<T>>,
) : ExpressionEvaluator<T> {

    override val evaluate = DeepRecursiveFunction<Node, T> { node ->
        when (node) {
            is InfixOperatorNode -> evaluateInfix.callRecursive(node)
            is InfixrOperatorNode -> evaluateInfixr.callRecursive(node)
            is PrefixOperatorNode -> evaluatePrefix.callRecursive(node)
            is PostfixOperatorNode -> evaluatePostfix.callRecursive(node)
            is ValueNode<*> -> node.value as T
            else -> error("Unknown expression node")
        }
    }

    private val evaluateInfix: DeepRecursiveFunction<InfixOperatorNode, T> = DeepRecursiveFunction {
        with(it) {
            var result = evaluate.callRecursive(leftOperand)

            for (node in operationNodes) {
                val rightOperatorResult = evaluate.callRecursive(node.operand)
                result = node.operator.text.applyBinary(result, rightOperatorResult)
            }

            result
        }
    }

    private val evaluateInfixr: DeepRecursiveFunction<InfixrOperatorNode, T> = DeepRecursiveFunction {
        with(it) {
            var result = evaluate.callRecursive(leftOperand)

            if (operation != null) {
                val rightOperationResult = evaluate.callRecursive(operation.operand)
                result = operation.operator.text.applyBinary(result, rightOperationResult)
            }

            result
        }
    }

    private val evaluatePrefix: DeepRecursiveFunction<PrefixOperatorNode, T> = DeepRecursiveFunction {

        with(it) {
            var result = evaluate.callRecursive(operand)

            for (node in operatorNodes) {
                result = node.text.applyUnary(result)
            }

            result
        }
    }

    private val evaluatePostfix: DeepRecursiveFunction<PostfixOperatorNode, T> = DeepRecursiveFunction {
        with(it) {
            var result = evaluate.callRecursive(operand)

            for (node in operatorNodes) {
                result = node.text.applyUnary(result)
            }

            result
        }
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
