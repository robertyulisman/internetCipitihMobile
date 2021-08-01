package com.gadihkaratau.lamejorescancionesreik.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import androidx.viewpager.widget.ViewPager
import butterknife.ButterKnife
import com.gadihkaratau.lamejorescancionesreik.R
import com.gadihkaratau.lamejorescancionesreik.adapters.SectionsPagerAdapter
import com.gadihkaratau.lamejorescancionesreik.barqiads.AdsManager
import com.gadihkaratau.lamejorescancionesreik.barqiads.ConfigAds
import com.gadihkaratau.lamejorescancionesreik.db.tabel.FavoriteSongHelper
import com.gadihkaratau.lamejorescancionesreik.db.tabel.NamePlaylistHelper
import com.gadihkaratau.lamejorescancionesreik.db.tabel.PlaylistSongHelper
import com.gadihkaratau.lamejorescancionesreik.fragment.FavoriteFragment
import com.gadihkaratau.lamejorescancionesreik.fragment.FavoriteFragment.OnFavoriteFragmentListener
import com.gadihkaratau.lamejorescancionesreik.fragment.LocalFragment
import com.gadihkaratau.lamejorescancionesreik.fragment.PlaylistFragment
import com.gadihkaratau.lamejorescancionesreik.fragment.SongFragment
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnReloadPlaylistListener
import com.gadihkaratau.lamejorescancionesreik.models.event.MessageEvent
import com.gadihkaratau.lamejorescancionesreik.models.song.Song
import com.gadihkaratau.lamejorescancionesreik.models.song.SongBuffPlay
import com.gadihkaratau.lamejorescancionesreik.models.song.SongPlayer
import com.gadihkaratau.lamejorescancionesreik.services.MusicPlayerService
import com.gadihkaratau.lamejorescancionesreik.utils.GlobalBus
import com.gadihkaratau.lamejorescancionesreik.utils.Preferences
import com.gadihkaratau.lamejorescancionesreik.utils.Tools
import com.gadihkaratau.lamejorescancionesreik.utils.toTextHtml
import com.gadihkaratau.lamejorescancionesreik.viewModel.MainViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
//import com.google.firebase.analytics.FirebaseAnalytics
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_home.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.alert
import org.koin.java.KoinJavaComponent

class HomeActivity : AppCompatActivity(), View.OnClickListener, OnReloadPlaylistListener, OnFavoriteFragmentListener {

    private val adsManager = KoinJavaComponent.inject(AdsManager::class.java)
    private var positionTabFragment = 0
    private var positionSongPlayingFragment = 0
    private var isPlaylistFirstInit = false
    private val handler = Handler()
    private var songFragment: SongFragment? = null
    private var favoriteFragment: FavoriteFragment? = null
    private var playlistFragment: PlaylistFragment? = null
    private var localFragment: LocalFragment? = null
    private var song: Song? = null
    private var songBuffPlay: SongBuffPlay? = null
    private var mainViewModel: MainViewModel? = null
    private var songArrayList = ArrayList<Song>()
    private val ONESIGNAL_APP_ID = "1f167f65-d846-4954-80b1-29d9c13bf511"
//    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    private val mUpdateTimeTask: Runnable = object : Runnable {
        override fun run() {
            updateTimerAndSeekBar()
            if (songBuffPlay!!.isPlaying) handler.postDelayed(this, 1000)
        }
    }
    private var tempId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.subtitle = getString(R.string.subtile)

        showGuidePlayMusic()

        Tools.setSystemBarColor(this)
        openDataBase()
        adsManager.value.initData()
        adsManager.value.showBanner(llAdView, mopubBanner, startAppBanner);
        songFragment = SongFragment()
        favoriteFragment = FavoriteFragment()
        playlistFragment = PlaylistFragment()
        localFragment = LocalFragment()
        try {
            songArrayList = ArrayList(intent.getParcelableArrayListExtra(EXTRA_ARRAY_LIST_SONG))
            val bundle = Bundle()
            bundle.putParcelableArrayList(EXTRA_ARRAY_LIST_SONG, songArrayList)
            songFragment?.arguments = bundle

        } catch (e: Exception) {
            e.printStackTrace()
        }

        songFragment?.setOnReloadPlaylistListener(this)
        songFragment?.setOnFavoriteFragmentListener(this)
        playlistFragment?.setOnReloadPlaylistListener(this)
        favoriteFragment?.setOnFavoriteFragmentListener(this)
        favoriteFragment?.setOnReloadPlaylistListener(this)
        localFragment?.setOnFavoriteFragmentListener(this)
        localFragment?.setOnReloadPlaylistListener(this)

