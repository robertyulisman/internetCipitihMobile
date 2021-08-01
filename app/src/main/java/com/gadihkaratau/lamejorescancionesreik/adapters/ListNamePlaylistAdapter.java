package com.gadihkaratau.lamejorescancionesreik.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gadihkaratau.lamejorescancionesreik.R;
import com.gadihkaratau.lamejorescancionesreik.models.playlist.NamePlaylist;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListNamePlaylistAdapter extends RecyclerView.Adapter<ListNamePlaylistAdapter.ViewHolder> {

    private ArrayList<NamePlaylist> namePlaylists = new ArrayList<>();
    private OnPlaylistAdapterClicked onPlaylistAdapterClicked;

    @NonNull
    @Override
    public ListNamePlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListNamePlaylistAdapter.ViewHolder holder, int position) {
        holder.bind(namePlaylists.get(position));
    }

    @Override
    public int getItemCount() {
        return namePlaylists.size();
    }

    public ArrayList<NamePlaylist> getNamePlaylists() {
        return namePlaylists;
    }

    public void setNamePlaylists(ArrayList<NamePlaylist> namePlaylists) {
        this.namePlaylists = namePlaylists;
        notifyDataSetChanged();
    }

    public OnPlaylistAdapterClicked getOnPlaylistAdapterClicked() {
        return onPlaylistAdapterClicked;
    }

    public void setOnPlaylistAdapterClicked(OnPlaylistAdapterClicked onPlaylistAdapterClicked) {
        this.onPlaylistAdapterClicked = onPlaylistAdapterClicked;
    }

    public interface OnPlaylistAdapterClicked {
        void onClicked(NamePlaylist namePlaylist);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvNamePlaylist)
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(NamePlaylist namePlaylist) {
            tvName.setText(namePlaylist.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPlaylistAdapterClicked.onClicked(namePlaylist);
                }
            });
        }
    }
}
