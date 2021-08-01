package com.gadihkaratau.lamejorescancionesreik.fragment;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gadihkaratau.lamejorescancionesreik.R;
import com.gadihkaratau.lamejorescancionesreik.activity.PlaylistSongActivity;
import com.gadihkaratau.lamejorescancionesreik.adapters.ListGridSongPlaylistAdapter;
import com.gadihkaratau.lamejorescancionesreik.asynTask.LoadGridPlaylistAsync;
import com.gadihkaratau.lamejorescancionesreik.db.tabel.NamePlaylistHelper;
import com.gadihkaratau.lamejorescancionesreik.db.tabel.PlaylistSongHelper;
import com.gadihkaratau.lamejorescancionesreik.interfaces.LoadGridPlaylistCallback;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnAddPlaylistCallback;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnDialogCallback;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnReloadPlaylistListener;
import com.gadihkaratau.lamejorescancionesreik.models.playlist.GridPlaylist;
import com.gadihkaratau.lamejorescancionesreik.models.playlist.NamePlaylist;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;
import com.gadihkaratau.lamejorescancionesreik.utils.SpacingItemDecoration;
import com.gadihkaratau.lamejorescancionesreik.utils.Tools;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment implements LoadGridPlaylistCallback {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private ListGridSongPlaylistAdapter listGridSongPlaylistAdapter;
    private Unbinder unbinder;
    private OnReloadPlaylistListener onReloadPlaylistListener;
    private AlertDialog.Builder alertDialog;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        progressBar.setVisibility(View.VISIBLE);

        alertDialog = new AlertDialog.Builder(getContext());

        listGridSongPlaylistAdapter = new ListGridSongPlaylistAdapter();
        recyclerView.setAdapter(listGridSongPlaylistAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getContext(), 3), true));
        recyclerView.setHasFixedSize(true);
        listGridSongPlaylistAdapter.setOnListGridPlaylistListener(new ListGridSongPlaylistAdapter.OnListGridPlaylistListener() {
            @Override
            public void onClicked(GridPlaylist gridPlaylist) {
                gotoPlaylistSong(gridPlaylist);
            }
        });
        listGridSongPlaylistAdapter.setOnPopMenuClickListener(new ListGridSongPlaylistAdapter.OnPopMenuClickListener() {
            @Override
            public void onUpdate(NamePlaylist namePlaylist) {
                Tools.showDialogEditPlaylist(getContext(), namePlaylist, new OnAddPlaylistCallback() {
                    @Override
                    public void onSuccessAddPlaylist(Song song) {
                        onReloadPlaylistListener.onReloadPlaylist();
                    }
                });
            }

            @Override
            public void onDelete(NamePlaylist namePlaylist) {
                Tools.showAlertDialog(alertDialog, getString(R.string.confirmation), getString(R.string.msg_delete_playlist, namePlaylist.getName()), new OnDialogCallback() {
                    @Override
                    public void onNoClicked() {

                    }

                    @Override
                    public void onOkClicked() {
                        long result = NamePlaylistHelper.getInstance(getContext()).deleteById(namePlaylist.getId());
                        if (result > 0) {
                            Toast.makeText(getContext(), getString(R.string.success_delete_playlist, namePlaylist.getName()), Toast.LENGTH_SHORT).show();
                            onReloadPlaylistListener.onReloadPlaylist();
                        } else
                            Toast.makeText(getContext(), getString(R.string.failed_delete_playlist, namePlaylist.getName()), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        loadGridPlaylist();
    }

    private void gotoPlaylistSong(GridPlaylist gridPlaylist) {
        Intent intent = new Intent(getActivity(), PlaylistSongActivity.class);
        intent.putExtra(PlaylistSongActivity.EXTRA_GRID_PLAYLIST, gridPlaylist);
        startActivity(intent);

    }


    public void loadGridPlaylist() {
        new LoadGridPlaylistAsync(this, NamePlaylistHelper.getInstance(getContext()), PlaylistSongHelper.getInstance(getContext())).execute();
    }

    @Override
    public void preExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void postExecute(ArrayList<GridPlaylist> gridPlaylists) {
        progressBar.setVisibility(View.INVISIBLE);
        if (gridPlaylists != null)
            listGridSongPlaylistAdapter.setGridPlaylists(gridPlaylists);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadGridPlaylist();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    public void setOnReloadPlaylistListener(OnReloadPlaylistListener onReloadPlaylistListener) {
        this.onReloadPlaylistListener = onReloadPlaylistListener;
    }

    public ListGridSongPlaylistAdapter getListGridSongPlaylistAdapter() {
        return listGridSongPlaylistAdapter;
    }
}
