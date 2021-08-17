package com.copperleaf.kudzu.parser.value

import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.chars.DigitParser
import com.copperleaf.kudzu.parser.many.AtLeastParser
import com.copperleaf.kudzu.parser.mapped.MappedParser
import com.copperleaf.kudzu.parser.maybe.MaybeParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

@ExperimentalStdlibApi
class DoubleLiteralParser : Parser<ValueNode<Double>> {
    private val parser by lazy {
        MappedParser(
            SequenceParser(
                MaybeParser(
                    CharInParser('-')
                ),
                AtLeastParser(
                    DigitParser(),
                    minSize = 1
                ),
                CharInParser('.'),
                AtLeastParser(
                    DigitParser(),
                    minSize = 1
                )
            ),
            remapErrors = { _, _ ->
                ParserException(
                    "Expected double literal",
                    this@DoubleLiteralParser,
                    this
                )
            }
        ) { it.text.toDouble() }
    }

    override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<ValueNode<Double>>> = parser.parse
}
