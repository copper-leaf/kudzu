package com.copperleaf.kudzu.parser.tag

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.Parser

/**
 * A configurable tag-based parser, usually for things like string interpolation of HTML-like parsers. Usually, raw text
 * is scanned until some kind of "opening tag" is encountered, the scanning process continues recursively, and then a
 * "closing tag" ends the recursive parsing.
 */
@ExperimentalStdlibApi
class TagBuilder<Opening : Node>(
    val name: String,
    val openingParser: Parser<Opening>,
    val closingParser: Parser<*>
)
