package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.parsedCorrectly
import kotlin.test.Test
import strikt.api.expectThat

class TestScan {

    @Test
    fun testBasicScan() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = ScanParser(DigitParser())

        input = "asdf 1"
        output = underTest.test(input)
        expected = """
            (ScanNode: 'asdf ')
        """
        expectThat(output).parsedCorrectly(expected, allowRemaining = true)
    }

    @Test
    fun testScanThenConsume() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val stoppingCondition = DigitParser()
        val underTest = SequenceParser(
            ScanParser(stoppingCondition),
            stoppingCondition
        )

        input = "asdf 1"
        output = underTest.test(input)
        expected = """
            (SequenceNode:
              (ScanNode: 'asdf ')
              (CharNode: '1')
            )
        """
        expectThat(output).parsedCorrectly(expected)
    }

    @Test
    fun testManyScanThenConsume() {
        var input: String
        var output: Pair<Node, ParserContext>?
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
              (SequenceNode:
                (ScanNode: 'asdf ')
                (CharNode: '1')
              )
              (SequenceNode:
                (ScanNode: ' qwerty ')
                (CharNode: '2')
              )
            )
        """
        expectThat(output).parsedCorrectly(expected)
    }

    @Test
    fun testClogScanner() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val clogFormat1 = SequenceParser(
            CharInParser('{'),
            CharInParser('}')
        )
        val clogFormat2 = SequenceParser(
            CharInParser('$'),
            CharInParser('{'),
            TokenParser(name = "varName"),
            CharInParser('}')
        )
        val clogFormat3 = SequenceParser(
            CharInParser('#'),
            CharInParser('{'),
            DigitParser(name = "varIndex"),
            CharInParser('}')
        )
        val clogs = ChoiceParser(
            clogFormat1,
            clogFormat2,
            clogFormat3
        )

        val underTest = ManyParser(
            ChoiceParser(
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
              (ChoiceNode:
                (ScanNode: 'asdf ')
              )
              (ChoiceNode:
                (ChoiceNode:
                  (SequenceNode:
                    (CharNode: '{')
                    (CharNode: '}')
                  )
                )
              )
              (ChoiceNode:
                (ScanNode: ' qwerty ')
              )
              (ChoiceNode:
                (ChoiceNode:
                  (SequenceNode:
                    (CharNode: '#')
                    (CharNode: '{')
                    (CharNode:varIndex: '1')
                    (CharNode: '}')
                  )
                )
              )
              (ChoiceNode:
                (ScanNode: ' qwerty ')
              )
              (ChoiceNode:
                (ChoiceNode:
                  (SequenceNode:
                    (CharNode: '$')
                    (CharNode: '{')
                    (WordNode:varName: 'asdf')
                    (CharNode: '}')
                  )
                )
              )
              (ChoiceNode:
                (ScanNode: ' asdf')
              )
            )
        """
        expectThat(output).parsedCorrectly(expected)

        input = "{}#{1}\${asdf}"
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()
    }
}
