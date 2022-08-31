package com.minui.borrowthing;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.minui.borrowthing.model.Score;
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

        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle("상품평");
        ac.setDisplayHomeAsUpEnabled(true);

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

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((int)ratingBar.getRating() == 0){
                    Toast.makeText(getApplicationContext(), "평점을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Score score = new Score((int)ratingBar.getRating());

                SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String accessToken = sp.getString("accessToken", "");
                Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
                BorrowApi borrowApi = retrofit.create(BorrowApi.class);

                Call<BorrowResult> call;
                call = borrowApi.setRating("Bearer " + accessToken, goodsId, score);
                showProgress("평점을 등록하는 중...");
                call.enqueue(new Callback<BorrowResult>() {
                    @Override
                    public void onResponse(Call<BorrowResult> call, Response<BorrowResult> response) {
                        dismissProgress();
                        if (response.isSuccessful()) {
                            BorrowResult borrowResult = response.body();

                            if(borrowResult.getResult().toString().equals("success")) {
                                Toast.makeText(getApplication(), "평점 등록이 완료됐습니다.", Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor = sp.edit();
                                int ratingCount = sp.getInt("ratingCount", 0);
                                editor.putInt("ratingCount", ratingCount+1);
                                editor.apply();
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

    }
    private void setImgCommunity(int index) {
        try {
            GlideUrl url = new GlideUrl(Config.IMAGE_URL + item.getImgUrl().get(index).getImageUrl(), new LazyHeaders.Builder().addHeader("User-Agent", "Android").build());
            Glide.with(EvaluationActivity.this).load(url).into(imgBorrow);
        } catch (Exception e) {
            imgBorrow.setImageResource(R.drawable.ic_photo);
        }
    }
    void showProgress(String message) {
        dialog = new ProgressDialog(EvaluationActivity.this);
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