package me.cristiangomez.radioappconcept.ui.start

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.start_fragment.*
import me.cristiangomez.radioappconcept.R
import me.cristiangomez.radioappconcept.data.repository.spotify.ArtistRepository
import me.cristiangomez.radioappconcept.receiver.PlayerBroadcastReceiver
import me.cristiangomez.radioappconcept.receiver.PlayerMetadataBroadcastReceiver
import me.cristiangomez.radioappconcept.service.PlayerService
import me.cristiangomez.radioappconcept.util.PreferencesManager

class StartFragment : Fragment() {

    companion object {
        fun newInstance() = StartFragment()
    }

    private lateinit var viewModel: StartViewModel
    private lateinit var metadataBroadcastReceiver: BroadcastReceiver
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var localBroadcastManager: LocalBroadcastManager
    private var currentMetaData: String? = null

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
        stop.setOnClickListener {
            showPlayButton()
            localBroadcastManager.sendBroadcast(Intent(PlayerBroadcastReceiver.ACTION_STOP))
        }
        pause.setOnClickListener {
            showPlayButton()
            localBroadcastManager.sendBroadcast(Intent(PlayerBroadcastReceiver.ACTION_PAUSE))
        }
        metadataBroadcastReceiver = PlayerMetadataBroadcastReceiver { metaData: String ->
            if (currentMetaData == null || currentMetaData != metaData) {
                currentMetaData = metaData
                updateMetaData(metaData)
            }
        }
        (volume as AppCompatSeekBar).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    preferencesManager.setVolume(progress / 100f)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
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
        currentMetaData = preferencesManager.getMetadata()
        if (currentMetaData != null) {
            updateMetaData(currentMetaData!!)
        }
        preferencesManager.sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        val intentFilter = IntentFilter()
        intentFilter.addAction(PlayerMetadataBroadcastReceiver.ACTION_NEW_META_DATA)
        localBroadcastManager.registerReceiver(metadataBroadcastReceiver, intentFilter)
    }

    private fun updatePlayerUI() {
        if (preferencesManager.isPlaying()) {
            showPauseButton()
        } else {
            preferencesManager.setMetaData(null)
            showPlayButton()
        }
        volume.progress = (preferencesManager.getVolume() * 100).toInt()
    }

    private fun updateMetaData(metaData: String) {
        artistImageView.setImageResource(R.drawable.ic_album)
        val metaList = metaData.split(" - ")
        songArtistTextView.text = metaList.firstOrNull()
        songTitleTextView.text = metaList.getOrNull(1)
        val artist = metaList.firstOrNull()?.split("ft", ignoreCase = true)?.firstOrNull()
        if (artist != null && !artist.isBlank()) {
            val artistRepository = ArtistRepository(requireContext().applicationContext)
            val circularProgressDrawable = CircularProgressDrawable(requireContext())
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            artistRepository.searchArtist(artist)
                    .subscribe({ it ->
                        Glide.with(requireContext())
                                .load(it.first().images.first().url.toUri())
                                .apply {
                                    RequestOptions()
                                            .placeholder(circularProgressDrawable)
                                }
                                .into(artistImageView)
                    }, { it ->
                        it.printStackTrace()
                    })
        }
    }

    private fun showPlayButton() {
        stop.visibility = View.INVISIBLE
        pause.visibility = View.INVISIBLE
        play.visibility = View.VISIBLE
    }

    private fun showPauseButton() {
        play.visibility = View.INVISIBLE
        pause.visibility = View.VISIBLE
        stop.visibility = View.VISIBLE
    }

}
