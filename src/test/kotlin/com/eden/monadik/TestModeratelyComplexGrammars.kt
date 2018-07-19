package com.eden.monadik

import com.eden.monadik.parser.AtLeastParser
import com.eden.monadik.parser.CharInParser
import com.eden.monadik.parser.CharNotInParser
import com.eden.monadik.parser.ChoiceParser
import com.eden.monadik.parser.DigitParser
import com.eden.monadik.parser.LazyParser
import com.eden.monadik.parser.ManyParser
import com.eden.monadik.parser.MaybeParser
import com.eden.monadik.parser.SequenceParser
import org.junit.jupiter.api.Test
import strikt.api.expect

class TestModeratelyComplexGrammars {

    @Test
    fun testClogParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = getClogParser()

        input = "asdf, asdf {this is my message} asdfasdfasdf"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (ChoiceNode:
                (ManyNode:
                  (CharNode: 'a')
                  (CharNode: 's')
                  (CharNode: 'd')
                  (CharNode: 'f')
                  (CharNode: ',')
                  (CharNode: ' ')
                  (CharNode: 'a')
                  (CharNode: 's')
                  (CharNode: 'd')
                  (CharNode: 'f')
                  (CharNode: ' ')
                )
              )
              (ChoiceNode:
                (SequenceNode:
                  (CharNode: '{')
                  (ManyNode:
                    (CharNode: 't')
                    (CharNode: 'h')
                    (CharNode: 'i')
                    (CharNode: 's')
                    (CharNode: ' ')
                    (CharNode: 'i')
                    (CharNode: 's')
                    (CharNode: ' ')
                    (CharNode: 'm')
                    (CharNode: 'y')
                    (CharNode: ' ')
                    (CharNode: 'm')
                    (CharNode: 'e')
                    (CharNode: 's')
                    (CharNode: 's')
                    (CharNode: 'a')
                    (CharNode: 'g')
                    (CharNode: 'e')
                  )
                  (CharNode: '}')
                )
              )
              (ChoiceNode:
                (ManyNode:
                  (CharNode: ' ')
                  (CharNode: 'a')
                  (CharNode: 's')
                  (CharNode: 'd')
                  (CharNode: 'f')
                  (CharNode: 'a')
                  (CharNode: 's')
                  (CharNode: 'd')
                  (CharNode: 'f')
                  (CharNode: 'a')
                  (CharNode: 's')
                  (CharNode: 'd')
                  (CharNode: 'f')
                )
              )
            )
        """
        expect(output).parsedCorrectly(expected)
    }

    @Test
    fun testArithmeticParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = getArithmeticParser()

        input = "1 + (-2 * 3 * 4 * 5 * (6 / 7) - (8 / 9))"
        output = underTest.test(input, true)
        expected = """
            (SequenceNode:
              (SequenceNode:
                (SequenceNode:
                  (MaybeNode: (empty))
                  (ChoiceNode:
                    (ManyNode:
                      (CharNode: '1')
                    )
                  )
                )
                (ManyNode:

                )
              )
              (ManyNode:
                (SequenceNode:
                  (ChoiceNode:
                    (CharNode: '+')
                  )
                  (SequenceNode:
                    (SequenceNode:
                      (MaybeNode: (empty))
                      (ChoiceNode:
                        (SequenceNode:
                          (CharNode: '(')
                          (SequenceNode:
                            (SequenceNode:
                              (SequenceNode:
                                (MaybeNode:
                                  (CharNode: '-')
                                )
                                (ChoiceNode:
                                  (ManyNode:
                                    (CharNode: '2')
                                  )
                                )
                              )
                              (ManyNode:
                                (SequenceNode:
                                  (ChoiceNode:
                                    (CharNode: '*')
                                  )
                                  (SequenceNode:
                                    (MaybeNode: (empty))
                                    (ChoiceNode:
                                      (ManyNode:
                                        (CharNode: '3')
                                      )
                                    )
                                  )
                                )
                                (SequenceNode:
                                  (ChoiceNode:
                                    (CharNode: '*')
                                  )
                                  (SequenceNode:
                                    (MaybeNode: (empty))
                                    (ChoiceNode:
                                      (ManyNode:
                                        (CharNode: '4')
                                      )
                                    )
                                  )
                                )
                                (SequenceNode:
                                  (ChoiceNode:
                                    (CharNode: '*')
                                  )
                                  (SequenceNode:
                                    (MaybeNode: (empty))
                                    (ChoiceNode:
                                      (ManyNode:
                                        (CharNode: '5')
                                      )
                                    )
                                  )
                                )
                                (SequenceNode:
                                  (ChoiceNode:
                                    (CharNode: '*')
                                  )
                                  (SequenceNode:
                                    (MaybeNode: (empty))
                                    (ChoiceNode:
                                      (SequenceNode:
                                        (CharNode: '(')
                                        (SequenceNode:
                                          (SequenceNode:
                                            (SequenceNode:
                                              (MaybeNode: (empty))
                                              (ChoiceNode:
                                                (ManyNode:
                                                  (CharNode: '6')
                                                )
                                              )
                                            )
                                            (ManyNode:
                                              (SequenceNode:
                                                (ChoiceNode:
                                                  (CharNode: '/')
                                                )
                                                (SequenceNode:
                                                  (MaybeNode: (empty))
                                                  (ChoiceNode:
                                                    (ManyNode:
                                                      (CharNode: '7')
                                                    )
                                                  )
                                                )
                                              )
                                            )
                                          )
                                          (ManyNode:

                                          )
                                        )
                                        (CharNode: ')')
                                      )
                                    )
                                  )
                                )
                              )
                            )
                            (ManyNode:
                              (SequenceNode:
                                (ChoiceNode:
                                  (CharNode: '-')
                                )
                                (SequenceNode:
                                  (SequenceNode:
                                    (MaybeNode: (empty))
                                    (ChoiceNode:
                                      (SequenceNode:
                                        (CharNode: '(')
                                        (SequenceNode:
                                          (SequenceNode:
                                            (SequenceNode:
                                              (MaybeNode: (empty))
                                              (ChoiceNode:
                                                (ManyNode:
                                                  (CharNode: '8')
                                                )
                                              )
                                            )
                                            (ManyNode:
                                              (SequenceNode:
                                                (ChoiceNode:
                                                  (CharNode: '/')
                                                )
                                                (SequenceNode:
                                                  (MaybeNode: (empty))
                                                  (ChoiceNode:
                                                    (ManyNode:
                                                      (CharNode: '9')
                                                    )
                                                  )
                                                )
                                              )
                                            )
                                          )
                                          (ManyNode:

                                          )
                                        )
                                        (CharNode: ')')
                                      )
                                    )
                                  )
                                  (ManyNode:

                                  )
                                )
                              )
                            )
                          )
                          (CharNode: ')')
                        )
                      )
                    )
                    (ManyNode:

                    )
                  )
                )
              )
            )
        """
        expect(output).parsedCorrectly(expected)
    }

    @Test
    fun testClogExpressionParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = getClogExpressionParser()

        input = "you have {1+(-2*3)} messages"
        output = underTest.test(input, true)
        expected = """
        (ManyNode:
          (ChoiceNode:
            (ManyNode:
              (CharNode: 'y')
              (CharNode: 'o')
              (CharNode: 'u')
              (CharNode: 'h')
              (CharNode: 'a')
              (CharNode: 'v')
              (CharNode: 'e')
            )
          )
          (ChoiceNode:
            (SequenceNode:
              (CharNode: '{')
              (SequenceNode:
                (SequenceNode:
                  (SequenceNode:
                    (MaybeNode: (empty))
                    (ChoiceNode:
                      (ManyNode:
                        (CharNode: '1')
                      )
                    )
                  )
                  (ManyNode:

                  )
                )
                (ManyNode:
                  (SequenceNode:
                    (ChoiceNode:
                      (CharNode: '+')
                    )
                    (SequenceNode:
                      (SequenceNode:
                        (MaybeNode: (empty))
                        (ChoiceNode:
                          (SequenceNode:
                            (CharNode: '(')
                            (SequenceNode:
                              (SequenceNode:
                                (SequenceNode:
                                  (MaybeNode:
                                    (CharNode: '-')
                                  )
                                  (ChoiceNode:
                                    (ManyNode:
                                      (CharNode: '2')
                                    )
                                  )
                                )
                                (ManyNode:
                                  (SequenceNode:
                                    (ChoiceNode:
                                      (CharNode: '*')
                                    )
                                    (SequenceNode:
                                      (MaybeNode: (empty))
                                      (ChoiceNode:
                                        (ManyNode:
                                          (CharNode: '3')
                                        )
                                      )
                                    )
                                  )
                                )
                              )
                              (ManyNode:

                              )
                            )
                            (CharNode: ')')
                          )
                        )
                      )
                      (ManyNode:

                      )
                    )
                  )
                )
              )
              (CharNode: '}')
            )
          )
          (ChoiceNode:
            (ManyNode:
              (CharNode: 'm')
              (CharNode: 'e')
              (CharNode: 's')
              (CharNode: 's')
              (CharNode: 'a')
              (CharNode: 'g')
              (CharNode: 'e')
              (CharNode: 's')
            )
          )
        )
        """
        expect(output).parsedCorrectly(expected)
    }

    /*
    Code island parser
     */
    private fun getClogParser(): Parser {
        val rawParser = AtLeastParser(
                1,
                CharNotInParser('{', '}')
        )

        val clogParser = SequenceParser(
                CharInParser('{'),
                rawParser,
                CharInParser('}')
        )

        val statementParser = ChoiceParser(
                rawParser,
                clogParser
        )

        val messageParser = ManyParser(
                statementParser
        )

        return messageParser
    }

    /*
    Recursively-grouped, binary operation expression parser

    number     ::= digit+
    factor     ::= '-'? (number | '(' expression ')')
    term       ::= factor (('*'| '/') factor)*
    expression ::= term   (('+'| '-') term  )*
     */
    private fun getArithmeticParser(): Parser {
        val number = AtLeastParser(1, DigitParser())

        val lazyExpressionParser = LazyParser()

        val factorParser = SequenceParser(
                MaybeParser(
                        CharInParser('-')
                ),
                ChoiceParser(
                        number,
                        SequenceParser(
                                CharInParser('('),
                                lazyExpressionParser,
                                CharInParser(')')
                        )
                )
        )

        val termParser = SequenceParser(
                factorParser,
                ManyParser(
                        SequenceParser(
                                ChoiceParser(
                                        CharInParser('*'),
                                        CharInParser('/')
                                ),
                                factorParser
                        )
                )
        )

        val expressionParser = SequenceParser(
                termParser,
                ManyParser(
                        SequenceParser(
                                ChoiceParser(
                                        CharInParser('+'),
                                        CharInParser('-')
                                ),
                                termParser
                        )
                )
        )

        lazyExpressionParser.parser = expressionParser

        return expressionParser
    }

    /*
    Code island parser using a arithmetic expression as the island contents
     */
    private fun getClogExpressionParser(): Parser {
        val rawParser = AtLeastParser(
                1,
                CharNotInParser('{', '}')
        )

        val clogParser = SequenceParser(
                CharInParser('{'),
                getArithmeticParser(),
                CharInParser('}')
        )

        val statementParser = ChoiceParser(
                rawParser,
                clogParser
        )

        val messageParser = ManyParser(
                statementParser
        )

        return messageParser
    }

}
