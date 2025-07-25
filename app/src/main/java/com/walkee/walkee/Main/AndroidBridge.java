package com.walkee.walkee.Main;

import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.app.Activity;
import android.widget.Toast;

import android.util.Log;

public class AndroidBridge {
    private static final String TAG = "AndroidBridge"; // TAG 변수 선언
    Activity activity;

    public AndroidBridge(Activity activity) {
        this.activity = activity;
    }

    // React Native WebView에서 postMessage를 통해 호출되는 함수
    @JavascriptInterface
    public void postMessage(String message) {
        if ("EXIT_APP".equals(message)) {
            Log.d(TAG, "postMessage called with message: " + message);
            Toast.makeText(activity, "JS에서 전달받은 메시지: " + message, Toast.LENGTH_SHORT).show();

            activity.runOnUiThread(() -> {
                activity.finishAffinity();
                System.runFinalization();
                System.exit(0);  // 강제 종료
            });
        }
    }

}