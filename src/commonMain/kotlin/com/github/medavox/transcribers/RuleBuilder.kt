package com.github.medavox.transcribers

/** Necessary because all BaseRule arguments can be replaced with simpler subtypes:
* [outputString] : `(soFar:String, theMatches:MatchGroupCollection) -> String` can also just be [String]
* [unconsumedMatcher] and [consumedMatcher] :Regex can both just be [String]s
* [lettersConsumed] : ((theMatches:MatchGroupCollection) ->Int)? can also be an Int, or null
in order to support all posible desired combinations of arguments with just kotlin constructors (and optional parameters),
There would need to be a number of constructors equal to
every combination of alternate types for every combination of present arguments:

```
(unconsumedMatcher:String, outputString:String)
(unconsumedMatcher:Regex, outputString:String)
(consumedMatcher:String, unconsumedMatcher:String, outputString:String)
(consumedMatcher:String, unconsumedMatcher:Regex, outputString:String)
(consumedMatcher:Regex, unconsumedMatcher:Regex, outputString:String)
(consumedMatcher:Regex, unconsumedMatcher:Regex, outputString:(soFar:String, theMatches:MatchGroupCollection) -> String)
...
```
 */
class RuleBuilder(private val unconsumedMatcher:Regex) {
    private var consumedMatcher:Regex? = null
    private var outputString:(soFar:String, theMatches:MatchGroupCollection) -> String = {s, _ -> s}
    private var lettersConsumed:((theMatches:MatchGroupCollection) ->Int)?=null
    private var label:String? = null

    constructor(unconsumedMatcher: String) :this(Regex(unconsumedMatcher))
    fun build():BaseRule {
        return BaseRule(consumedMatcher, unconsumedMatcher, outputString, lettersConsumed, label ?: "")
    }

    fun afterWordBoundary():RuleBuilder {
        consumedMatcher = Regex("(^|[^a-zA-Z_0-9])")
        return this
    }

    fun consumedMatch(consumedMatch:Regex):RuleBuilder {
        this.consumedMatcher = consumedMatch
        return this
    }
    fun consumedMatch(consumedMatch:String):RuleBuilder {
        this.consumedMatcher = Regex(consumedMatch)
        return this
    }
    fun outputString(outputString:(soFar:String, theMatches:MatchGroupCollection) -> String):RuleBuilder {
        this.outputString = outputString
        return this
    }
    fun outputString(outputString:String):RuleBuilder {
        this.outputString = {s, m -> s+outputString}
        return this
    }
    fun lettersConsumed(lettersConsumed:(theMatches:MatchGroupCollection) -> Int):RuleBuilder {
        this.lettersConsumed = lettersConsumed
        return this
    }
    fun lettersConsumed(lettersConsumed:Int):RuleBuilder {
        this.lettersConsumed = {lettersConsumed}
        return this
    }

    fun label(label:String):RuleBuilder {
        this.label = label
        return this
    }
}