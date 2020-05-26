package com.github.medavox.transcribers

/***This API takes a context-free approach:
 * Regex is matched to the start of the string only,
 * and the output String is not interpreted as Regex.
 *
 * Therefore, there is no state held by the Transcriber;
 * only simple substitutions matched by Regular expressions may be used.
 **/
abstract class RuleBasedTranscriber:Transcriber {
    data class UnmatchedOutput(val newWorkingInput:String, val newConsumed:String, val output:(soFar:String) -> String) {
        constructor(newWorkingInput: String, newConsumed:String, output:String):this(newWorkingInput, newConsumed, {it+output})
        constructor(newWorkingInput: String, output:String):this(newWorkingInput, "", {it+output})
    }
    private var reportedChars:String = ""
    private val reportedStrings:MutableSet<String> = mutableSetOf()
    fun reportOnceAndCopy(it:String):UnmatchedOutput {
        if(!reportedChars.contains(it[0])) {
            err.println("copying unknown char '${it[0]}'/'${it[0].toInt().unicodeName}' to output...")
            reportedChars += it[0]
        }
        return UnmatchedOutput(it.substring(1), it[0].toString(), it[0].toString())
    }

    val reportAndSkip:(String) -> UnmatchedOutput get() = {
        err.println("unknown char '${it[0]}'; skipping...")
        UnmatchedOutput(it.substring(1), it[0].toString(),"")
    }

    val reportAndCopy:(String) -> UnmatchedOutput get() = {
        err.println("copying unknown char '${it[0]}' to output...")
        UnmatchedOutput(it.substring(1), it[0].toString(), it[0].toString())
    }

    val copy:(String) -> UnmatchedOutput get() = {
        UnmatchedOutput(it.substring(1), it[0].toString(), it[0].toString())
    }

    fun reportOnceAndCopy(remainingInput:String, unmatchedChars:Int): UnmatchedOutput {
        val unmatched = remainingInput.substring(0, unmatchedChars)
        if(!reportedStrings.contains(unmatched)) {
            err.println("copying unknown char '$unmatched' to output...")
            //err.println("copying unknown char '$unmatched'/'${unmatched.forEach { it.toInt().unicodeName}}' to output...")
            reportedStrings += unmatched
        }
        return UnmatchedOutput( newWorkingInput = remainingInput.substring(unmatchedChars),
                                newConsumed = unmatched,
                                output = unmatched
        )
    }

    fun reportAndSkip(remainingInput:String, unmatchedChars:Int): UnmatchedOutput  {
        val unmatched = remainingInput.substring(0, unmatchedChars)
        err.println("unknown char '${remainingInput[0]}'; skipping...")
        return UnmatchedOutput(newWorkingInput = remainingInput.substring(unmatchedChars),
                newConsumed = unmatched,
                output = "")
    }

    fun reportAndCopy(remainingInput:String, unmatchedChars:Int): UnmatchedOutput {
        val unmatched = remainingInput.substring(0, unmatchedChars)
        err.println("copying unknown char '${remainingInput[0]}' to output...")
        return UnmatchedOutput( newWorkingInput = remainingInput.substring(unmatchedChars),
                newConsumed = unmatched,
                output = unmatched
        )
    }

    fun copy(remainingInput:String, unmatchedChars:Int): UnmatchedOutput  {
        val unmatched = remainingInput.substring(0, unmatchedChars)
        return UnmatchedOutput( newWorkingInput = remainingInput.substring(unmatchedChars),
                newConsumed = unmatched,
                output = unmatched
        )
    }

