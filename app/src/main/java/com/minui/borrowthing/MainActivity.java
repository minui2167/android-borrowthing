package com.minui.borrowthing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.minui.borrowthing.api.UserApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.LocationApi;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.handler.BackKeyHandler;
import com.minui.borrowthing.model.AreaInfo;
import com.minui.borrowthing.model.AreaRes;
import com.minui.borrowthing.model.MyLocation;
import com.minui.borrowthing.model.Result;
import com.minui.borrowthing.model.UserRes;
import com.minui.borrowthing.model.UsersLike;
import com.minui.borrowthing.model.UsersLikeItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    // 프래그먼트
    BottomNavigationView navigationView;
    public Fragment firstFragment;
    Fragment secondFragment;
    public Fragment thirdFragment;
    Fragment forthFragment;
    public Fragment searchFragment;
    Fragment chatFragment;
    Fragment fragment;

    // ui
    Menu menu;

    //백버튼 핸들러
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    // 변수
    public static Context context; // 액티비티 접근
    String accessToken; // 로그인 토큰
    boolean hidden = false; // 액션바 메뉴 감출건지
    boolean alarm = false; // 알람 했는지 todo 서버에서 받기
    ActionBar ac; // 액션바
    boolean category = true; // 홈버튼이 카테고리인지 백버튼인지
    Result result = new Result(); // 지역받기
    String region; // 지역명
    Double longitude; // 위도
    Double latitude; // 경도
    String nickname;
    MyLocation myLocation;

    // getMyLocation() 을 위한 변수
    ArrayList<AreaInfo> areaList = new ArrayList<>();
    boolean isMyArea = false;

    // 위치
    LocationManager locationManager;
    LocationListener locationListener;
    ProgressDialog asyncDialog;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 객체 대입
        context = this;

        // 액션바 제목 백버튼 설정
        ac = getSupportActionBar();
        ac.setTitle("");
        ac.setHomeAsUpIndicator(R.drawable.ic_list_30);
        ac.setDisplayHomeAsUpEnabled(true);

        // 프래그먼트 대입
        firstFragment = new FirstFragment();
        secondFragment = new SecondFragment();
        thirdFragment = new ThirdFragment();
        forthFragment = new ForthFragment();
        searchFragment = new SearchFragment();
        chatFragment = new ChatFragment();

        // 위치 받아올때까지 기디리기
        asyncDialog = new ProgressDialog(MainActivity.this);
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setCancelable(false);
        asyncDialog.setMessage("위치를 찾는중 입니다...");

        // 바텀뷰
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.firstFragment) {
                    fragment = firstFragment;
                    getMyLocation();

                    hidden = false;
                    ac.setHomeAsUpIndicator(R.drawable.ic_list_30);
                    category = true;

                } else if (itemId == R.id.secondFragment) {
                    if(longitude == null) {
                        asyncDialog.show();
                        return false;
                    }
                    if(isLogin() && getMyLocation()) {
                        fragment = secondFragment;
                        ac.setTitle("지도");
                        hidden = true;
                        ac.setHomeAsUpIndicator(R.drawable.ic_back_30);
                        category = false;
                    } else if(isLogin()==true && getMyLocation() == false){
                        Toast.makeText(getApplication(), "동네 설정 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                        return false;
                    } else if(isLogin() == false){
                        login();
                        return false;
                    }


                }

                 else if (itemId == R.id.thirdFragment) {
                    fragment = thirdFragment;
                    ac.setTitle("커뮤니티");
                    hidden = true;
                    ac.setHomeAsUpIndicator(R.drawable.ic_back_30);
                    category = false;
                } else if (itemId == R.id.forthFragment) {
                    if (isLogin()) {
                        fragment = forthFragment;
                        ac.setTitle("내 정보");
                        hidden = false;
                        ac.setHomeAsUpIndicator(R.drawable.ic_back_30);
                        category = false;
                    } else {
                        login();
                        return false;
                    }
                }
                return loadFragment(fragment);
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.i("myLocation", "경도 : " + location.getLongitude());
                Log.i("myLocation", "위도 : " + location.getLatitude());
                longitude = location.getLongitude();
                latitude = location.getLatitude();

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
                            result = response.body();
                            myLocation = new MyLocation(result.getResults()[0].getRegion().getArea1().getName(), result.getResults()[0].getRegion().getArea2().getName(),result.getResults()[0].getRegion().getArea3().getName());
//                            setMyLocation();

                            if (navigationView.getSelectedItemId() == R.id.firstFragment) {
//                                ac.setTitle(result.getResults()[0].getRegion().getArea3().getName());
                                try {
                                    ac.setTitle(areaList.get(0).getEmd());
                                }catch (Exception e){
                                    ac.setTitle("");
                                }
                            }
                            if (!asyncDialog.isShowing()) {
                                return;
                            }
                            openMap();
                        }
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {

                    }
                });
            }
        };

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((MainActivity) MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, -1, 3, locationListener);
        }

        navigationView.setSelectedItemId(R.id.firstFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume", "Done");
//        ac.setTitle("");
//        getMyLocation();
    }

    public void setMyLocation() {
        if (myLocation == null) {
            return;
        }
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        UserApi userApi = retrofit.create(UserApi.class);
        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Call<UserRes> call = userApi.setLocation("Bearer " + accessToken, myLocation);
        call.enqueue(new Callback<UserRes>() {
            @Override
            public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                if(response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(Call<UserRes> call, Throwable t) {

            }
        });
    }

    void openMap() {
        fragment = secondFragment;
        ac.setTitle("지도");
        hidden = true;
        ac.setHomeAsUpIndicator(R.drawable.ic_back_30);
        category = false;
        asyncDialog.dismiss();
        loadFragment(fragment);
        navigationView.setSelectedItemId(R.id.secondFragment);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
            localBuilder.setTitle("권한 설정")
                    .setMessage("권한을 설정하지 않으면 앱 사용이 불가능합니다.")
                    .setPositiveButton("권한 설정하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions((MainActivity) MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                        }
                    })
                    .setNegativeButton("종료하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).create().show();
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, -1, 3, locationListener);
            showProgress("위치를 설정중입니다.");
        }
        return;
    }

    // 프래그먼트 로드
    public boolean loadFragment(Fragment fragment) {
        if(fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
            return true;
        }
        return false;
    }

    // 액션바 메뉴 보여주기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (hidden) {
            return true;
        }
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // 액션바 메뉴와 백버튼 클릭시 이벤트
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuChat) {
              if (isLogin()) {
                  fragment = chatFragment;
                  loadFragment(fragment);
                  hidden = true;
                  ac.setHomeAsUpIndicator(R.drawable.ic_back_30);
                  ac.setTitle("채팅방 목록");
                  category = false;
              } else {
                  login();
                  return false;
              }
        } else if (itemId == R.id.menuSearch) {
            fragment = searchFragment;
            loadFragment(fragment);
            hidden = true;
            ac.setHomeAsUpIndicator(R.drawable.ic_back_30);
            ac.setTitle("검색");
            category = false;
        } else if (itemId == R.id.menuBell) {
            alarm = alarm?false:true;
            if (alarm) {
                menu.getItem(2).setIcon(R.drawable.ic_bell_black);
            } else {
                menu.getItem(2).setIcon(R.drawable.ic_bell);
            }
        }
        else if (itemId == android.R.id.home) {
            if (category) {
                hidden = false;
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                startActivity(intent);
            } else {
                fragment = firstFragment;
                getMyLocation();
                hidden = false;
                ac.setHomeAsUpIndicator(R.drawable.ic_list_30);
                category = true;
                loadFragment(fragment);
                navigationView.setSelectedItemId(R.id.firstFragment);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    boolean isLogin() {
        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");
        if (accessToken.isEmpty()) {
            return false;
        }
        return true;
    }

    void login() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    void showProgress(String message) {
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }

    void dismissProgress() {
        dialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        /* 다음 4가지 형태 중 하나 선택해서 사용 */

        //backKeyHandler.onBackPressed();
        //backKeyHandler.onBackPressed("\'뒤로\' 버튼을 두 번 누르면 종료됩니다.\n입력한 내용이 지워집니다.");
        //backKeyHandler.onBackPressed(5);
        backKeyHandler.onBackPressed("버튼을 한번 더 누르면 종료됩니다.", 3);
    }

    public boolean getMyLocation() {
        areaList.clear();
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        UserApi userApi = retrofit.create(UserApi.class);
        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Call<AreaRes> call = userApi.getMyLocation("Bearer " + accessToken);
        call.enqueue(new Callback<AreaRes>() {
            @Override
            public void onResponse(Call<AreaRes> call, Response<AreaRes> response) {
                if(response.isSuccessful()) {
                    AreaRes areaRes = response.body();
                    areaList.addAll(areaRes.getItems());
                    if(areaList.isEmpty()){
                        isMyArea = false;
                        ac.setTitle("");
                    } else{
                        isMyArea = true;
                        ac.setTitle(areaList.get(0).getEmd());
                    }

                }
            }

            @Override
            public void onFailure(Call<AreaRes> call, Throwable t) {

            }
        });
        return isMyArea;
    }

}