package me.cristiangomez.radioappconcept.ui.player

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.start_fragment.*
import me.cristiangomez.radioappconcept.R
import me.cristiangomez.radioappconcept.data.model.MetaData
import me.cristiangomez.radioappconcept.receiver.PlayerBroadcastReceiver
import me.cristiangomez.radioappconcept.service.PlayerService
import me.cristiangomez.radioappconcept.util.PreferencesManager

class PlayerFragment : Fragment() {

    companion object {
        fun newInstance() = PlayerFragment()
    }

    private lateinit var viewModel: PlayerViewModel
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var localBroadcastManager: LocalBroadcastManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.start_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PlayerViewModel::class.java)
        viewModel.getMetaDataLiveData(requireContext().applicationContext)?.observe(this, Observer {
            viewModel.updateArtist(it.artist, requireContext())
            updateMetaData(it)
        })
        viewModel.artistInfoLiveData.observe(this, Observer {
            if (it != null) {
                it.images.firstOrNull()?.url?.let {
                    Picasso.get()
                            .load(it)
                            .tag(it)
                            .into(artistImageView)
                }
            } else {
                artistImageView.setImageResource(R.drawable.ic_album)
            }
        })

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
    }

    override fun onPause() {
        preferencesManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        updatePlayerUI()
        preferencesManager.sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    private fun updatePlayerUI() {
        if (preferencesManager.isPlaying()) {
            showPauseButton()
        } else {
            preferencesManager.setMetaData(null)
            showPlayButton()
        }
        if (volume != null) {
            volume.progress = (preferencesManager.getVolume() * 100).toInt()
        }
    }

    private fun updateMetaData(metaData: MetaData) {
        songArtistTextView.text = metaData.artist
        songTitleTextView.text = metaData.song
    }

    private fun showPlayButton() {
        if (play != null) {
            stop.visibility = View.INVISIBLE
            pause.visibility = View.INVISIBLE
            play.visibility = View.VISIBLE
        }
    }

    private fun showPauseButton() {
        if (play != null) {
            play.visibility = View.INVISIBLE
            pause.visibility = View.VISIBLE
            stop.visibility = View.VISIBLE
        }
    }

}
