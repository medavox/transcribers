package com.github.medavox.ipa_transcribers

/**Kitchen-sink Rule. Allows you to specify any and all features of a rule.*/
class EverythingRule(consumedMatcher:Regex?, unconsumedMatcher: Regex,
        outputString: (soFar:String, theMatches:MatchGroupCollection) -> String,
        lettersConsumed: Int?=null)
    : IRule(consumedMatcher, unconsumedMatcher, outputString, lettersConsumed)