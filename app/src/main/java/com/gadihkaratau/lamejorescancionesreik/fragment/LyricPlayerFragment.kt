package com.gadihkaratau.lamejorescancionesreik.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gadihkaratau.lamejorescancionesreik.R
import com.gadihkaratau.lamejorescancionesreik.utils.toTextHtml
import kotlinx.android.synthetic.main.fragment_lyric_player.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


private const val ARG_LYRIC = "lyric"

class LyricPlayerFragment : Fragment(), AnkoLogger {

    private var lyric: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lyric = it.getString(ARG_LYRIC)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lyric_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lyric?.let {
            tvLyric.text = it.toTextHtml()
            info("lyric : $it")
        }
    }

    fun resetLyric(lyric: String?) {
        with(lyric) {
            tvLyric.text = if (isNullOrEmpty()) getString(R.string.no_lyric_available_msg) else this?.toTextHtml()
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(lyric: String?) =
                LyricPlayerFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_LYRIC, lyric)
                    }
                }
    }
}