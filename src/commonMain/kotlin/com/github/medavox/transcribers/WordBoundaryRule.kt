package com.github.medavox.transcribers

/**Works like a normal [Rule], but also checks that the last character in the consumed string is not a word character.
 *
 * This is a workaround to simulate "\bword\b" patterns. */
class WordBoundaryRule(matcher:Regex, outputString:String, lettersConsumed:Int? = null)
    :IRule(Regex("(^|[^a-zA-Z_0-9])"), matcher, { s, _->s+outputString }, if(lettersConsumed != null) {{lettersConsumed}} else null ) {
    constructor(match:String, outputString:String, lettersConsumed:Int? = null)
            :this(Regex(match), outputString, lettersConsumed)
}

