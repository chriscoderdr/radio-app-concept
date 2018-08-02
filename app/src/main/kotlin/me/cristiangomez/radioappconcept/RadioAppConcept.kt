package me.cristiangomez.radioappconcept

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import me.cristiangomez.radioappconcept.util.PreferencesManager


class RadioAppConcept: Application() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()
        PreferencesManager(this).setMetaData(null)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }
}