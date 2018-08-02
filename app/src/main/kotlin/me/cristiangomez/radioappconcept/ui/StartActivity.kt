package me.cristiangomez.radioappconcept.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.start_activity.*
import me.cristiangomez.radioappconcept.R
import me.cristiangomez.radioappconcept.service.PlayerService

class StartActivity : AppCompatActivity() {
    var currentSelectedItemId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, PlayerService::class.java))
        } else {
            startService(Intent(this, PlayerService::class.java))
        }
        NavigationUI.setupWithNavController(bottom_navigation,
                findNavController(R.id.nav_controller))
    }

}
