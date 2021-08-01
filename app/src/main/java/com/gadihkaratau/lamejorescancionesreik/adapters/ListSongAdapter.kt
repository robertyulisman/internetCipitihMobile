package com.gadihkaratau.lamejorescancionesreik.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.gadihkaratau.lamejorescancionesreik.R
import com.gadihkaratau.lamejorescancionesreik.barqiads.ConfigAds
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnPopMenuSongListener
import com.gadihkaratau.lamejorescancionesreik.interfaces.OnSongClickAdapterListener
import com.gadihkaratau.lamejorescancionesreik.models.song.Song
import com.gadihkaratau.lamejorescancionesreik.models.song.SongPlayer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_music_song.view.*
import java.util.*

class ListSongAdapter : RecyclerView.Adapter<ListSongAdapter.ViewHolder>(), Filterable {
    private var songArrayList = ArrayList<Song>()
    private var tempSongArrayList = ArrayList<Song>()
    private var onSongClickAdapterListener: OnSongClickAdapterListener? = null
    private var onPopMenuSongListener: OnPopMenuSongListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music_song, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, songArrayList[position])
    }

    override fun getItemCount(): Int {
        return songArrayList.size
    }

    fun getSongArrayList(): ArrayList<Song> {
        return songArrayList
    }

    fun setSongArrayList(songArrayList: ArrayList<Song>) {
        this.songArrayList = songArrayList
        tempSongArrayList = this.songArrayList
        notifyDataSetChanged()
    }

    fun setOnSongClickAdapterListener(onSongClickAdapterListener: OnSongClickAdapterListener?) {
        this.onSongClickAdapterListener = onSongClickAdapterListener
    }

    fun setOnPopMenuSongListener(onPopMenuSongListener: OnPopMenuSongListener?) {
        this.onPopMenuSongListener = onPopMenuSongListener
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val query = charSequence.toString().toLowerCase()
                songArrayList = if (query.isEmpty()) tempSongArrayList else {
                    val filterList = ArrayList<Song>()
                    for (data in tempSongArrayList) {
                        if (data.title.toLowerCase().contains(query) || data.artist.toLowerCase().contains(query)) filterList.add(data)
                    }
                    filterList
                }
                val filterResults = FilterResults()
                filterResults.values = songArrayList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                songArrayList = filterResults.values as ArrayList<Song>
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(index: Int, song: Song) {
            with(itemView) {
                tvTitle.text = song.title
                tvArtist.text = song.artist
                itemView.setOnClickListener {
                    onSongClickAdapterListener?.onAdapterClicked(SongPlayer(index, songArrayList))
                }
                if (ConfigAds.isShowImageAudio)
                    song.img?.let {
                        Glide.with(itemView.context)
                                .load(it)
                                .apply(RequestOptions().override(100, 100))
                                .placeholder(R.drawable.photo_male_3)
                                .error(R.drawable.photo_male_3)
                                .into(imgSong)
                    }
                imgMore?.setOnClickListener {
                    val popupMenu = PopupMenu(itemView.context, imgMore)
                    popupMenu.inflate(R.menu.menu_adapter_song)
                    val menu = popupMenu.menu
                    menu.findItem(R.id.actionDownload).isVisible = false
                    popupMenu.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.actionDownload -> onPopMenuSongListener?.onDownload(song)
                            R.id.actionAddToPlaylist -> onPopMenuSongListener?.onAddToPlaylist(song)
                            R.id.actionAddToFavorites -> onPopMenuSongListener?.onAddToFavorite(song)
                        }
                        false
                    }
                    popupMenu.show()
                }
            }
        }
    }
}