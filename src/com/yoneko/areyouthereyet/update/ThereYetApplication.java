package com.yoneko.areyouthereyet.update;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.flurry.android.FlurryAgent;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.multidex.MultiDex;

import io.fabric.sdk.android.Fabric;

/**
 * Created by risaki on 2/20/16.
 */
public class ThereYetApplication extends Application {

    public static String FLURRY_KEY = "JGBTZJXTZFXBS5VY6T56";
    //TODO: turn off debug at the end.
    public static boolean debug = false;

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(debug).build())
                .build();
    }

    public static String getVersionName(Context context) {
        PackageInfo pInfo;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "error";
    }

    public static int getVersionCode(Context context) {
        PackageInfo pInfo;

        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
