package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.node.Node

abstract class Parser<NodeType : Node> {

    abstract fun predict(input: ParserContext): Boolean

    abstract fun parse(input: ParserContext): ParserResult<NodeType>
}
