package com.gadihkaratau.lamejorescancionesreik.fragment;


import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gadihkaratau.lamejorescancionesreik.BuildConfig;
import com.gadihkaratau.lamejorescancionesreik.R;
import com.gadihkaratau.lamejorescancionesreik.activity.HomeActivity;
import com.gadihkaratau.lamejorescancionesreik.adapters.ListSongAdapter;
import com.gadihkaratau.lamejorescancionesreik.barqiads.ConfigAds;
import com.gadihkaratau.lamejorescancionesreik.db.tabel.FavoriteSongHelper;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnAddPlaylistCallback;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnDialogCallback;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnPopMenuSongListener;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnReloadPlaylistListener;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnSongClickAdapterListener;
import com.gadihkaratau.lamejorescancionesreik.models.infoApp.InfoApp;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;
import com.gadihkaratau.lamejorescancionesreik.models.song.SongPlayer;
import com.gadihkaratau.lamejorescancionesreik.utils.Tools;
import com.gadihkaratau.lamejorescancionesreik.viewModel.MainViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongFragment extends Fragment {

    private final String TAG = SongFragment.class.getSimpleName();
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private Unbinder unbinder;
    private OnReloadPlaylistListener onReloadPlaylistListener;
    private FavoriteFragment.OnFavoriteFragmentListener onFavoriteFragmentListener;
    private ListSongAdapter listMusicAdapter;
    private MainViewModel mainViewModel;
    private HomeActivity homeActivity;
    private AlertDialog.Builder alertDialog;


    public SongFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        progressBar.setVisibility(View.VISIBLE);
        alertDialog = new AlertDialog.Builder(getContext());

        listMusicAdapter = new ListSongAdapter();
        recyclerView.setAdapter(listMusicAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        homeActivity = (HomeActivity) getActivity();
        mainViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);

        try {
            if (getArguments() != null) {
                ArrayList<Song> songArrayList = getArguments().getParcelableArrayList(HomeActivity.EXTRA_ARRAY_LIST_SONG);
                listMusicAdapter.setSongArrayList(songArrayList);
                progressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "ini " + e.getMessage());
            e.printStackTrace();
        }

        setDialogUpdateAndRedirect();

        listMusicAdapter.setOnSongClickAdapterListener(new OnSongClickAdapterListener() {
            @Override
            public void onAdapterClicked(SongPlayer songPlayer) {
                try {
                    homeActivity.playSongItem(songPlayer);
                    homeActivity.showInt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        listMusicAdapter.setOnPopMenuSongListener(new OnPopMenuSongListener() {
            @Override
            public void onAddToPlaylist(Song song) {
                Tools.showDialogPlaylist(getContext(), song, new OnAddPlaylistCallback() {
                    @Override
                    public void onSuccessAddPlaylist(Song song) {
                        onReloadPlaylistListener.onReloadPlaylist();
                    }
                });
            }

            @Override
            public void onAddToFavorite(Song song) {
                Tools.showAlertDialog(alertDialog, getString(R.string.confirmation), getString(R.string.msg_add_to_favorite, song.getTitle()), new OnDialogCallback() {
                    @Override
                    public void onNoClicked() {

                    }

                    @Override
                    public void onOkClicked() {
                        boolean isExist = FavoriteSongHelper.getInstance(getContext()).isExist(song.getId());
                        if (!isExist) {
                            long resultInsert = FavoriteSongHelper.getInstance(getContext()).insert(song);
                            if (resultInsert > 0) {
                                Toast.makeText(getContext(), getString(R.string.msg_success_add_to_favorite, song.getTitle()), Toast.LENGTH_SHORT).show();
                                onFavoriteFragmentListener.refreshFavoriteFragment();
                            } else
                                Toast.makeText(getContext(), getString(R.string.failed_delete_playlist, song.getTitle()), Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getContext(), getString(R.string.msg_song_ready_on_favorite, song.getTitle()), Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onDownload(Song song) {

            }
        });


    }

    private void setDialogUpdateAndRedirect() {

        mainViewModel.setInfoAppMutableLiveData(BuildConfig.APPLICATION_ID);
        mainViewModel.getInfoAppMutableLiveData().observe(this, new Observer<InfoApp>() {
            @Override
            public void onChanged(InfoApp infoApp) {
                if (infoApp != null) {

                    try {

                        double currVersion = Double.parseDouble(BuildConfig.VERSION_NAME);
                        double storeVersion = Double.parseDouble(infoApp.getVersion());

                        //Date date = new Date(infoApp.getUpdated());
                        //String dateUpdate = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.getDefault()).format(date);

                        if (storeVersion > currVersion) {
                            Tools.showAlertDialog(alertDialog, getString(R.string.update_info), getString(R.string.msg_update_app, getString(R.string.app_name)) + "<br/><br/>Recent Changes<br/>" + infoApp.getRecentChanges(), getString(R.string.update_now), getString(R.string.update_later), new OnDialogCallback() {
                                @Override
                                public void onNoClicked() {

                                }

                                @Override
                                public void onOkClicked() {
                                    Tools.rateAction(getActivity());
                                }
                            });
                        }
                    } catch (Exception e) {
                        if (e.getMessage() != null)
                            Log.e("SongFragment", e.getMessage());
                        e.printStackTrace();
                    }


                } else if (ConfigAds.INSTANCE.getUrlRedirect() != null && ConfigAds.INSTANCE.isOnRedirect()) {
                    Tools.showAlertDialog(alertDialog, getString(R.string.update_info), getString(R.string.msg_update_redirect, getString(R.string.app_name)), getString(R.string.update_now), getString(R.string.update_later), new OnDialogCallback() {
                        @Override
                        public void onNoClicked() {

                        }

                        @Override
                        public void onOkClicked() {
                            Tools.redirectAction(getActivity(), ConfigAds.INSTANCE.getUrlRedirect());
                        }
                    });
                }
            }
        });

    }


    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    public void setOnReloadPlaylistListener(OnReloadPlaylistListener onReloadPlaylistListener) {
        this.onReloadPlaylistListener = onReloadPlaylistListener;
    }

    public void setOnFavoriteFragmentListener(FavoriteFragment.OnFavoriteFragmentListener onFavoriteFragmentListener) {
        this.onFavoriteFragmentListener = onFavoriteFragmentListener;
    }

    public ListSongAdapter getListMusicAdapter() {
        return listMusicAdapter;
    }
}
