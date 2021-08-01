package com.gadihkaratau.lamejorescancionesreik.barqiads.ads.unityads

import android.widget.LinearLayout
import com.gadihkaratau.lamejorescancionesreik.barqiads.ads.IAds

interface IUnityAds : IAds {
    fun showBanner(adView: LinearLayout)
}