package com.github.medavox.ipa_transcribers

/**The simplest rule type.
 * Use this when you just need to specify a [Regex] to match the beginning of the remaining input string,
 * and what that match should be replaced with.
 * You can also optionally specify how far along the input string to advance afterwards, so you can match characters
 * that you don't want to consume (look-ahead matching).*/
class Rule(matcher:Regex, outputString:String, lettersConsumed:Int? = null)
    :IRule(null, matcher, { s, _->s+outputString }, lettersConsumed) {
    constructor(match:String, outputString:String, lettersConsumed:Int? = null)
            :this(Regex(match), outputString, lettersConsumed)
}

