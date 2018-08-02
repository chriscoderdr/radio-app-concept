package me.cristiangomez.radioappconcept.util.validator

import android.content.Context
import me.cristiangomez.radioappconcept.R

class LengthValidator(private val minLength: Int? = null, private val maxLength: Int? = null) : Validator<String> {
    override var errorMessage: String? = null

    override fun validate(input: String, context: Context): Boolean {
        var isValid = true
        if (minLength != null) {
            if (input.count() < minLength) {
                isValid = false
                errorMessage = context.getString(R.string.error_min_length, minLength)
            }
        }
        if (maxLength != null) {
            if (input.count() > maxLength) {
                errorMessage = context.getString(R.string.error_max_length, maxLength)
                isValid = false
            }
        }
        return isValid
    }

    override fun shouldStop(): Boolean {
        return false
    }
}