package me.cristiangomez.radioappconcept.ui.start

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import me.cristiangomez.radioappconcept.R
import me.cristiangomez.radioappconcept.receiver.PlayerBroadcastReceiver
import me.cristiangomez.radioappconcept.receiver.PlayerMetadataBroadcastReceiver
import me.cristiangomez.radioappconcept.service.PlayerService
import me.cristiangomez.radioappconcept.util.PreferencesManager
import kotlinx.android.synthetic.main.start_fragment.*

class StartFragment : Fragment() {

    companion object {
        fun newInstance() = StartFragment()
    }

    private lateinit var viewModel: StartViewModel
    private lateinit var metadataBroadcastReceiver: BroadcastReceiver
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var localBroadcastManager: LocalBroadcastManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.start_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(StartViewModel::class.java)
        preferencesManager = PreferencesManager(requireContext())
        localBroadcastManager = LocalBroadcastManager.getInstance(requireContext())
        play.setOnClickListener {
            showPauseButton()
            requireContext().startService(Intent(requireContext(),
                    PlayerService::class.java))
            localBroadcastManager.sendBroadcast(Intent(PlayerBroadcastReceiver.ACTION_PLAY))
        }
        pause.setOnClickListener {
            showPlayButton()
            localBroadcastManager.sendBroadcast(Intent(PlayerBroadcastReceiver.ACTION_STOP))
        }
        metadataBroadcastReceiver = PlayerMetadataBroadcastReceiver { metaData: String ->
            val metaList = metaData.split(" - ")
            songArtistTextView.setText(metaList.firstOrNull())
            songTitleTextView.setText(metaList.getOrNull(1))
        }
        sharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> updatePlayerUI() }
        // TODO: Use the ViewModel
    }

    override fun onPause() {
        super.onPause()
        preferencesManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        localBroadcastManager.unregisterReceiver(metadataBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        updatePlayerUI()
        preferencesManager.sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        val intentFilter = IntentFilter()
        intentFilter.addAction(PlayerMetadataBroadcastReceiver.ACTION_NEW_META_DATA)
        localBroadcastManager.registerReceiver(metadataBroadcastReceiver, intentFilter)
    }

    private fun updatePlayerUI() {
        if (preferencesManager.isPlaying()) {
            showPauseButton()
        } else {
            showPlayButton()
        }
    }

    private fun showPlayButton() {
        pause.visibility = View.INVISIBLE
        play.visibility = View.VISIBLE
    }

    private fun showPauseButton() {
        play.visibility = View.INVISIBLE
        pause.visibility = View.VISIBLE
    }

}
