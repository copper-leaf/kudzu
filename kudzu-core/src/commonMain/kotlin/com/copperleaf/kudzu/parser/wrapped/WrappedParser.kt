package com.copperleaf.kudzu.parser.wrapped

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.ParseFunction
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext

public open class WrappedParser<T : Node>(
    private val parser: Parser<T>
) : Parser<T> {
    public constructor(
        provideParser: () -> Parser<T>
    ) : this(provideParser())

    final override fun predict(input: ParserContext): Boolean {
        return parser.predict(input)
    }

    final override val parse: ParseFunction<T> get() = parser.parse
}
