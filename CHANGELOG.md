## 2.6.0 - 2021-07-15

- Adds a bunch of Sequence parsers for fixed lengths to preserve strong generic typing. `SequenceParser` is now a 
    factory function instead of a constructor, returning the appropriate sequence parser for the number of "varargs".
- Validate that the start and end tags match in `TagParser`
- Enables hierarchical sourcesets

## 2.5.0 - 2021-05-12

- Adds `NoopParser` for implementing self-closing tags with the `TagParser`
- Adds `AnyTextParser`, which is like `AnyTextParser`, but parses any non-whitespace characters

## 2.4.0 - 2021-05-05

- Introduces new `TagParser` for parsing recursive, inline, tag-based markup. This is suitable for parsing things like 
  HTML spans or the inline formats of Markdown.
  - Features: 
    - Fully custom opening and closing tags
    - Allows tags to contain the same start and end tags (such as for Markdown)
- `PredictionParser` allows more tailored prediction, which can be useful for things like simpler tag parsers that push
  open/close tag matching into the evaluation phase instead of enforcing it during parsing.
- The implementation of `ScanParser` has been tweaked such that it requires at least 1 character able to be parsed. This '
  also means that it no longer always predicts true, but delegated prediction to its stopping condition: only if its 
  stopping condition predicts false does `ScanParser` predict true.
- The implementation of `LiteralTokenParser` has been tweaked such that it requires the entire expected token to match
  in order to predict true, instead of predicting only based on the next character.

## 2.3.0 - 2021-04-26

- Adds support for operator aliases in `ExpressionParser`

## 2.2.0 - 2021-04-26

- Adds `SeparatedbyParser` for more easily parsing lists of items separated by an ignored value (like commas)

## 2.1.0 - 2021-04-23

- Adds parsers for hexidecimal characters, standard escaped characters, escaped unicode sequences, generic identifiers
- Adds parsers for literal values: int, double, boolean, char, string, null
- improvements to expression parser: parentheses, AST simplification, implemented with DeepRecursiveFunction

## 2.0.0 - 2021-04-21

- Major cleanup of internal implementation and public APIs. The general structure/usage of the library remains mostly 
  the same, but there are some notable differences in implementation and in the public API:
  - All Parsers classes have been moved into subpackages of `com.copperleaf.kudzu.parser` to better encapsulate their 
    parsing function/behavior.
  - All Node classes have been moved to the  to `com.copperleaf.kudzu.node` package, each in sub-packages that match the
    Parser package that produces them.
  - The Visitor API has been rewritten and moved to the `com.copperleaf.kudzu.visitor` package.
  - The Expression API has been rewritten to greatly reduce the boilerplate needed for implementation. In particular, it
    no longer evaluates expressions within the Visitor API, but manually implements expression evaluation on its own 
    with a custom `ExpressionEvaluator`. Operators also manually implement their own evaluation function, instead of 
    having that separated into a separate `EvaluableOperator` as before. These operators create the operator lookup 
    table, and the whole expression and its operators parse/evaluate on a particular type parameter.
  - Parsers no longer have a `name` property for themselves. To name a particular node, use the `NamedParser` to wrap 
    and annotate it. 
  - Parsing and Visitation are now implemented using Kotlin's `DeepRecursiveFunction` utility, which helps avoid 
    StackOverflow errors and gives us more readable stacktraces.
  - Several abstract classes have been converted into interfaces, and several other classes were split into and 
    interface with default, concrete implementations managed internally to the library.

## 1.0.0 - 2021-04-10

- Release as MPP library to MavenCentral

## 0.1.0 - 2019-03-04

- Initial release
