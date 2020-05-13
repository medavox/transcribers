package com.github.medavox.transcribers

/**Modify (revise) the output string so far.
 * Useful when a later character modifies the pronunciation of an earlier one.
 * */
class RevisingRule(match:Regex, outputString:(soFar:String) -> String, lettersConsumed:Int? = null)
    : IRule(null, match, { s, _ -> outputString(s) },
    if(lettersConsumed != null) fun(m:MatchGroupCollection):Int { return lettersConsumed} else null ) {
    constructor(match:String, outputString:(soFar:String) -> String, lettersConsumed:Int? = null)
    :this(Regex(match), outputString, lettersConsumed)
}