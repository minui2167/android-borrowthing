package com.minui.borrowthing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.minui.borrowthing.adapter.BorrowAdapter;
import com.minui.borrowthing.api.BorrowApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.BorrowResult;
import com.minui.borrowthing.model.item;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 판매내역
public class SalesHistoryActivity extends AppCompatActivity {
    Button btnIng;
    Button btnEnd;
    Button btnWait;
    ProgressDialog dialog;

    // 리사이클러 뷰 관련 멤버변수
    RecyclerView recyclerView;
    BorrowAdapter adapter;
    ArrayList<item> itemList = new ArrayList<>();

    // 페이징에 필요한 변수
    int offset = 0;
    int limit = 20;
    int count = 0;


    // 상품의 거래 상태
    int status = 0;

    String calledContext = "";
    boolean isgetNetworkData = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_history);
        btnWait = findViewById(R.id.btnWait);
        btnIng = findViewById(R.id.btnIng);
        btnEnd = findViewById(R.id.btnEnd);
        recyclerView = findViewById(R.id.recyclerView);

        // 거래 대기 버튼 클릭
        btnWait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = 0;
                calledContext = "salesHistoryStatus0";
                getNetworkData();
            }
        });

        // 거래중 버튼 클릭
        btnIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = 1;
                calledContext = "salesHistoryStatus1";
                getNetworkData();
            }
        });

        // 거래 완료 버튼 클릭
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = 2;
                calledContext = "salesHistoryStatus2";
                getNetworkData();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isgetNetworkData)
            getNetworkData();
    }

    void dismissProgress() {
        dialog.dismiss();
    }

    void showProgress(String message) {
        dialog = new ProgressDialog(SalesHistoryActivity.this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }

    private void getNetworkData() {
        itemList.clear();

        offset = 0;
        limit = 20;
        count = 0;

        showProgress("게시물 가져오는중...");
        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Log.i("accessToken", accessToken);
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        BorrowApi borrowApi = retrofit.create(BorrowApi.class);

        Call<BorrowResult> call;

        call = borrowApi.getSalesHistory("Bearer " + accessToken, offset, limit, status);

        call.enqueue(new Callback<BorrowResult>() {
            @Override
            public void onResponse(Call<BorrowResult> call, Response<BorrowResult> response) {
                dismissProgress();
                if (response.isSuccessful()) {
                    isgetNetworkData = true;
                    BorrowResult borrowResult = response.body();
                    count = borrowResult.getCount();
                    itemList.addAll(borrowResult.getItems());
                    Log.i("item size", ""+itemList.size());
                    offset = offset + count;

                    adapter = new BorrowAdapter(getApplication(), itemList, calledContext);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<BorrowResult> call, Throwable t) {
                dismissProgress();
            }
        });
    }
}