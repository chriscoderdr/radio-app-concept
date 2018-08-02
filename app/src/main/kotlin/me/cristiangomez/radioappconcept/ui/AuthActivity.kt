package me.cristiangomez.radioappconcept.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.cristiangomez.radioappconcept.R

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }

    companion object {
        const val AUTH_SUCCESS_RESULT_CODE = 5690
    }

}
