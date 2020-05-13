package com.github.medavox.transcribers

/**Modify (revise) the output string so far.
 * Useful when a later character modifies the pronunciation of an earlier one.
 * */
class RevisingRule(match:Regex, outputString:(soFar:String) -> String, lettersUsed:Int? = null)
    : IRule(null, match, { s, _ -> outputString(s) }, lettersUsed) {
    constructor(match:String, outputString:(soFar:String) -> String, lettersUsed:Int? = null)
    :this(Regex(match), outputString, lettersUsed)
}