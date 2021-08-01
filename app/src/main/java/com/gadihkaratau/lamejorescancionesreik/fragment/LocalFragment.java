package com.gadihkaratau.lamejorescancionesreik.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gadihkaratau.lamejorescancionesreik.R;
import com.gadihkaratau.lamejorescancionesreik.activity.HomeActivity;
import com.gadihkaratau.lamejorescancionesreik.adapters.ListSongAdapter;
import com.gadihkaratau.lamejorescancionesreik.asynTask.LoadSongLocalAsync;
import com.gadihkaratau.lamejorescancionesreik.db.tabel.FavoriteSongHelper;
import com.gadihkaratau.lamejorescancionesreik.interfaces.LoadSongAsynCallback;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnAddPlaylistCallback;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnDialogCallback;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnPopMenuSongListener;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnReloadPlaylistListener;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnSongClickAdapterListener;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;
import com.gadihkaratau.lamejorescancionesreik.models.song.SongPlayer;
import com.gadihkaratau.lamejorescancionesreik.utils.Tools;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocalFragment extends Fragment implements LoadSongAsynCallback {


    private static final int REQUEST_PERMISSION = 1;
    private ListSongAdapter listSongAdapter;
    private ProgressBar progressBar;
    private HomeActivity homeActivity;
    private OnReloadPlaylistListener onReloadPlaylistListener;
    private FavoriteFragment.OnFavoriteFragmentListener onFavoriteFragmentListener;
    private AlertDialog.Builder alertDialog;

    public LocalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_local, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alertDialog = new AlertDialog.Builder(getContext());
        listSongAdapter = new ListSongAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(listSongAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        homeActivity = (HomeActivity) getActivity();
        progressBar = view.findViewById(R.id.progressBar);
        listSongAdapter.setOnSongClickAdapterListener(new OnSongClickAdapterListener() {
            @Override
            public void onAdapterClicked(SongPlayer songPlayer) {
                homeActivity.playSongItem(songPlayer);
            }
        });
        listSongAdapter.setOnPopMenuSongListener(new OnPopMenuSongListener() {
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

        if (checkPermission())
            loadSongStorage();
    }

    private void loadSongStorage() {
        new LoadSongLocalAsync(getContext(), this).execute();
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                return true;
            else {
                requestPermission();
                return false;
            }
        } else
            return true;
    }

    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            boolean isGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (isGranted)
                loadSongStorage();
        }
    }

    @Override
    public void preExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void postExecute(ArrayList<Song> songArrayList) {
        progressBar.setVisibility(View.INVISIBLE);
        if (songArrayList.size() > 0) {
            listSongAdapter.setSongArrayList(songArrayList);
        }
    }

    public void setOnReloadPlaylistListener(OnReloadPlaylistListener onReloadPlaylistListener) {
        this.onReloadPlaylistListener = onReloadPlaylistListener;
    }

    public void setOnFavoriteFragmentListener(FavoriteFragment.OnFavoriteFragmentListener onFavoriteFragmentListener) {
        this.onFavoriteFragmentListener = onFavoriteFragmentListener;
    }

    public ListSongAdapter getListSongAdapter() {
        return listSongAdapter;
    }
}
