package com.minui.borrowthing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.minui.borrowthing.model.AreaInfo;
import com.minui.borrowthing.model.AreaRes;
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

    AreaInfo areaInfo;
    int activityMeters = 10000;

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
//            Log.i("testtt" , "latitude : " + latitude + " logitude : " + longitude );
        }

        // 동네 인증 버튼 클릭
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

        // 활동 범위 설정하기
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SetAreaActivity.this);
                alertDialog.setTitle("활동 범위 설정하기");
                alertDialog.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    // 긍정 버튼을 눌렀을 때의 함수
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showProgress("활동 범위 설정 중 입니다..");
                        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
                        UserApi userApi = retrofit.create(UserApi.class);
                        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                        String accessToken = sp.getString("accessToken", "");
                        Call<AreaRes> call = userApi.setActivityMeters("Bearer " + accessToken, new AreaInfo(activityMeters));
                        call.enqueue(new Callback<AreaRes>() {
                            @Override
                            public void onResponse(Call<AreaRes> call, Response<AreaRes> response) {
                                dismissProgress();
                                Log.i("test", response.code()+"");
                                if(response.isSuccessful()) {
                                    Toast.makeText(getApplication(), "활동 범위가 " + activityMeters/1000 + "KM로 설정되었습니다.", Toast.LENGTH_SHORT).show();
                                } else if(response.code() == 400){
                                    Toast.makeText(getApplication(), "동네 설정을 먼저 해주세요.", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onFailure(Call<AreaRes> call, Throwable t) {
                                dismissProgress();
                            }
                        });

                    }
                });
                // 부정버튼을 눌렀을때
                // 리스너 메소드는 필요 없으니 null로 설정
                alertDialog.setNegativeButton("취소", null);
                String[] items = {"5 KM","10 KM","15 KM"};
                int checkedItem = 1;

                alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                activityMeters = 5000;
                                break;
                            case 1:
                                activityMeters = 10000;
                                break;
                            case 2:
                                activityMeters = 15000;
                                break;

                        }
                    }
                });
                AlertDialog alert = alertDialog.create();
                alert.setCanceledOnTouchOutside(false);

                alert.show();

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
                Log.i("test", response.code()+"");
                if(response.isSuccessful()) {
                    Toast.makeText(getApplication(), "동네 설정이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                } else if(response.code() == 400){
                    Toast.makeText(getApplication(), "지원하는 동네가 아닙니다.", Toast.LENGTH_SHORT).show();
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