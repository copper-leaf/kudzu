package com.copperleaf.kudzu.parser.tag

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.many.ManyNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.tag.TagParserBuilder.checkTagsAreValid

/**
 * A configurable tag-based parser, usually for things like string interpolation of HTML-like parsers. Usually, raw text
 * is scanned until some kind of "opening tag" is encountered, the scanning process continues recursively, and then a
 * "closing tag" ends the recursive parsing.
 */
class TagParser(
    private val tags: List<TagBuilder<*, *>>,
    private val allowSameTagRecursion: Boolean = false
) : Parser<ManyNode<Node>> {

    private val parser: Parser<ManyNode<Node>> by lazy {
        checkTagsAreValid(tags)
        TagParserBuilder.createTagScanningParser(tags, allowSameTagRecursion)
    }

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override val parse: DeepRecursiveFunction<ParserContext, ParserResult<ManyNode<Node>>> = parser.parse
}
