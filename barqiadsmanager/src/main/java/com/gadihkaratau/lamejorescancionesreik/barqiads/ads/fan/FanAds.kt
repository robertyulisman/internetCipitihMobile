package com.gadihkaratau.lamejorescancionesreik.barqiads.ads.fan

import android.content.Context
import android.widget.LinearLayout
import com.gadihkaratau.lamejorescancionesreik.barqiads.ConfigAds
import com.facebook.ads.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*

class FanAds(private val context: Context) : IFan, AudienceNetworkAds.InitListener, AnkoLogger {

    private var interstitialAd: InterstitialAd? = null
    private var bannerAdView: AdView? = null

    override fun onInitialized(result: AudienceNetworkAds.InitResult?) {
        info("onInitialized FAN result : ${result?.message}")
    }

    override fun initialize() {
        AudienceNetworkAds.buildInitSettings(context).withInitListener(this).initialize()
        AdSettings.setTestMode(ConfigAds.isTestAds)
        AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CRASH_DEBUG_MODE);
    }

    private fun requestInterstitial() {
        if (interstitialAd != null) {
            interstitialAd?.destroy()
            interstitialAd = null
        }
        interstitialAd = InterstitialAd(context, ConfigAds.fanInter)
        interstitialAd?.loadAd(
                interstitialAd!!
                        .buildLoadAdConfig()
                        .withAdListener(interstitialListener)
                        .withCacheFlags(EnumSet.of(CacheFlag.VIDEO))
                        .build())
    }

    override fun initData() {
        requestInterstitial()
    }

    override fun showBanner(adView: LinearLayout) {
        bannerAdView?.destroy()
        bannerAdView = null
        bannerAdView = AdView(context, ConfigAds.fanBanner, AdSize.BANNER_HEIGHT_50)
        bannerAdView?.let { nonNullBannerAdView ->
            adView.addView(nonNullBannerAdView)
            nonNullBannerAdView.loadAd(
                    nonNullBannerAdView.buildLoadAdConfig().withAdListener(bannerListener).build())
        }
    }

    override fun showInterstitial() {
        if (interstitialAd != null || interstitialAd!!.isAdLoaded) {
            interstitialAd?.show()
            info("showInterstitial fan")
        }
    }

    private val bannerListener = object : AdListener {
        override fun onAdClicked(p0: Ad?) {
            info("onAdClicked")
        }

        override fun onError(ad: Ad?, error: AdError?) {
            info("onError ad: $ad, error : ${error?.errorMessage}")
        }

        override fun onAdLoaded(p0: Ad?) {
            info("onAdLoaded")
        }

        override fun onLoggingImpression(p0: Ad?) {
            info("onLoggingImpression")
        }
    }

    private val interstitialListener = object : InterstitialAdListener {
        override fun onInterstitialDisplayed(p0: Ad?) {
            info("onInterstitialDisplayed")
        }

        override fun onAdClicked(p0: Ad?) {
            info("onAdClicked")
        }

        override fun onInterstitialDismissed(p0: Ad?) {
            info("onInterstitialDismissed")
            requestInterstitial()
        }

        override fun onError(p0: Ad?, error: AdError?) {
            info("onError : ${error?.errorMessage}")
        }

        override fun onAdLoaded(p0: Ad?) {
            info("onAdLoaded")
        }

        override fun onLoggingImpression(p0: Ad?) {
            info("onLoggingImpression")
        }
    }

}