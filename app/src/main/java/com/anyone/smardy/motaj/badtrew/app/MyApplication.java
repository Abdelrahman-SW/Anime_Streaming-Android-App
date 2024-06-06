package com.anyone.smardy.motaj.badtrew.app;

import androidx.multidex.MultiDexApplication;

//import com.onesignal.OneSignal
// import com.verizon.ads.VASAds;

public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        //verizon sdk init
        //VASAds.initialize(this, VASAds.getSiteId());
        // OneSignal Initialization
//        OneSignal.startInit(this)
//                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                .unsubscribeWhenNotificationsAreDisabled(true)
//                .init();
    }
}
