package com.gadihkaratau.lamejorescancionesreik.fragment;


import android.app.AlertDialog;
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
import com.gadihkaratau.lamejorescancionesreik.adapters.ListSongFavoriteAdapter;
import com.gadihkaratau.lamejorescancionesreik.asynTask.LoadSongFavoriteAsyn;
import com.gadihkaratau.lamejorescancionesreik.db.tabel.FavoriteSongHelper;
import com.gadihkaratau.lamejorescancionesreik.interfaces.LoadSongAsynCallback;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnAddPlaylistCallback;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnDialogCallback;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnPopMenuSongFavoriteListener;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnReloadPlaylistListener;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnSongClickAdapterListener;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;
import com.gadihkaratau.lamejorescancionesreik.models.song.SongPlayer;
import com.gadihkaratau.lamejorescancionesreik.utils.Tools;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment implements LoadSongAsynCallback {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Unbinder unbinder;
    private ListSongFavoriteAdapter listSongFavoriteAdapter;
    private OnReloadPlaylistListener onReloadPlaylistListener;
    private AlertDialog.Builder alertDialog;
    private OnFavoriteFragmentListener onFavoriteFragmentListener;
    private HomeActivity homeActivity;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        alertDialog = new AlertDialog.Builder(getContext());
        homeActivity = (HomeActivity) getActivity();

        listSongFavoriteAdapter = new ListSongFavoriteAdapter();
        recyclerView.setAdapter(listSongFavoriteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        loadFavoriteFragment();
        listSongFavoriteAdapter.setOnSongClickAdapterListener(new OnSongClickAdapterListener() {
            @Override
            public void onAdapterClicked(SongPlayer songPlayer) {
                homeActivity.playSongItem(songPlayer);
                homeActivity.showInt();
            }
        });
        listSongFavoriteAdapter.setOnPopMenuSongFavoriteListener(new OnPopMenuSongFavoriteListener() {
            @Override
            public void onRemoveFromFavorite(Song song) {
                Tools.showAlertDialog(alertDialog, getString(R.string.confirmation), getString(R.string.msg_delete_playlist, song.getTitle()), new OnDialogCallback() {
                    @Override
                    public void onNoClicked() {

                    }

                    @Override
                    public void onOkClicked() {
                        long result = FavoriteSongHelper.getInstance(getContext()).deleteById(song.getId());
                        if (result > 0) {
                            Toast.makeText(getContext(), getString(R.string.msg_success_delete_from_favorite, song.getTitle()), Toast.LENGTH_SHORT).show();
                            onFavoriteFragmentListener.refreshFavoriteFragment();
                        } else
                            Toast.makeText(getContext(), getString(R.string.msg_failed_delete_from_favorite, song.getTitle()), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onAddToPlaylist(Song song) {
                Tools.showDialogPlaylist(getContext(), song, new OnAddPlaylistCallback() {
                    @Override
                    public void onSuccessAddPlaylist(Song song) {
                        onReloadPlaylistListener.onReloadPlaylist();
                    }
                });
            }
        });
    }

    public void loadFavoriteFragment() {
        new LoadSongFavoriteAsyn(FavoriteSongHelper.getInstance(getContext()), this).execute();
    }

    @Override
    public void preExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void postExecute(ArrayList<Song> songArrayList) {
        if (songArrayList != null)
            listSongFavoriteAdapter.setSongArrayList(songArrayList);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    public void setOnFavoriteFragmentListener(OnFavoriteFragmentListener onFavoriteFragmentListener) {
        this.onFavoriteFragmentListener = onFavoriteFragmentListener;
    }

    public void setOnReloadPlaylistListener(OnReloadPlaylistListener onReloadPlaylistListener) {
        this.onReloadPlaylistListener = onReloadPlaylistListener;
    }

    public ListSongFavoriteAdapter getListSongFavoriteAdapter() {
        return listSongFavoriteAdapter;
    }

    public interface OnFavoriteFragmentListener {
        void refreshFavoriteFragment();
    }
}
