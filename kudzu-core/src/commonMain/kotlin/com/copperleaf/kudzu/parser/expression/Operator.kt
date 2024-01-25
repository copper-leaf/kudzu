package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.text.LiteralTokenParser

/**
 * Configure [ExpressionParser] with a list of operators of configurable precedence and associativity. These operators
 * become both the Parser for the operator during parsing, and also the operator lookup table during evaluation with
 * [ExpressionEvaluator]. All operators must have unique names to ensure the parser result maps back to the proper
 * applyFn.
 */
public sealed class Operator<T : Any>(
    public val name: String,
    public val parser: Parser<*>,
    public val precedence: Precedence,
    public val aliases: List<String>
) : Comparable<Operator<T>> {
    public abstract fun expandAliases(): List<Operator<T>>

    public abstract class UnaryOperator<T : Any>(
        name: String,
        parser: Parser<*>,
        precedence: Precedence,
        aliases: List<String>
    ) : Operator<T>(name, parser, precedence, aliases) {
        public abstract val applyFn: (T) -> T
    }

    public abstract class BinaryOperator<T : Any>(
        name: String,
        parser: Parser<*>,
        precedence: Precedence,
        aliases: List<String>
    ) : Operator<T>(name, parser, precedence, aliases) {
        public abstract val applyFn: (T, T) -> T
    }

    public class Prefix<T : Any>(
        op: String,
        precedence: Int,
        aliases: List<String> = emptyList(),
        override val applyFn: (T) -> T
    ) : UnaryOperator<T>(op, LiteralTokenParser(op), Precedence(precedence, 1), aliases) {
        override fun expandAliases(): List<Operator<T>> {
            val actualOperatorNames = listOf(name, *aliases.toTypedArray())

            return actualOperatorNames.map {
                Prefix(
                    op = it,
                    precedence = precedence.precedence,
                    applyFn = applyFn
                )
            }
        }
    }

    public class Postfix<T : Any>(
        op: String,
        precedence: Int,
        aliases: List<String> = emptyList(),
        override val applyFn: (T) -> T
    ) : UnaryOperator<T>(op, LiteralTokenParser(op), Precedence(precedence, 2), aliases) {
        override fun expandAliases(): List<Operator<T>> {
            val actualOperatorNames = listOf(name, *aliases.toTypedArray())

            return actualOperatorNames.map {
                Postfix(
                    op = it,
                    precedence = precedence.precedence,
                    applyFn = applyFn
                )
            }
        }
    }

    public class Infixr<T : Any>(
        op: String,
        precedence: Int,
        aliases: List<String> = emptyList(),
        override val applyFn: (T, T) -> T
    ) : BinaryOperator<T>(op, LiteralTokenParser(op), Precedence(precedence, 3), aliases) {
        override fun expandAliases(): List<Operator<T>> {
            val actualOperatorNames = listOf(name, *aliases.toTypedArray())

            return actualOperatorNames.map {
                Infixr(
                    op = it,
                    precedence = precedence.precedence,
                    applyFn = applyFn
                )
            }
        }
    }

    public class Infix<T : Any>(
        op: String,
        precedence: Int,
        aliases: List<String> = emptyList(),
        override val applyFn: (T, T) -> T
    ) : BinaryOperator<T>(op, LiteralTokenParser(op), Precedence(precedence, 4), aliases) {
        override fun expandAliases(): List<Operator<T>> {
            val actualOperatorNames = listOf(name, *aliases.toTypedArray())

            return actualOperatorNames.map {
                Infix(
                    op = it,
                    precedence = precedence.precedence,
                    applyFn = applyFn
                )
            }
        }
    }

    override fun compareTo(other: Operator<T>): Int {
        return compareValuesBy(this, other) { it.precedence }
    }

    public data class Precedence(
        val precedence: Int,
        val associativity: Int
    ) : Comparable<Precedence> {
        override fun compareTo(other: Precedence): Int {
            return compareValuesBy(this, other, { it.precedence }, { it.associativity })
        }
    }
}
