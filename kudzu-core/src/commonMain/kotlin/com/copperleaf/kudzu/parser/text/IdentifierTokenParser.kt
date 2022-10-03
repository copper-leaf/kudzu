package com.copperleaf.kudzu.parser.text

import com.copperleaf.kudzu.node.text.TextNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.chars.LetterOrDigitParser
import com.copperleaf.kudzu.parser.chars.LetterParser
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.maybe.MaybeParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

/**
 * Consume a token that is a valid, generic identifier. An identifier is a letter or underscore followed by zero or more letter,
 * digit, or underscore characters.
 */
class IdentifierTokenParser : Parser<TextNode> {
    private val parser: Parser<TextNode> by lazy {
        FlatMappedParser(
            SequenceParser(
                ExactChoiceParser(
                    LetterParser(),
                    CharInParser('_'),
                ),
                MaybeParser(
                    ManyParser(
                        ExactChoiceParser(
                            LetterOrDigitParser(),
                            CharInParser('_'),
                        )
                    )
                ),
            )
        ) {
            TextNode(it.text, it.context)
        }
    }

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<TextNode>> = parser.parse
}
