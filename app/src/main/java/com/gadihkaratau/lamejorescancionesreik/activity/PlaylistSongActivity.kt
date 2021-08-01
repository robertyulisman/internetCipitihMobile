package com.gadihkaratau.lamejorescancionesreik.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.ButterKnife
import butterknife.OnClick
import com.gadihkaratau.lamejorescancionesreik.R
import com.gadihkaratau.lamejorescancionesreik.adapters.ListSongPlaylistAdaper
import com.gadihkaratau.lamejorescancionesreik.barqiads.AdsManager
import com.gadihkaratau.lamejorescancionesreik.db.tabel.FavoriteSongHelper
import com.gadihkaratau.lamejorescancionesreik.db.tabel.PlaylistSongHelper
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnDialogCallback
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnPopMenuSongPlaylistListener
import com.gadihkaratau.lamejorescancionesreik.models.event.MessageEvent
import com.gadihkaratau.lamejorescancionesreik.models.playlist.GridPlaylist
import com.gadihkaratau.lamejorescancionesreik.models.song.Song
import com.gadihkaratau.lamejorescancionesreik.models.song.SongBuffPlay
import com.gadihkaratau.lamejorescancionesreik.models.song.SongPlayer
import com.gadihkaratau.lamejorescancionesreik.services.MusicPlayerService
import com.gadihkaratau.lamejorescancionesreik.utils.GlobalBus
import com.gadihkaratau.lamejorescancionesreik.utils.Tools
import kotlinx.android.synthetic.main.activity_playlist_song.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.java.KoinJavaComponent

class PlaylistSongActivity : AppCompatActivity() {
    private val adsManager = KoinJavaComponent.inject(AdsManager::class.java)

    var progressBarSongMiniPlayer: ProgressBar? = null
    private var listSongPlaylistAdaper: ListSongPlaylistAdaper? = null
    private var isFirstPlay = false
    private var songBuffPlay: SongBuffPlay? = null
    private val handler = Handler()
    private var context: Context? = null
    private var alertDialog: AlertDialog.Builder? = null
    private val mUpdateTimeTask: Runnable = object : Runnable {
        override fun run() {
            updateTimerAndSeekBar()
            if (songBuffPlay!!.isPlaying) handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_song)
        ButterKnife.bind(this)
        context = this
        adsManager.value.initData()
        adsManager.value.showBanner(llAdView, mopubBanner, startAppBanner);
        alertDialog = AlertDialog.Builder(context)
        val gridPlaylist: GridPlaylist? = intent.getParcelableExtra(EXTRA_GRID_PLAYLIST)
        songBuffPlay = SongBuffPlay(false, false)

        supportActionBar?.apply {
            title = gridPlaylist?.namePlaylist?.name
            setDisplayHomeAsUpEnabled(true)
        }


        PlaylistSongHelper.getInstance(context).open()
        listSongPlaylistAdaper = ListSongPlaylistAdaper()
        recyclerView.adapter = listSongPlaylistAdaper
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        listSongPlaylistAdaper?.songArrayList = gridPlaylist?.songArrayList
        listSongPlaylistAdaper?.setOnSongClickAdapterListener { songPlayer ->
            if (!isFirstPlay) {
                setPlaylistSongAndPlay(songPlayer)
                isFirstPlay = true
                isPlaylistPlaying = true
            } else playSongByIndex(songPlayer.indexSong)
            setMiniPlayer(songPlayer.songArrayList[songPlayer.indexSong])
            adsManager.value.showInterstitial()
        }
        listSongPlaylistAdaper!!.setOnPopMenuSongPlaylistListener(object : OnPopMenuSongPlaylistListener {
            override fun onRemoveFromPlaylist(song: Song) {
                val result = PlaylistSongHelper.getInstance(context).deleteById(song.id).toLong()
                if (result > 0) {
                    Toast.makeText(context, getString(R.string.msg_success_delete_from_playlist, song.title), Toast.LENGTH_SHORT).show()
                    val position = listSongPlaylistAdaper!!.songArrayList.indexOf(song)
                    listSongPlaylistAdaper!!.removeSong(song, position)
                } else Toast.makeText(context, getString(R.string.msg_failed_delete_from_playlist, song.title), Toast.LENGTH_SHORT).show()
            }

            override fun onAddToFavorite(song: Song) {
                Tools.showAlertDialog(alertDialog, getString(R.string.confirmation), getString(R.string.msg_add_to_favorite, song.title), object : OnDialogCallback {
                    override fun onNoClicked() {}
                    override fun onOkClicked() {
                        val isExist = FavoriteSongHelper.getInstance(context).isExist(song.id)
                        if (!isExist) {
                            val resultInsert = FavoriteSongHelper.getInstance(context).insert(song)
                            if (resultInsert > 0) {
                                Toast.makeText(context, getString(R.string.msg_success_add_to_favorite, song.title), Toast.LENGTH_SHORT).show()
                            } else Toast.makeText(context, getString(R.string.failed_delete_playlist, song.title), Toast.LENGTH_SHORT).show()
                        } else Toast.makeText(context, getString(R.string.msg_song_ready_on_favorite, song.title), Toast.LENGTH_SHORT).show()
                    }
                })
            }
        })
        player_control!!.visibility = View.GONE
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

    private fun updateTimerAndSeekBar() {
        val progress = MusicPlayerService.exoPlayer.currentPosition
        progressBarSongMiniPlayer!!.progress = (progress * 100 / MusicPlayerService.exoPlayer.duration).toInt()
    }

    private fun setToggleBtnPlay(isPlay: Boolean) {
        if (isPlay) bt_playMiniPlayer!!.setImageResource(R.drawable.ic_pause) else bt_playMiniPlayer!!.setImageResource(R.drawable.ic_play_arrow)
    }

    @OnClick(R.id.bt_playMiniPlayer)
    fun toggleOnOffSong() {
        val intent = Intent(this, MusicPlayerService::class.java)
        intent.action = MusicPlayerService.ACTION_TOGGLE
        startService(intent)
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

    @OnClick(R.id.player_control)
    fun gotoPlayerActivity() {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(HomeActivity.EXTRA_SONG, song)
        startActivity(intent)
    }

    private fun setMiniPlayer(song: Song?) {
        if (song != null) {
            if (player_control!!.visibility != View.VISIBLE) player_control!!.visibility = View.VISIBLE
            tvTitleMiniPlayer!!.text = song.title
            tvArtistMiniPlayer!!.text = song.artist
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun eventAudioPlayer(messageEvent: MessageEvent) {
        if (isPlaylistPlaying) {
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
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onSongChange(song: Song?) {
        if (isPlaylistPlaying) {
            setMiniPlayer(song)
            Companion.song = song
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        handler.removeCallbacks(mUpdateTimeTask)
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        GlobalBus.getBus().register(this)
    }

    override fun onStop() {
        GlobalBus.getBus().unregister(this)
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_playlist_activity, menu)
        val actionSearch = menu.findItem(R.id.actionSearch)
        val searchView = actionSearch.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                listSongPlaylistAdaper!!.filter.filter(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    companion object {
        const val EXTRA_GRID_PLAYLIST = "gridPlaylist"
        var isPlaylistPlaying = false
        var song: Song? = null
    }
}