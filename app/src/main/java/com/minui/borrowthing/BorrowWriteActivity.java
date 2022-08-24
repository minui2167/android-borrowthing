package com.minui.borrowthing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputEditText;

public class BorrowWriteActivity extends AppCompatActivity {

    TextInputEditText txtTitle;
    AutoCompleteTextView txtCategory;

    //변수
    boolean isClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_write);

        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle("게시물 등록");
        ac.setHomeAsUpIndicator(R.drawable.ic_back_30);
        ac.setDisplayHomeAsUpEnabled(true);

        txtCategory = findViewById(R.id.txtCategory);
        String[] categories = new String[]{"도서", "디지털 기기", "생활가전", "스포츠/레져", "취미/게임", "의류"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(BorrowWriteActivity.this, android.R.layout.simple_dropdown_item_1line, categories);
        txtCategory.setAdapter(arrayAdapter);
        txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtCategory.showDropDown();
            }
        });

    }

    // 액션바 메뉴 보여주기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.community, menu);
        return true;
    }

    // 액션바 메뉴와 백버튼 클릭시 이벤트
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (isClicked == false) {
            isClicked = true;
        } else {
            return true;
        }

        if (itemId == android.R.id.home) {
            finish();
        } else if(itemId == R.id.menuCheck) {

        }

        return super.onOptionsItemSelected(item);
    }
}