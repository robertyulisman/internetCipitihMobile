package com.gadihkaratau.lamejorescancionesreik.adapters;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gadihkaratau.lamejorescancionesreik.R;
import com.gadihkaratau.lamejorescancionesreik.models.playlist.GridPlaylist;
import com.gadihkaratau.lamejorescancionesreik.models.playlist.NamePlaylist;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListGridSongPlaylistAdapter extends RecyclerView.Adapter<ListGridSongPlaylistAdapter.ViewHolder> implements Filterable {

    private ArrayList<GridPlaylist> gridPlaylists = new ArrayList<>();
    private ArrayList<GridPlaylist> tempGridPlaylists = new ArrayList<>();
    private OnListGridPlaylistListener onListGridPlaylistListener;
    private OnPopMenuClickListener onPopMenuClickListener;

    @NonNull
    @Override
    public ListGridSongPlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListGridSongPlaylistAdapter.ViewHolder holder, int position) {
        holder.bind(gridPlaylists.get(position));
    }

    @Override
    public int getItemCount() {
        return gridPlaylists.size();
    }

    public ArrayList<GridPlaylist> getGridPlaylists() {
        return gridPlaylists;
    }

    public void setGridPlaylists(ArrayList<GridPlaylist> gridPlaylists) {
        this.gridPlaylists = gridPlaylists;
        tempGridPlaylists = this.gridPlaylists;
        notifyDataSetChanged();
    }

    public void setOnListGridPlaylistListener(OnListGridPlaylistListener onListGridPlaylistListener) {
        this.onListGridPlaylistListener = onListGridPlaylistListener;
    }

    public void setOnPopMenuClickListener(OnPopMenuClickListener onPopMenuClickListener) {
        this.onPopMenuClickListener = onPopMenuClickListener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString().toLowerCase();
                if (query.isEmpty())
                    gridPlaylists = tempGridPlaylists;
                else {
                    ArrayList<GridPlaylist> filterList = new ArrayList<>();
                    for (GridPlaylist data : tempGridPlaylists) {
                        if (data.getNamePlaylist().getName().toLowerCase().contains(query))
                            filterList.add(data);
                    }
                    gridPlaylists = filterList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = gridPlaylists;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                gridPlaylists = (ArrayList<GridPlaylist>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnListGridPlaylistListener {
        void onClicked(GridPlaylist gridPlaylist);
    }

    public interface OnPopMenuClickListener {
        void onUpdate(NamePlaylist namePlaylist);

        void onDelete(NamePlaylist namePlaylist);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        ImageView imageView;
        @BindView(R.id.tvNamePlaylist)
        TextView tvNamePlaylist;
        @BindView(R.id.tvCountPlaylist)
        TextView tvCountPlaylist;
        @BindView(R.id.imgBtnMore)
        ImageButton imgBtnMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(GridPlaylist gridPlaylist) {
            tvNamePlaylist.setText(gridPlaylist.getNamePlaylist().getName());
            tvCountPlaylist.setText(itemView.getResources().getString(R.string.countSong, gridPlaylist.getSongArrayList().size()));
            if (gridPlaylist.getSongArrayList().size() > 0)
                Glide.with(itemView.getContext())
                        .load(gridPlaylist.getSongArrayList().get(gridPlaylist.getSongArrayList().size() - 1).getImg())
                        .placeholder(R.drawable.image_3)
                        .error(R.drawable.image_3)
                        .into(imageView);
            else
                imageView.setImageResource(R.drawable.image_3);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onListGridPlaylistListener.onClicked(gridPlaylist);
                }
            });
            imgBtnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(), imgBtnMore);
                    popupMenu.inflate(R.menu.menu_item_grid_playlist);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.actionEdit:
                                    onPopMenuClickListener.onUpdate(gridPlaylist.getNamePlaylist());
                                    break;
                                case R.id.actionDelete:
                                    onPopMenuClickListener.onDelete(gridPlaylist.getNamePlaylist());
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

        }
    }
}
