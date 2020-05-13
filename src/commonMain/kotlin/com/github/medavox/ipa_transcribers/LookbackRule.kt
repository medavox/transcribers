package com.github.medavox.ipa_transcribers

/**Match against BOTH the start of the input string,
 * AND the end of the already-consumed string - the input characters that have already been matched so far.
 * Useful for rules where a match only applies after a different match.*/
class LookbackRule(consumedMatcher:Regex, unconsumedMatcher:Regex, output:String, lettersConsumed:Int?=null )
    : IRule(consumedMatcher, unconsumedMatcher, { s, _->s+output}, lettersConsumed) {

    constructor(consumedMatcher:String, unconsumedMatcher:String, output:String, lettersConsumed:Int?=null )
    :this(Regex(consumedMatcher), Regex(unconsumedMatcher), output, lettersConsumed)
}