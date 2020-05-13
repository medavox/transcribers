package com.github.medavox.transcribers

/**Works like a normal [Rule], but also checks that the last character in the consumed string is not a word character.
 *
 * This is a workaround to simulate "\bword\b" patterns. */
class WordBoundaryRule(matcher:Regex,
                       outputString:(soFar:String, theMatches:MatchGroupCollection) -> String,
                       lettersConsumed:Int? = null
):BaseRule(Regex("(^|[^a-zA-Z_0-9])"),
    matcher, { s, _->s+outputString },
    if(lettersConsumed != null) fun(m:MatchGroupCollection):Int { return lettersConsumed} else null
) {
    constructor(match:String,
                outputString:String,
                lettersConsumed:Int? = null
    ):this(Regex(match),
        {s, _ -> s+outputString},
        lettersConsumed
    )
}

