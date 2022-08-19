package com.minui.borrowthing;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MyReviseActivity extends AppCompatActivity {

    EditText editNickname;
    EditText editNewPassword;
    EditText editNewPassword2;
    Button btnRevise;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_revise);

        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle("회원정보 수정");
        ac.setHomeAsUpIndicator(R.drawable.ic_back_30);
        ac.setDisplayHomeAsUpEnabled(true);

        editNickname = findViewById(R.id.editNickname);
        editNewPassword = findViewById(R.id.editNewPassword);
        editNewPassword2 = findViewById(R.id.editNewPassword2);
        btnRevise = findViewById(R.id.btnRevise);

        btnRevise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // todo 닉네임 수정
                String nickname = editNickname.getText().toString().trim();
                if (nickname.isEmpty()) {
                    Toast.makeText(MyReviseActivity.this, "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }


                // todo 새 비밀번호
                // 비밀번호 가져온다.
                String password = editNewPassword.getText().toString().trim();
                // 비번 길이 체크
                if(password.length() < 4 || password.length() > 12) {
                    Toast.makeText(MyReviseActivity.this, "비번길이는 4자이상 12자이하로 만들어주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }


                // todo 새 비밀번호 확인
                String password2 = editNewPassword2.getText().toString().trim();
                // 비번 길이 체크
                if(password2.length() < 4 || password2.length() > 12) {
                    Toast.makeText(MyReviseActivity.this, "비번길이는 4자이상 12자이하로 만들어주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // todo 비밀번호 두개 일치하는지 확인
                if (password == password2) {
                    Toast.makeText(MyReviseActivity.this , "비밀번호가 서로 일치하지 않습니다" , Toast.LENGTH_SHORT).show();
                    return;
                }

                showProgress("회원정보 수정 중입니다...");




            }
        });



    }
    void showProgress(String message) {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }
}