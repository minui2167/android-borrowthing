package com.minui.borrowthing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.minui.borrowthing.api.UserApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.LocationApi;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.MyLocation;
import com.minui.borrowthing.model.Result;
import com.minui.borrowthing.model.UserRes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SetAreaActivity extends AppCompatActivity {

    Button btnCertification;
    Button btnSetting;

    LocationManager locationManager;
    LocationListener locationListener;
    double latitude;
    double longitude;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    String region; // 지역명

    MyLocation myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_area);

        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle("내 위치 설정");
        ac.setDisplayHomeAsUpEnabled(true);

        btnCertification = findViewById(R.id.btnCertification);
        btnSetting = findViewById(R.id.btnSetting);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(lastKnowLocation != null){
            latitude = lastKnowLocation.getLatitude();
            longitude = lastKnowLocation.getLongitude();
            Log.i("testtt" , "latitude : " + latitude + " logitude : " + longitude );
        }
        btnCertification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Retrofit retrofit = NetworkClient.getRetrofitClient(Config.NAVER_URL);
                LocationApi api = retrofit.create(LocationApi.class);

                Call<Result> call = api.getLocation(longitude + "," + latitude, "admcode", "json", BuildConfig.NAVER_ID, BuildConfig.NAVER_PASSWORD);

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        if (response.isSuccessful()) {
                            try {
                                dismissProgress();
                            } catch (Exception e) {

                            }
                            Result result = response.body();
                            myLocation = new MyLocation(result.getResults()[0].getRegion().getArea1().getName(), result.getResults()[0].getRegion().getArea2().getName(),result.getResults()[0].getRegion().getArea3().getName());
                            setMyLocation();
//                            Log.i("test", myLocation.getEmdName());

                        }
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {

                    }
                });
            }
        });
    }

    @Override
    public  boolean onSupportNavigateUp(){
        // 1. finish() 이용
//        finish();

        // 2. 기계의 백버튼 눌렀을 때 호출되는 콜백 함수를 이용
        onBackPressed();
        return true;
    }

    void showProgress(String message) {
        dialog = new ProgressDialog(SetAreaActivity.this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }

    public void setMyLocation() {
        if (myLocation == null) {
            return;
        }
        showProgress("동네 설정 중입니다.");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        UserApi userApi = retrofit.create(UserApi.class);
        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Call<UserRes> call = userApi.setLocation("Bearer " + accessToken, myLocation);
        call.enqueue(new Callback<UserRes>() {
            @Override
            public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                dismissProgress();
                if(response.isSuccessful()) {
                    Toast.makeText(getApplication(), "동네 설정이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserRes> call, Throwable t) {
                dismissProgress();
            }
        });
    }

    void dismissProgress() {
        dialog.dismiss();
    }
}