package com.walkee.walkee.Common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @SuppressLint("JavascriptInterface")
    public void webViewSetting(WebView webView, Object Bridge) {
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.addJavascriptInterface(Bridge, "HybridApp");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                return LinkedUrl(url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                // 필요한 리소스를 확인
                String[] requestedResources = request.getResources();
                for (String resource : requestedResources) {
                    if (resource.equals(PermissionRequest.RESOURCE_VIDEO_CAPTURE) || // 카메라
                            resource.equals(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) { // 마이크
                        // 권한 허용
                        request.grant(request.getResources());
                        return;
                    }
                }
                // 필요한 리소스가 없으면 거부
                request.deny();

            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true, false);
            }
        });

    }






    public boolean LinkedUrl(String url) {
        if (url.startsWith("sms:")) {
            // sms: 문자 메시지 작성
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
            startActivity(intent);
            return true;
        } else if (url.startsWith("tel:")) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            //전화거는 화면까지만 이동 시킬꺼면 Intent.ACTION_DIAL
            //전화를 바로 걸려면 Intent.ACTION_CALL
            // 전화번호를 누르면 키패드에 전화번호 작성 호출. (전화를 바로 걸려면 Intent.ACTION_CALL 로 코드 변경하기)
            startActivity(intent);
            return true;
        }
        return false;
    }
}
