package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.thenLog
import org.junit.jupiter.api.Test
import strikt.api.expectThat

class TestExpression {

    @Test
    fun testWordParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = ExpressionParser()

        input = "a ^ b * c + d + e"
        output = underTest.test(input, skipWhitespace = true)

        expectThat(output)
                .parsedCorrectly()
                .thenLog()
    }

}
