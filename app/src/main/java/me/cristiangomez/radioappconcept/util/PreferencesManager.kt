package me.cristiangomez.radioappconcept.util

import android.content.Context
import androidx.core.content.edit
import me.cristiangomez.radioappconcept.R

class PreferencesManager(context: Context) {
    val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.app_name),
            Context.MODE_PRIVATE
    )

    fun isPlaying(): Boolean {
        return sharedPreferences.getBoolean(PREFERENCE_IS_PLAYING, false)
    }

    fun setIsPlaying(isPlaying: Boolean) {
        sharedPreferences.edit {
            putBoolean(PREFERENCE_IS_PLAYING, isPlaying)
        }
    }

    companion object {
        const val PREFERENCE_IS_PLAYING = "IS_PLAYING"
    }
}