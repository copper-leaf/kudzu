package com.copperleaf.kudzu.parser.sequence

import com.copperleaf.kudzu.node.sequence.Sequence2Node
import com.copperleaf.kudzu.node.sequence.Sequence3Node
import com.copperleaf.kudzu.node.sequence.Sequence4Node
import com.copperleaf.kudzu.node.sequence.Sequence5Node
import com.copperleaf.kudzu.node.sequence.Sequence6Node
import com.copperleaf.kudzu.node.sequence.Sequence7Node
import com.copperleaf.kudzu.node.sequence.Sequence8Node
import com.copperleaf.kudzu.node.sequence.Sequence9Node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parser.chars.DigitParser
import com.copperleaf.kudzu.test
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class TestFixedSequences : StringSpec({

    "testSequence2Parser" {
        SequenceParser(
            DigitParser(),
            DigitParser(),
        ).test("12")
            .parsedCorrectly()
            .first
            .also {
                Sequence2Node::class as KClass<*> shouldBe it::class
            }
            .also { (c, n1, n2) ->
                "1:1" shouldBe c.startPosition.toString()
                "1:3" shouldBe c.endPosition.toString()

                '1' shouldBe n1.char
                '2' shouldBe n2.char
            }
    }

    "testSequence3Parser" {
        SequenceParser(
            DigitParser(),
            DigitParser(),
            DigitParser(),
        ).test("123")
            .parsedCorrectly()
            .first
            .also {
                Sequence3Node::class as KClass<*> shouldBe it::class
            }
            .also { (c, n1, n2, n3) ->
                "1:1" shouldBe c.startPosition.toString()
                "1:4" shouldBe c.endPosition.toString()

                '1' shouldBe n1.char
                '2' shouldBe n2.char
                '3' shouldBe n3.char
            }
    }

    "testSequence4Parser" {
        SequenceParser(
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
        ).test("1234")
            .parsedCorrectly()
            .first
            .also {
                Sequence4Node::class as KClass<*> shouldBe it::class
            }
            .also { (c, n1, n2, n3, n4) ->
                "1:1" shouldBe c.startPosition.toString()
                "1:5" shouldBe c.endPosition.toString()

                '1' shouldBe n1.char
                '2' shouldBe n2.char
                '3' shouldBe n3.char
                '4' shouldBe n4.char
            }
    }

    "testSequence5Parser" {
        SequenceParser(
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
        ).test("12345")
            .parsedCorrectly()
            .first
            .also {
                Sequence5Node::class as KClass<*> shouldBe it::class
            }
            .also { (c, n1, n2, n3, n4, n5) ->
                "1:1" shouldBe c.startPosition.toString()
                "1:6" shouldBe c.endPosition.toString()

                '1' shouldBe n1.char
                '2' shouldBe n2.char
                '3' shouldBe n3.char
                '4' shouldBe n4.char
                '5' shouldBe n5.char
            }
    }

    "testSequence6Parser" {
        SequenceParser(
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
        ).test("123456")
            .parsedCorrectly()
            .first
            .also {
                Sequence6Node::class as KClass<*> shouldBe it::class
            }
            .also { (c, n1, n2, n3, n4, n5, n6) ->
                "1:1" shouldBe c.startPosition.toString()
                "1:7" shouldBe c.endPosition.toString()

                '1' shouldBe n1.char
                '2' shouldBe n2.char
                '3' shouldBe n3.char
                '4' shouldBe n4.char
                '5' shouldBe n5.char
                '6' shouldBe n6.char
            }
    }

    "testSequence7Parser" {
        SequenceParser(
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
        ).test("1234567")
            .parsedCorrectly()
            .first
            .also {
                Sequence7Node::class as KClass<*> shouldBe it::class
            }
            .also { (c, n1, n2, n3, n4, n5, n6, n7) ->
                "1:1" shouldBe c.startPosition.toString()
                "1:8" shouldBe c.endPosition.toString()

                '1' shouldBe n1.char
                '2' shouldBe n2.char
                '3' shouldBe n3.char
                '4' shouldBe n4.char
                '5' shouldBe n5.char
                '6' shouldBe n6.char
                '7' shouldBe n7.char
            }
    }

    "testSequence8Parser" {
        SequenceParser(
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
        ).test("12345678")
            .parsedCorrectly()
            .first
            .also {
                Sequence8Node::class as KClass<*> shouldBe it::class
            }
            .also { (c, n1, n2, n3, n4, n5, n6, n7, n8) ->
                "1:1" shouldBe c.startPosition.toString()
                "1:9" shouldBe c.endPosition.toString()

                '1' shouldBe n1.char
                '2' shouldBe n2.char
                '3' shouldBe n3.char
                '4' shouldBe n4.char
                '5' shouldBe n5.char
                '6' shouldBe n6.char
                '7' shouldBe n7.char
                '8' shouldBe n8.char
            }
    }

    "testSequence9Parser" {
        SequenceParser(
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
        ).test("123456789")
            .parsedCorrectly()
            .first
            .also {
                Sequence9Node::class shouldBe it::class as KClass<*>
            }
            .also { (c, n1, n2, n3, n4, n5, n6, n7, n8, n9) ->
                "1:1" shouldBe c.startPosition.toString()
                "1:10" shouldBe c.endPosition.toString()

                '1' shouldBe n1.char
                '2' shouldBe n2.char
                '3' shouldBe n3.char
                '4' shouldBe n4.char
                '5' shouldBe n5.char
                '6' shouldBe n6.char
                '7' shouldBe n7.char
                '8' shouldBe n8.char
                '9' shouldBe n9.char
            }
    }
})
