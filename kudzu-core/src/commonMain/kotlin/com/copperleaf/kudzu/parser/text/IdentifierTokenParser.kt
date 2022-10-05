package com.copperleaf.kudzu.parser.text

import com.copperleaf.kudzu.node.text.TextNode
import com.copperleaf.kudzu.parser.wrapped.WrappedParser
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
public class IdentifierTokenParser : WrappedParser<TextNode>({
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
})
