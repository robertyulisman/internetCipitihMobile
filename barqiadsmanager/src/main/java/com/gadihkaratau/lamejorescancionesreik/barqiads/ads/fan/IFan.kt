package com.gadihkaratau.lamejorescancionesreik.barqiads.ads.fan

import android.widget.LinearLayout
import com.gadihkaratau.lamejorescancionesreik.barqiads.ads.IAds

interface IFan : IAds {
    fun showBanner(adView: LinearLayout)
}