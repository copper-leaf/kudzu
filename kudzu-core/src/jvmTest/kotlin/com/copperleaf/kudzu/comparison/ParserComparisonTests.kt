package com.copperleaf.kudzu.comparison

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parser.expression.ExpressionParser
import com.copperleaf.kudzu.parser.expression.Operator
import com.copperleaf.kudzu.parser.expression.TestExpression
import com.copperleaf.kudzu.performance.performanceTest
import com.copperleaf.kudzu.test
import com.github.h0tk3y.betterParse.combinators.and
import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.skip
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import me.alllex.parsus.parser.map
import me.alllex.parsus.parser.or
import me.alllex.parsus.parser.times
import me.alllex.parsus.token.literalToken
import me.alllex.parsus.token.regexToken
import kotlin.math.pow
import kotlin.test.Test
import com.github.h0tk3y.betterParse.combinators.leftAssociative as betterParseLeftAssociative
import com.github.h0tk3y.betterParse.grammar.Grammar as BetterParseGrammar
import com.github.h0tk3y.betterParse.grammar.parser as betterParseParserFn
import com.github.h0tk3y.betterParse.parser.Parser as BetterParseParser
import me.alllex.parsus.parser.Grammar as ParsusGrammar
import me.alllex.parsus.parser.Parser as ParsusParser
import me.alllex.parsus.parser.leftAssociative as parsusLeftAssociative
import me.alllex.parsus.parser.parser as parsusParserFn

@OptIn(ExperimentalStdlibApi::class)
@Suppress("UNUSED_VARIABLE")
class ParserComparisonTests {

    val enabled = false

// Test Cases
// ---------------------------------------------------------------------------------------------------------------------

    val simpleExpression = run {
        "1 - 2 * (3 + 4 / 5 ^ 6 * (7 - 8)) * 9"
    }
    val deeplyNestedExpression = run {
        val depth = 100_000
        val expresssionStart = (1..depth).joinToString(separator = "") { "1 + (" }
        val expresssionEnd = (1..depth).joinToString(separator = "") { ")" }

        "$expresssionStart 1 + 1 $expresssionEnd"
    }

// Kudzu
// ---------------------------------------------------------------------------------------------------------------------

    val kudzuParser = ExpressionParser<Double>(
        termParser = { TestExpression.IntAsDoubleParser() },
        simplifyAst = false,

        operators = listOf(
            Operator.Infix(op = "+", 40) { l, r -> l + r },
            Operator.Infix(op = "-", 40) { l, r -> l - r },
            Operator.Infix(op = "*", 60) { l, r -> l * r },
            Operator.Infix(op = "/", 60) { l, r -> l / r },

            Operator.Prefix(op = "-", 80) { r -> -r },
            Operator.Infixr(op = "^", 70) { l, r -> l.pow(r) },
        )
    )

    @Test // this test takes a long time, so only run it as-needed and not part of normal development cycles
    fun kudzuSimpleExpression() {
        if (!enabled) return

        performanceTest(10_000, 1000) {
            expectThat(
                kudzuParser.test(simpleExpression, skipWhitespace = true, logErrors = true)
            ).parsedCorrectly()
        }
    }

    @Test // this test takes a long time, so only run it as-needed and not part of normal development cycles
    fun kudzuDeeplyNestedExpression() {
        if (!enabled) return

        performanceTest(1, 0) {
            expectThat(
                kudzuParser.test(deeplyNestedExpression, skipWhitespace = true, logErrors = true)
            ).parsedCorrectly()
        }
    }

// Parsus
// ---------------------------------------------------------------------------------------------------------------------

    sealed class Expr {
        data class Con(val value: Int) : Expr()
        data class Neg(val expr: Expr) : Expr()
        data class Pow(val left: Expr, val right: Expr) : Expr()
        data class Mul(val left: Expr, val right: Expr) : Expr()
        data class Div(val left: Expr, val right: Expr) : Expr()
        data class Add(val left: Expr, val right: Expr) : Expr()
        data class Sub(val left: Expr, val right: Expr) : Expr()
    }

