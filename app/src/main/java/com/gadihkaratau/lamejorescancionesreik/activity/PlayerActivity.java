package com.gadihkaratau.lamejorescancionesreik.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gadihkaratau.lamejorescancionesreik.R;
import com.gadihkaratau.lamejorescancionesreik.barqiads.AdsManager;
import com.gadihkaratau.lamejorescancionesreik.models.event.MessageEvent;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;
import com.gadihkaratau.lamejorescancionesreik.services.MusicPlayerService;
import com.gadihkaratau.lamejorescancionesreik.utils.GlobalBus;
import com.gadihkaratau.lamejorescancionesreik.utils.Preferences;
import com.gadihkaratau.lamejorescancionesreik.utils.Tools;
import com.google.android.exoplayer2.util.Log;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.koin.java.KoinJavaComponent;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import kotlin.Lazy;


public class PlayerActivity extends AppCompatActivity {


    public static final String RESULT_PENDING_INTENT = "resultPendingIntent";
    private static final String TAG = "PlayerActivity";

    @SuppressLint("StaticFieldLeak")
    public static Activity playerActivity;
    private final Handler handler = new Handler();
    private final Lazy<AdsManager> adsManager = KoinJavaComponent.inject(AdsManager.class);
    private TextView tvTitle, tvArtist, tvDuration, tvCurrentDuration;
    private Song song;
    private SeekBar seekBarProgress;
    private ImageButton btnPlay;
    private CircularImageView imgSong;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private boolean fromPendingIntent;
    private boolean isPlaying;
    private final Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            setCurrDuration();
            if (isPlaying)
                handler.postDelayed(this, 1000);
        }
    };
    private boolean isBuffering;
    private ProgressBar progressBarLoading;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player2);

        playerActivity = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Tools.setSystemBarColor(this, R.color.grey_1000);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayout llAdView = findViewById(R.id.llAdView);
        adsManager.getValue().initData();
//        adsManager.getValue().showBanner(llAdView);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

//        tvTitle = findViewById(R.id.tvTitle);
//        tvArtist = findViewById(R.id.tvArtist);
        tvDuration = findViewById(R.id.tv_song_total_duration);
        tvCurrentDuration = findViewById(R.id.tv_song_current_duration);
        btnPlay = findViewById(R.id.bt_play);
        seekBarProgress = findViewById(R.id.seek_song_progressbar);
