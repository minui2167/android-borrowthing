package com.minui.borrowthing;

import static com.minui.borrowthing.MainActivity.context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.minui.borrowthing.api.UserApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.User;
import com.minui.borrowthing.model.UserRes;

import java.io.IOException;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText userId;
    TextInputEditText userPassword;
    TextInputEditText userName;
    TextInputEditText userPhone;
    TextInputEditText userNickname;
    Button btnRegister;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle("회원가입");
        ac.setHomeAsUpIndicator(R.drawable.ic_back_30);
        ac.setDisplayHomeAsUpEnabled(true);

        userId = findViewById(R.id.txtKeyword);
        userPassword = findViewById(R.id.userPassword);
        userName = findViewById(R.id.userName);
        userPhone = findViewById(R.id.userPhone);
        userNickname = findViewById(R.id.userNickname);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이메일 가져온다. 이메일 형식체크
                String email = userId.getText().toString().trim();
                Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;

                if(pattern.matcher(email).matches() == false) {
                    Toast.makeText(RegisterActivity.this, "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 비밀번호 가져온다.
                String password = userPassword.getText().toString().trim();
                // 비번 길이 체크
                if(password.length() < 4 || password.length() > 12) {
                    Toast.makeText(RegisterActivity.this, "비번길이는 4자이상 12자이하로 만들어주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 이름를 가져와서, 빈 문자열인지만 체크
                String name = userName.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 번호를 가져와서, 빈 문자열인지만 체크
                String phone = userPhone.getText().toString().trim();
                if (phone.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 닉네임을 가져와서, 빈 문자열인지만 체크
                String nickname = userNickname.getText().toString().trim();
                if (nickname.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                showProgress("회원가입 중입니다...");

                Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
                UserApi userApi = retrofit.create(UserApi.class);
                User user = new User(email, password, name, phone, nickname);

                Call<UserRes> call = userApi.register(user);
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        dismissProgress();
                        if (response.isSuccessful()) {
                            UserRes userRes = response.body();
                            ((MainActivity) context).nickname = nickname;
                            SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("accessToken", userRes.getAccessToken());
                            editor.apply();
//                            ((MainActivity) context).setMyLocation();
                            finish();
                        } else {
                            try {
                                String body = response.errorBody().string();
                                switch(body.split("users.")[1].split("_")[0]) {
                                    case "email":
                                        showDialog("중복된 이메일입니다.");
                                        break;
                                    case "phoneNumber":
                                        showDialog("중복된 번호입니다.");
                                        break;
                                    case "nickname":
                                        showDialog("중복된 닉네임입니다.");
                                        break;
                                }
                                Log.e("test", body.split("users.")[1].split("_")[0]);
                            }   catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {
                        dismissProgress();
                    }
                });
            }
        });
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

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle(message);
        builder.setPositiveButton("확인", null);
        builder.show();
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