    /**Applies the rule which consumes the most characters.
     *
     * Attempt 2
     * This greedy matcher matches against the most specific rule,
     *  not the one that consumes the most characters.

        Given the example:
        Rule ("abc", "def", 1),
        Rule("ab", "xy")

        The first rule will match, even though the second one consumes more characters.

        Includes the consumed match (if any) in the specificity metric.
     *///todo: when 2 rules are of equal specificity, use the one that appears first
    fun String.processGreedily(rules:List<BaseRule>, onNoRuleMatch:(unmatched:String) -> UnmatchedOutput) : String =
        this.processGreedily({rules}, onNoRuleMatch)
    fun String.processGreedily(rules:()->List<BaseRule>, onNoRuleMatch:(unmatched:String) -> UnmatchedOutput) : String {
        var out:String = ""
        var processingWord:String = this
        var consumed = ""
        loop@ while(processingWord.isNotEmpty()) {
            //get the regex result of the unconsumedInput and alreadyConsumed matchers,
            //because we'll be using them a lot.
            //using a triple makes it easier to keep the IRule together with its MatchResults
            val candidateRules = rules().map {
                Triple<BaseRule, MatchResult?, MatchResult?>(it,
                    it.unconsumedMatcher.find(processingWord),
                    it.consumedMatcher?.findAll(consumed)?.lastOrNull())
            }.filter {(_, unconsumed, consumedMatch) -> //filter out rules that don't match:
                //the unconsumed matcher must match at the start, and
                unconsumed?.range?.start == 0 &&
                //the consumed matcher must either be null (unspecified), or
                (consumedMatch == null ||
                //it must match at the end of the "already-consumed input" string
                consumedMatch.range.endInclusive == consumed.length-1)
            }.map { (rule, uncon, con) -> //make the unconsumed MatchResult non-null
                Triple<BaseRule, MatchResult, MatchResult?>(rule, uncon!!, con)
            }

            if(candidateRules.isEmpty()) {//no rule matched; call the lambda!
                val unmatchedOutput = onNoRuleMatch(processingWord)
                processingWord = unmatchedOutput.newWorkingInput
                consumed += unmatchedOutput.newConsumed
                out = unmatchedOutput.output(out)
            }else {
                //find the rule that matches (but does not necessarily consume) the most characters
                val (rule, unconsumedMatch) = candidateRules.maxBy { (_, uncon, con) ->
                    uncon.value.length + (con?.value?.length ?: 0)
                }!!
                //println("rule '$rule' matches '$processingWord'")
                out = rule.outputString(out, unconsumedMatch.groups)
                //number of letters consumed is the match length, unless explicitly specified
                val actualLettersConsumed = rule.lettersConsumed?.invoke(unconsumedMatch.groups) ?: unconsumedMatch.value.length
                if (actualLettersConsumed > 0) {
                    consumed += processingWord.substring(0, actualLettersConsumed)
                    processingWord = processingWord.substring(actualLettersConsumed)
                    continue@loop
                }//else keep going through the rule list, staying at the same position  in the input
            }
        }
        return out
    }

    fun String.processWithRules(rules:List<BaseRule>, onNoRuleMatch:(unmatched:String) -> UnmatchedOutput) : String =
        this.processWithRules({rules}, onNoRuleMatch)
    fun String.processWithRules(rules:()->List<BaseRule>, onNoRuleMatch:(unmatched:String) -> UnmatchedOutput) : String {
        var out:String = ""
        var processingWord:String = this
        var consumed = ""
        loop@ while(processingWord.isNotEmpty()) {
            //uses the first rule which matches -- so rule order matters
            for (rule in rules()) {
                val unconsumedMatch:MatchResult? = rule.unconsumedMatcher.find(processingWord)

                val consumedMatches:Boolean = rule.consumedMatcher == null ||// if it's null, that counts as matching:
                        //rules that don't specify a consumedMatcher aren't checked against it

                        //if it has been specified by this rule, it has to match at the end of the already-consumed string
                    rule.consumedMatcher.findAll(consumed).lastOrNull()?.range?.endInclusive == consumed.length-1

                //if the rule matches the start of the remaining string, and the end of the consumed string
                if(consumedMatches && unconsumedMatch?.range?.start == 0) {
                    //println("rule '$rule' matches '$processingWord'")
                    out = rule.outputString(out, unconsumedMatch.groups)
                    //number of letters consumed is the match length, unless explicitly specified
                    val actualLettersConsumed = rule.lettersConsumed?.invoke(unconsumedMatch.groups) ?: unconsumedMatch.value.length
                    if(actualLettersConsumed > 0) {
                        consumed += processingWord.substring(0, actualLettersConsumed)
                        processingWord = processingWord.substring(actualLettersConsumed)
                        continue@loop
                    }//else keep going through the rule list
                }
            }
            //no rule matched; call the lambda!
            val unmatchedOutput = onNoRuleMatch(processingWord)
            processingWord = unmatchedOutput.newWorkingInput
            consumed += unmatchedOutput.newConsumed
            out = unmatchedOutput.output(out)
        }
        //System.out.println("consumed: $consumed")
        return out
    }
}