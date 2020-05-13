package com.github.medavox.transcribers

expect object err {
    fun print(err:String)
    fun println(err:String)
}

/**Get the unicode name of the character with this codepoint.*/
expect val Int.unicodeName:String
