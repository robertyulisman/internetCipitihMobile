package com.gadihkaratau.lamejorescancionesreik.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gadihkaratau.lamejorescancionesreik.R
import com.gadihkaratau.lamejorescancionesreik.barqiads.ConfigAds
import com.gadihkaratau.lamejorescancionesreik.models.song.Song
import com.gadihkaratau.lamejorescancionesreik.utils.loadImageFromUrl
import kotlinx.android.synthetic.main.fragment_thumbnail_player.*

private const val ARG_SONG = "Song"

class ThumbnailPlayerFragment : Fragment() {

    private var song: Song? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            song = it.getParcelable(ARG_SONG)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_thumbnail_player, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        song?.let { setData(it) }
    }

    fun setData(song: Song?) {
        song?.apply {
            tvTitle.text = title
            tvArtist.text = artist
            if (ConfigAds.isShowImageAudio)
                imgSong.loadImageFromUrl(img)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(song: Song?) =
                ThumbnailPlayerFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_SONG, song)
                    }
                }
    }
}