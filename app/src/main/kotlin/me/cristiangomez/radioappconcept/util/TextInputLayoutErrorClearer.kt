package me.cristiangomez.radioappconcept.util

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout

class TextInputLayoutErrorClearer(val inputLayout: TextInputLayout) : TextWatcher {
    override fun afterTextChanged(editable: Editable?) {
        if (inputLayout.error != null) {
            inputLayout.error = null
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
}