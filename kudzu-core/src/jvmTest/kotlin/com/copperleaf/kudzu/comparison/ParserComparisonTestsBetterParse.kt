package com.copperleaf.kudzu.comparison

import com.copperleaf.kudzu.comparison.ParserComparisonTests.deeplyNestedExpression
import com.copperleaf.kudzu.comparison.ParserComparisonTests.simpleExpression
import com.copperleaf.kudzu.performance.performanceTest
import com.github.h0tk3y.betterParse.combinators.and
import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.skip
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import io.kotest.core.spec.style.StringSpec
import kotlin.math.pow
import com.github.h0tk3y.betterParse.combinators.leftAssociative as betterParseLeftAssociative
import com.github.h0tk3y.betterParse.grammar.Grammar as BetterParseGrammar
import com.github.h0tk3y.betterParse.grammar.parser as betterParseParserFn
import com.github.h0tk3y.betterParse.parser.Parser as BetterParseParser

@Suppress("UNUSED_VARIABLE")
class ParserComparisonTestsBetterParse : StringSpec({
    // this test takes a long time, so only run it as-needed and not part of normal development cycles
    "betterSimpleExpression".config(enabled = ParserComparisonTests.enabled) {
        performanceTest(10_000, 1000) {
            val result = BetterParseArithmeticsEvaluator().parseToEnd(simpleExpression)
        }
    }

    // this test takes a long time, so only run it as-needed and not part of normal development cycles
    "betterDeeplyNestedExpression".config(enabled = ParserComparisonTests.enabled) {
        performanceTest(1, 0) {
            val result = BetterParseArithmeticsEvaluator().parseToEnd(deeplyNestedExpression)
        }
    }
})

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
