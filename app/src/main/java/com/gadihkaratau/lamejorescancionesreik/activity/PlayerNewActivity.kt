package com.gadihkaratau.lamejorescancionesreik.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.gadihkaratau.lamejorescancionesreik.R
import com.gadihkaratau.lamejorescancionesreik.adapters.SectionsPagerAdapter
import com.gadihkaratau.lamejorescancionesreik.barqiads.AdsManager
import com.gadihkaratau.lamejorescancionesreik.barqiads.ConfigAds
import com.gadihkaratau.lamejorescancionesreik.fragment.LyricPlayerFragment
import com.gadihkaratau.lamejorescancionesreik.fragment.ThumbnailPlayerFragment
import com.gadihkaratau.lamejorescancionesreik.models.event.MessageEvent
import com.gadihkaratau.lamejorescancionesreik.models.song.Song
import com.gadihkaratau.lamejorescancionesreik.services.MusicPlayerService
import com.gadihkaratau.lamejorescancionesreik.utils.*
import com.google.android.exoplayer2.Player
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import kotlinx.android.synthetic.main.activity_player_new.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alert
import org.jetbrains.anko.imageResource
import org.koin.java.KoinJavaComponent.inject
import java.util.*
import java.util.concurrent.TimeUnit

class PlayerNewActivity : AppCompatActivity(), AnkoLogger {

    private var isBuffering = false
    private var isPlaying = false
    private var song: Song? = null
    private val handler = Handler()
    private val mUpdateTimeTask: Runnable = object : Runnable {
        override fun run() {
            setCurrDuration()
            if (isPlaying) handler.postDelayed(this, 1000)
        }
    }

