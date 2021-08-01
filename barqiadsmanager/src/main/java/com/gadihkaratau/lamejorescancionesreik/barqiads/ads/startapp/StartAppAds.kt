package com.gadihkaratau.lamejorescancionesreik.barqiads.ads.startapp

import android.content.Context
import android.view.View
import com.gadihkaratau.lamejorescancionesreik.barqiads.ConfigAds
import com.gadihkaratau.lamejorescancionesreik.barqiads.utils.visible
import com.startapp.sdk.ads.banner.Banner
import com.startapp.sdk.ads.banner.BannerListener
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.StartAppSDK
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class StartAppAds(private val context: Context) : IStartApp, AnkoLogger {

    override fun initialize() {
        StartAppSDK.init(context, ConfigAds.startAppId, true)
        StartAppSDK.setTestAdsEnabled(ConfigAds.isTestAds);
    }

    override fun initData() {

    }

    override fun showBanner(banner: Banner) {
        banner.visible()
    }

    override fun showInterstitial() {
        StartAppAd.showAd(context);
    }

    private val bannerListener = object : BannerListener {

        override fun onClick(p0: View?) {
            info("banner onClick $p0")
        }

        override fun onFailedToReceiveAd(p0: View?) {
            info("banner onFailedToReceiveAd $p0")
        }

        override fun onImpression(p0: View?) {
            info("banner onImpression $p0")
        }

        override fun onReceiveAd(p0: View?) {
            info("banner onReceiveAd $p0")
        }

    }

}