    abstract class ParsusAbstractArithmeticGrammar<T> : ParsusGrammar<T>() {
        init {
            register(regexToken("\\s+", ignored = true))
        }

        val lpar by literalToken("(")
        val rpar by literalToken(")")
        val pow by literalToken("^")
        val times by literalToken("*")
        val div by literalToken("/")
        val plus by literalToken("+")
        val minus by literalToken("-")
        val int by regexToken("\\d+")

        val number by parsusParserFn { int() } map { it.text.toInt() }
        val braced by parsusParserFn { -lpar * expr() * -rpar }

        abstract val expr: ParsusParser<T>
        override val root by parsusParserFn { expr() }
    }

    object ParsusExprCalculator : ParsusAbstractArithmeticGrammar<Int>() {

        val const by number

        val term by parsusParserFn {
            val neg = !minus
            val v = any(const, braced)
            if (neg) -v else v
        }

        val powExpr by parsusParserFn {
            parsusLeftAssociative(term, pow) { l, _, r ->
                l.toDouble().pow(r.toDouble()).toInt()
            }
        }

        val mulExpr by parsusParserFn {
            parsusLeftAssociative(powExpr, times or div) { l, o, r ->
                if (o.token == times) l * r else l / r
            }
        }

        val addExpr by parsusParserFn {
            parsusLeftAssociative(mulExpr, plus or minus) { l, o, r ->
                if (o.token == plus) l + r else l - r
            }
        }

        override val expr by addExpr
    }

    @Test // this test takes a long time, so only run it as-needed and not part of normal development cycles
    fun parsusSimpleExpression() {
        if (!enabled) return

        performanceTest(10_000, 1000) {
            val computedValue = ParsusExprCalculator.parseToEnd(simpleExpression)
        }
    }

    @Test // this test takes a long time, so only run it as-needed and not part of normal development cycles
    fun parsusDeeplyNestedExpression() {
        if (!enabled) return

        performanceTest(1, 0) {
            val computedValue = ParsusExprCalculator.parseToEnd(deeplyNestedExpression)
        }
    }

// Better-Parse
// ---------------------------------------------------------------------------------------------------------------------

    class BetterParseArithmeticsEvaluator : BetterParseGrammar<Int>() {
        val num by regexToken("-?\\d+")
        val lpar by literalToken("(")
        val rpar by literalToken(")")
        val mul by literalToken("*")
        val pow by literalToken("^")
        val div by literalToken("/")
        val minus by literalToken("-")
        val plus by literalToken("+")
        val ws by regexToken("\\s+", ignore = true)

        val number by num use { text.toInt() }
        val term: BetterParseParser<Int> by number or
            (skip(minus) and betterParseParserFn(::term) map { -it }) or
            (skip(lpar) and betterParseParserFn(::rootParser) and skip(rpar))

        val powChain by betterParseLeftAssociative(term, pow) { a, _, b -> a.toDouble().pow(b.toDouble()).toInt() }

        val divMulChain by betterParseLeftAssociative(powChain, div or mul use { type }) { a, op, b ->
            if (op == div) a / b else a * b
        }

        val subSumChain by betterParseLeftAssociative(divMulChain, plus or minus use { type }) { a, op, b ->
            if (op == plus) a + b else a - b
        }

        override val rootParser: BetterParseParser<Int> by subSumChain
    }

    @Test // this test takes a long time, so only run it as-needed and not part of normal development cycles
    fun betterSimpleExpression() {
        if (!enabled) return

        performanceTest(10_000, 1000) {
            val result = BetterParseArithmeticsEvaluator().parseToEnd(simpleExpression)
        }
    }

    @Test // this test takes a long time, so only run it as-needed and not part of normal development cycles
    fun betterDeeplyNestedExpression() {
        if (!enabled) return

        performanceTest(1, 0) {
            val result = BetterParseArithmeticsEvaluator().parseToEnd(deeplyNestedExpression)
        }
    }
}
