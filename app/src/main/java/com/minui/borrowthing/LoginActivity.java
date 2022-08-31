package com.minui.borrowthing;

import static com.minui.borrowthing.MainActivity.context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.minui.borrowthing.api.UserApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.UserRes;
import com.minui.borrowthing.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    // ui
    TextInputLayout textInputLayoutId;
    TextInputLayout textInputLayoutPassword;
    TextInputEditText userId;
    TextInputEditText userPassword;
    Button btnLogin;
    Button btnRegister;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle("로그인");
        ac.setHomeAsUpIndicator(R.drawable.ic_back_30);
        ac.setDisplayHomeAsUpEnabled(true);

        // ui
        textInputLayoutId = findViewById(R.id.textInputLayoutId);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        userId = findViewById(R.id.txtKeyword);
        userPassword = findViewById(R.id.userPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);


        // 포커스되면 색바꾸기
        userId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                int color = b ? Color.parseColor("#3498db"): Color.GRAY;
                textInputLayoutId.setStartIconTintList(ColorStateList.valueOf(color));
            }
        });

        // 포커스되면 색바꾸기
        userPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                int color = b ? Color.parseColor("#3498db"): Color.GRAY;
                textInputLayoutPassword.setStartIconTintList(ColorStateList.valueOf(color));}
        });

        // 로그인
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
                UserApi userApi = retrofit.create(UserApi.class);
                User user = new User(userId.getText().toString().trim(), userPassword.getText().toString().trim());

                Call<UserRes> call = userApi.login(user);
                showProgress("로그인 중입니다...");
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {

                        dismissProgress();
                        if (response.isSuccessful()) {
                            UserRes userRes = response.body();
                            ((MainActivity) context).nickname = userRes.getNickname();
                            SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("accessToken", userRes.getAccessToken());
                            editor.putString("nickName", userRes.getNickname());
                            editor.putInt("ratingCount", userRes.getRatingCount());
                            editor.apply();
//                            ((MainActivity) context).setMyLocation();
                            finish();
                        } else {
                            try {
                                JSONObject jsonobject = new JSONObject( response.errorBody().string());
                                showDialog(jsonobject.getString("error"));
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {
                        dismissProgress();
                        showDialog("로그인 네트워크 에러");
                    }
                });
            }
        });

        // 회원가입
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("activity", getIntent().getStringExtra("activity"));
                startActivity(intent);
            }
        });


    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(message);
        builder.setPositiveButton("확인", null);
        builder.show();
    }

    // 액션바 메뉴 보여주기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    // 액션바 메뉴와 백버튼 클릭시 이벤트
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    void showProgress(String message) {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }

    void dismissProgress() {
        dialog.dismiss();
    }
}