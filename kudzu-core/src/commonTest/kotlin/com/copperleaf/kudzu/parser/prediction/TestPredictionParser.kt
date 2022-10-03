package com.copperleaf.kudzu.parser.prediction

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isFalse
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.predict.PredictionParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser
import com.copperleaf.kudzu.parser.text.AnyTokenParser
import com.copperleaf.kudzu.test
import kotlin.test.Test

class TestPredictionParser {

    private val parser = SequenceParser(
        CharInParser('<'),
        AnyTokenParser(),
        CharInParser('>')
    )

    private val predictionParser = PredictionParser(parser)

    @Test
    fun testWithoutPrediction() {
        "<a>".run {
            expectThat(parser.predict(ParserContext.fromString(this))).isTrue()
            expectThat(parser.test(this)).parsedCorrectly()
        }
        "<".run {
            expectThat(parser.predict(ParserContext.fromString(this))).isTrue()
            expectThat(parser.test(this)).parsedIncorrectly()
        }
        "</a>".run {
            expectThat(parser.predict(ParserContext.fromString(this))).isTrue()
            expectThat(parser.test(this)).parsedIncorrectly()
        }
    }

    @Test
    fun testWithPrediction() {
        "<a>".run {
            expectThat(predictionParser.predict(ParserContext.fromString(this))).isTrue()
            expectThat(predictionParser.test(this)).parsedCorrectly()
        }
        "<".run {
            expectThat(predictionParser.predict(ParserContext.fromString(this))).isFalse()
            expectThat(predictionParser.test(this)).parsedIncorrectly()
        }
        "</a>".run {
            expectThat(predictionParser.predict(ParserContext.fromString(this))).isFalse()
            expectThat(predictionParser.test(this)).parsedIncorrectly()
        }
    }
}
