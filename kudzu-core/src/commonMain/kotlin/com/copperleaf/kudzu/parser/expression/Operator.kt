package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.text.LiteralTokenParser

/**
 * Configure [ExpressionParser] with a list of operators of configurable precedence and associativity. These operators
 * become both the Parser for the operator during parsing, and also the operator lookup table during evaluation with
 * [ExpressionEvaluator]. All operators must have unique names to ensure the parser result maps back to the proper
 * applyFn.
 */
@ExperimentalStdlibApi
sealed class Operator<T : Any>(
    val name: String,
    val parser: Parser<*>,
    val precedence: Precedence
) : Comparable<Operator<T>> {

    @ExperimentalStdlibApi
    abstract class UnaryOperator<T : Any>(
        name: String,
        parser: Parser<*>,
        precedence: Precedence
    ) : Operator<T>(name, parser, precedence) {
        abstract val applyFn: (T) -> T
    }

    @ExperimentalStdlibApi
    abstract class BinaryOperator<T : Any>(
        name: String,
        parser: Parser<*>,
        precedence: Precedence
    ) : Operator<T>(name, parser, precedence) {
        abstract val applyFn: (T, T) -> T
    }

    @ExperimentalStdlibApi
    class Prefix<T : Any>(
        op: String,
        precedence: Int,
        override val applyFn: (T) -> T
    ) : UnaryOperator<T>(op, LiteralTokenParser(op), Precedence(precedence, 1))

    @ExperimentalStdlibApi
    class Postfix<T : Any>(
        op: String,
        precedence: Int,
        override val applyFn: (T) -> T
    ) : UnaryOperator<T>(op, LiteralTokenParser(op), Precedence(precedence, 2))

    @ExperimentalStdlibApi
    class Infixr<T : Any>(
        op: String,
        precedence: Int,
        override val applyFn: (T, T) -> T
    ) : BinaryOperator<T>(op, LiteralTokenParser(op), Precedence(precedence, 3))

    @ExperimentalStdlibApi
    class Infix<T : Any>(
        op: String,
        precedence: Int,
        override val applyFn: (T, T) -> T
    ) : BinaryOperator<T>(op, LiteralTokenParser(op), Precedence(precedence, 4))

    override fun compareTo(other: Operator<T>): Int {
        return compareValuesBy(this, other) { it.precedence }
    }

    data class Precedence(
        val precedence: Int,
        val associativity: Int
    ) : Comparable<Precedence> {
        override fun compareTo(other: Precedence): Int {
            return compareValuesBy(this, other, { it.precedence }, { it.associativity })
        }
    }
}
