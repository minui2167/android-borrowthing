package com.minui.borrowthing;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.minui.borrowthing.api.BorrowApi;
import com.minui.borrowthing.api.ChatApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.BorrowResult;
import com.minui.borrowthing.model.ChatRoom;
import com.minui.borrowthing.model.ChatRoomRes;
import com.minui.borrowthing.model.item;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BorrowDetailStatusIngActivity extends AppCompatActivity {
    ImageView imgBorrow;
    FloatingActionButton fabLeft;
    FloatingActionButton fabRight;
    TextInputEditText txtContent;
    RatingBar ratingBar;
    Button btnCompleted;
    TextView txtDetail;
    ImageView btnChat;
    int index = 0;
    boolean isClicked = false;
    com.minui.borrowthing.model.item item;
    int goodsId;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    String type = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_detail_status_ing);

        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle("거래 중");
        ac.setDisplayHomeAsUpEnabled(true);

        imgBorrow = findViewById(R.id.imgBorrow);
        fabLeft = findViewById(R.id.fabLeft);
        fabRight = findViewById(R.id.fabRight);
        txtContent = findViewById(R.id.txtContent);
        ratingBar = findViewById(R.id.ratingBar);
        txtDetail = findViewById(R.id.txtDetail);
        btnCompleted = findViewById(R.id.btnCompleted);
        btnChat = findViewById(R.id.btnChat);
        fabLeft.setVisibility(View.GONE);
        fabRight.setVisibility(View.GONE);

        item = (item) getIntent().getSerializableExtra("item");

        if (item.getImgUrl().size() > 1) {
            fabLeft.setVisibility(View.VISIBLE);
            fabRight.setVisibility(View.VISIBLE);
        }
        try {
            GlideUrl url = new GlideUrl(Config.IMAGE_URL + item.getImgUrl().get(0).getImageUrl(), new LazyHeaders.Builder().addHeader("User-Agent", "Android").build());
            Glide.with(BorrowDetailStatusIngActivity.this).load(url).into(imgBorrow);
            Log.i("imageUrl : ", item.getImgUrl().get(0).getImageUrl()+"");
        } catch (Exception e) {
            imgBorrow.setImageResource(R.drawable.ic_photo);
        }
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

        txtDetail.setText("가격: " + item.getPrice() + "원\n기간: " + item.getRentalPeriod().replace(",", " ~ "));
        if (item.getIsAuthor() == 1)
            btnCompleted.setVisibility(View.GONE);
        // 거래완료하기
        btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String accessToken = sp.getString("accessToken", "");
                Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
                BorrowApi borrowApi = retrofit.create(BorrowApi.class);

                Call<BorrowResult> call;
                call = borrowApi.setTransactionCompletion("Bearer " + accessToken, goodsId);
                showProgress("거래 완료 중...");
                call.enqueue(new Callback<BorrowResult>() {
                    @Override
                    public void onResponse(Call<BorrowResult> call, Response<BorrowResult> response) {
                        dismissProgress();
                        if (response.isSuccessful()) {
                            BorrowResult borrowResult = response.body();

                            if(borrowResult.getResult().toString().equals("success")) {
                                Toast.makeText(getApplication(), "거래 완료됐습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<BorrowResult> call, Throwable t) {
                        dismissProgress();
                    }
                });
            }
        });
        // 채팅버튼
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String accessToken = sp.getString("accessToken", "");
                Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
                ChatApi chatApi = retrofit.create(ChatApi.class);
                Log.i("gggg", item.getIsAuthor()+"");
                if (item.getIsAuthor() == 1) {
                    type = "seller";
                }else{
                    type = "buyer";
                }

                Call<ChatRoomRes> call;
                call = chatApi.setChatRoom("Bearer " + accessToken, goodsId, type);
                showProgress("채팅방 입장 중...");
                call.enqueue(new Callback<ChatRoomRes>() {
                    @Override
                    public void onResponse(Call<ChatRoomRes> call, Response<ChatRoomRes> response) {
                        dismissProgress();
                        if (response.isSuccessful()) {
                            ChatRoomRes chatRoomRes = response.body();
                            Log.i("test", chatRoomRes.getResult()+"");
                            ChatRoom chatRoom = chatRoomRes.getItems().get(0);
                            Intent intent = new Intent(BorrowDetailStatusIngActivity.this, ChatActivity.class);
                            intent.putExtra("type", type);
                            intent.putExtra("item", item);
                            intent.putExtra("chatRoom", chatRoom);

                            startActivity(intent);

                        }
                    }

                    @Override
                    public void onFailure(Call<ChatRoomRes> call, Throwable t) {
                        dismissProgress();
                    }
                });

            }
        });
    }

    private void setImgCommunity(int index) {
        try {
            GlideUrl url = new GlideUrl(Config.IMAGE_URL + item.getImgUrl().get(index).getImageUrl(), new LazyHeaders.Builder().addHeader("User-Agent", "Android").build());
            Glide.with(BorrowDetailStatusIngActivity.this).load(url).into(imgBorrow);
        } catch (Exception e) {
            imgBorrow.setImageResource(R.drawable.ic_photo);
        }
    }
    void showProgress(String message) {
        dialog = new ProgressDialog(BorrowDetailStatusIngActivity.this);
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