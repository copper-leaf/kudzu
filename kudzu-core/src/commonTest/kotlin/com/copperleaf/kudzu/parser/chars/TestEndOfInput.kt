package com.copperleaf.kudzu.parser.chars

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isEqualTo
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser
import com.copperleaf.kudzu.test
import kotlin.test.Test

class TestEndOfInput {

    @Test
    fun testEndOfInputParser_blankInput() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = EndOfInputParser()

        input = ""
        output = underTest.test(input)
        expected = """(EndOfInputNode: 'EOF')"""

        expectThat(output).parsedCorrectly(expected)
        expectThat(output!!.first.text).isEqualTo("EOF")
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()
    }

    @Test
    fun testEndOfInputParser_consumedFullSequence() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = SequenceParser(
            ManyParser(LetterOrDigitParser()),
            EndOfInputParser()
        )

        input = "asdf"
        output = underTest.test(input)
        expected = """
            |(Sequence2Node:
            |  (ManyNode:
            |    (CharNode: 'a')
            |    (CharNode: 's')
            |    (CharNode: 'd')
            |    (CharNode: 'f')
            |  )
            |  (EndOfInputNode: 'EOF')
            |)
            |""".trimMargin()

        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()
    }

    @Test
    fun testEndOfInputParserFails_inputRemaining() {
        var input: String
        var output: ParserResult<Node>?
        val underTest = SequenceParser(
            ManyParser(LetterOrDigitParser()),
            EndOfInputParser()
        )

        input = "asdf "
        output = underTest.test(
            input,
            expectedErrorMessage = """
            |Parse error at 1:5 (EndOfInputParser)
            |
            |Expected end of input, but still had input remaining
            |
            |1|asdf 
            |>>>>>>^
        """.trimMargin()
        )

        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()
    }
}
