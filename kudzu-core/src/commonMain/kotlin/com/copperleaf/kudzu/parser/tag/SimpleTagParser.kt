package com.copperleaf.kudzu.parser.tag

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.tag.TagNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

/**
 * A simple, non-necessarily-recursive tag parser. It matches a sequence of "open tag", "content", "close tag".
 */
@ExperimentalStdlibApi
@Suppress("UNCHECKED_CAST")
class SimpleTagParser<Opening : Node, Content : Node>(
    val name: String,
    val openingParser: Parser<Opening>,
    val contentParser: Parser<Content>,
    val closingParser: Parser<*>,
) : Parser<TagNode<Opening, Content>> {

    private val parser: Parser<TagNode<Opening, Content>> by lazy {
        FlatMappedParser(
            SequenceParser(
                openingParser,
                contentParser,
                closingParser,
            )
        ) {
            val (open, content, _) = it.children
            TagNode(open as Opening, content as Content, it.context)
        }
    }

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override val parse get() = parser.parse
}
