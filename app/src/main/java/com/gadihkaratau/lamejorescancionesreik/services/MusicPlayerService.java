package com.gadihkaratau.lamejorescancionesreik.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;

import com.gadihkaratau.lamejorescancionesreik.R;
import com.gadihkaratau.lamejorescancionesreik.activity.PlayerActivity;
import com.gadihkaratau.lamejorescancionesreik.application.MyApplication;
import com.gadihkaratau.lamejorescancionesreik.models.event.MessageEvent;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;
import com.gadihkaratau.lamejorescancionesreik.models.song.SongPlayer;
import com.gadihkaratau.lamejorescancionesreik.utils.GlobalBus;
import com.gadihkaratau.lamejorescancionesreik.utils.Tools;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.gadihkaratau.lamejorescancionesreik.services.AudioPlayerService.MEDIA_SESSION_TAG;

public class MusicPlayerService extends IntentService implements Player.EventListener {

    public static final String ACTION_SET_PLAYLIST = "action.ACTION_SET_PLAYLIST";
    public static final String ACTION_TOGGLE = "action.ACTION_TOGGLE";
    public static final String ACTION_PLAY = "action.ACTION_PLAY";
    public static final String ACTION_NEXT = "action.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "action.ACTION_PREVIOUS";
    public static final String INDEX_SONG = "indexSong";
    public static final String EXTRA_PLAYER_SONG = "songPlayer";
    private static final String PLAYBACK_CHANNEL_ID = "playback_channel";
    private static final int PLAYBACK_NOTIFICATION_ID = 1;
    public static ExoPlayer exoPlayer;
    private Context context;
    private PlayerNotificationManager playerNotificationManager;
    private MediaSessionCompat mediaSession;
    private MediaSessionConnector mediaSessionConnector;
    private ArrayList<Song> songArrayList = new ArrayList<>();
    private Song currSongPlay;
    private int positionSong;

    public MusicPlayerService() {
        super("musicPlayerService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        currSongPlay = new Song();
        exoPlayer = new SimpleExoPlayer.Builder(context).build();
        exoPlayer.addListener(this);
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                context,
                PLAYBACK_CHANNEL_ID,
                R.string.exo_download_notification_channel_name,
                R.string.exo_controls_play_description,
                PLAYBACK_NOTIFICATION_ID,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @NotNull
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        positionSong = player.getCurrentWindowIndex();
                        currSongPlay = songArrayList.get(player.getCurrentWindowIndex());
                        GlobalBus.getBus().postSticky(currSongPlay);
                        return currSongPlay.getTitle();
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        Intent intent = new Intent(context, PlayerActivity.class);
                        intent.putExtra(PlayerActivity.RESULT_PENDING_INTENT, currSongPlay);
                        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player player) {
                        return currSongPlay.getArtist();
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                        Bitmap icon = Tools.getBitmapFromURL(context, currSongPlay.getImg());
                        if (icon != null)
                            return icon;
                        return BitmapFactory.decodeResource(context.getResources(), R.drawable.image_3);
                    }
                },
                new PlayerNotificationManager.NotificationListener() {
                    @Override
                    public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                        startForeground(notificationId, notification);
                    }

                    @Override
                    public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                        stopSelf();
                    }
                }
        );

        playerNotificationManager.setPlayer(exoPlayer);
        playerNotificationManager.setColorized(true);

        mediaSession = new MediaSessionCompat(context, MEDIA_SESSION_TAG);
        mediaSession.setActive(true);
        playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());

        mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setQueueNavigator(new TimelineQueueNavigator(mediaSession) {
            @NotNull
            @Override
            public MediaDescriptionCompat getMediaDescription(@NotNull Player player, int windowIndex) {
                return Tools.getMediaDescription(context, currSongPlay);
            }
        });
        mediaSessionConnector.setPlayer(exoPlayer);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        String action = intent != null ? intent.getAction() : null;
        if (action != null) {
            switch (action) {
                case ACTION_SET_PLAYLIST:
                    SongPlayer songPlayer = intent.getParcelableExtra(EXTRA_PLAYER_SONG);
                    if (songPlayer != null)
                        setPlaylist(songPlayer);
                    break;
                case ACTION_PLAY:
                    int indexSong = intent.getIntExtra(INDEX_SONG, 0);
                    startPlayer(indexSong, 0);
                    break;
                case ACTION_TOGGLE:
                    playPausePlayer();
                    break;
                case ACTION_PREVIOUS:
                    previousPlayer();
                    break;
                case ACTION_NEXT:
                    nextPlayer();
                    break;
            }
        }
        return START_STICKY;
    }

    private MediaSource buildMediaSource(Uri uri) {
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, getString(R.string.app_name)));
        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(
                ((MyApplication) getApplication()).getDownloadCache(),
                dataSourceFactory,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
        return new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(uri);
    }

    private void setPlaylist(SongPlayer songPlayer) {
        songArrayList = songPlayer.getSongArrayList();
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();
        for (Song song : songPlayer.getSongArrayList()) {
            MediaSource mediaSource = buildMediaSource(Uri.parse(song.getUrl()));
            concatenatingMediaSource.addMediaSource(mediaSource);
        }
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.prepare(concatenatingMediaSource);
        startPlayer(songPlayer.getIndexSong(), 0);
    }

    public void startPlayer(int position, long positionMs) {
        GlobalBus.getBus().postSticky(new MessageEvent("isBuffer", true));
        if (!exoPlayer.getPlayWhenReady())
            exoPlayer.setPlayWhenReady(true);
        exoPlayer.seekTo(position, positionMs);
    }

    private void playPausePlayer() {
        boolean isPlaying = !exoPlayer.getPlayWhenReady();
        exoPlayer.setPlayWhenReady(isPlaying);
    }

    private void nextPlayer() {
        if (positionSong == songArrayList.size() - 1)
            positionSong = 0;
        else
            positionSong++;
        exoPlayer.seekTo(positionSong, 0);
    }

    private void previousPlayer() {
        if (positionSong == 0)
            positionSong = songArrayList.size() - 1;
        else
            positionSong--;
        exoPlayer.seekTo(positionSong, 0);
    }


    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        GlobalBus.getBus().postSticky(new MessageEvent("isPlaying", isPlaying));
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playWhenReady && playbackState == Player.STATE_READY)
            GlobalBus.getBus().postSticky(new MessageEvent("isBuffer", false));
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        GlobalBus.getBus().postSticky(new MessageEvent("onShuffleChanged", shuffleModeEnabled));
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
        GlobalBus.getBus().postSticky(new MessageEvent("onRepeatModeChanged", repeatMode));
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        if (positionSong < songArrayList.size() - 1)
            nextPlayer();

    }

    @Override
    public void onDestroy() {
        mediaSession.release();
        mediaSessionConnector.setPlayer(null);
        playerNotificationManager.setPlayer(null);
        exoPlayer.stop();
        exoPlayer.release();
        super.onDestroy();
    }
}
