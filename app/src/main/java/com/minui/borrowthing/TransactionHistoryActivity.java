package com.minui.borrowthing;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

// 거래내역
public class TransactionHistoryActivity extends AppCompatActivity {
    Button btnPurchaseHistory;
    Button btnSalesHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle("거래 내역");
        ac.setDisplayHomeAsUpEnabled(true);

        btnPurchaseHistory = findViewById(R.id.btnPurchaseHistory);
        btnSalesHistory = findViewById(R.id.btnSalesHistory);

        btnPurchaseHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 구매내역
                Intent intent = new Intent(getApplication(), PurchaseHistoryActivity.class);
                startActivity(intent);
            }
        });

        btnSalesHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 판매내역
                Intent intent = new Intent(getApplication(), SalesHistoryActivity.class);
                startActivity(intent);
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
}