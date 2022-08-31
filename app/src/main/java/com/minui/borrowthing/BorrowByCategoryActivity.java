package com.minui.borrowthing;

import static com.minui.borrowthing.MainActivity.context;

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
import android.view.Menu;
import android.view.MenuItem;

import com.minui.borrowthing.adapter.BorrowAdapter;
import com.minui.borrowthing.api.BorrowApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.BorrowResult;
import com.minui.borrowthing.model.UserRes;
import com.minui.borrowthing.model.item;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BorrowByCategoryActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    BorrowAdapter adapter;
    ArrayList<item> itemList = new ArrayList<>();

    // 페이징에 필요한 변수
    int offset = 0;
    int limit = 20;
    int count = 0;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    // 카테고리 ID
    int category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_by_category);

        category = getIntent().getIntExtra("category", 0);
        String categoryName = "";
        if (category == 1)
            categoryName = "도서";
        else if (category == 2)
            categoryName = "디지털 기기";
        else if (category == 3)
            categoryName = "생활 가전";
        else if (category == 4)
            categoryName = "스포츠/레저";
        else if (category == 5)
            categoryName = "취미/게임";
        else if (category == 6)
            categoryName = "의류";
        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle(categoryName);
        ac.setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(BorrowByCategoryActivity.this));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if(  lastPosition+1  == totalCount  ){

                    if(count == limit){
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
        Log.i("test", accessToken);
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        BorrowApi borrowApi = retrofit.create(BorrowApi.class);

        Call<BorrowResult> call;
        if (accessToken.isEmpty()) {
            call = borrowApi.getGoods(offset, limit, category);
        } else {
            call = borrowApi.getGoods(offset, limit, category, "Bearer " + accessToken);
        }
        Log.i("test", "111");
        call.enqueue(new Callback<BorrowResult>() {
            @Override
            public void onResponse(Call<BorrowResult> call, Response<BorrowResult> response) {
                dismissProgress();
                if (response.isSuccessful()) {
                    BorrowResult borrowResult = response.body();
                    count = borrowResult.getCount();
                    itemList.addAll(borrowResult.getItems());
                    offset = offset + count;

                    adapter = new BorrowAdapter(BorrowByCategoryActivity.this, itemList, "borrowByCategory");
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
        if (accessToken.isEmpty()) {
            call = borrowApi.getGoods(offset, limit, category);
        } else {
            call = borrowApi.getGoods(offset, limit, category, "Bearer " + accessToken);
        }

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

    void showProgress(String message) {
        dialog = new ProgressDialog(BorrowByCategoryActivity.this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }

    void dismissProgress() {
        dialog.dismiss();
    }
    @Override
    public  boolean onSupportNavigateUp(){
        // 1. finish() 이용
//        finish();

        // 2. 기계의 백버튼 눌렀을 때 호출되는 콜백 함수를 이용
        onBackPressed();
        return true;
    }

    public void setConcerned(int index) {
        if (!((MainActivity) context).isLogin()) {
            ((MainActivity) context).login();
            return;
        }
        showProgress("관심상품 등록중...");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        BorrowApi borrowApi = retrofit.create(BorrowApi.class);
        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");

        if (itemList.get(index).getIsWish() == 0) {
            Call<UserRes> call = borrowApi.setConcerned("Bearer " + accessToken, itemList.get(index).getId());
            call.enqueue(new Callback<UserRes>() {
                @Override
                public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                    dismissProgress();
                    if (response.isSuccessful()) {
                        itemList.get(index).setIsWish(1);
                        adapter.notifyDataSetChanged();
                    } else {
                        try {
                            JSONObject jsonobject = new JSONObject( response.errorBody().string());
                            showDialog(jsonobject.getString("error"));
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserRes> call, Throwable t) {
                    dismissProgress();
                }
            });
        } else {
            Call<UserRes> call = borrowApi.setConcernedCancel("Bearer " + accessToken, itemList.get(index).getId());
            call.enqueue(new Callback<UserRes>() {
                @Override
                public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                    dismissProgress();
                    if (response.isSuccessful()) {
                        itemList.get(index).setIsWish(0);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<UserRes> call, Throwable t) {
                    dismissProgress();
                }
            });
        }
    }
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BorrowByCategoryActivity.this);
        builder.setTitle(message);
        builder.setPositiveButton("확인", null);
        builder.show();
    }

}