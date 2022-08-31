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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.minui.borrowthing.adapter.BorrowCommentAdapter;
import com.minui.borrowthing.adapter.CommunityCommentAdapter;
import com.minui.borrowthing.api.BorrowApi;
import com.minui.borrowthing.api.CommunityApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.BorrowComment;
import com.minui.borrowthing.model.BorrowCommentResult;
import com.minui.borrowthing.model.BorrowResult;
import com.minui.borrowthing.model.Comment;
import com.minui.borrowthing.model.Community;
import com.minui.borrowthing.model.CommunityComment;
import com.minui.borrowthing.model.CommunityCommentResult;
import com.minui.borrowthing.model.UserRes;
import com.minui.borrowthing.model.item;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BorrowDetailActivity extends AppCompatActivity {

    ImageView imgBorrow;
    FloatingActionButton fabLeft;
    FloatingActionButton fabRight;
    TextInputEditText txtContent;
    ImageView imgHeart;
    TextView txtDetail;
    TextView txtComment;
    Button btnRegister;
    Button btnDeal;
    // 변수
    int index = 0;
    item item;
    boolean isClicked = false;
    int goodsId;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    // 리사이클러 뷰 관련 멤버변수
    RecyclerView recyclerView;
    BorrowCommentAdapter adapter;
    ArrayList<BorrowComment> borrowCommentList = new ArrayList<>();

    // 페이징에 필요한 변수
    int offset = 0;
    int limit = 20;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_detail);

        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle("게시물 상세");
        ac.setHomeAsUpIndicator(R.drawable.ic_back_30);
        ac.setDisplayHomeAsUpEnabled(true);

        imgBorrow = findViewById(R.id.imgBorrow);
        fabLeft = findViewById(R.id.fabLeft);
        fabRight = findViewById(R.id.fabRight);
        txtContent = findViewById(R.id.txtContent);
        imgHeart = findViewById(R.id.imgHeart);
        txtDetail = findViewById(R.id.txtDetail);
        txtComment = findViewById(R.id.txtComment);
        btnDeal = findViewById(R.id.btnDeal);
        btnRegister = findViewById(R.id.btnRegister);
        fabLeft.setVisibility(View.GONE);
        fabRight.setVisibility(View.GONE);
        btnDeal.setVisibility(View.VISIBLE);


        item = (item) getIntent().getSerializableExtra("item");
        if (item.getImgUrl().size() > 1) {
            fabLeft.setVisibility(View.VISIBLE);
            fabRight.setVisibility(View.VISIBLE);
        }
        try {
            GlideUrl url = new GlideUrl(Config.IMAGE_URL + item.getImgUrl().get(0).getImageUrl(), new LazyHeaders.Builder().addHeader("User-Agent", "Android").build());
            Glide.with(BorrowDetailActivity.this).load(url).into(imgBorrow);
        } catch (Exception e) {
            imgBorrow.setImageResource(R.drawable.ic_photo);
        }

        if (item.getIsAuthor() == 1 || item.getStatus() != 0)
            btnDeal.setVisibility(View.GONE);

        goodsId = item.getId();

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
                if (item.getImgUrl().size() - 1 == index) {
                    return;
                }
                index++;
                setImgCommunity(index);
            }
        });

        txtContent.setClickable(false);
        txtContent.setFocusable(false);
        txtContent.setText(item.getContent());

        if (item.getIsWish() == 0) {
            imgHeart.setImageResource(R.drawable.ic_heart);
        } else {
            imgHeart.setImageResource(R.drawable.heart_red);
        }
        String period = item.getRentalPeriod().replace(",", "~");
        txtDetail.setText("가격: " + item.getPrice()+ "원"+ "\n기간: " + period);

        imgHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((MainActivity) context).isLogin()) {
                    ((MainActivity) context).login();
                    return;
                }
                showProgress("관심상품 등록중...");
                Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
                BorrowApi borrowApi = retrofit.create(BorrowApi.class);
                SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String accessToken = sp.getString("accessToken", "");

                if (item.getIsWish() == 0) {
                    Call<UserRes> call = borrowApi.setConcerned("Bearer " + accessToken, goodsId);
                    call.enqueue(new Callback<UserRes>() {
                        @Override
                        public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                            dismissProgress();
                            if(response.isSuccessful()) {
                                item.setIsWish(1);
                                imgHeart.setImageResource(R.drawable.heart_red);

                            }
                        }

                        @Override
                        public void onFailure(Call<UserRes> call, Throwable t) {
                            dismissProgress();
                        }
                    });
                } else {
                    Call<UserRes> call = borrowApi.setConcernedCancel("Bearer " + accessToken,goodsId);
                    call.enqueue(new Callback<UserRes>() {
                        @Override
                        public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                            dismissProgress();
                            if(response.isSuccessful()) {
                                item.setIsWish(0);
                                imgHeart.setImageResource(R.drawable.ic_heart);
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
                BorrowApi borrowApi = retrofit.create(BorrowApi.class);
                SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String accessToken = sp.getString("accessToken", "");
                Comment comment = new Comment(txtComment.getText().toString().trim());

                Call<UserRes> call = borrowApi.setComment("Bearer " + accessToken, goodsId, comment);
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        dismissProgress();
                        if (response.isSuccessful()) {
                            getNetworkData();
                            btnRegister.setText("");
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {
                        dismissProgress();
                    }
                });
            }
        });

        btnDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress("댓글 다는중...");
                Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
                BorrowApi borrowApi = retrofit.create(BorrowApi.class);
                SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String accessToken = sp.getString("accessToken", "");
                Comment comment = new Comment(txtComment.getText().toString().trim());

                Call<BorrowResult> call = borrowApi.setTransactionRequest("Bearer " + accessToken, goodsId);
                call.enqueue(new Callback<BorrowResult>() {
                    @Override
                    public void onResponse(Call<BorrowResult> call, Response<BorrowResult> response) {
                        dismissProgress();
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplication(), "거래 신청 됐습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<BorrowResult> call, Throwable t) {
                        dismissProgress();
                    }
                });
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(BorrowDetailActivity.this));

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
        borrowCommentList.clear();

        offset = 0;
        limit = 20;
        count = 0;

        showProgress("댓글 가져오는중...");
        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        BorrowApi borrowApi = retrofit.create(BorrowApi.class);

        Call<BorrowCommentResult> call;
        if (accessToken.isEmpty()) {
            call = borrowApi.getCommentList(goodsId, offset, limit);
        } else {
            call = borrowApi.getCommentList("Bearer " + accessToken, goodsId, offset, limit);
        }

        call.enqueue(new Callback<BorrowCommentResult>() {
            @Override
            public void onResponse(Call<BorrowCommentResult> call, Response<BorrowCommentResult> response) {
                dismissProgress();
                BorrowCommentResult borrowCommentResult = response.body();
                count = borrowCommentResult.getCount();
                borrowCommentList.addAll(borrowCommentResult.getItems());
                offset = offset + count;

                adapter = new BorrowCommentAdapter(BorrowDetailActivity.this, borrowCommentList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<BorrowCommentResult> call, Throwable t) {
                dismissProgress();
            }
        });
    }

    private void addNetworkData() {
        showProgress("댓글 가져오는중...");
        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        BorrowApi borrowApi = retrofit.create(BorrowApi.class);

        Call<BorrowCommentResult> call;
        if (accessToken.isEmpty()) {
            call = borrowApi.getCommentList(goodsId, offset, limit);
        } else {
            call = borrowApi.getCommentList("Bearer " + accessToken, goodsId, offset, limit);
        }

        call.enqueue(new Callback<BorrowCommentResult>() {
            @Override
            public void onResponse(Call<BorrowCommentResult> call, Response<BorrowCommentResult> response) {
                dismissProgress();
                BorrowCommentResult borrowCommentResult = response.body();
                count = borrowCommentResult.getCount();
                borrowCommentList.addAll(borrowCommentResult.getItems());
                offset = offset + count;

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<BorrowCommentResult> call, Throwable t) {
                dismissProgress();
            }
        });
    }

    void showProgress(String message) {
        dialog = new ProgressDialog(BorrowDetailActivity.this);
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
            GlideUrl url = new GlideUrl(Config.IMAGE_URL + item.getImgUrl().get(index).getImageUrl(), new LazyHeaders.Builder().addHeader("User-Agent", "Android").build());
            Glide.with(BorrowDetailActivity.this).load(url).into(imgBorrow);
        } catch (Exception e) {
            imgBorrow.setImageResource(R.drawable.ic_photo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("test", item.getIsAuthor() + "");
        if (item.getIsAuthor() == 0) {
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
            Intent intent = new Intent(BorrowDetailActivity.this, BorrowWriteActivity.class);
            intent.putExtra("revise", true);
            intent.putExtra("goodsId", goodsId);
            startActivity(intent);
        } else if (itemId == R.id.menuDelete) {
            showProgress("삭제중...");
            Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
            BorrowApi borrowApi = retrofit.create(BorrowApi.class);
            SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
            String accessToken = sp.getString("accessToken", "");
            Call<UserRes> call = borrowApi.deleteBorrow("Bearer " + accessToken, goodsId);
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
        BorrowApi borrowApi = retrofit.create(BorrowApi.class);

        Call<UserRes> call = borrowApi.reviseComment("Bearer " + accessToken, goodsId, borrowCommentList.get(index).getId(), comment);
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
            }
        });

    }

    public void deleteComment(int index) {
        showProgress("댓글 삭제하는중...");
        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        BorrowApi borrowApi = retrofit.create(BorrowApi.class);

        Call<UserRes> call = borrowApi.deleteComment("Bearer " + accessToken, goodsId, borrowCommentList.get(index).getId());
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
            }
        });
    }
}