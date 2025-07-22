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
import android.webkit.WebView;
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
        AndroidBridge androidBridge = new AndroidBridge(this);
        webViewSetting(webView, androidBridge);
        FirebaseApp.initializeApp(this);
        //DisconnectHandler dh = new DisconnectHandler(this,this);
        //dh.netWorkChecking(this,getSupportFragmentManager());

        webView.loadUrl(PageInfo.INDEX_PAGE);


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
        if (System.currentTimeMillis() > backKeyPressedTime + 1000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 500);
            return;
        } else if (System.currentTimeMillis() <= backKeyPressedTime + 1000) {
            finishAffinity();
        }
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
}
