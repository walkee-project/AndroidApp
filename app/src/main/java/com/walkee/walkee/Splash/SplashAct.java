package com.walkee.walkee.Splash;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.walkee.walkee.Main.MainActivity;
import com.walkee.walkee.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

public class SplashAct extends Activity {

    //@RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);


        //ImageView kakao = (ImageView) findViewById(R.id.kakao);
        //Glide.with(this).load(R.drawable.kakao).into(kakao);

        //WebView webView;
        //webView.loadUrl("https://kakao-app.herokuapp.com/splash");

        // 기기 권한 설정 하는곳
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("기기 접근 권한이 필요합니다.")
                .setDeniedMessage("기기 접근권한을 허용하지 않아 종료됩니다.\n[설정] > " + getResources().getString(R.string.app_name) + " > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.POST_NOTIFICATIONS
                )
                .check();

    }


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashAct.this, MainActivity.class);
                    startActivity(intent);
                }
            }, 2000);

        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(SplashAct.this, deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            finish();
        }
    };
}
