package com.copperleaf.kudzu.parser.chars

import com.copperleaf.kudzu.node.chars.CharNode
import com.copperleaf.kudzu.parser.wrapped.WrappedParser
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.many.TimesParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

/**
 * Consumes an escaped character from the input, and converts it to the actual Kotlin Char.
 *
 * Predicts true when:
 *   - parsing is successful
 *
 * Parsing fails when:
 *   - the next input character is not in the whitelisted characters
 *   - there is no more input
 *   - the escape character is used without input remaining to escape
 */
public class EscapedCharParser : WrappedParser<CharNode>(
    FlatMappedParser(
        ExactChoiceParser(
            normalEscapeChar,
            unicodeEscapeChar
        )
    ) {
        it.node as CharNode
    }
) {
    private companion object {
        private val normalEscapeChar = FlatMappedParser(
            SequenceParser(
                CharInParser('\\'),
                CharInParser('\\', 'r', 'n', 't', '\'', '"'),
            )
        ) { (nodeContext, _, charNode) ->
            val escapedChar = when (charNode.text) {
                "\\" -> '\\'
                "r" -> '\r'
                "n" -> '\n'
                "t" -> '\t'
                "'" -> '\''
                "\"" -> '"'
                else -> error("invalid escape char: ${charNode.text}")
            }

            CharNode(escapedChar, nodeContext)
        }

        private val unicodeEscapeChar = FlatMappedParser(
            SequenceParser(
                CharInParser('\\'),
                CharInParser('u'),
                TimesParser(
                    HexDigitParser(),
                    times = 4
                )
            )
        ) { (nodeContext, _, _, unicodeHexNodes) ->
            val codePoint = unicodeHexNodes.text.toInt(16)
            val unicodeChar = codePoint.toChar()

            CharNode(unicodeChar, nodeContext)
        }
    }
}
