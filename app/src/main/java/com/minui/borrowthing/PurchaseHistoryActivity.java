package com.minui.borrowthing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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

//구매내역
public class PurchaseHistoryActivity extends AppCompatActivity {
    Button btnIng;
    Button btnEnd;
    Button btnRating;
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

    int isRating = 0;
    String calledContext = "";
    boolean isgetNetworkData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle("구매 내역");
        ac.setDisplayHomeAsUpEnabled(true);

        btnIng = findViewById(R.id.btnIng);
        btnEnd = findViewById(R.id.btnWait);
        btnRating = findViewById(R.id.btnEnd);
        recyclerView = findViewById(R.id.recyclerView);


        // 거래중 버튼 클릭
        btnIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = 1;
                isRating = 1;
                calledContext = "purchaseHistoryStatus1";
                getNetworkData();
            }
        });

        // 거래 완료 버튼 클릭
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = 2;
                isRating = 1;
                calledContext = "purchaseHistoryStatus2";
                getNetworkData();
            }
        });

        // 상품평 버튼 클릭
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = 2;
                isRating = 0;
                calledContext = "purchaseHistoryStatus2NotRating";
                getNetworkData();
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(PurchaseHistoryActivity.this));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if (lastPosition + 1 == totalCount) {

                    if (count == limit) {
                        // 네트워크 통해서, 데이터를 더 불러오면 된다.
                        addNetworkData();
                    }
                }
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isgetNetworkData)
            getNetworkData();
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
        if(isRating == 1)
            call = borrowApi.getPurchaseHistory("Bearer " + accessToken, offset, limit, status);
        else
            call = borrowApi.getNotRatingPurchaseHistory("Bearer " + accessToken, offset, limit);
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

    private void addNetworkData() {
        showProgress("게시물 가져오는중...");
        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        BorrowApi borrowApi = retrofit.create(BorrowApi.class);

        Call<BorrowResult> call;

        call = borrowApi.getPurchaseHistory("Bearer " + accessToken, offset, limit, status);

        call.enqueue(new Callback<BorrowResult>() {
            @Override
            public void onResponse(Call<BorrowResult> call, Response<BorrowResult> response) {
                dismissProgress();
                if (response.isSuccessful()) {
                    BorrowResult borrowResult = response.body();
                    count = borrowResult.getCount();
                    itemList.addAll(borrowResult.getItems());
                    offset = offset + count;

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BorrowResult> call, Throwable t) {
                dismissProgress();
            }
        });
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PurchaseHistoryActivity.this);
        builder.setTitle(message);
        builder.setPositiveButton("확인", null);
        builder.show();
    }
    void dismissProgress() {
        dialog.dismiss();
    }

    void showProgress(String message) {
        dialog = new ProgressDialog(PurchaseHistoryActivity.this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
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