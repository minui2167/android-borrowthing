package com.minui.borrowthing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.minui.borrowthing.adapter.CommunityAdapter;
import com.minui.borrowthing.api.CommunityApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.Community;
import com.minui.borrowthing.model.CommunityResult;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyCommunityListActivity extends AppCompatActivity {

    // 리사이클러 뷰 관련 멤버변수
    RecyclerView recyclerView;
    CommunityAdapter adapter;
    ArrayList<Community> communityList = new ArrayList<>();

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    Button btnMyPosting;
    Button btnMyLikes;
    // 페이징에 필요한 변수
    int offset = 0;
    int limit = 10;
    int count = 0;


    String calledContext = "";
    boolean isgetNetworkData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_community_list);

        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle("나의 커뮤니티");
        ac.setDisplayHomeAsUpEnabled(true);

        btnMyPosting = findViewById(R.id.btnIng);
        btnMyLikes = findViewById(R.id.btnEnd);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyCommunityListActivity.this));

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

        btnMyPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calledContext = "myPosting";
                getNetworkData();
            }
        });


        btnMyLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calledContext = "myLikes";
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

    private void getNetworkData() {
        communityList.clear();

        offset = 0;
        limit = 10;
        count = 0;

        showProgress("게시물 가져오는중...");
        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        CommunityApi communityApi = retrofit.create(CommunityApi.class);

        Call<CommunityResult> call;
        if(calledContext.equals("myPosting"))
            call = communityApi.getMyCommunityList(offset, limit, "Bearer " + accessToken);
        else
            call = communityApi.getMyLikesList(offset, limit, "Bearer " + accessToken);

        call.enqueue(new Callback<CommunityResult>() {
            @Override
            public void onResponse(Call<CommunityResult> call, Response<CommunityResult> response) {
                if(response.isSuccessful()){
                    dismissProgress();
                    isgetNetworkData = true;
                    CommunityResult communityResult = response.body();
                    count = communityResult.getCount();
                    communityList.addAll(communityResult.getItems());
                    offset = offset + count;

                    adapter = new CommunityAdapter(MyCommunityListActivity.this, communityList, calledContext);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<CommunityResult> call, Throwable t) {
                dismissProgress();
            }
        });
    }

    private void addNetworkData() {
        showProgress("게시물 가져오는중...");
        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        CommunityApi communityApi = retrofit.create(CommunityApi.class);

        Call<CommunityResult> call;

        if(calledContext.equals("myPosting"))
            call = communityApi.getMyCommunityList(offset, limit, "Bearer " + accessToken);
        else
            call = communityApi.getMyLikesList(offset, limit, "Bearer " + accessToken);

        call.enqueue(new Callback<CommunityResult>() {
            @Override
            public void onResponse(Call<CommunityResult> call, Response<CommunityResult> response) {
                if(response.isSuccessful()){
                    dismissProgress();
                    CommunityResult communityResult = response.body();
                    count = communityResult.getCount();
                    communityList.addAll(communityResult.getItems());
                    offset = offset + count;

                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<CommunityResult> call, Throwable t) {
                dismissProgress();
            }
        });
    }



    void showProgress(String message) {
        dialog = new ProgressDialog(MyCommunityListActivity.this);
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
}