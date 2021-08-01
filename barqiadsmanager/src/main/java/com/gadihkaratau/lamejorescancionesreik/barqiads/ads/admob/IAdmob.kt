package com.gadihkaratau.lamejorescancionesreik.barqiads.ads.admob

import android.widget.LinearLayout
import com.gadihkaratau.lamejorescancionesreik.barqiads.ads.IAds

interface IAdmob : IAds {
    fun showBanner(adView: LinearLayout)
}