//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

// Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
        tab_layout.setupWithViewPager(view_pager)
        setUpViewPager(view_pager)
        view_pager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tab_layout))
        tab_layout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                positionTabFragment = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        player_control.visibility = View.GONE

        //handle intent from PlayerActivity
        val action = intent.action
        action?.let {
            val bundle = intent.extras
            if (bundle != null) {
                val song = intent.extras?.getParcelable<Song>(EXTRA_SONG)
                setMiniPlayer(song)
            }
        }
        songBuffPlay = SongBuffPlay(false, false)
        if (savedInstanceState != null) {
            song = savedInstanceState.getParcelable(EXTRA_SONG)
            songBuffPlay = savedInstanceState.getParcelable(EXTRA_SONG_BUFF_PLAY)
            setMiniPlayer(song)
            setOnOffBuffering(songBuffPlay!!.isBuffering)
            setToggleBtnPlay(songBuffPlay!!.isPlaying)
        }
        mainViewModel = ViewModelProvider(this, NewInstanceFactory()).get(MainViewModel::class.java)
    }

    private fun openDataBase() {
        NamePlaylistHelper.getInstance(this).open()
        PlaylistSongHelper.getInstance(this).open()
        FavoriteSongHelper.getInstance(this).open()
    }

    private fun updateTimerAndSeekBar() {
        val progress = MusicPlayerService.exoPlayer.currentPosition
        progressBarSongMiniPlayer!!.progress = (progress * 100 / MusicPlayerService.exoPlayer.duration).toInt()
    }

    private fun setToggleBtnPlay(isPlay: Boolean) {
        if (isPlay) bt_playMiniPlayer!!.setImageResource(R.drawable.ic_pause) else bt_playMiniPlayer!!.setImageResource(R.drawable.ic_play_arrow)
    }

    private fun setOnOffBuffering(isBuffering: Boolean) {
        if (isBuffering) {
            progressBarLoadingMiniPlayer!!.visibility = View.VISIBLE
            bt_playMiniPlayer!!.visibility = View.INVISIBLE
        } else {
            progressBarLoadingMiniPlayer!!.visibility = View.INVISIBLE
            bt_playMiniPlayer!!.visibility = View.VISIBLE
        }
    }

    private fun setUpViewPager(viewPager: ViewPager?) {
        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        sectionsPagerAdapter.addFragment(songFragment, resources.getString(R.string.song))
        sectionsPagerAdapter.addFragment(favoriteFragment, resources.getString(R.string.favorites))
        sectionsPagerAdapter.addFragment(playlistFragment, resources.getString(R.string.playlist))
        sectionsPagerAdapter.addFragment(localFragment, resources.getString(R.string.library))
        viewPager!!.offscreenPageLimit = sectionsPagerAdapter.count
        viewPager.adapter = sectionsPagerAdapter
    }

    private fun setMiniPlayer(song: Song?) {
        if (song != null) {
            this.song = song
            if (player_control!!.visibility != View.VISIBLE) player_control!!.visibility = View.VISIBLE
            tvTitleMiniPlayer!!.text = song.title
            tvArtistMiniPlayer!!.text = song.artist
        }
    }

    private fun gotoPlayerActivity() {
        val intent = Intent(this, PlayerNewActivity::class.java)
        intent.putExtra(EXTRA_SONG, song)
        startActivityForResult(intent, EXTRA_REQUEST_PLAYER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EXTRA_REQUEST_PLAYER && resultCode == 2)
            adsManager.value.showBanner(llAdView, mopubBanner, startAppBanner);
    }

    fun playSongItem(songPlayer: SongPlayer) {
        try {
            setMiniPlayer(songPlayer.songArrayList[songPlayer.indexSong])
            if (positionSongPlayingFragment == positionTabFragment && isPlaylistFirstInit && !PlaylistSongActivity.isPlaylistPlaying) playSongByIndex(songPlayer.indexSong) else {
                isPlaylistFirstInit = true
                positionSongPlayingFragment = positionTabFragment
                setPlaylistSongAndPlay(songPlayer)
            }
            if (PlaylistSongActivity.isPlaylistPlaying) PlaylistSongActivity.isPlaylistPlaying = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setPlaylistSongAndPlay(songPlayer: SongPlayer) {
        val intent = Intent(this, MusicPlayerService::class.java)
        intent.action = MusicPlayerService.ACTION_SET_PLAYLIST
        intent.putExtra(MusicPlayerService.EXTRA_PLAYER_SONG, songPlayer)
        startService(intent)
    }

    private fun playSongByIndex(index: Int) {
        val intent = Intent(this, MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.INDEX_SONG, index)
        intent.action = MusicPlayerService.ACTION_PLAY
        startService(intent)
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
            songBuffPlay!!.isPlaying = isPlaying
            setToggleBtnPlay(isPlaying)
            mUpdateTimeTask.run()
            if (isPlaying) setOnOffBuffering(false)
        }
        if (messageEvent.name == "isBuffer") {
            val isBuffer = messageEvent.isState
            songBuffPlay!!.isBuffering = isBuffer
            setOnOffBuffering(isBuffer)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onSongChange(song: Song) {
        this.song = song
        setMiniPlayer(song)
        if (song.id != tempId) {
            tempId = song.id
            mainViewModel!!.setCounterViewMusic(song.id)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(EXTRA_SONG, song)
        outState.putParcelable(EXTRA_SONG_BUFF_PLAY, songBuffPlay)
    }

    override fun onBackPressed() {}
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        val actionSearch = menu.findItem(R.id.actionSearch)
        val searchView = actionSearch.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                when (positionTabFragment) {
                    0 -> songFragment?.listMusicAdapter?.filter?.filter(newText)
                    1 -> favoriteFragment?.listSongFavoriteAdapter?.filter?.filter(newText)
                    2 -> playlistFragment?.listGridSongPlaylistAdapter?.filter?.filter(newText)
                    3 -> localFragment?.listSongAdapter?.filter?.filter(newText)
                }
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionChangeLanguage -> {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
            }
            R.id.actionShare -> Tools.shareApp(this)
            R.id.actionRateApp -> Tools.rateAction(this)
            R.id.actionPrivacyPolicy -> showPrivacyPolicy()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showPrivacyPolicy() {
        ConfigAds.privacyPolicyApp.toTextHtml()?.let {
            alert(it, getString(R.string.privacy_policy)) {
                positiveButton("OK") {}
            }.show()
        }
    }


    private fun showGuidePlayMusic() {
        if (!Preferences.geHasGuideAudio(this))
            getString(R.string.msg_guide_play).toTextHtml()?.let {
                alert(it, getString(R.string.info)) {
                    positiveButton("OK") {
                        Preferences.setHasGuideAudio(this@HomeActivity, true)
                    }
                    isCancelable = false
                }.show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(mUpdateTimeTask)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_playMiniPlayer -> toggleOnOffSong()
            R.id.player_control -> gotoPlayerActivity()
        }
    }

    override fun onStart() {
        super.onStart()
        GlobalBus.getBus().register(this)
    }

    override fun onStop() {
        GlobalBus.getBus().unregister(this)
        super.onStop()
    }

    fun showInt() {
        adsManager.value.showInterstitial()
    }

    override fun onReloadPlaylist() {
        playlistFragment!!.loadGridPlaylist()
    }

    override fun refreshFavoriteFragment() {
        favoriteFragment!!.loadFavoriteFragment()
    }

    companion object {
        private val TAG = HomeActivity::class.java.simpleName

        @JvmField
        var EXTRA_SONG = "extraSong"

        @JvmField
        var EXTRA_ARRAY_LIST_SONG = "extraArrayListSong"
        var EXTRA_REQUEST_PLAYER = 1
        var EXTRA_SONG_BUFF_PLAY = "extraSongBuffPlay"

        @JvmField
        var ACTION_PLAYER_ACTIVITY = "actionPlayerActivity"
    
    }

}