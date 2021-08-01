package com.gadihkaratau.lamejorescancionesreik.barqiads.ads.startapp

import com.gadihkaratau.lamejorescancionesreik.barqiads.ads.IAds
import com.startapp.sdk.ads.banner.Banner

interface IStartApp : IAds {
    fun showBanner(banner: Banner)
}