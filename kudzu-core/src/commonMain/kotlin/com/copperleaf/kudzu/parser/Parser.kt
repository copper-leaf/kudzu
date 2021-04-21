package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.node.Node

@ExperimentalStdlibApi
interface Parser<NodeType : Node> {
    fun predict(input: ParserContext): Boolean
    val parse: DeepRecursiveFunction<ParserContext, ParserResult<NodeType>>
}
