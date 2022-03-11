package com.hmscl.huawei.smart_mediation

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import com.huawei.hms.ads.nativead.NativeAd
import com.smartadserver.android.library.mediation.SASMediationNativeAdContent
import com.smartadserver.android.library.model.SASNativeVideoAdElement
import kotlin.math.roundToInt

open class SASHuaweiNativeAdContent constructor(var huaweiNativeAd: NativeAd) :
    SASMediationNativeAdContent {

    private val nativeVideoAdElement = SASNativeVideoAdElement()

    companion object {
        private const val HTTPS_TAG = "https://"
        private const val HTTP_TAG = "http://"
        private var TAG = SASHuaweiNativeAdContent::class.java.simpleName
    }

    init {
        lateinit var videoUrl: String
        if (huaweiNativeAd.videoOperator.hasVideo()) {

            if (huaweiNativeAd.video.uri != null)
                Log.d(TAG, "Huawei Native Ad Video url exist")
            else
                Log.d(TAG, "Huawei Native Ad Video url empty")

            videoUrl = huaweiNativeAd.video.uri.toString()

            nativeVideoAdElement.videoUrl = videoUrl
            nativeVideoAdElement.isAutoplay = true
            nativeVideoAdElement.backgroundColor = Color.WHITE
            nativeVideoAdElement.videoVerticalPosition =
                SASNativeVideoAdElement.VIDEO_POSITION_CENTER
            nativeVideoAdElement.mediaWidth = 1000
            nativeVideoAdElement.mediaHeight =
                (1000 / huaweiNativeAd.video.aspectRatio).roundToInt()

            Log.d(TAG, "Huawei Native Ad Video aspectRatio is ${huaweiNativeAd.video.aspectRatio}")

            val videoStartPixel: String = videoUrl
            if (videoStartPixel.isNotEmpty()) {
                nativeVideoAdElement.setEventTrackingURLs(
                    SASNativeVideoAdElement.TRACKING_EVENT_NAME_START,
                    arrayOf(videoStartPixel)
                )
            }

            val videoEndPixel: String =
                huaweiNativeAd.video.duration.toString()
            if (videoEndPixel.isNotEmpty()) {
                nativeVideoAdElement.setEventTrackingURLs(
                    SASNativeVideoAdElement.TRACKING_EVENT_NAME_COMPLETE,
                    arrayOf(videoEndPixel)
                )
            }
        }
    }

    override fun getTitle(): String {
        return huaweiNativeAd.title
    }

    override fun getSubTitle(): String {
        return huaweiNativeAd.adSource
    }

    override fun getBody(): String {
        return if (huaweiNativeAd.description != null) {
            huaweiNativeAd.description
        } else ""
    }

    override fun getIconUrl(): String {
        return if (huaweiNativeAd.icon != null) {
            huaweiNativeAd.icon.uri.toString().replace(HTTP_TAG, HTTPS_TAG)
        } else ""
    }

    override fun getIconWidth(): Int {
        return if (huaweiNativeAd.icon != null) {
            huaweiNativeAd.icon.width
        } else -1
    }

    override fun getIconHeight(): Int {
        return if (huaweiNativeAd.icon != null) {
            huaweiNativeAd.icon.height
        } else -1
    }

    override fun getCoverImageUrl(): String {
        return huaweiNativeAd.images[0].uri.toString().replace(HTTP_TAG, HTTPS_TAG)
    }

    override fun getCoverImageWidth(): Int {
        return huaweiNativeAd.images[0].width
    }

    override fun getCoverImageHeight(): Int {
        return huaweiNativeAd.images[0].height
    }

    override fun getRating(): Float {
        return if (huaweiNativeAd.rating != null)
            huaweiNativeAd.rating.toFloat()
        else 5f
    }

    override fun getCallToAction(): String {
        return huaweiNativeAd.callToAction
    }

    override fun getSponsoredMessage(): String {
        return ""
    }

    override fun getMediaElement(): SASNativeVideoAdElement? {
        return nativeVideoAdElement
    }

    override fun getMediaView(context: Context): View? {
        return huaweiNativeAd.mediaContent as View?
    }

    override fun unregisterView(view: View) {
        Log.i(TAG, "unregisterView method called.")
    }

    override fun registerView(view: View, array: Array<out View>?) {
        Log.i(TAG, "registerView method called.")
    }

    override fun getAdChoicesUrl(): String {
        return if (huaweiNativeAd.choicesInfo != null) {
            huaweiNativeAd.choicesInfo.toString()
        } else ""
    }
}