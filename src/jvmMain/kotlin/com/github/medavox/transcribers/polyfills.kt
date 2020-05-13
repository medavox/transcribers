package com.github.medavox.transcribers

actual object err {
    actual fun print(err: String) = System.err.print(err)
    actual fun println(err: String)  = System.err.println(err)
}

actual val Int.unicodeName: String get() = Character.getName(this)
