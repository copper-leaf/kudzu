package com.copperleaf.kudzu.parser.text

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.chars.DigitParser
import com.copperleaf.kudzu.parser.choice.PredictiveChoiceParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.named.NamedParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser
import com.copperleaf.kudzu.test
import kotlin.test.Test

class TestScan {

    @Test
    fun testBasicScan() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = ScanParser(DigitParser())

        input = "asdf 1"
        output = underTest.test(input)
        expected = """
            (TextNode: 'asdf ')
        """
        expectThat(output).parsedCorrectly(expected, allowRemaining = true)
    }

    @Test
    fun testScanThenConsume() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val stoppingCondition = DigitParser()
        val underTest = SequenceParser(
            ScanParser(stoppingCondition),
            stoppingCondition
        )

        input = "asdf 1"
        output = underTest.test(input)
        expected = """
            (Sequence2Node:
              (TextNode: 'asdf ')
              (CharNode: '1')
            )
        """
        expectThat(output).parsedCorrectly(expected)
    }

    @Test
    fun testManyScanThenConsume() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val stoppingCondition = DigitParser()
        val underTest = ManyParser(
            SequenceParser(
                ScanParser(stoppingCondition),
                stoppingCondition
            )
        )

        input = "asdf 1 qwerty 2"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (Sequence2Node:
                (TextNode: 'asdf ')
                (CharNode: '1')
              )
              (Sequence2Node:
                (TextNode: ' qwerty ')
                (CharNode: '2')
              )
            )
        """
        expectThat(output).parsedCorrectly(expected)
    }

    @Test
    fun testClogScanner() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val clogFormat1 = SequenceParser(
            CharInParser('{'),
            CharInParser('}')
        )
        val clogFormat2 = SequenceParser(
            CharInParser('$'),
            CharInParser('{'),
            NamedParser(AnyTokenParser(), name = "varName"),
            CharInParser('}')
        )
        val clogFormat3 = SequenceParser(
            CharInParser('#'),
            CharInParser('{'),
            NamedParser(DigitParser(), name = "varIndex"),
            CharInParser('}')
        )
        val clogs = PredictiveChoiceParser(
            clogFormat1,
            clogFormat2,
            clogFormat3
        )

        val underTest = ManyParser(
            PredictiveChoiceParser(
                clogs,
                ScanParser(clogs)
            )
        )

        input = "asdf {}"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()

        input = "asdf \${asdf}"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()

        input = "asdf #{1}"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()

        input = "asdf {} qwerty #{1} qwerty \${asdf} asdf"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (Choice2Node.Option2:
                (TextNode: 'asdf ')
              )
              (Choice2Node.Option1:
                (Choice3Node.Option1:
                  (Sequence2Node:
                    (CharNode: '{')
                    (CharNode: '}')
                  )
                )
              )
              (Choice2Node.Option2:
                (TextNode: ' qwerty ')
              )
              (Choice2Node.Option1:
                (Choice3Node.Option3:
                  (Sequence4Node:
                    (CharNode: '#')
                    (CharNode: '{')
                    (NamedNode:varIndex:
                      (CharNode: '1')
                    )
                    (CharNode: '}')
                  )
                )
              )
              (Choice2Node.Option2:
                (TextNode: ' qwerty ')
              )
              (Choice2Node.Option1:
                (Choice3Node.Option2:
                  (Sequence4Node:
                    (CharNode: '${'$'}')
                    (CharNode: '{')
                    (NamedNode:varName:
                      (TextNode: 'asdf')
                    )
                    (CharNode: '}')
                  )
                )
              )
              (Choice2Node.Option2:
                (TextNode: ' asdf')
              )
            )
        """
        expectThat(output).parsedCorrectly(expected)

        input = "{}#{1}\${asdf}"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
    }
}
