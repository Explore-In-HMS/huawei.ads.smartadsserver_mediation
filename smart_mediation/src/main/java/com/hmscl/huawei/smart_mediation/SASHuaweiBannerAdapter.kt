package com.hmscl.huawei.smart_mediation

import android.content.Context
import android.util.Log
import android.view.View
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.banner.BannerView
import com.smartadserver.android.library.mediation.SASMediationBannerAdapter
import com.smartadserver.android.library.mediation.SASMediationBannerAdapterListener
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Exception


class SASHuaweiBannerAdapter : SASHuaweiAdapterBase(), SASMediationBannerAdapter {


    private lateinit var adView: View
    val CLASS_NAME_BANNER = SASHuaweiBannerAdapter::class.java.simpleName


    override fun requestBannerAd(
        context: Context,
        serverParametersString: String,
        clientParameters: MutableMap<String, Any>,
        bannerAdapterListener: SASMediationBannerAdapterListener
    ) {
        Log.d(CLASS_NAME_BANNER, "requestBannerAd()")
        HwAds.init(context)

        try {
            val adUnitID: String = getAdUnitId(serverParametersString)
            val bannerAdSize: BannerAdSize = getAdSize(serverParametersString)
            val adRequest: AdParam = configureAdRequest(context, clientParameters)
            val bannerView = BannerView(context)
            bannerView.adId = adUnitID
            bannerView.bannerAdSize = bannerAdSize
            val adListener: AdListener = createAdListener(bannerAdapterListener, bannerView)
            bannerView.adListener = adListener
            bannerView.loadAd(adRequest)
            adView = bannerView
        } catch (e: Exception) {
            val stacktrace =
                StringWriter().also { e.printStackTrace(PrintWriter(it)) }.toString().trim()
            Log.e(CLASS_NAME_BANNER, "Banner - loadAd() - Request Banner Ad Failed: $stacktrace")
            bannerAdapterListener.adRequestFailed(
                "Banner - loadAd() - Request Banner Ad Failed: $stacktrace",
                false
            )
        }

    }


    private fun createAdListener(
        bannerAdapterListener: SASMediationBannerAdapterListener,
        bannerView: BannerView
    ): AdListener {

        return object : AdListener() {
            override fun onAdFailed(errorCode: Int) {
                super.onAdFailed(errorCode)
                Log.e(CLASS_NAME_BANNER, "Huawei Ads Banner onAdFailed Error Code :$errorCode")
                val isNoAd = errorCode == AdParam.ErrorCode.NO_AD
                bannerAdapterListener.adRequestFailed(
                    "Huawei Ads Banner onAdFailed Error Code :$errorCode",
                    isNoAd
                )
            }

            override fun onAdLeave() {
                super.onAdLeave()
                Log.d(CLASS_NAME_BANNER, "Huawei Ads Banner onAdLeave")
                bannerAdapterListener.onAdLeftApplication()
            }

            override fun onAdOpened() {
                super.onAdOpened()
                Log.d(CLASS_NAME_BANNER, "Huawei Ads Banner onAdOpened")

            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(CLASS_NAME_BANNER, "Huawei Ads Banner onAdLoaded")
                bannerAdapterListener.onBannerLoaded(bannerView)

            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.d(CLASS_NAME_BANNER, "Huawei Ads Banner onAdClicked")
                bannerAdapterListener.onAdClicked()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.d(CLASS_NAME_BANNER, "Huawei Ads Banner onAdImpression")
            }

            override fun onAdClosed() {
                super.onAdClosed()
                bannerAdapterListener.onAdClosed()
                Log.d(CLASS_NAME_BANNER, "Huawei Ads Banner onAdClosed")
            }
        }
    }

    private fun getAdSize(serverParametersString: String): BannerAdSize {
        val parameters: List<String> = serverParametersString.split(",")
        return if (parameters.size > 2) {
            Log.d(CLASS_NAME_BANNER, "Banner size is read successfully.")
            val width = parameters[1]
            val height = parameters[2]
            BannerAdSize(width.toInt(), height.toInt())
        } else {
            Log.d(CLASS_NAME_BANNER, "Banner size set to default 320x50.")
            BannerAdSize.BANNER_SIZE_320_50

        }
    }

    override fun onDestroy() {
        Log.d(CLASS_NAME_BANNER, "Huawei Ads Banner onDestroy")
        if (adView is BannerView) {
            (adView as BannerView).destroy()
        }
    }

}