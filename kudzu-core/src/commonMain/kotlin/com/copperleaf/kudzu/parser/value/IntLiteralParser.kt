package com.copperleaf.kudzu.parser.value

import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.parser.ParseFunction
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.chars.DigitParser
import com.copperleaf.kudzu.parser.many.AtLeastParser
import com.copperleaf.kudzu.parser.mapped.MappedParser
import com.copperleaf.kudzu.parser.maybe.MaybeParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

public class IntLiteralParser : Parser<ValueNode<Int>> {
    private val parser by lazy {
        MappedParser(
            SequenceParser(
                MaybeParser(
                    CharInParser('-')
                ),
                AtLeastParser(
                    DigitParser(),
                    minSize = 1
                )
            ),
            remapErrors = { _, _ ->
                ParserException(
                    "Expected integer literal",
                    this@IntLiteralParser,
                    this
                )
            }
        ) { it.text.toInt() }
    }

    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override val parse: ParseFunction<ValueNode<Int>> = parser.parse
}
