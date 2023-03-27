package com.example.williamhybrid;

import android.app.Application;

import com.igaworks.v2.core.AdBrixRm;
import com.igaworks.v2.core.result.OnDeeplinkResult;
import com.igaworks.v2.core.result.OnDeferredDeeplinkResult;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        sdkInit();
    }
    private void sdkInit(){
        AdBrixRm.init(this, "{앱키}", "{시크릿키}");
        AdBrixRm.setOnDeeplinkListener(new AdBrixRm.onDeeplinkListener() {
            @Override
            public void onReceive(OnDeeplinkResult onDeeplinkResult) {
                String deeplinkUrl = onDeeplinkResult.getDeeplink();
            }
        });
        AdBrixRm.setOnDeferredDeeplinkListener(new AdBrixRm.onDeferredDeeplinkListener() {
            @Override
            public void onReceive(OnDeferredDeeplinkResult onDeferredDeeplinkResult) {
                String deeplinkUrl = onDeferredDeeplinkResult.getDeeplink();
            }
        });
    }
}
