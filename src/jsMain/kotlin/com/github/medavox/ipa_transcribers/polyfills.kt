package com.github.medavox.ipa_transcribers

import org.w3c.dom.HTMLTextAreaElement
import kotlin.browser.document

actual object err {
    val errorsTextArea = document.getElementById("errors_text") as HTMLTextAreaElement
    actual fun print(err: String) {
        errorsTextArea.textContent += err
    }

    actual fun println(err: String) {
        return print(err+"\n")
    }
}

actual val Int.unicodeName: String
    get() {
        TODO()
    }
