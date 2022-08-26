package com.minui.borrowthing;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.BorrowResult;
import com.minui.borrowthing.model.Rating;
import com.minui.borrowthing.model.item;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EvaluationActivity extends AppCompatActivity {
    ImageView imgBorrow;
    FloatingActionButton fabLeft;
    FloatingActionButton fabRight;
    TextInputEditText txtContent;
    RatingBar ratingBar;
    Button btnRegister;

    int index = 0;
    boolean isClicked = false;
    item item;
    int goodsId;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        imgBorrow = findViewById(R.id.imgBorrow);
        fabLeft = findViewById(R.id.fabLeft);
        fabRight = findViewById(R.id.fabRight);
        txtContent = findViewById(R.id.txtContent);
        ratingBar = findViewById(R.id.ratingBar);
        btnRegister = findViewById(R.id.btnRegister);

        fabLeft.setVisibility(View.GONE);
        fabRight.setVisibility(View.GONE);


        item = (item) getIntent().getSerializableExtra("item");

        if (item.getImgUrl().size() > 1) {
            fabLeft.setVisibility(View.VISIBLE);
            fabRight.setVisibility(View.VISIBLE);
        }
        try {
            GlideUrl url = new GlideUrl(Config.IMAGE_URL + item.getImgUrl().get(0).getImageUrl(), new LazyHeaders.Builder().addHeader("User-Agent", "Android").build());
            Glide.with(EvaluationActivity.this).load(url).into(imgBorrow);
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

//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int rating = (int)ratingBar.getRating();
//                if (rating == 0){
//                    Toast.makeText(getApplicationContext(), "평점을 입력해주세요.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
//                String accessToken = sp.getString("accessToken", "");
//                Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
//                BorrowApi borrowApi = retrofit.create(BorrowApi.class);
//
//                Call<BorrowResult> call;
//                call = borrowApi.setRating("Bearer " + accessToken, goodsId, rating);
//
//                call.enqueue(new Callback<BorrowResult>() {
//                    @Override
//                    public void onResponse(Call<BorrowResult> call, Response<BorrowResult> response) {
//                        dismissProgress();
//                        if (response.isSuccessful()) {
//                            BorrowResult borrowResult = response.body();
//                            count = borrowResult.getCount();
//
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<BorrowResult> call, Throwable t) {
//                        dismissProgress();
//                    }
//                });
//            }
//        });

    }
    private void setImgCommunity(int index) {
        try {
            GlideUrl url = new GlideUrl(Config.IMAGE_URL + item.getImgUrl().get(index).getImageUrl(), new LazyHeaders.Builder().addHeader("User-Agent", "Android").build());
            Glide.with(EvaluationActivity.this).load(url).into(imgBorrow);
        } catch (Exception e) {
            imgBorrow.setImageResource(R.drawable.ic_photo);
        }
    }
}