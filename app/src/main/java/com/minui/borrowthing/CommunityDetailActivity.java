package com.minui.borrowthing;

import static com.minui.borrowthing.MainActivity.context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.minui.borrowthing.adapter.CommunityCommentAdapter;
import com.minui.borrowthing.api.CommunityApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.Comment;
import com.minui.borrowthing.model.Community;
import com.minui.borrowthing.model.CommunityComment;
import com.minui.borrowthing.model.CommunityCommentResult;
import com.minui.borrowthing.model.UserRes;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommunityDetailActivity extends AppCompatActivity {

    ImageView imgCommunity;
    FloatingActionButton fabLeft;
    FloatingActionButton fabRight;
    TextInputEditText txtContent;
    ImageView imgThumb;
    TextView txtLikes;
    TextInputEditText txtComment;
    Button btnRegister;

    // 변수
    int index = 0;
    Community community;
    boolean isClicked = false;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    // 리사이클러 뷰 관련 멤버변수
    RecyclerView recyclerView;
    CommunityCommentAdapter adapter;
    ArrayList<CommunityComment> communityCommentList = new ArrayList<>();

    // 페이징에 필요한 변수
    int offset = 0;
    int limit = 20;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail);

        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle("게시물 상세");
        ac.setHomeAsUpIndicator(R.drawable.ic_back_30);
        ac.setDisplayHomeAsUpEnabled(true);

        imgCommunity = findViewById(R.id.imgCommunity);
        fabLeft = findViewById(R.id.fabLeft);
        fabRight = findViewById(R.id.fabRight);
        txtContent = findViewById(R.id.txtContent);
        imgThumb = findViewById(R.id.imgThumb);
        txtLikes = findViewById(R.id.txtLikes);
        txtComment = findViewById(R.id.txtComment);
        btnRegister = findViewById(R.id.btnRegister);
        fabLeft.setVisibility(View.GONE);
        fabRight.setVisibility(View.GONE);

        community = (Community) getIntent().getSerializableExtra("community");
        if (community.getImgUrl().size() > 1) {
            fabLeft.setVisibility(View.VISIBLE);
            fabRight.setVisibility(View.VISIBLE);
        }
        try {
            GlideUrl url = new GlideUrl(Config.IMAGE_URL + community.getImgUrl().get(0).getImageUrl(), new LazyHeaders.Builder().addHeader("User-Agent", "Android").build());
            Glide.with(CommunityDetailActivity.this).load(url).into(imgCommunity);
        } catch (Exception e) {
            imgCommunity.setImageResource(R.drawable.nolmage);
        }

        fabLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index == 0) {
                    return;
                }
                index--;
                setImgCommunity(index);
            }
        });

        fabRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (community.getImgUrl().size() - 1 == index) {
                    return;
                }
                index++;
                setImgCommunity(index);
            }
        });

        txtContent.setClickable(false);
        txtContent.setFocusable(false);
        txtContent.setText(community.getContent());

        if (community.getIsLike() == 0) {
            imgThumb.setImageResource(R.drawable.ic_thumb_outline);
        } else {
            imgThumb.setImageResource(R.drawable.ic_thumb);
        }

        txtLikes.setText(community.getLikesCount() + "");

        imgThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((MainActivity) context).isLogin()) {
                    ((MainActivity) context).login();
                    return;
                }
                showProgress("추천 하는중...");
                Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
                CommunityApi communityApi = retrofit.create(CommunityApi.class);
                SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String accessToken = sp.getString("accessToken", "");

                if (community.getIsLike() == 0) {
                    Call<UserRes> call = communityApi.setLike(community.getId(), "Bearer " + accessToken);
                    call.enqueue(new Callback<UserRes>() {
                        @Override
                        public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                            dismissProgress();
                            if(response.isSuccessful()) {
                                community.setIsLike(1);
                                community.setLikesCount(response.body().getLikesCount());
                                txtLikes.setText(community.getLikesCount() + "");
                                imgThumb.setImageResource(R.drawable.ic_thumb);

                            }
                        }

                        @Override
                        public void onFailure(Call<UserRes> call, Throwable t) {
                            dismissProgress();
                        }
                    });
                } else {
                    Call<UserRes> call = communityApi.setLikeCancel(community.getId(), "Bearer " + accessToken);
                    call.enqueue(new Callback<UserRes>() {
                        @Override
                        public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                            dismissProgress();
                            if(response.isSuccessful()) {
                                community.setIsLike(0);
                                community.setLikesCount(response.body().getLikesCount());
                                txtLikes.setText(community.getLikesCount() + "");
                                imgThumb.setImageResource(R.drawable.ic_thumb_outline);
                            }
                        }

                        @Override
                        public void onFailure(Call<UserRes> call, Throwable t) {
                            dismissProgress();
                        }
                    });
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((MainActivity) context).isLogin()) {
                    ((MainActivity) context).login();
                    return;
                }

                showProgress("댓글 다는중...");
                Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
                CommunityApi communityApi = retrofit.create(CommunityApi.class);
                SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String accessToken = sp.getString("accessToken", "");
                Comment comment = new Comment(txtComment.getText().toString().trim());

                Call<UserRes> call = communityApi.setComment("Bearer " + accessToken, community.getId(), comment);
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        dismissProgress();
                        if (response.isSuccessful()) {
                            getNetworkData();
                            txtComment.setText("");
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {
                        dismissProgress();
                    }
                });
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommunityDetailActivity.this));

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

        getNetworkData();
    }

    private void getNetworkData() {
        communityCommentList.clear();

        offset = 0;
        limit = 20;
        count = 0;

        showProgress("댓글 가져오는중...");
        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        CommunityApi communityApi = retrofit.create(CommunityApi.class);

        Call<CommunityCommentResult> call;
        if (accessToken.isEmpty()) {
            call = communityApi.getCommentList(community.getId(), offset, limit);
        } else {
            call = communityApi.getCommentList("Bearer " + accessToken, community.getId(), offset, limit);
        }

        call.enqueue(new Callback<CommunityCommentResult>() {
            @Override
            public void onResponse(Call<CommunityCommentResult> call, Response<CommunityCommentResult> response) {
                dismissProgress();
                CommunityCommentResult communityCommentResult = response.body();
                count = communityCommentResult.getCount();
                communityCommentList.addAll(communityCommentResult.getItems());
                offset = offset + count;

                adapter = new CommunityCommentAdapter(CommunityDetailActivity.this, communityCommentList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<CommunityCommentResult> call, Throwable t) {
                dismissProgress();
            }
        });
    }

    private void addNetworkData() {
        showProgress("댓글 가져오는중...");
        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        CommunityApi communityApi = retrofit.create(CommunityApi.class);

        Call<CommunityCommentResult> call;
        if (accessToken.isEmpty()) {
            call = communityApi.getCommentList(community.getId(), offset, limit);
        } else {
            call = communityApi.getCommentList("Bearer " + accessToken, community.getId(), offset, limit);
        }

        call.enqueue(new Callback<CommunityCommentResult>() {
            @Override
            public void onResponse(Call<CommunityCommentResult> call, Response<CommunityCommentResult> response) {
                dismissProgress();
                CommunityCommentResult communityCommentResult = response.body();
                count = communityCommentResult.getCount();
                communityCommentList.addAll(communityCommentResult.getItems());
                offset = offset + count;

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CommunityCommentResult> call, Throwable t) {
                dismissProgress();
            }
        });
    }

    void showProgress(String message) {
        dialog = new ProgressDialog(CommunityDetailActivity.this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }

    void dismissProgress() {
        dialog.dismiss();
    }

    private void setImgCommunity(int index) {
        try {
            GlideUrl url = new GlideUrl(Config.IMAGE_URL + community.getImgUrl().get(index).getImageUrl(), new LazyHeaders.Builder().addHeader("User-Agent", "Android").build());
            Glide.with(CommunityDetailActivity.this).load(url).into(imgCommunity);
        } catch (Exception e) {
            imgCommunity.setImageResource(R.drawable.ic_photo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (community.getIsAuthor() == 0) {
            return true;
        }
        getMenuInflater().inflate(R.menu.community_detail, menu);
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
        } else if (itemId == R.id.menuRevise) {
            finish();
            Intent intent = new Intent(CommunityDetailActivity.this, CommunityWriteActivity.class);
            intent.putExtra("revise", true);
            intent.putExtra("postingId", community.getId());
            startActivity(intent);
        } else if (itemId == R.id.menuDelete) {
            showProgress("삭제중...");
            Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
            CommunityApi communityApi = retrofit.create(CommunityApi.class);
            SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
            String accessToken = sp.getString("accessToken", "");
            Call<UserRes> call = communityApi.deleteCommunity("Bearer " + accessToken, community.getId());
            call.enqueue(new Callback<UserRes>() {
                @Override
                public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                    dismissProgress();
                    if(response.isSuccessful()) {
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<UserRes> call, Throwable t) {
                    dismissProgress();
                    isClicked = false;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    public void reviseComment(String cmt, int index) {
        Comment comment = new Comment(cmt);
        showProgress("댓글 수정하는중...");
        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        CommunityApi communityApi = retrofit.create(CommunityApi.class);

        Call<UserRes> call = communityApi.reviseComment("Bearer " + accessToken, community.getId(), communityCommentList.get(index).getId(), comment);
        call.enqueue(new Callback<UserRes>() {
            @Override
            public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                dismissProgress();
                if (response.isSuccessful()) {
                    Log.i("test", "test");
                    getNetworkData();
                }
            }

            @Override
            public void onFailure(Call<UserRes> call, Throwable t) {
                dismissProgress();
            }
        });

    }

    public void deleteComment(int index) {
        showProgress("댓글 삭제하는중...");
        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        CommunityApi communityApi = retrofit.create(CommunityApi.class);

        Call<UserRes> call = communityApi.deleteComment("Bearer " + accessToken, community.getId(), communityCommentList.get(index).getId());
        call.enqueue(new Callback<UserRes>() {
            @Override
            public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                dismissProgress();
                if (response.isSuccessful()) {
                    getNetworkData();
                }
            }

            @Override
            public void onFailure(Call<UserRes> call, Throwable t) {
                dismissProgress();
                isClicked = false;
            }
        });
    }
}