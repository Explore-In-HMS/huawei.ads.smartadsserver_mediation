<h1 align="center">Huawei-Smart Ad Server Mediation Github Documentation</h3>

 ![Latest Version](https://img.shields.io/badge/latestVersion-1.0.0-yellow) ![Kotlin](https://img.shields.io/badge/language-kotlin-blue)
<br>
![Supported Platforms](https://img.shields.io/badge/Supported_Platforms:-Native_Android-orange)
 
# Introduction

In this documentation we explained how to use Huawei-Smartadserver mediation.

# Compatibility

|   | Banner Ad | Interstitial Ad | Rewarded Ad | Native Ad |
| --- | --- | --- | --- | --- |
| Native (Java/Kotlin) | ✅ | ✅ | ✅ | ✅ |

# How to start?
  
## Create an ad unit on Huawei Publisher Service

1. Sign in to [Huawei Developer Console](https://developer.huawei.com/consumer/en/console) and create an AdUnit.

## Create a mediation on Smart ad server publisher console


1. Sign in to [Smartadserver Console](https://manage.smartadserver.com/)
2. Go to "**Inventory -> Sites & Pages**" and create a site and a page for your mediation. (skip this part if you have your Smartadserver site ID and page ID)
3. Go to "**Inventory -> Formats**" and create an Ad unit  for your Ad type (put 0x0 for the size and note your Format ID).
4. Go to "**Campaigns -> Campaign List**" and create a new campaign, (Select mediation on the pop-up,give your campaign a name and an expiration date)
5. Select "**New Insertion**" inside your created campaign.
6. Switch to classical mode UI from left side of the web page, give your insertion a name, select "**Mobile**" as category, select "**AdNetwork / Mediation**" as channel  and select "**Pay Ads**" for the type. Select other fields according to your needs and click "**Save and Go to Next Step**"
7. On the "**Placement tab**", select your ad unit that is created on the step-3, and select the site and page that are created on the step-2. Click "**Save and Go to Next Step**".
8. On the "**Creatives tab**", click "**Template Library**", on "**Offical Templates**" select "**In-App Mediation Custom Adapter**" 
9. A section will be seen named "**Predefined template**". Give it a name, and select the ad type (Banner,Rewarded etc.)
10. For the "**Banner width**" and "**Banner height**" don't write anything.
11. For the "**Adapter Class**" and "**Placement info**" check the table below.


## Custom Data for the Huawei Adapter (Predefined template)

|  Ad Types | Banner Ad | Interstitial Ad | Rewarded Ad | Native Ad |
| --- | --- | --- | --- | --- |
| Adapter Class | com.hmscl.huawei.ads.smart_mediation.SASHuaweiBannerAdapter |  com.hmscl.huawei.ads.smart_mediation.SASHuaweiInterstitialAdapter |  com.hmscl.huawei.ads.smart_mediation.SASHuaweiRewardedVideoAdapter |  com.hmscl.huawei.ads.smart_mediation.SASHuaweiNativeAdapter  |
| Placement info	| Write the Huawei Ad unit ID,banner width and banner height (sepetate them with ",") | Write the Huawei Ad unit ID with no spaces.| Write the Huawei Ad unit ID with no spaces. | Write the Huawei Ad unit ID with no spaces. |


### Custom Data Details for Banner Ads


1-) Placement Info for Banner example
 
![placementinfoPNG](https://user-images.githubusercontent.com/53767481/154480432-12a48af5-e8a1-4eae-bfa6-c9b8978c3b07.PNG)

2-) Ad width and height values for Huawei Banner Ads :

![image](https://user-images.githubusercontent.com/53767481/154481304-3b6ee843-203d-4441-9fd0-1dfd77dd376d.png)




**Important Note:**  Only BANNER_SIZE_360_57 and BANNER_SIZE_360_144 are supported in the Chinese mainland.


<h1 id="integrate-huawei-sdk">
Integrate the Huawei Ads SDK
</h1>

In the **project-level** build.gradle, include Huawei's Maven repository.

```groovy
repositories {
    google()
    jcenter() // Also, make sure jcenter() is included
    maven { url 'https://developer.huawei.com/repo/' } // Add this line
    maven {url "https://jitpack.io"} // Add this line
}

...

allprojects {
    repositories {
        google()
        jcenter() // Also, make sure jcenter() is included
        maven { url 'https://developer.huawei.com/repo/' } //Add this line
        maven {url "https://jitpack.io"} // Add this line
    }
}
```
<h1 id="app-level">
</h1>
In the app-level build.gradle, include Huawei Ads dependency (required by the adapter) and the adapter

```groovy
dependencies {
    implementation 'com.huawei.hms:ads:3.4.49.305'
    implementation 'our-library'
    implementation 'com.smartadserver.android:smart-core-sdk-huawei-support:1.0.0'
}
```


[Check the latest Huawei Ads SDK here](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/publisher-service-version-change-history-0000001050066909)

[Check the latest version of adapter here](#version-change-history)



**Important:** _To add Huawei Ads Kit SDK and Mediation adapter, the native project should be opened with Android Studio._

## **Permissions**
The HUAWEI Ads SDK (com.huawei.hms:ads) has integrated the required permissions. Therefore, you do not need to apply for these permissions. <br />

**android.permission.ACCESS_NETWORK_STATE:** Checks whether the current network is available.   <br/>

**android.permission.ACCESS_WIFI_STATE:** Obtains the current Wi-Fi connection status and the information about WLAN hotspots. <br />

**android.permission.BLUETOOTH:** Obtains the statuses of paired Bluetooth devices. (The permission can be removed if not necessary.) <br />

**android.permission.CAMERA:** Displays AR ads in the Camera app. (The permission can be removed if not necessary.) <br />

**android.permission.READ_CALENDAR:** Reads calendar events and their subscription statuses. (The permission can be removed if not necessary.) <br />

**android.permission.WRITE_CALENDAR:** Creates a calendar event when a user clicks the subscription button in an ad. (The permission can be removed if not necessary.) <br />

## **Configuring Obfuscation Scripts**
Before building the APK, configure the obfuscation configuration file to prevent the HUAWEI Ads SDK () from being obfuscated.

Open the obfuscation configuration file proguard-rules.pro in the app-level directory of your Android project, and add configurations to exclude the HUAWEI Ads SDK from obfuscation.

```groovy
-keep class com.huawei.openalliance.ad.** { *; }
-keep class com.huawei.hms.ads.** { *; }
```

## **Configuring Network Permissions**
To allow HTTP and HTTPS network requests on devices with targetSdkVersion 28 or later, configure the following information in the AndroidManifest.xml file 
(not adding it might effect rewarded ad mediation):

```groovy
<application
    ...
    android:usesCleartextTraffic="true"
    >
    ...
</application>
```



# Version Change History

## 1.0.0

First version of the Custom Adapter.


# Platforms

## Native

This section demonstrates how to use smartadserver mediation feature with Huawei Ads Kit on Native android app.

Make sure to check the article on (edit after medium) [How to use Huawei Ads with AdMob mediation (Native Android)](https://medium.com/huawei-developers/how-to-use-huawei-ads-with-admob-mediation-native-android-8fc41438dfad)

Firstly, integrate the Display SDK for Android

[Smartadserver Android Display SDK](https://documentation.smartadserver.com/displaySDK/android/gettingstarted.html) can be used for all ad types.

**Note** : Developers can find app level build.gradle in their project from __**"app-folder/app/build.gradle"**__

### **Banner Ad**

To use _Banner_ ads in Native android apps, please check the Display SDK. Click [here](https://documentation.smartadserver.com/displaySDK/android/integration/banner.html) to get more information about Display SDKs _Banner_ Ad development.

### **Interstitial Ad**

To use Interstitial ads in Native android apps, please check the Display SDK. Click [here](https://documentation.smartadserver.com/displaySDK/android/integration/interstitial.html) to get more information about Display SDKs Interstitial Ad development.

### **Rewarded Ad**

To use _Rewarded_ ads in Native android _Rewarded_, please check the Display SDK. Click [here](https://documentation.smartadserver.com/displaySDK/android/integration/rewardedvideo.html) to get more information about Display SDKs _Banner_ Ad development.

### **Native Ads**

To use _Native_ ads in Native android apps, please check the Display SDK. Click [here](https://documentation.smartadserver.com/displaySDK/android/integration/nativeads.html) to get more information about Display SDKs _Native_ Ad development.


**Note:** Click [here](https://github.com/smartadserver/smart-display-android-samples) to check the Smartadserver's offical demo project.


# Screenshots

## Smartadserver
<table>
<tr>
<td>
<img src="https://user-images.githubusercontent.com/53767481/155124684-e8b75689-6d5d-4a1e-abba-fef4c2398180.png" width="200">
 
Banner Ad
</td>

<td>
<img src="https://user-images.githubusercontent.com/53767481/155125418-dcfed49a-8852-46ea-a84d-7ef26658a4c1.png" width="200">

Interstitial Ad
</td>

<td>
<img src="https://user-images.githubusercontent.com/53767481/155125537-8b0a2c0a-a776-44ea-b2d0-7bc6624704e1.png" width="200">

Rewarded Ad
</td>
 
<td>
<img src="https://user-images.githubusercontent.com/53767481/155125689-3fff3099-7031-4080-8092-7f8d45b8aa73.png" width="200">

Native Ad
</td>
</tr>
</tr>
</table>

## Huawei
<table>
<tr>
<td>
<img src="https://user-images.githubusercontent.com/53767481/155125746-b0976044-d7f1-41fc-acf9-687e85dd1a0e.png" width="200">

Banner Ad
</td>


<td>
<img src="https://user-images.githubusercontent.com/53767481/155125986-7d44f098-6700-4a86-9853-4d564bdc7b8a.png" width="200">

Interstitial Ad
</td>

<td>
<img src="https://user-images.githubusercontent.com/53767481/155126045-2f9d578b-418b-4744-9df6-766410dff383.png" width="200">

Rewarded Ad
</td>
<td>
<img src="https://user-images.githubusercontent.com/53767481/155126115-729a9b6d-6081-4061-b958-e78239f5833b.png" width="200">

Native Ad
</td>

</tr>
</tr>
</table>
