package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.many.TimesParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser
import com.copperleaf.kudzu.parser.text.AnyTokenParser
import com.copperleaf.kudzu.parser.text.RequiredWhitespaceParser
import com.copperleaf.kudzu.parser.value.IntLiteralParser
import com.copperleaf.kudzu.test
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TestErrorMessages : StringSpec({

    "testSurroundingText1Line" {
        val input = ParserContext.fromString("one two three")

        assertEquals(
            """
            |1|one two three
            |>>^
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(1, 1)
            )
        )
        assertEquals(
            """
            |1|one two three
            |>>>>>>^
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(1, 5)
            )
        )
    }

    "testSurroundingText2Lines" {
        val input = ParserContext.fromString(
            """
            |one two three
            |four five six
            """.trimMargin()
        )

        assertEquals(
            """
            |1|one two three
            |>>^
            |2|four five six
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(1, 1)
            )
        )
        assertEquals(
            """
            |1|one two three
            |>>>>>>^
            |2|four five six
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(1, 5)
            )
        )
        assertEquals(
            """
            |1|one two three
            |2|four five six
            |>>^
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(2, 1)
            )
        )
        assertEquals(
            """
            |1|one two three
            |2|four five six
            |>>>>>>>^
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(2, 6)
            )
        )
    }

    "testSurroundingTextMultipleLines" {
        val input = ParserContext.fromString(
            """
            |one two three
            |four five six
            |seven
            |eight nine
            |ten eleven twelve
            """.trimMargin()
        )

        // line 1
        assertEquals(
            """
            |1|one two three
            |>>^
            |2|four five six
            |3|seven
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(1, 1)
            )
        )
        assertEquals(
            """
            |1|one two three
            |>>>>>>^
            |2|four five six
            |3|seven
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(1, 5)
            )
        )

        // line 2
        assertEquals(
            """
            |1|one two three
            |2|four five six
            |>>^
            |3|seven
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(2, 1)
            )
        )
        assertEquals(
            """
            |1|one two three
            |2|four five six
            |>>>>>>>^
            |3|seven
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(2, 6)
            )
        )

        // line 3
        assertEquals(
            """
            |2|four five six
            |3|seven
            |>>^
            |4|eight nine
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(3, 1)
            )
        )
        assertEquals(
            """
            |2|four five six
            |3|seven
            |>>>>>>^
            |4|eight nine
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(3, 5)
            )
        )

        // line 4
        assertEquals(
            """
            |3|seven
            |4|eight nine
            |>>^
            |5|ten eleven twelve
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(4, 1)
            )
        )
        assertEquals(
            """
            |3|seven
            |4|eight nine
            |>>>>>>>>^
            |5|ten eleven twelve
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(4, 7)
            )
        )

        // line 5
        assertEquals(
            """
            |3|seven
            |4|eight nine
            |5|ten eleven twelve
            |>>^
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(5, 1)
            )
        )
        assertEquals(
            """
            |3|seven
            |4|eight nine
            |5|ten eleven twelve
            |>>>>>>^
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(5, 5)
            )
        )
    }

    "testSurroundingTextMoreThan10Lines" {
        val input = ParserContext.fromString(
            """
            |one
            |two
            |three
            |four
            |five
            |six
            |seven
            |eight
            |nine
            |ten
            |eleven
            |twelve
            """.trimMargin()
        )

        assertEquals(
            """
            | 8|eight
            | 9|nine
            |>>>>>>^
            |10|ten
            """.trimMargin(),
            input.getSurroundingText(
                SourcePosition(9, 4)
            )
        )
    }

    "testActualExceptionMessageManyLines" {
        val input = ParserContext.fromString(
            """
            |one,1
            |two,2
            |three,3
            |four,4
            |five,5
            |six,6
            |seven,7
            |eight,8
            |nine,
            |ten,10
            """.trimMargin()
        )
        val underTest = TimesParser(
            SequenceParser(
                AnyTokenParser(),
                CharInParser(','),
                IntLiteralParser(),
                RequiredWhitespaceParser()
            ),
            10
        )

        underTest.test(
            input,
            expectedErrorMessage = """
            |Parse error at 9:6 (IntLiteralParser)
            |
            |Expected integer literal
            |
            | 8|eight,8
            | 9|nine,
            |>>>>>>>>^
            |10|ten,10
            """.trimMargin()
        )
    }
})

private fun assertEquals(a: Any, b: Any) {
    a shouldBe b
}
