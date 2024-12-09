package com.copperleaf.kudzu.parser.text

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isEqualTo
import com.copperleaf.kudzu.isNotNull
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.test
import kotlin.test.Test

class TestIdentifier {

    @Test
    fun testIdentifierTokenParser() {
        val parser = IdentifierTokenParser()

        val inputs = listOf(
            "a" to "a",
            "ab" to "ab",
            "a1" to "a1",
            "a_" to "a_",

            "1" to null,
            "1a" to null,
            "11" to null,
            "1_" to null,

            "_" to "_",
            "_a" to "_a",
            "_1" to "_1",
            "__" to "__",

            "_a1_b2_c3_" to "_a1_b2_c3_",
        )

        inputs.map { (input, expectedResult) ->
            val output = parser.test(input)

            if (expectedResult != null) {
                expectThat(output)
                    .parsedCorrectly()
                    .node()
                    .isNotNull()
                    .also { it.text.isEqualTo(expectedResult) }
            } else {
                expectThat(output)
                    .parsedIncorrectly()
            }
        }
    }
}
