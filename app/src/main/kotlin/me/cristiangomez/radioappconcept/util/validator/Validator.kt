package me.cristiangomez.radioappconcept.util.validator

import android.content.Context

interface Validator<T> {
    fun validate(input: T, context: Context): Boolean
    fun shouldStop(): Boolean
    var errorMessage: String?
}