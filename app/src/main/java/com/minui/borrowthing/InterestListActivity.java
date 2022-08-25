package com.minui.borrowthing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.minui.borrowthing.adapter.BorrowAdapter;
import com.minui.borrowthing.model.item;

import java.util.ArrayList;

public class InterestListActivity extends AppCompatActivity {



    RecyclerView recyclerViewList;


    // 페이징에 필요한 변수
    int offset = 0;
    int limit = 20;
    int count = 0;


    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_list);

        recyclerViewList = findViewById(R.id.recyclerViewList);


        recyclerViewList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });


    }
}