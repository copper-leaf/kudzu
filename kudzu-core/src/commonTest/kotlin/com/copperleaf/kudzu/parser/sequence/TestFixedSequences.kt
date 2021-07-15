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
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalStdlibApi::class)
class TestFixedSequences {

    @Test
    fun testSequence2Parser() {
        SequenceParser(
            DigitParser(),
            DigitParser(),
        ).test("12")
            .parsedCorrectly()
            .first
            .also {
                assertEquals(Sequence2Node::class as KClass<*>, it::class)
            }
            .also { (c, n1, n2) ->
                assertEquals("1:1", c.startPosition.toString())
                assertEquals("1:3", c.endPosition.toString())

                assertEquals('1', n1.char)
                assertEquals('2', n2.char)
            }
    }

    @Test
    fun testSequence3Parser() {
        SequenceParser(
            DigitParser(),
            DigitParser(),
            DigitParser(),
        ).test("123")
            .parsedCorrectly()
            .first
            .also {
                assertEquals(Sequence3Node::class as KClass<*>, it::class)
            }
            .also { (c, n1, n2, n3) ->
                assertEquals("1:1", c.startPosition.toString())
                assertEquals("1:4", c.endPosition.toString())

                assertEquals('1', n1.char)
                assertEquals('2', n2.char)
                assertEquals('3', n3.char)
            }
    }

    @Test
    fun testSequence4Parser() {
        SequenceParser(
            DigitParser(),
            DigitParser(),
            DigitParser(),
            DigitParser(),
        ).test("1234")
            .parsedCorrectly()
            .first
            .also {
                assertEquals(Sequence4Node::class as KClass<*>, it::class)
            }
            .also { (c, n1, n2, n3, n4) ->
                assertEquals("1:1", c.startPosition.toString())
                assertEquals("1:5", c.endPosition.toString())

                assertEquals('1', n1.char)
                assertEquals('2', n2.char)
                assertEquals('3', n3.char)
                assertEquals('4', n4.char)
            }
    }

    @Test
    fun testSequence5Parser() {
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
                assertEquals(Sequence5Node::class as KClass<*>, it::class)
            }
            .also { (c, n1, n2, n3, n4, n5) ->
                assertEquals("1:1", c.startPosition.toString())
                assertEquals("1:6", c.endPosition.toString())

                assertEquals('1', n1.char)
                assertEquals('2', n2.char)
                assertEquals('3', n3.char)
                assertEquals('4', n4.char)
                assertEquals('5', n5.char)
            }
    }

    @Test
    fun testSequence6Parser() {
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
                assertEquals(Sequence6Node::class as KClass<*>, it::class)
            }
            .also { (c, n1, n2, n3, n4, n5, n6) ->
                assertEquals("1:1", c.startPosition.toString())
                assertEquals("1:7", c.endPosition.toString())

                assertEquals('1', n1.char)
                assertEquals('2', n2.char)
                assertEquals('3', n3.char)
                assertEquals('4', n4.char)
                assertEquals('5', n5.char)
                assertEquals('6', n6.char)
            }
    }

    @Test
    fun testSequence7Parser() {
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
                assertEquals(Sequence7Node::class as KClass<*>, it::class)
            }
            .also { (c, n1, n2, n3, n4, n5, n6, n7) ->
                assertEquals("1:1", c.startPosition.toString())
                assertEquals("1:8", c.endPosition.toString())

                assertEquals('1', n1.char)
                assertEquals('2', n2.char)
                assertEquals('3', n3.char)
                assertEquals('4', n4.char)
                assertEquals('5', n5.char)
                assertEquals('6', n6.char)
                assertEquals('7', n7.char)
            }
    }

    @Test
    fun testSequence8Parser() {
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
                assertEquals(Sequence8Node::class as KClass<*>, it::class)
            }
            .also { (c, n1, n2, n3, n4, n5, n6, n7, n8) ->
                assertEquals("1:1", c.startPosition.toString())
                assertEquals("1:9", c.endPosition.toString())

                assertEquals('1', n1.char)
                assertEquals('2', n2.char)
                assertEquals('3', n3.char)
                assertEquals('4', n4.char)
                assertEquals('5', n5.char)
                assertEquals('6', n6.char)
                assertEquals('7', n7.char)
                assertEquals('8', n8.char)
            }
    }

    @Test
    fun testSequence9Parser() {
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
                assertEquals(Sequence9Node::class, it::class as KClass<*>)
            }
            .also { (c, n1, n2, n3, n4, n5, n6, n7, n8, n9) ->
                assertEquals("1:1", c.startPosition.toString())
                assertEquals("1:10", c.endPosition.toString())

                assertEquals('1', n1.char)
                assertEquals('2', n2.char)
                assertEquals('3', n3.char)
                assertEquals('4', n4.char)
                assertEquals('5', n5.char)
                assertEquals('6', n6.char)
                assertEquals('7', n7.char)
                assertEquals('8', n8.char)
                assertEquals('9', n9.char)
            }
    }
}
