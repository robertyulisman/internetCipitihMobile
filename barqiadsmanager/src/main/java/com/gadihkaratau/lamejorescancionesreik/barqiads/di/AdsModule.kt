package com.gadihkaratau.lamejorescancionesreik.barqiads.di

import com.gadihkaratau.lamejorescancionesreik.barqiads.AdsManager
import com.gadihkaratau.lamejorescancionesreik.barqiads.ads.admob.AdmobAds
import com.gadihkaratau.lamejorescancionesreik.barqiads.ads.fan.FanAds
import com.gadihkaratau.lamejorescancionesreik.barqiads.ads.mopub.MopubAds
import com.gadihkaratau.lamejorescancionesreik.barqiads.ads.startapp.StartAppAds
import com.gadihkaratau.lamejorescancionesreik.barqiads.ads.unityads.MyUnityAds
import org.koin.dsl.module

val adsModule = module {
    single { AdmobAds(get()) }
    single { MyUnityAds(get()) }
    single { FanAds(get()) }
    single { MopubAds(get()) }
    single { StartAppAds(get()) }

    single {
        AdsManager(get(), get(), get(), get(), get())
    }
}