    private val adsManager = inject(AdsManager::class.java)
    private var thumbnailPlayerFragment: ThumbnailPlayerFragment? = null
    private var lyricPlayerFragment: LyricPlayerFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_new)

        initToolbar()
        initData()
        setUpViewPager()
        resetDefaultData()
        initListener()
        showGuide()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        Tools.setSystemBarColor(this, R.color.grey_1000)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initData() {
        adsManager.value.initData()
        adsManager.value.showBanner(llAdView, mopubBanner, startAppBanner)
        song = intent.getParcelableExtra(HomeActivity.EXTRA_SONG)
        song?.let {
            thumbnailPlayerFragment = ThumbnailPlayerFragment.newInstance(it)
            lyricPlayerFragment = LyricPlayerFragment.newInstance(it.lyric)
        }
    }

    private fun resetDefaultData() {
        tv_song_total_duration.text = getString(R.string.default_duration)
        tv_song_current_duration.text = getString(R.string.default_duration)
        seek_song_progressbar.progress = 0
        progressBarLoading.invisible()
    }

    private fun initListener() {
        bt_play.setOnClickListener {
            toggleOnOffSong()
        }
        seek_song_progressbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = MusicPlayerService.exoPlayer.duration
                    val position = progress * duration / 100
                    MusicPlayerService.exoPlayer.seekTo(position)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        bt_prev.setOnClickListener {
            val intent = Intent(this, MusicPlayerService::class.java)
            intent.action = MusicPlayerService.ACTION_PREVIOUS
            startService(intent)
            adsManager.value.showInterstitial()
        }
        bt_next.setOnClickListener {
            val intent = Intent(this, MusicPlayerService::class.java)
            intent.action = MusicPlayerService.ACTION_NEXT
            startService(intent)
            adsManager.value.showInterstitial()
        }
        bt_repeat.setOnClickListener {
            setRepeatMode()
        }
        bt_shuffle.setOnClickListener {
            val isShuffle = MusicPlayerService.exoPlayer.shuffleModeEnabled
            MusicPlayerService.exoPlayer.shuffleModeEnabled = !isShuffle
        }
    }

    private fun setUpViewPager() {
        tabLayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        val sectionPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        with(sectionPagerAdapter) {
            addFragment(thumbnailPlayerFragment, "")
            addFragment(lyricPlayerFragment, "")
            viewPager.apply {
                offscreenPageLimit = count
                adapter = sectionPagerAdapter
            }
        }
    }

    private fun toggleOnOffSong() {
        val intent = Intent(this, MusicPlayerService::class.java)
        intent.action = MusicPlayerService.ACTION_TOGGLE
        startService(intent)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun eventAudioPlayer(messageEvent: MessageEvent) {
        if (messageEvent.name == "isPlaying") {
            val isPlaying = messageEvent.isState
            this.isPlaying = isPlaying
            setBtnPlay(isPlaying)
            if (isPlaying) {
                mUpdateTimeTask.run()
                setTotalDuration()
            }
        }
        if (messageEvent.name == "isBuffer") {
            val isBuffer = messageEvent.isState
            isBuffering = isBuffer
            setBtnBuffering(isBuffering)
        }
        if (messageEvent.name == "onShuffleChanged") {
            val onShuffleModeEnabledChanged = messageEvent.isState
            setBtnShuffle(onShuffleModeEnabledChanged)
        }
        if (messageEvent.name == "onRepeatModeChanged") {
            val repeatMode = messageEvent.mode
            setBtnRepeat(repeatMode)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onSongChange(song: Song?) {
        this.song = song
        initDataSong(song)
        setTotalDuration()
    }

    override fun onStart() {
        super.onStart()
        GlobalBus.getBus().register(this)
    }

    override fun onStop() {
        GlobalBus.getBus().unregister(this)
        super.onStop()
    }

    private fun initDataSong(song: Song?) {
        song?.let {
            thumbnailPlayerFragment?.setData(it)
            lyricPlayerFragment?.resetLyric(it.lyric)
        }
    }

    private fun setCurrDuration() {
        val progress = MusicPlayerService.exoPlayer.currentPosition
        seek_song_progressbar.progress = (progress * 100 / MusicPlayerService.exoPlayer.duration).toInt()
        val progressString = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(progress),
                TimeUnit.MILLISECONDS.toMinutes(progress) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(progress)),
                TimeUnit.MILLISECONDS.toSeconds(progress) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progress)))
        tv_song_current_duration.text = progressString
    }

    private fun setTotalDuration() {
        val duration = MusicPlayerService.exoPlayer.duration
        val time = if (duration > 0) Tools.convertLongDurationToTimeAudio(duration) else getString(R.string.default_duration)
        tv_song_total_duration.text = time
    }

    private fun setRepeatMode() {
        var repeatMode = Preferences.getRepeatKey(this) + 1
        if (repeatMode > 2) repeatMode = 0
        Preferences.setRepeatKey(this, repeatMode)
        MusicPlayerService.exoPlayer.repeatMode = repeatMode
    }

    private fun setBtnPlay(isPlay: Boolean) {
        bt_play.imageResource = if (!isPlay) R.drawable.ic_play_arrow else R.drawable.ic_pause
    }

    private fun setBtnRepeat(repeatMode: Int) {
        bt_repeat?.apply {
            if (repeatMode == Player.REPEAT_MODE_ONE) {
                imageResource = R.drawable.ic_repeat_one_black_24dp
                setColorFilter(resources.getColor(R.color.grey_10))
            } else {
                imageResource = R.drawable.ic_repeat
                if (repeatMode == Player.REPEAT_MODE_ALL) {
                    setColorFilter(resources.getColor(R.color.grey_10))
                } else {
                    setColorFilter(resources.getColor(R.color.grey_40))
                }
            }
        }
    }

    private fun setBtnShuffle(isShuffle: Boolean) {
        bt_shuffle.setColorFilter(if (isShuffle) resources.getColor(R.color.grey_10) else resources.getColor(R.color.grey_40))
    }

    private fun setBtnBuffering(isBuffering: Boolean) {
        bt_play.apply {
            if (isBuffering) {
                setImageResource(R.drawable.ic_close_black_24dp)
                progressBarLoading.visible()
            } else {
                progressBarLoading.invisible()
                if (isPlaying) setImageResource(R.drawable.ic_pause) else setImageResource(R.drawable.ic_play_arrow)
            }
        }
    }

    private fun showGuide() {
        if (!Preferences.getIsOkGuideLyric(this))
            alert(getString(R.string.msg_guide_lyric), getString(R.string.info)) {
                isCancelable = false
                positiveButton(getString(R.string.ok)) {
                    GlobalScope.launch {
                        Preferences.setIsOkGuideLyric(this@PlayerNewActivity, true)
                    }
                }
            }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_player_activity, menu)
        Tools.changeMenuIconColor(menu, Color.WHITE)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                setResult(ConfigAds.modeAds, Intent())
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        handler.removeCallbacks(mUpdateTimeTask)
        super.onDestroy()
    }
}