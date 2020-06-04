package com.github.medavox.transcribers

/**Use Regex capturing groups (from the match) in the output string.
 * Useful for matching a whole class of characters with one rule (eg vowels),
 * and repeating that character in the output.
 * The capturing groups aren't accessed with the traditional string markers (\1 or $1),
 * but rather with the passed [MatchGroupCollection]'s methods.*/
class CapturingRule(match:Regex,
                    outputString: (soFar:String, theMatches:MatchGroupCollection) -> String,
                    lettersConsumed: Int?=null,
                    label:String = ""
):BaseRule(null,
    match,
    outputString,
    if(lettersConsumed != null) fun(m:MatchGroupCollection):Int { return lettersConsumed} else null,
    label
) {
    constructor(match: String,
                outputString: (soFar:String, theMatches:MatchGroupCollection) -> String,
                lettersConsumed: Int?=null,
                label:String = ""
    ):this(Regex(match),
        outputString,
        lettersConsumed,
        label
    )
}