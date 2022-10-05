package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.node.Node

public typealias ParseFunction<T> = DeepRecursiveFunction<ParserContext, ParserResult<T>>
public typealias ParseScope<T> = DeepRecursiveScope<ParserContext, ParserResult<T>>

@Suppress("NOTHING_TO_INLINE")
public inline fun <T : Node> runParser(
    noinline block: suspend ParseScope<T>.(ParserContext) -> ParserResult<T>
): ParseFunction<T> {
    return DeepRecursiveFunction(block)
}
