package com.hmscl.huawei.smart_mediation

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.NonPersonalizedAd
import com.smartadserver.android.library.mediation.SASMediationAdapter
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Exception


open class SASHuaweiAdapterBase {

    val ADAPTER_NAME = SASHuaweiAdapterBase::class.java.simpleName

    fun getAdUnitId(serverParametersString: String): String {
        val parameters: List<String> = serverParametersString.split(",")
        if (parameters.isNotEmpty()) {
            Log.d(ADAPTER_NAME, "adUnitID read successfully.")
            return parameters[0]

        }
        return ""
    }

    fun configureAdRequest(context: Context, clientParameters: MutableMap<String, Any>): AdParam {

        val adParamBuilder = AdParam.Builder()

        try {
            val GDPRApplies = clientParameters[SASMediationAdapter.GDPR_APPLIES_KEY] as String?

            val userConsent = if ("false".equals(GDPRApplies, ignoreCase = true)) {
                true
            } else {

                val sharedPreferences: SharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context)
                val smartConsentStatus =
                    sharedPreferences.getString("Smart_advertisingConsentStatus", null)
                "1" == smartConsentStatus
            }

            if (userConsent) {
                adParamBuilder.setNonPersonalizedAd(NonPersonalizedAd.ALLOW_ALL)
                Log.d(ADAPTER_NAME, "user consent is 1,personalized ads are allowed.")
            } else {
                adParamBuilder.setNonPersonalizedAd(NonPersonalizedAd.ALLOW_NON_PERSONALIZED)
                Log.d(ADAPTER_NAME, "user consent is 0,only non-personalized ads are allowed.")
            }
        } catch (e: Exception) {
            val stacktrace =
                StringWriter().also { e.printStackTrace(PrintWriter(it)) }.toString().trim()
            Log.e(ADAPTER_NAME, "catch block: $stacktrace")
        }

        /**
         * TCF2.0
         */
        try {
            val sharedPref = context.getSharedPreferences(
                "SharedPreferences",
                Context.MODE_PRIVATE
            )
            val tcfString = sharedPref?.getString("IABTCF_TCString", "");

            if (tcfString != null && tcfString != "") {
                adParamBuilder.setConsent(tcfString)
                Log.d(ADAPTER_NAME, "TCF 2.0 string has been set.")
            }
        } catch (exception: Exception) {
            Log.e(ADAPTER_NAME, "Tcf String couldn't read.")
        }

        return adParamBuilder.build()
    }


}