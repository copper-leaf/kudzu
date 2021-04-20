package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.node.Node

abstract class Parser<NodeType : Node> {

    abstract fun predict(input: ParserContext): Boolean

    @OptIn(ExperimentalStdlibApi::class)
    abstract val parse: DeepRecursiveFunction<ParserContext, ParserResult<NodeType>>
}
