package com.minui.borrowthing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class BorrowListByAreaActivity extends AppCompatActivity {
    int sidoId;
    int siggId;
    int emdId;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_list_by_area);
        String areaTag = getIntent().getStringExtra("tag");
        name = getIntent().getStringExtra("title");

        if (!areaTag.isEmpty()){
            sidoId = Integer.parseInt(areaTag.split(",")[0]);
            siggId = Integer.parseInt(areaTag.split(",")[1]);
            emdId = Integer.parseInt(areaTag.split(",")[2]);
        }


    }
}