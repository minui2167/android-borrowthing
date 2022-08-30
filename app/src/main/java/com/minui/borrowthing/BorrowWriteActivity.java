package com.minui.borrowthing;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.minui.borrowthing.api.BorrowApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.UserRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BorrowWriteActivity extends AppCompatActivity {

    TextInputEditText txtTitle;
    AutoCompleteTextView txtCategory;
    ImageView imgBorrow;
    FloatingActionButton fabLeft;
    FloatingActionButton fabRight;
    TextInputEditText txtContent;
    TextInputEditText txtPrice;
    TextInputEditText txtPeriod;

    //변수
    ArrayList<Uri> uriList = new ArrayList<>();
    ArrayList<File> fileList = new ArrayList<>();
    ArrayList<MultipartBody.Part> multiPartBodyPartList = new ArrayList<>();
    int index = 0;
    boolean revise = false;
    boolean isClicked = false;
    String date;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_write);
        revise = getIntent().getBooleanExtra("revise", false);

        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle("게시물 등록");
        ac.setHomeAsUpIndicator(R.drawable.ic_back_30);
        ac.setDisplayHomeAsUpEnabled(true);

        txtTitle = findViewById(R.id.txtKeyword);
        txtCategory = findViewById(R.id.txtCategory);
        imgBorrow = findViewById(R.id.imgBorrow);
        fabLeft = findViewById(R.id.fabLeft);
        fabRight = findViewById(R.id.fabRight);
        fabLeft.setVisibility(View.GONE);
        fabRight.setVisibility(View.GONE);
        txtContent = findViewById(R.id.txtContent);
        txtPrice = findViewById(R.id.txtPrice);
        txtPeriod = findViewById(R.id.txtPeriod);

        String[] categories = new String[]{"도서", "디지털 기기", "생활가전", "스포츠/레져", "취미/게임", "의류"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(BorrowWriteActivity.this, android.R.layout.simple_dropdown_item_1line, categories);
        txtCategory.setAdapter(arrayAdapter);
        txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtCategory.showDropDown();
            }
        });

        verifyStoragePermissions(BorrowWriteActivity.this);

        imgBorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                launcher.launch(intent);
            }
        });

        fabLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index == 0) {
                    return;
                }
                index--;
                imgBorrow.setImageURI(uriList.get(index));
            }
        });

        fabRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uriList.size() - 1 == index) {
                    return;
                }
                index++;
                imgBorrow.setImageURI(uriList.get(index));
            }
        });

        txtPeriod.setFocusable(false);
        txtPeriod.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                LocalDateTime min = LocalDateTime.now();
                LocalDateTime max = min.plusDays(30);
                builder.setSelection(new Pair(min.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), max.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
                CalendarConstraints.Builder calendarConstraintBuilder = new CalendarConstraints.Builder();
                calendarConstraintBuilder.setValidator(DateValidatorPointForward.now());
                builder.setCalendarConstraints(calendarConstraintBuilder.build());
                MaterialDatePicker<Pair<Long, Long>> picker = builder.build();
                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        date = df.format(selection.first) + "," +df.format(selection.second);
                        txtPeriod.setText(date);
                    }
                });
                picker.show(getSupportFragmentManager(), "dateRangePicker");
            }
        });
    }

    void setBorrow() {
        String title = txtTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(BorrowWriteActivity.this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = txtCategory.getText().toString().trim();
        if (category.isEmpty()) {
            Toast.makeText(BorrowWriteActivity.this, "카테고리를 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        switch(category) {
            case "도서":
                category = "1";
                break;
            case "디지털 기기":
                category = "2";
                break;
            case "생활가전":
                category = "3";
                break;
            case "스포츠/레져":
                category = "4";
                break;
            case "취미/게임":
                category = "5";
                break;
            case "의류":
                category = "6";
                break;
        }

        String content = txtContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(BorrowWriteActivity.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String price = txtPrice.getText().toString().trim();
        if (price.isEmpty()) {
            Toast.makeText(BorrowWriteActivity.this, "가격을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (date.isEmpty()) {
            Toast.makeText(BorrowWriteActivity.this, "날짜를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (uriList.size() == 0) {
            Toast.makeText(BorrowWriteActivity.this, "사진을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress("작성중 입니다...");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        BorrowApi borrowApi = retrofit.create(BorrowApi.class);

        for (int i = 0;i < fileList.size();i++) {
            RequestBody fileBody = RequestBody.create(fileList.get(i), MediaType.parse("image/*"));
            MultipartBody.Part photo = MultipartBody.Part.createFormData("photo", "photo", fileBody);
            multiPartBodyPartList.add(photo);
        }

        RequestBody titleBody = RequestBody.create(title, MediaType.parse("text/plain"));
        RequestBody categoryBody = RequestBody.create(category, MediaType.parse("text/plain"));
        RequestBody contentBody = RequestBody.create(content, MediaType.parse("text/plain"));
        RequestBody priceBody = RequestBody.create(price, MediaType.parse("text/plain"));
        RequestBody dateBody = RequestBody.create(date, MediaType.parse("text/plain"));

        SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");

        Call<UserRes> call;
        if (revise) {
            call = borrowApi.reviseBorrow("Bearer " + accessToken, getIntent().getIntExtra("goodsId", 0),titleBody, multiPartBodyPartList, contentBody, priceBody, dateBody, categoryBody);
        } else {
            call = borrowApi.setBorrow("Bearer " + accessToken, titleBody, multiPartBodyPartList, contentBody, priceBody, dateBody, categoryBody);
        }

        call.enqueue(new Callback<UserRes>() {
            @Override
            public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                dismissProgress();
                if (response.isSuccessful()) {
                    finish();
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
                isClicked = false;
            }
        });
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult data)
                {
                    if (data.getResultCode() == Activity.RESULT_OK)
                    {
                        Intent intent = data.getData();
                        ClipData clipData = intent.getClipData();

                        if(clipData!=null) {
                            for(int i = 0;i < clipData.getItemCount();i++) {
                                Uri urione = clipData.getItemAt(i).getUri();
                                uriList.add(urione);
                                fileList.add(new File(getPathFromUri(urione)));
                            }
                            imgBorrow.setImageURI(uriList.get(0));
                            if (uriList.size() > 1) {
                                fabLeft.setVisibility(View.VISIBLE);
                                fabRight.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            });

    public String getPathFromUri(Uri uri){

        Cursor cursor = getContentResolver().query(uri, null, null, null, null );

        cursor.moveToNext();

        @SuppressLint("Range") String path = cursor.getString( cursor.getColumnIndex( "_data" ) );

        cursor.close();

        return path;
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permission = ActivityCompat.checkSelfPermission(BorrowWriteActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
            localBuilder.setTitle("권한 설정")
                    .setMessage("권한을 설정하지 않으면 앱 사용이 불가능합니다.")
                    .setPositiveButton("권한 설정하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            verifyStoragePermissions(BorrowWriteActivity.this);
                        }
                    })
                    .setNegativeButton("종료하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).create().show();
        }
    }

    // 액션바 메뉴 보여주기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.community, menu);
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
        } else if(itemId == R.id.menuCheck) {
            setBorrow();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BorrowWriteActivity.this);
        builder.setTitle(message);
        builder.setPositiveButton("확인", null);
        builder.show();
    }

    void showProgress(String message) {
        dialog = new ProgressDialog(BorrowWriteActivity.this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }

    void dismissProgress() {
        dialog.dismiss();
    }
}