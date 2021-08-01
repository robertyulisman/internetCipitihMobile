package com.gadihkaratau.lamejorescancionesreik.utils

import com.gadihkaratau.lamejorescancionesreik.barqiads.model.ConfigAdsModel
import com.gadihkaratau.lamejorescancionesreik.models.setting.Setting

object Mapper {
    fun mapSettingToConfigAdsModel(input: Setting) =
            with(input) {
                ConfigAdsModel(
                        isShowAds = isShowAds,
                        isTestAds = isTestAds,
                        isShowImageAudio = isShowImageAudio,
                        modeAds = modeAds,
                        idBannerAdmob = idBannerAdmob,
                        idIntAdmob = idIntAdmob,
                        idNativeAdmob = idNativeAdmob,
                        idRewardAdmob = idRewardAdmob,
                        openIdAdmob = openIdAdmob,
                        unityGameID = unityGameID,
                        unityBanner = unityBanner,
                        unityInter = unityInter,
                        fanBanner = fanBanner,
                        fanInter = fanInter,
                        mopubBanner = mopubBanner,
                        mopubInter = mopubInter,
                        startAppId = startAppId,
                        intervalInt = intervalInt,
                        isOnRedirect = isOnRedirect,
                        urlRedirect = urlRedirect,
                        urlMoreApp = urlMoreApp,
                        privacyPolicyApp = privacyPolicyApp,
                )
            }


}