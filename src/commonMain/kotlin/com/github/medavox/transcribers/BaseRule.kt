package com.github.medavox.transcribers

/**Specifies one replacement rule, from a Regex matching native text,
 * to the IPA characters corresponding to them.
 *
 *  Features:
 - optionally specify number of letters consumed, if different from match length
 - (per-rule) either a string or lambda. The lambda can access state persisting across the whole string
 - execute a lambda function on no rule matched

In Russian, we need to know the previous consonant to check if it can be softened;
but we also need to print out the following vowel so we don't reprocess it again as a word-initial.

this would mean one rule for every pair of softenable consonant + softening vowel,
or 5 * 15 = 75 rules.

this is bad, because it doesn't scale for languages with even more combination sounds.

So instead we need to be able to 'look back' at previous, consumed letters -- preferably in a Regex-friendly way.
instead of feeding the chopped-off string (with the characters consumed so far removed) to one regex,
we have to match regexes on two strings: the consumed-so-far characters AND the unconsumed characters.
The regex for the consumed-so-far string is optional.

@constructor
primary constructor
 @property consumedMatcher
 */
open class BaseRule(
    /**Matches the already-consumed part of the input string,
     * starting from the most recently consumed character - the end of the used-up input-string.
     * If not null, BOTH matchers must match.*/
    val consumedMatcher:Regex?,

    /**The incoming native text that this rule operates on.*/
    val unconsumedMatcher: Regex,

    /**A lambda which returns the new output string, __replacing the whole of the old output string.__
     * Use this constructor if your rule has side effects, such as counting vowels so far.
     * @param soFar the entirety of the output string so far, for editing*/
    val outputString: (soFar:String, theMatches:MatchGroupCollection) -> String,

    /**The number of letters of native/input text that have been 'consumed' by this rule.
     * if not specified, defaults to the size of the Regex match.*/
    val lettersConsumed:((theMatches:MatchGroupCollection) ->Int)?=null
) {


    /**Instance-cloning functionality.
     * Used to be automatically generated when IRule was a data class.*/
    fun copy(
        consumedMatcher:Regex?=null,
        unconsumedMatcher: Regex?=null,
        outputString: ((soFar:String, theMatches:MatchGroupCollection) -> String)?=null,
        lettersConsumed: ((theMatches:MatchGroupCollection) ->Int)?=null
    ): BaseRule =
        BaseRule(
            consumedMatcher ?: this.consumedMatcher,
            unconsumedMatcher ?: this.unconsumedMatcher,
            outputString ?: this.outputString,
            lettersConsumed ?: this.lettersConsumed
        )

    /**Instance-cloning functionality.
     * Used to be automatically generated when IRule was a data class.*/
    fun copy(
        consumedMatcher:Regex?=null,
        unconsumedMatcher: Regex?=null,
        outputString: ((soFar:String) -> String)?=null,
        lettersConsumed: ((theMatches:MatchGroupCollection) ->Int)?=null
    ): BaseRule = copy(
        consumedMatcher ?: this.consumedMatcher,
        unconsumedMatcher ?: this.unconsumedMatcher,
        if(outputString != null) {s, _ -> outputString(s)} else this.outputString,
        lettersConsumed ?: this.lettersConsumed
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BaseRule

        if (consumedMatcher != other.consumedMatcher) return false
        if (unconsumedMatcher != other.unconsumedMatcher) return false
        if (outputString != other.outputString) return false
        if (lettersConsumed != other.lettersConsumed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = consumedMatcher?.hashCode() ?: 0
        result = 31 * result + unconsumedMatcher.hashCode()
        result = 31 * result + outputString.hashCode()
        result = 31 * result + lettersConsumed.hashCode()
        return result
    }

    override fun toString(): String {
        return "IRule(consumedMatcher=$consumedMatcher, unconsumedMatcher=$unconsumedMatcher, " +
                "outputString=$outputString, lettersConsumed=$lettersConsumed)"
    }

    /*fun asKotlin():String {
        return "Rule("+
                (if(consumedMatcher==null) "" else "\"$consumedMatcher\", ")+
                "\"$unconsumedMatcher\", "+
                "\""+outputString("")+"\""+
                (if(lettersConsumed==null) "" else ", $lettersConsumed")+
                ")"
    }*/
}