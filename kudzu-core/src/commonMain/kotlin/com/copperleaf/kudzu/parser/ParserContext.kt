package com.copperleaf.kudzu.parser

/**
 * An abstraction around input for managing how to consume characters from text. The implementation is expected to be
 * immutable and should not maintain internal state. Rather, when a Parser consumes input from the context, it returns
 * a [ParserResult] with the node it parsed and a new context which points to the next location in the input.
 *
 * In practice, this does not necessarily need to be strictly immutable, but it should be implemented in such a way that
 * characters can be read from the input at arbitrary locations without the need for manually storing/recovering source
 * locations at the parser level. For instance, a Stream can be read, but the characters it consumes from the Stream
 * should be cached in memory to allow repeated reads at the same source location, and to allow "backtracking" in the
 * input without needing to actually backtrack in the Stream itself, which may not always be possible (for example if
 * reading from a Socket).
 *
 * The default implementation of [ParserContextImpl] operates entirely in-memory and is completely immutable, to avoid
 * these kinds of issues, but may not be suitable for larger inputs.
 */
public interface ParserContext {
    public val sourcePosition: SourcePosition

    public fun isEmpty(): Boolean

    public fun nextChar(): Pair<Char, ParserContext>

    public fun validateNextChar(fn: (Char) -> Boolean): Boolean

    public fun getSurroundingText(position: SourcePosition): String

    public companion object {
        public fun fromString(input: String, skipWhitespace: Boolean = false): ParserContext {
            return ParserContextImpl(input, skipWhitespace = skipWhitespace)
        }
    }
}
