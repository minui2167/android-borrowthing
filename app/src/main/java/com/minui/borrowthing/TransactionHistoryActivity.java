package com.minui.borrowthing;

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
}