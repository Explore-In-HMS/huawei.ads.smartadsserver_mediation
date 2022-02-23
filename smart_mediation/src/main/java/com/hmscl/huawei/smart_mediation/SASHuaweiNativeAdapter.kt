package com.hmscl.huawei.smart_mediation

import android.content.Context
import android.util.Log
import android.view.View
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.nativead.NativeAd
import com.huawei.hms.ads.nativead.NativeAdLoader
import com.huawei.hms.ads.nativead.NativeView
import com.smartadserver.android.library.mediation.SASMediationNativeAdAdapter
import com.smartadserver.android.library.mediation.SASMediationNativeAdAdapterListener
import java.io.PrintWriter
import java.io.StringWriter

class SASHuaweiNativeAdapter : SASHuaweiAdapterBase(), SASMediationNativeAdAdapter {

    private lateinit var adView: View
    private lateinit var nativeAd: NativeAd

    lateinit var adUnitId: String
    lateinit var adRequest: AdParam
    lateinit var nativeView: NativeView
    lateinit var builder: NativeAdLoader.Builder
    lateinit var adLoader: NativeAdLoader

    private val CLASS_NAME_NATIVE = SASHuaweiNativeAdapter::class.java.simpleName

    override fun requestNativeAd(
        context: Context,
        serverParametersString: String,
        clientParameters: MutableMap<String, Any>,
        nativeAdapterListener: SASMediationNativeAdAdapterListener
    ) {
        Log.d(CLASS_NAME_NATIVE, "requestNativeAd()")
        HwAds.init(context)

        try {
            adUnitId = getAdUnitId(serverParametersString)
            adRequest = configureAdRequest(context, clientParameters)
            nativeView = NativeView(context)
            builder = NativeAdLoader.Builder(context, adUnitId)
            adLoader = builder.build()
            adLoader.loadAd(adRequest)
        } catch (e: Exception) {
            val stacktrace =
                StringWriter().also { e.printStackTrace(PrintWriter(it)) }.toString().trim()
            Log.e(
                CLASS_NAME_NATIVE,
                "Native - loadAd() - Request Native Ad Failed: $stacktrace"
            )
            nativeAdapterListener.adRequestFailed(
                "$CLASS_NAME_NATIVE : Native - loadAd() - Request Native Ad Failed:",
                false
            )
        }

        setNativeListener(builder, nativeAdapterListener, onSuccess = {
            nativeView.setNativeAd(it)
            adView = nativeView
        }, onFail = {
            Log.e(
                CLASS_NAME_NATIVE,
                "$CLASS_NAME_NATIVE = setNativeListener() failed errorCode: $it"
            )
        })
    }

    private fun setNativeListener(
        builder: NativeAdLoader.Builder,
        nativeAdapterListener: SASMediationNativeAdAdapterListener,
        onSuccess: ((nativeAd: NativeAd) -> Unit)? = null,
        onFail: ((errorCode: Int) -> Unit)? = null
    ) {
        builder.setNativeAdLoadedListener {
            nativeAd = it
            nativeAdapterListener.onNativeAdLoaded(SASHuaweiNativeAdContent(nativeAd))
            onSuccess?.invoke(nativeAd)
        }.setAdListener(object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
                Log.d(CLASS_NAME_NATIVE, "$CLASS_NAME_NATIVE = onAdClosed()")
                nativeAdapterListener.onAdClosed()
            }

            override fun onAdFailed(errorCode: Int) {
                super.onAdFailed(errorCode)
                Log.e(CLASS_NAME_NATIVE, "$CLASS_NAME_NATIVE = onAdFailed() errorCode: $errorCode")

                val isNoAd = errorCode == AdParam.ErrorCode.NO_AD
                nativeAdapterListener.adRequestFailed(errorCode.toString(), isNoAd)
                onFail?.invoke(errorCode)
            }

            override fun onAdLeave() {
                super.onAdLeave()
                nativeAdapterListener.onAdLeftApplication()
                Log.d(CLASS_NAME_NATIVE, "$CLASS_NAME_NATIVE = onAdLeave()")
            }

            override fun onAdOpened() {
                super.onAdOpened()
                Log.d(CLASS_NAME_NATIVE, "$CLASS_NAME_NATIVE = onAdOpened()")
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(CLASS_NAME_NATIVE, "$CLASS_NAME_NATIVE = onAdLoaded()")
            }

            override fun onAdClicked() {
                super.onAdClicked()
                nativeAdapterListener.onAdClicked()
                Log.d(CLASS_NAME_NATIVE, "$CLASS_NAME_NATIVE = oAdClicked()")
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.d(CLASS_NAME_NATIVE, "$CLASS_NAME_NATIVE = onAdImpression()")
            }
        }).build()
    }

    override fun onDestroy() {
        if (adView is NativeView) {
            (adView as NativeView).destroy()
            Log.d(CLASS_NAME_NATIVE, "$CLASS_NAME_NATIVE = onDestroy()")
        }
    }
}