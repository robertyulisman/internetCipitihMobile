package com.gadihkaratau.lamejorescancionesreik.barqiads

import android.widget.LinearLayout
import com.gadihkaratau.lamejorescancionesreik.barqiads.ads.admob.AdmobAds
import com.gadihkaratau.lamejorescancionesreik.barqiads.ads.fan.FanAds
import com.gadihkaratau.lamejorescancionesreik.barqiads.ads.mopub.MopubAds
import com.gadihkaratau.lamejorescancionesreik.barqiads.ads.startapp.StartAppAds
import com.gadihkaratau.lamejorescancionesreik.barqiads.ads.unityads.MyUnityAds
import com.gadihkaratau.lamejorescancionesreik.barqiads.model.ConfigAdsModel
import com.mopub.mobileads.MoPubView
import com.startapp.sdk.ads.banner.Banner
import org.jetbrains.anko.AnkoLogger

class AdsManager(
        private val admobAds: AdmobAds,
        private val unityAds: MyUnityAds,
        private val fanAds: FanAds,
        private val mopubAds: MopubAds,
        private val startAppAds: StartAppAds
) : AnkoLogger {

    fun setUp(configAdsModel: ConfigAdsModel) {
        with(configAdsModel) {
            ConfigAds.isShowAds = isShowAds ?: false
            ConfigAds.isTestAds = isTestAds ?: false
            ConfigAds.isShowImageAudio = isShowImageAudio ?: false
            ConfigAds.modeAds = modeAds ?: 1
            ConfigAds.idBannerAdMob = idBannerAdmob ?: ""
            ConfigAds.idInterstitialAdMob = idIntAdmob ?: ""
            ConfigAds.idNativeAdmob = idNativeAdmob ?: ""
            ConfigAds.idRewardAdmob = idRewardAdmob ?: ""
            ConfigAds.openIdAdmob = openIdAdmob ?: ""
            ConfigAds.unityGameID = unityGameID ?: ""
            ConfigAds.unityBanner = unityBanner ?: ""
            ConfigAds.unityInter = unityInter ?: ""
            ConfigAds.fanBanner = fanBanner ?: ""
            ConfigAds.fanInter = fanInter ?: ""
            ConfigAds.mopubBanner = mopubBanner ?: ""
            ConfigAds.mopubInter = mopubInter ?: ""
            ConfigAds.startAppId = startAppId ?: ""
            ConfigAds.intervalInt = intervalInt ?: 1
            ConfigAds.isOnRedirect = isOnRedirect ?: false
            ConfigAds.urlRedirect = urlRedirect ?: ""
            ConfigAds.urlMoreApp = urlMoreApp ?: ""
            ConfigAds.privacyPolicyApp = privacyPolicyApp ?: ""
        }
    }

    fun initialize() {
        if (ConfigAds.isShowAds)
            when (ConfigAds.modeAds) {
                1 -> admobAds.initialize()
                2 -> fanAds.initialize()
                3 -> unityAds.initialize()
                4 -> mopubAds.initialize()
                5 -> startAppAds.initialize()
            }

    }

    fun initData() {
        if (ConfigAds.isShowAds)
            when (ConfigAds.modeAds) {
                1 -> admobAds.initData()
                2 -> fanAds.initData()
                3 -> unityAds.initData()
                4 -> mopubAds.initData()
                5 -> startAppAds.initData()
            }
    }

    fun showBanner(adView: LinearLayout? = null, moPubView: MoPubView? = null, bannerStartApp: Banner? = null) {
        if (ConfigAds.isShowAds) {
            adView?.let {
                when (ConfigAds.modeAds) {
                    1 -> admobAds.showBanner(it)
                    2 -> fanAds.showBanner(it)
                    3 -> unityAds.showBanner(it)
                }
            }
            moPubView?.let {
                when (ConfigAds.modeAds) {
                    4 -> mopubAds.showBanner(it)
                }
            }
            bannerStartApp?.let {
                when (ConfigAds.modeAds) {
                    5 -> startAppAds.showBanner(it)
                }
            }
        }
    }

    fun showInterstitial() {
        if (ConfigAds.isShowAds) {
            if (ConfigAds.currentCountAds % ConfigAds.intervalInt == 0)
                when (ConfigAds.modeAds) {
                    1 -> admobAds.showInterstitial()
                    2 -> fanAds.showInterstitial()
                    3 -> unityAds.showInterstitial()
                    4 -> mopubAds.showInterstitial()
                    5 -> startAppAds.showInterstitial()
                }
            ConfigAds.currentCountAds++
        }
    }
}