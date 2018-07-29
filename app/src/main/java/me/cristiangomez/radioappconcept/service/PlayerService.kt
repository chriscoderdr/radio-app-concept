package me.cristiangomez.radioappconcept.service

import android.app.IntentService
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import androidx.core.net.toUri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import me.cristiangomez.radioappconcept.BuildConfig
import me.cristiangomez.radioappconcept.StartActivity
import me.cristiangomez.radioappconcept.receiver.PlayerBroadcastReceiver
import me.cristiangomez.radioappconcept.receiver.PlayerMetadataBroadcastReceiver
import me.cristiangomez.radioappconcept.util.PreferencesManager
import saschpe.exoplayer2.ext.icy.IcyHttpDataSourceFactory

class PlayerService : IntentService(PlayerService::class.java.canonicalName) {
    private var currentTitle = ""
    private var isRunning = true
    private var player: ExoPlayer? = null
    private var playerNotificationManager: PlayerNotificationManager? = null
    private var preferencesManager: PreferencesManager? = null

    override fun onHandleIntent(intent: Intent?) {
        preferencesManager = PreferencesManager(this)
        player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        val uri = BuildConfig.RADIO_STREAMING_URL.toUri()


        playerNotificationManager = PlayerNotificationManager(this,
                PLAYER_NOTIFICATION_CHANNEL_ID, NOTIFICATION_ID, object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun createCurrentContentIntent(player: Player?): PendingIntent? {
                return PendingIntent.getActivity(this@PlayerService, 0,
                        Intent(this@PlayerService, StartActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT)
            }

            override fun getCurrentContentText(player: Player?): String? {
                return null
            }

            override fun getCurrentContentTitle(player: Player?): String {
                return currentTitle
            }

            override fun getCurrentLargeIcon(player: Player?, callback: PlayerNotificationManager.BitmapCallback?): Bitmap? {
                return null
            }
        })
        playerNotificationManager?.setPlayer(player)
        playerNotificationManager?.setFastForwardIncrementMs(0)
        playerNotificationManager?.setRewindIncrementMs(0)
        playerNotificationManager?.setUseNavigationActions(false)
        playerNotificationManager?.setUseChronometer(true)

        val icyHttpDataSourceFactory = IcyHttpDataSourceFactory.Builder(BuildConfig.APPLICATION_ID)
                .setIcyHeadersListener { _ ->
                }
                .setIcyMetadataChangeListener { icyMetadata ->
                    currentTitle = icyMetadata.streamTitle
                    val data = Intent(PlayerMetadataBroadcastReceiver.ACTION_NEW_META_DATA)
                    data.putExtra(PlayerMetadataBroadcastReceiver.EXTRA_META_DATA,
                            currentTitle)
                    LocalBroadcastManager.getInstance(this)
                            .sendBroadcast(data)
                    /*
                     * I know this looks weird but I haven't found a way to update
                     * to update the notification aside from changing some data in the
                     * notification manager
                     */
                    playerNotificationManager?.setFastForwardIncrementMs(1000)
                    playerNotificationManager?.setFastForwardIncrementMs(0)
                    playerNotificationManager?.setRewindIncrementMs(0)

                }
                .build()
        val dataSourceFactory = DefaultDataSourceFactory(applicationContext, null, icyHttpDataSourceFactory)
        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .setExtractorsFactory(DefaultExtractorsFactory())
                .createMediaSource(uri)
        player?.addListener(object : Player.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
            }

            override fun onSeekProcessed() {
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
            }

            override fun onLoadingChanged(isLoading: Boolean) {
            }

            override fun onPositionDiscontinuity(reason: Int) {
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                preferencesManager?.setIsPlaying(playWhenReady)
            }
        })
        player?.prepare(mediaSource)
        val intentFilter = IntentFilter()
        intentFilter.addAction(PlayerBroadcastReceiver.ACTION_PAUSE)
        intentFilter.addAction(PlayerBroadcastReceiver.ACTION_PLAY)
        intentFilter.addAction(PlayerBroadcastReceiver.ACTION_STOP)
        player?.playWhenReady = true
        preferencesManager?.setIsPlaying(true)
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(PlayerBroadcastReceiver({
            if (!player!!.playWhenReady) {
                player?.playWhenReady = true
                preferencesManager?.setIsPlaying(true)
            }
        }, {
            if (player!!.playWhenReady) {
                player?.playWhenReady = false
                preferencesManager?.setIsPlaying(false)
            }
        }, {
            clearPlayer()
            stopSelf()
        }), intentFilter)
        playerNotificationManager?.setNotificationListener(object : PlayerNotificationManager.NotificationListener {
            override fun onNotificationCancelled(notificationId: Int) {
                clearPlayer()
                stopSelf()
            }

            override fun onNotificationStarted(notificationId: Int, notification: Notification?) {
                startForeground(notificationId, notification)
            }
        })
        while (isRunning) {

        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        clearPlayer()
        super.onTaskRemoved(rootIntent)
    }

    private fun clearPlayer() {
        player?.release()
        playerNotificationManager?.setPlayer(null)
        isRunning = false
        preferencesManager?.setIsPlaying(false)
    }

    companion object {
        const val PLAYER_NOTIFICATION_CHANNEL_ID = "PLAYER"
        const val NOTIFICATION_ID = 1000
    }
}
