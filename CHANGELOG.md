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
