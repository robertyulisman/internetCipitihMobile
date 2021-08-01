package com.gadihkaratau.lamejorescancionesreik.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gadihkaratau.lamejorescancionesreik.R;
import com.gadihkaratau.lamejorescancionesreik.models.song.Song;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerViewSongLocalAdapter extends RecyclerView.Adapter<RecyclerViewSongLocalAdapter.ViewHolder> {

    private ArrayList<Song> songArrayList = new ArrayList<>();
    private onClickAdapterListener onClickAdapterListener;

    @NonNull
    @Override
    public RecyclerViewSongLocalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewSongLocalAdapter.ViewHolder holder, int position) {
        holder.bind(position, songArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public void setOnClickAdapterListener(RecyclerViewSongLocalAdapter.onClickAdapterListener onClickAdapterListener) {
        this.onClickAdapterListener = onClickAdapterListener;
    }

    public ArrayList<Song> getSongArrayList() {
        return songArrayList;
    }

    public void setSongArrayList(ArrayList<Song> songArrayList) {
        this.songArrayList = songArrayList;
        notifyDataSetChanged();
    }

    public interface onClickAdapterListener {
        void onAdapterClicked(int index, Song song);

        void onAddToPlaylist(Song song);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvArtist;
        private ImageView imgSong;
        private ImageButton imgMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            imgSong = itemView.findViewById(R.id.imgSong);
            imgMore = itemView.findViewById(R.id.imgMore);
        }

        void bind(final int index, final Song song) {
            tvTitle.setText(song.getTitle());

            tvArtist.setText(song.getArtist());
//            Uri uri = Uri.withAppendedPath(Uri.parse("content://media/external/audio/albumart"),song.getImg());
            Log.d("songLocal", song.getImg());
            Glide.with(itemView.getContext())
                    .load(song.getImg())
                    .placeholder(itemView.getResources().getDrawable(R.drawable.photo_male_3))
                    .error(itemView.getResources().getDrawable(R.drawable.photo_male_3))
                    .into(imgSong);
            imgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(), imgMore);
                    popupMenu.inflate(R.menu.menu_adapter_song);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.actionAddToPlaylist) {
                                onClickAdapterListener.onAddToPlaylist(song);
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAdapterListener.onAdapterClicked(index, song);
                }
            });
        }

    }
}
