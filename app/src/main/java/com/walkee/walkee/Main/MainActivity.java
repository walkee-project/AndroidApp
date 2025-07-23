package com.walkee.walkee.Main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieManager;
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
    private WebView webView;
    private long backKeyPressedTime = 0;

    Toast toast;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_icemain);
        webView = findViewById(R.id.main_view);

        // 쿠키 설정 추가
        setupCookieManager();

        AndroidBridge androidBridge = new AndroidBridge(this);
        webViewSetting(webView, androidBridge);
        FirebaseApp.initializeApp(this);
        //DisconnectHandler dh = new DisconnectHandler(this,this);
        //dh.netWorkChecking(this,getSupportFragmentManager());

        webView.loadUrl(PageInfo.INDEX_PAGE);
    }

    // 쿠키 매니저 설정 메소드 추가
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

        // 쿠키 동기화 (Android 5.0 이상)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        }

        Log.d("MainActivity", "Cookie manager setup completed");
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
    }

    @Override
    public void onBackPressed() {
        // 그냥 back 메시지 하나만 보냄
        webView.evaluateJavascript("window.dispatchEvent(new CustomEvent('ANDROID_BACK'))", null);
    }

    public void QR_Btn() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

            String ScanResult = result.getContents();

            System.out.println("result : " + ScanResult);

            Toast.makeText(this, ScanResult, Toast.LENGTH_LONG).show();

            StringBuilder script = new StringBuilder();

            script.append("javascript:output('" + ScanResult + "')");

            webView.evaluateJavascript(String.valueOf(script), null);
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