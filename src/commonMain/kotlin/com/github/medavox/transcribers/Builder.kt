package com.github.medavox.transcribers

class Builder(private val unconsumedMatcher:Regex) {
    private var consumedMatcher:Regex? = null
    private var outputString:(soFar:String, theMatches:MatchGroupCollection) -> String = {s, _ -> s}
    private var lettersConsumed:((theMatches:MatchGroupCollection) ->Int)?=null

    constructor(unconsumedMatcher: String) :this(Regex(unconsumedMatcher))
    fun build():BaseRule {
        return BaseRule(consumedMatcher, unconsumedMatcher, outputString, lettersConsumed)
    }

    fun afterWordBoundary():Builder {
        consumedMatcher = Regex("(^|[^a-zA-Z_0-9])")
        return this
    }

    fun consumedMatch(consumedMatch:Regex):Builder {
        this.consumedMatcher = consumedMatch
        return this
    }
    fun consumedMatch(consumedMatch:String):Builder {
        this.consumedMatcher = Regex(consumedMatch)
        return this
    }
    fun outputString(outputString:(soFar:String, theMatches:MatchGroupCollection) -> String):Builder {
        this.outputString = outputString
        return this
    }
    fun outputString(outputString:String):Builder {
        this.outputString = {s, m -> s+outputString}
        return this
    }
    fun lettersConsumed(lettersConsumed:(theMatches:MatchGroupCollection) -> Int):Builder {
        this.lettersConsumed = lettersConsumed
        return this
    }
    fun lettersConsumed(lettersConsumed:Int):Builder {
        this.lettersConsumed = {lettersConsumed}
        return this
    }
}