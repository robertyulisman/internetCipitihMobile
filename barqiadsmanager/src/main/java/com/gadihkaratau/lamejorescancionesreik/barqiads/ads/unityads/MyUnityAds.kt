package com.gadihkaratau.lamejorescancionesreik.barqiads.ads.unityads

import android.app.Activity
import android.content.Context
import android.widget.LinearLayout
import com.gadihkaratau.lamejorescancionesreik.barqiads.ConfigAds
import com.gadihkaratau.lamejorescancionesreik.barqiads.utils.activity
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.UnityAds
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MyUnityAds(private val context: Context) : IUnityAds, IUnityAdsListener, BannerView.IListener, AnkoLogger {

    private lateinit var activity: Activity

    override fun initialize() {
        UnityAds.initialize(context, ConfigAds.unityGameID, ConfigAds.isTestAds)
    }

    override fun initData() {
        UnityAds.addListener(this)
    }

    override fun showBanner(adView: LinearLayout) {
        adView.context.activity()?.let {
            activity = it
        }
        val bottomBanner = BannerView(activity, ConfigAds.unityBanner, UnityBannerSize(320, 50))
        bottomBanner.listener = this
        adView.addView(bottomBanner)
        bottomBanner.load()
    }

    override fun showInterstitial() {
        if (UnityAds.isReady(ConfigAds.unityInter))
            UnityAds.show(activity, ConfigAds.unityInter)
    }

    override fun onUnityAdsStart(placementID: String?) {
        info("onUnityAdsStart placementID : $placementID")
    }

    override fun onUnityAdsFinish(placementId: String?, p1: UnityAds.FinishState?) {
        info("onUnityAdsFinish placementID : $placementId")
    }

    override fun onUnityAdsError(error: UnityAds.UnityAdsError?, message: String?) {
        info("onUnityAdsError error : $error, message : $message")
    }

    override fun onUnityAdsReady(placementId: String?) {
        info("onUnityAdsReady placementId : $placementId")
    }

    override fun onBannerLeftApplication(p0: BannerView?) {

    }

    override fun onBannerClick(p0: BannerView?) {

    }

    override fun onBannerLoaded(p0: BannerView?) {
        info("onBannerLoaded")
    }

    override fun onBannerFailedToLoad(p0: BannerView?, p1: BannerErrorInfo?) {
        info("onBannerFailedToLoad")
    }

}