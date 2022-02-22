package com.hmscl.huawei.smart_mediation

import android.app.Activity
import android.content.Context
import android.util.Log
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.InterstitialAd
import com.smartadserver.android.library.mediation.SASMediationInterstitialAdapter
import com.smartadserver.android.library.mediation.SASMediationInterstitialAdapterListener
import com.smartadserver.android.library.util.SASUtil
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.ref.WeakReference


class SASHuaweiInterstitialAdapter : SASHuaweiAdapterBase(), SASMediationInterstitialAdapter {

    private val CLASS_NAME_INTERSTITIAL = SASHuaweiInterstitialAdapter::class.java.simpleName
    var interstitialAd: InterstitialAd? = null
    private lateinit var mContext: Context


    override fun requestInterstitialAd(
        context: Context,
        serverParametersString: String,
        clientParameters: MutableMap<String, Any>,
        interstitialAdapterListener: SASMediationInterstitialAdapterListener
    ) {

        Log.d(CLASS_NAME_INTERSTITIAL, "requestInterstitialAd()")
        HwAds.init(context)
        try {
            this.mContext = context
            val adUnitID: String = getAdUnitId(serverParametersString)
            val adRequest = configureAdRequest(context, clientParameters)
            val interstitialAdListener: AdListener = createAdListener(interstitialAdapterListener)
            val interstitialAd = InterstitialAd(context)
            interstitialAd.adId = adUnitID
            interstitialAd.adListener = interstitialAdListener
            interstitialAd.loadAd(adRequest)
            this.interstitialAd = interstitialAd
        } catch (e: Exception) {
            val stacktrace = StringWriter().also { e.printStackTrace(PrintWriter(it)) }.toString().trim()
            Log.e(
                CLASS_NAME_INTERSTITIAL,
                "Interstitial - loadAd() - Request Interstitial Ad Failed: $stacktrace"
            )
            interstitialAdapterListener.adRequestFailed(
                "Interstitial - loadAd() - Request Interstitial Ad Failed: $stacktrace",
                false
            )
        }


    }


    private fun createAdListener(interstitialAdapterListener: SASMediationInterstitialAdapterListener): AdListener {
        return object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(CLASS_NAME_INTERSTITIAL, "Huawei Ads onAdLoaded for interstitial")
                interstitialAdapterListener.onInterstitialLoaded()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.d(CLASS_NAME_INTERSTITIAL, "Huawei Ads onAdClicked for interstitial")
                interstitialAdapterListener.onAdClicked()

            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.d(CLASS_NAME_INTERSTITIAL, "Huawei Ads onAdImpression for interstitial")
            }

            override fun onAdClosed() {
                super.onAdClosed()
                interstitialAdapterListener.onAdClosed()
                onDestroy()
                Log.d(CLASS_NAME_INTERSTITIAL, "Huawei Ads  onAdClosed for interstitial")

            }

            override fun onAdLeave() {
                super.onAdLeave()
                interstitialAdapterListener.onAdLeftApplication()
                Log.d(CLASS_NAME_INTERSTITIAL, "Huawei Ads onAdLeave for interstitial")

            }

            override fun onAdFailed(p0: Int) {
                super.onAdFailed(p0)
                val isNoAd = p0 == AdParam.ErrorCode.NO_AD
                interstitialAdapterListener.adRequestFailed(
                    "$CLASS_NAME_INTERSTITIAL : Error code:$p0",
                    isNoAd
                )
                Log.d(CLASS_NAME_INTERSTITIAL, "Huawei Ads onAdFailed for interstitial")

            }

            override fun onAdOpened() {
                super.onAdOpened()
                interstitialAdapterListener.onInterstitialShown()
                Log.d(CLASS_NAME_INTERSTITIAL, "Huawei Ads onAdOpened for interstitial")
            }
        }
    }

    override fun showInterstitial() {
        if (isInterstitialAdLoaded()) {

            val mWeakActivity = WeakReference(mContext)
            interstitialAd.let {
                mWeakActivity.get()?.let {
                    SASUtil.getMainLooperHandler().post {
                        interstitialAd?.show(mContext as Activity?)
                    }
                } ?: throw Exception("Rewarded ad activity is null")
            }
        } else {
            throw Exception("No Huawei mobile ads interstitial ad shown.")
        }

    }

    private fun isInterstitialAdLoaded(): Boolean {
        if (interstitialAd != null) {
            if (interstitialAd is InterstitialAd) {
                return (interstitialAd as InterstitialAd).isLoaded
            }
        }
        return false
    }

    override fun onDestroy() {
        interstitialAd = null
        Log.d(CLASS_NAME_INTERSTITIAL, "onDestroy()")
    }

}