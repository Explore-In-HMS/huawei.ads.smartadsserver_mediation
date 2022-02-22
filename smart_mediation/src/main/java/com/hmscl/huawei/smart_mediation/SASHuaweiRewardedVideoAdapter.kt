package com.hmscl.huawei.smart_mediation

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.reward.*
import com.smartadserver.android.library.mediation.SASMediationRewardedVideoAdapter
import com.smartadserver.android.library.mediation.SASMediationRewardedVideoAdapterListener
import com.smartadserver.android.library.model.SASReward
import com.smartadserver.android.library.util.SASUtil
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.ref.WeakReference


class SASHuaweiRewardedVideoAdapter : SASHuaweiAdapterBase(), SASMediationRewardedVideoAdapter {

    private val CLASS_NAME_REWARDED = SASHuaweiRewardedVideoAdapter::class.java.simpleName


    private var rewardedVideoAd: RewardAd? = null
    private lateinit var mContext: Context
    private lateinit var rewardedVideoAdapterListener: SASMediationRewardedVideoAdapterListener


    override fun requestRewardedVideoAd(
        context: Context,
        serverParametersString: String,
        clientParameters: MutableMap<String, Any>,
        rewardedVideoAdapterListener: SASMediationRewardedVideoAdapterListener
    ) {

        Log.d(CLASS_NAME_REWARDED, "requestRewardedVideoAd()")
        this.mContext = context
        HwAds.init(mContext)
        this.rewardedVideoAdapterListener = rewardedVideoAdapterListener
        val adUnitID: String = getAdUnitId(serverParametersString)
        rewardedVideoAd = RewardAd(context, adUnitID)

        try {
            rewardedVideoAd!!.loadAd(
                configureAdRequest(context, clientParameters),
                createAdLoadCallback(rewardedVideoAdapterListener)
            )
        } catch (e: Exception) {
            val stacktrace = StringWriter().also { e.printStackTrace(PrintWriter(it)) }.toString().trim()
            Log.e(
                CLASS_NAME_REWARDED,
                "Rewarded - loadAd() - Request Rewarded Ad Failed: $stacktrace"
            )

            rewardedVideoAdapterListener.adRequestFailed(
                "$CLASS_NAME_REWARDED : Rewarded - loadAd() - Request Rewarded Ad Failed:",
                false
            )
        }


    }

    private fun createAdLoadCallback(rewardedVideoAdapterListener: SASMediationRewardedVideoAdapterListener): RewardAdLoadListener {
        return object : RewardAdLoadListener() {
            override fun onRewardedLoaded() {
                super.onRewardedLoaded()
                Log.d(CLASS_NAME_REWARDED, "onRewardedLoaded")
                rewardedVideoAdapterListener.onRewardedVideoLoaded()
            }

            override fun onRewardAdFailedToLoad(errorCode: Int) {
                super.onRewardAdFailedToLoad(errorCode)
                val isNoAd = errorCode == AdParam.ErrorCode.NO_AD
                Log.d(CLASS_NAME_REWARDED, "onRewardAdFailedToLoad")
                rewardedVideoAdapterListener.adRequestFailed(
                    "$CLASS_NAME_REWARDED : Error code:$errorCode",
                    isNoAd
                )
            }
        }
    }


    @Throws(Exception::class)
    override fun showRewardedVideoAd() {

        if (rewardedVideoAd!!.isLoaded) {

            val mWeakActivity = WeakReference(mContext)

            rewardedVideoAd.let {
                mWeakActivity.get()?.let {
                    SASUtil.getMainLooperHandler().post {

                        val mRewardedAdCallback = object : RewardAdStatusListener() {
                            override fun onRewardAdFailedToShow(p0: Int) {
                                super.onRewardAdFailedToShow(p0)
                                Log.d(CLASS_NAME_REWARDED, "onRewardAdFailedToShow")
                                rewardedVideoAdapterListener.onRewardedVideoFailedToShow("$CLASS_NAME_REWARDED : Error code:$p0")
                            }

                            override fun onRewardAdOpened() {
                                super.onRewardAdOpened()
                                Log.d(CLASS_NAME_REWARDED, "onRewardAdOpened")
                                rewardedVideoAdapterListener.onRewardedVideoShown()
                            }

                            override fun onRewardAdClosed() {
                                super.onRewardAdClosed()
                                rewardedVideoAdapterListener.onAdClosed()
                                Log.d(CLASS_NAME_REWARDED, "onRewardAdClosed")
                                onDestroy()
                            }

                            override fun onRewarded(rewardItem: Reward) {
                                super.onRewarded(rewardItem)
                                Log.d(CLASS_NAME_REWARDED, "onRewarded")
                                val reward = SASReward(
                                    rewardItem.name,
                                    rewardItem.amount.toDouble()
                                )
                                rewardedVideoAdapterListener.onReward(reward)

                            }

                        }
                        Handler(Looper.getMainLooper()).post {
                            rewardedVideoAd!!.show(
                                mContext as Activity?,
                                mRewardedAdCallback
                            )
                        }
                    }
                } ?: throw Exception("Rewarded ad activity is null")
            }

        } else {
            Log.d(CLASS_NAME_REWARDED, "Rewarded ad is not loaded properly.")
        }


    }

    override fun onDestroy() {
        Log.d(CLASS_NAME_REWARDED, "Rewarded ad onDestroy()")
        rewardedVideoAd?.destroy()
    }
}
