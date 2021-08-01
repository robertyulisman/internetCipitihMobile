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
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnPopMenuSongFavoriteListener;
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnSongClickAdapterListener;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;
import com.gadihkaratau.lamejorescancionesreik.models.song.SongPlayer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListSongFavoriteAdapter extends RecyclerView.Adapter<ListSongFavoriteAdapter.ViewHolder> implements Filterable {

    private ArrayList<Song> songArrayList = new ArrayList<>();
    private ArrayList<Song> tempSongArrayList = new ArrayList<>();
    private OnSongClickAdapterListener onSongClickAdapterListener;
    private OnPopMenuSongFavoriteListener onPopMenuSongFavoriteListener;

    @NonNull
    @Override
    public ListSongFavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListSongFavoriteAdapter.ViewHolder holder, int position) {
        holder.bind(position, songArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public void setSongArrayList(ArrayList<Song> songArrayList) {
        this.songArrayList = songArrayList;
        tempSongArrayList = this.songArrayList;
        notifyDataSetChanged();
    }

    public void setOnSongClickAdapterListener(OnSongClickAdapterListener onSongClickAdapterListener) {
        this.onSongClickAdapterListener = onSongClickAdapterListener;
    }

    public void setOnPopMenuSongFavoriteListener(OnPopMenuSongFavoriteListener onPopMenuSongFavoriteListener) {
        this.onPopMenuSongFavoriteListener = onPopMenuSongFavoriteListener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString().toLowerCase();
                if (query.isEmpty())
                    songArrayList = tempSongArrayList;
                else {
                    ArrayList<Song> filterList = new ArrayList<>();
                    for (Song data : tempSongArrayList) {
                        if (data.getTitle().toLowerCase().contains(query) || data.getArtist().toLowerCase().contains(query))
                            filterList.add(data);
                    }
                    songArrayList = filterList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = songArrayList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                songArrayList = (ArrayList<Song>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvArtist)
        TextView tvArtist;
        @BindView(R.id.imgSong)
        ImageView imgSong;
        @BindView(R.id.imgMore)
        ImageButton imgMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int index, Song song) {
            tvTitle.setText(song.getTitle());
            tvArtist.setText(song.getArtist());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSongClickAdapterListener.onAdapterClicked(new SongPlayer(index, songArrayList));
                }
            });
            String image = song.getImg();
            Glide.with(itemView.getContext())
                    .load(image)
                    .apply(new RequestOptions().override(100, 100))
                    .placeholder(itemView.getResources().getDrawable(R.drawable.photo_male_3))
                    .error(itemView.getResources().getDrawable(R.drawable.photo_male_3))
                    .into(imgSong);
            imgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(), imgMore);
                    popupMenu.inflate(R.menu.menu_adapter_song_favorite);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.actionRemoveFromFavorite:
                                    onPopMenuSongFavoriteListener.onRemoveFromFavorite(song);
                                    break;
                                case R.id.actionAddToPlaylist:
                                    onPopMenuSongFavoriteListener.onAddToPlaylist(song);
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
