package com.copperleaf.kudzu.parser.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.Parser

@ExperimentalStdlibApi
sealed class Operator(
    val parser: Parser<Node>,
    val precedence: Precedence
) : Comparable<Operator> {

    @ExperimentalStdlibApi
    class Prefix(
        op: Parser<*>,
        precedence: Int
    ) : Operator(op as Parser<Node>, Precedence(precedence, 1))

    @ExperimentalStdlibApi
    class Postfix(
        op: Parser<*>,
        precedence: Int
    ) : Operator(op as Parser<Node>, Precedence(precedence, 2))

    @ExperimentalStdlibApi
    class Infixr(
        op: Parser<*>,
        precedence: Int
    ) : Operator(op as Parser<Node>, Precedence(precedence, 3))

    @ExperimentalStdlibApi
    class Infix(
        op: Parser<*>,
        precedence: Int
    ) : Operator(op as Parser<Node>, Precedence(precedence, 4))

    override fun compareTo(other: Operator): Int {
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
