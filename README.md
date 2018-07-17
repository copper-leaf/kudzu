# Monadik
---

> A monadic (I think...) recursive-descent parser written in Kotlin

Monadik is a recursive-descent parser written in Kotlin, with the goal of immutability and simplicity. It is most an 
exercise for me to learn more about parsing algorithms.

## Why did it write it?

I've got several projects which require custom parsing, and after looking around at the various options available in 
Java and Kotlin, I've decided that I would just rather write my own. Most of the ones I found either require Java 8
(a deal-breaker if I want to use it on Android), or I found them to be very complex to use. This library is a parser 
combinator like the rest, and maybe I found the other options confusing just because I am still learning how these 
things work.

Regardless, I aim for this library to have a very strikt (heh) and clean separation between parsing the input into an 
AST, and the manipulation and/or evaluation of the AST, which will be implemented using a visitor pattern. You'll first 
build your grammar using the parsing primitives provided by Monadik, which will produce an immutable grammar that will 
recognize your language. This grammar will parse an input string into an AST. You can then attach visitors to that AST,
which will react to the nodes in the AST as you walk the tree.

## To-Do

- [x] Set up a parser that is able to recognize moderately-complex grammars, such as a calculator or code-islands 
- [ ] Implement a visitor pattern for evaluating the AST
- [ ] Be able to render the grammar in BNF
- [ ] Be able to render the grammar in a railroad diagram  
 