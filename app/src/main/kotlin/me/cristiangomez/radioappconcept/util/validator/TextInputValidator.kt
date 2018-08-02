package me.cristiangomez.radioappconcept.util.validator

import android.content.Context
import com.google.android.material.textfield.TextInputLayout

class TextInputValidator(private val fields: Map<TextInputLayout, List<Validator<String>>>,
                         val context: Context) {
    fun validate(): Boolean {
        var isValid = true
        fields.forEach {
            val value = it.key.editText!!.text!!.toString()
            for (validator in it.value) {
                if (!validator.validate(value, context)) {
                    isValid = false
                    it.key.error = validator.errorMessage
                    if (validator.shouldStop()) {
                        break
                    }
                }
            }
        }
        return isValid
    }
}