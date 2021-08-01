package com.gadihkaratau.lamejorescancionesreik.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.Nullable;

import com.gadihkaratau.lamejorescancionesreik.R;
import com.gadihkaratau.lamejorescancionesreik.activity.PlayerActivity;
import com.gadihkaratau.lamejorescancionesreik.application.MyApplication;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;
import com.gadihkaratau.lamejorescancionesreik.models.song.SongPlayer;
import com.gadihkaratau.lamejorescancionesreik.utils.Tools;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class AudioPlayerService extends Service {

    public static final String EXTRA_SONG = "song";
    public static final String PLAYBACK_CHANNEL_ID = "playback_channel";
    public static final int PLAYBACK_NOTIFICATION_ID = 1;
    public static final String MEDIA_SESSION_TAG = "audio_demo";
    public static final String EXTRA_SONG_PLAYER = "songPlayer";
    public static final String EXTRA_POSITION_MS = "positionMs";
    public static final String TAG = AudioPlayerService.class.getSimpleName();

    private final IBinder binder = new LocalBinder();
    private SimpleExoPlayer player;
    private Context context;
    private ArrayList<Song> songArrayList;
    private int sizePlaylist;
    private PlayerNotificationManager playerNotificationManager;
    private int position;
    private MediaSessionCompat mediaSession;
    private MediaSessionConnector mediaSessionConnector;

    public ArrayList<Song> getSongArrayList() {
        return songArrayList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                context,
                PLAYBACK_CHANNEL_ID,
                R.string.exo_download_notification_channel_name,
                R.string.exo_controls_play_description,
                PLAYBACK_NOTIFICATION_ID,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        return songArrayList.get(player.getCurrentWindowIndex()).getTitle();
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        Intent intent = new Intent(context, PlayerActivity.class);
                        intent.putExtra(PlayerActivity.RESULT_PENDING_INTENT, songArrayList.get(player.getCurrentWindowIndex()));
                        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player player) {
                        return songArrayList.get(player.getCurrentWindowIndex()).getArtist();
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                        Bitmap icon = Tools.getBitmapFromURL(context, songArrayList.get(player.getCurrentWindowIndex()).getImg());
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

        playerNotificationManager.setPlayer(player);
        playerNotificationManager.setColorized(true);

        mediaSession = new MediaSessionCompat(context, MEDIA_SESSION_TAG);
        mediaSession.setActive(true);
        playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());

        mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setQueueNavigator(new TimelineQueueNavigator(mediaSession) {
            @Override
            public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
                return Tools.getMediaDescription(context, songArrayList.get(windowIndex));
            }
        });
        mediaSessionConnector.setPlayer(player);
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

    private void setPlaylist() {
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();
        for (Song song : songArrayList) {
            MediaSource mediaSource = buildMediaSource(Uri.parse(song.getUrl()));
            concatenatingMediaSource.addMediaSource(mediaSource);
        }
        player.setPlayWhenReady(true);
        player.prepare(concatenatingMediaSource);
    }

    public void startPlayer(int position, long positionMs) {
        player.seekTo(position, positionMs);
    }

    public void resumePlayer() {
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    public void pausePlayer() {
        player.setPlayWhenReady(false);
        player.getPlaybackState();
    }

    public void previousPlayer() {
        position = player.getCurrentWindowIndex() - 1;
        if (position < 0)
            position = sizePlaylist - 1;
        player.seekTo(position, 0);
    }

    public void nextPlayer() {
        position = player.getCurrentWindowIndex() + 1;
        if (position > sizePlaylist - 1)
            position = 0;
        player.seekTo(position, 0);
    }

    public void setShuffleModeEnabled(boolean isShuffle) {
        player.setShuffleModeEnabled(isShuffle);
    }

    @Override
    public void onDestroy() {
        destroyAudio();
        super.onDestroy();
    }

    private void destroyAudio() {
        mediaSession.release();
        mediaSessionConnector.setPlayer(null);
        playerNotificationManager.setPlayer(null);
        player.release();
        player = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        destroyAudio();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Log.d(TAG, "onStartCommand");
            SongPlayer songPlayer = intent.getParcelableExtra(EXTRA_SONG_PLAYER);
            int positionMs = intent.getIntExtra(EXTRA_POSITION_MS, 0);
            if (songPlayer != null) {
                songArrayList = songPlayer.getSongArrayList();
                sizePlaylist = songPlayer.getSongArrayList().size();
                position = songPlayer.getIndexSong();
                setPlaylist();
                startPlayer(position, positionMs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public class LocalBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }


}
