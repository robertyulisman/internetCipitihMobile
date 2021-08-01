package com.gadihkaratau.lamejorescancionesreik.barqiads.ads.mopub

import com.gadihkaratau.lamejorescancionesreik.barqiads.ads.IAds
import com.mopub.mobileads.MoPubView

interface IMopub : IAds {
    fun showBanner(moPubView: MoPubView)
}