//        imgSong = findViewById(R.id.imgSong);
        ImageButton btnNext = findViewById(R.id.bt_next);
        ImageButton btnPrev = findViewById(R.id.bt_prev);
        btnRepeat = findViewById(R.id.bt_repeat);
        btnShuffle = findViewById(R.id.bt_shuffle);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        progressBarLoading.setVisibility(View.INVISIBLE);

        tvDuration.setText("00:00:00");
        tvCurrentDuration.setText("00:00:00");
        seekBarProgress.setProgress(0);

        song = getIntent().getParcelableExtra(HomeActivity.EXTRA_SONG);
        if (song != null) {
            initComponent(song);
            Log.d(TAG, "init song player");
        }

        fromPendingIntent = false;

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleOnOffSong();
            }
        });

        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long duration = MusicPlayerService.exoPlayer.getDuration();
                    long position = progress * duration / 100;
                    MusicPlayerService.exoPlayer.seekTo(position);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, MusicPlayerService.class);
                intent.setAction(MusicPlayerService.ACTION_PREVIOUS);
                startService(intent);
                adsManager.getValue().showInterstitial();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, MusicPlayerService.class);
                intent.setAction(MusicPlayerService.ACTION_NEXT);
                startService(intent);
                adsManager.getValue().showInterstitial();
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRepeatMode();
            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isShuffle = MusicPlayerService.exoPlayer.getShuffleModeEnabled();
                MusicPlayerService.exoPlayer.setShuffleModeEnabled(!isShuffle);
            }
        });

    }

    private void toggleOnOffSong() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction(MusicPlayerService.ACTION_TOGGLE);
        startService(intent);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventAudioPlayer(MessageEvent messageEvent) {
        if (messageEvent.getName().equals("isPlaying")) {
            boolean isPlaying = messageEvent.isState();
            this.isPlaying = isPlaying;
            setBtnPlay(isPlaying);
            if (isPlaying) {
                mUpdateTimeTask.run();
                rotateImageSong();
                setTotalDuration();
            }
        }
        if (messageEvent.getName().equals("isBuffer")) {
            boolean isBuffer = messageEvent.isState();
            this.isBuffering = isBuffer;
            setBtnBuffering(isBuffering);
        }
        if (messageEvent.getName().equals("onShuffleChanged")) {
            boolean onShuffleModeEnabledChanged = messageEvent.isState();
            setBtnShuffle(onShuffleModeEnabledChanged);
        }
        if (messageEvent.getName().equals("onRepeatModeChanged")) {
            int repeatMode = messageEvent.getMode();
            setBtnRepeat(repeatMode);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSongChange(Song song) {
        this.song = song;
        initComponent(song);
        setTotalDuration();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    protected void onStop() {
        GlobalBus.getBus().unregister(this);
        super.onStop();
    }

    private void initComponent(Song song) {
      /*  tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());
        Glide.with(getApplicationContext())
                .load(song.getImg())
                .placeholder(getResources().getDrawable(R.drawable.photo_male_3))
                .error((getResources().getDrawable(R.drawable.photo_male_3)))
                .into(imgSong);*/
    }

    private void setCurrDuration() {
        long progress = MusicPlayerService.exoPlayer.getCurrentPosition();
        seekBarProgress.setProgress((int) ((progress * 100) / MusicPlayerService.exoPlayer.getDuration()));
        String progressString = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(progress),
                TimeUnit.MILLISECONDS.toMinutes(progress) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(progress)),
                TimeUnit.MILLISECONDS.toSeconds(progress) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progress)));
        tvCurrentDuration.setText(progressString);
    }

    private void setTotalDuration() {
        final long duration = MusicPlayerService.exoPlayer.getDuration();
        String time = duration > 0 ? Tools.convertLongDurationToTimeAudio(duration) : "00:00:00";
        tvDuration.setText(time);
    }

    private void rotateImageSong() {
        if (isPlaying) {
            imgSong.animate().setDuration(100).rotation(imgSong.getRotation() + 1f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rotateImageSong();
                    super.onAnimationEnd(animation);
                }
            });
        }
    }

    private void setRepeatMode() {
        int repeatMode = Preferences.getRepeatKey(this) + 1;
        if (repeatMode > 2)
            repeatMode = 0;
        Preferences.setRepeatKey(this, repeatMode);
        MusicPlayerService.exoPlayer.setRepeatMode(repeatMode);
    }

    private void setBtnPlay(boolean isPlay) {
        if (!isBuffering) {
            if (!isPlay)
                btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow));
            else
                btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
        }
    }

    private void setBtnRepeat(int repeatMode) {
        if (repeatMode == MusicPlayerService.exoPlayer.REPEAT_MODE_ONE) {
            btnRepeat.setImageResource(R.drawable.ic_repeat_one_black_24dp);
            btnRepeat.setColorFilter(getResources().getColor(R.color.grey_10));
        } else {
            btnRepeat.setImageResource(R.drawable.ic_repeat);
            if (repeatMode == MusicPlayerService.exoPlayer.REPEAT_MODE_ALL) {
                btnRepeat.setColorFilter(getResources().getColor(R.color.grey_10));
            } else {
                btnRepeat.setColorFilter(getResources().getColor(R.color.grey_40));
            }
        }
    }

    private void setBtnShuffle(boolean isShuffle) {
        if (isShuffle)
            btnShuffle.setColorFilter(getResources().getColor(R.color.grey_10));
        else
            btnShuffle.setColorFilter(getResources().getColor(R.color.grey_40));
    }

    private void setBtnBuffering(boolean isBuffering) {
        if (isBuffering) {
            btnPlay.setImageResource(R.drawable.ic_close_black_24dp);
            progressBarLoading.setVisibility(View.VISIBLE);
        } else {
            progressBarLoading.setVisibility(View.INVISIBLE);
            if (isPlaying)
                btnPlay.setImageResource(R.drawable.ic_pause);
            else
                btnPlay.setImageResource(R.drawable.ic_play_arrow);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player_activity, menu);
        Tools.changeMenuIconColor(menu, Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (fromPendingIntent) {
                Intent intent = new Intent(PlayerActivity.this, HomeActivity.class);
                intent.setAction(HomeActivity.ACTION_PLAYER_ACTIVITY);
                intent.putExtra(HomeActivity.EXTRA_SONG, song);
                Log.d(TAG, "tidak finish");
                startActivity(intent);
            } else {
                finish();
                Log.d(TAG, "finish");
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(mUpdateTimeTask);
        super.onDestroy();
    }


}
