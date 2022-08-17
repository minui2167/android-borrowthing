package com.minui.borrowthing;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MyInformation extends AppCompatActivity {

    Button btnInformation;
    Button btnInterestsList;
    Button btnMyWrote;
    Button btnMyLocation;
    Button btnTransaction;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);

        btnInformation = findViewById(R.id.btnInformation);
        btnInterestsList = findViewById(R.id.btnInterestsList);
        btnMyWrote = findViewById(R.id.btnMyWrote);
        btnMyLocation = findViewById(R.id.btnMyLocation);
        btnTransaction = findViewById(R.id.btnTransaction);
        btnLogout = findViewById(R.id.btnLogout);



        btnInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo 내정보 수정하는 화면 구성
            }
        });

        btnInterestsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo 내 관심 목록 불러오기
            }
        });

        btnMyWrote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo 내가 쓴 글 목록 불러오기
            }
        });

        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo 내 현재 위치 설정
            }
        });

        btnTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo 거래내역 불러오기
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo 로그아웃 구성
            }
        });

    }
}