package me.cristiangomez.radioappconcept.util.validator

import android.content.Context
import me.cristiangomez.radioappconcept.R
import me.cristiangomez.radioappconcept.util.isValidEmail

class EmailValidator : Validator<String> {
    override var errorMessage: String? = null
    override fun validate(input: String, context: Context): Boolean {
        errorMessage = context.getString(R.string.error_email_invalid)
        return input.isValidEmail()
    }

    override fun shouldStop(): Boolean {
        return false
    }
}