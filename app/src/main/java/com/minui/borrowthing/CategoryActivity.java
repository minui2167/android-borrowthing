package com.minui.borrowthing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.google.android.material.chip.Chip;

public class CategoryActivity extends AppCompatActivity {

    // ui
    Chip chipBook;
    Chip chipTv;
    Chip chipHouse;
    Chip chipSports;
    Chip chipGame;
    Chip chipClothes;

    Boolean[] chips = new Boolean[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle("카테고리");
        ac.setDisplayHomeAsUpEnabled(true);

        chipBook = findViewById(R.id.chipBook);
        chipTv = findViewById(R.id.chipTv);
        chipHouse = findViewById(R.id.chipHouse);
        chipSports = findViewById(R.id.chipSports);
        chipGame = findViewById(R.id.chipGame);
        chipClothes = findViewById(R.id.chipClothes);
        chipBook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                chips[0] = b;
            }
        });
        chipTv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                chips[1] = b;
            }
        });
        chipHouse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                chips[2] = b;
            }
        });
        chipSports.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                chips[3] = b;
            }
        });
        chipGame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                chips[4] = b;
            }
        });
        chipClothes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                chips[5] = b;
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
}