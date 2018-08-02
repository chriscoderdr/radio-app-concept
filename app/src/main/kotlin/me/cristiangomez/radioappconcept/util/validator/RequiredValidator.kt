package me.cristiangomez.radioappconcept.util.validator

import android.content.Context
import me.cristiangomez.radioappconcept.R

class RequiredValidator : Validator<String> {

    override fun shouldStop(): Boolean {
        return true
    }

    override fun validate(input: String, context: Context): Boolean {
        errorMessage = context.getString(R.string.error_required)
        return input.isNotBlank()
    }

    override var errorMessage: String? = null
}