package com.walkee.walkee.Main;

import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.app.Activity;
import android.widget.Toast;

public class AndroidBridge {
    Activity activity;

    public AndroidBridge(Activity activity) {
        this.activity = activity;
    }

    // React Native WebView에서 postMessage를 통해 호출되는 함수
    @JavascriptInterface
    public void postMessage(String message) {
        if ("EXIT_APP".equals(message)) {
            activity.runOnUiThread(() -> {
                Toast.makeText(activity, "앱을 종료합니다.", Toast.LENGTH_SHORT).show();

                // ✅ 확실하게 종료 처리
                activity.finishAffinity();
                System.runFinalization();
                System.exit(0);  // 강제 종료
            });
        }
    }

}