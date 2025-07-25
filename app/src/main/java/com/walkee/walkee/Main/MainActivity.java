package com.walkee.walkee.Main;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.walkee.walkee.Common.BaseActivity;
import com.walkee.walkee.Common.PageInfo;

import com.walkee.walkee.R;
import com.google.firebase.FirebaseApp;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends BaseActivity {
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int FILE_CHOOSER_RESULT_CODE = 2; // 파일 선택 결과 코드 추가
    private static final int PERMISSION_REQUEST_CODE = 200; // 권한 요청 코드 추가

    private WebView webView;
    private long backKeyPressedTime = 0;
    private ValueCallback<Uri[]> mFilePathCallback; // 파일 선택 콜백 추가

    Toast toast;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_icemain);
        webView = findViewById(R.id.main_view);

        // 권한 요청 추가
        requestAllPermissions();

        // 쿠키 설정 추가
        setupCookieManager();

        // WebChromeClient 설정 추가
        setupWebChromeClient();

        FirebaseApp.initializeApp(this);
        //DisconnectHandler dh = new DisconnectHandler(this,this);
        //dh.netWorkChecking(this,getSupportFragmentManager());

// ✅ 이 줄 추가!
        AndroidBridge androidBridge = new AndroidBridge(this);
        webView.addJavascriptInterface(androidBridge, "AndroidBridge");
        webView.getSettings().setJavaScriptEnabled(true);


// WebView 설정
        webViewSetting(webView, androidBridge);


        webView.loadUrl(PageInfo.INDEX_PAGE);
    }

    // 모든 필요한 권한 요청
    private void requestAllPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    // WebChromeClient 설정 메소드 추가
    private void setupWebChromeClient() {
        webView.setWebChromeClient(new WebChromeClient() {
            // 위치 권한 처리
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                Log.d("MainActivity", "Geolocation permission requested for: " + origin);
                callback.invoke(origin, true, false);
            }

            // 파일 선택 처리 (Android 5.0+)
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {

                Log.d("MainActivity", "File chooser requested");

                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE);
                } catch (ActivityNotFoundException e) {
                    Log.e("MainActivity", "Cannot start file chooser", e);
                    mFilePathCallback = null;
                    Toast.makeText(MainActivity.this, "파일 선택기를 열 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
        });
    }

    // 쿠키 매니저 설정 메소드 (기존 + 추가 설정)
    private void setupCookieManager() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        // WebView 설정도 함께
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // 추가 설정들
        webSettings.setGeolocationEnabled(true); // 위치 정보 활성화
        webSettings.setAllowFileAccess(true); // 파일 접근 허용
        webSettings.setAllowContentAccess(true); // 컨텐츠 접근 허용
        webSettings.setMediaPlaybackRequiresUserGesture(false); // 미디어 자동 재생 허용

        // 쿠키 동기화 (Android 5.0 이상)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        }

        Log.d("MainActivity", "Cookie manager and WebView settings completed");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //startLocationService();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }

        // 새로운 권한 요청 결과 처리
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.w("MainActivity", "Permission denied: " + permissions[i]);
//                    Toast.makeText(this, permissions[i] + " 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("MainActivity", "Permission granted: " + permissions[i]);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // 그냥 back 메시지 하나만 보냄
        webView.evaluateJavascript("window.dispatchEvent(new CustomEvent('ANDROID_BACK'))", null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // QR 코드 스캔 결과 처리 (기존 코드)
        if(resultCode == Activity.RESULT_OK && requestCode != FILE_CHOOSER_RESULT_CODE){
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

            String ScanResult = result.getContents();

            System.out.println("result : " + ScanResult);

            Toast.makeText(this, ScanResult, Toast.LENGTH_LONG).show();

            StringBuilder script = new StringBuilder();

            script.append("javascript:output('" + ScanResult + "')");

            webView.evaluateJavascript(String.valueOf(script), null);
        }

        // 파일 선택 결과 처리 (새로 추가)
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (mFilePathCallback == null) return;

            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK && data != null) {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                    Log.d("MainActivity", "File selected: " + dataString);
                } else {
                    Log.d("MainActivity", "No file selected");
                }
            } else {
                Log.d("MainActivity", "File chooser cancelled");
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        }

//        Intent intent = new Intent();
//        intent.putExtra("result",result.getContents());
//        setResult(RESULT_OK, intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 쿠키 동기화 (메모리에서 디스크로)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().flush();
        }
    }
}