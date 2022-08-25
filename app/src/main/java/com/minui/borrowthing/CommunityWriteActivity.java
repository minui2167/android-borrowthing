package com.minui.borrowthing;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.minui.borrowthing.api.CommunityApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.UserRes;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class CommunityWriteActivity extends AppCompatActivity {

    TextInputEditText txtContent;
    ImageView imgCommunity;
    FloatingActionButton fabLeft;
    FloatingActionButton fabRight;

    //변수
    ArrayList<Uri> uriList = new ArrayList<>();
    ArrayList<File> fileList = new ArrayList<>();
    ArrayList<MultipartBody.Part> multiPartBodyPartList = new ArrayList<>();
    int index = 0;
    boolean revise = false;
    boolean isClicked = false;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_write);
        revise = getIntent().getBooleanExtra("revise", false);

        // 액션바 제목 백버튼 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle("게시물 등록");
        ac.setHomeAsUpIndicator(R.drawable.ic_back_30);
        ac.setDisplayHomeAsUpEnabled(true);

        txtContent = findViewById(R.id.txtContent);
        imgCommunity = findViewById(R.id.imgCommunity);
        fabLeft = findViewById(R.id.fabLeft);
        fabRight = findViewById(R.id.fabRight);
        fabLeft.setVisibility(View.GONE);
        fabRight.setVisibility(View.GONE);

        verifyStoragePermissions(CommunityWriteActivity.this);

        imgCommunity.setOnClickListener(new View.OnClickListener() {
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
                imgCommunity.setImageURI(uriList.get(index));
            }
        });

        fabRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uriList.size() - 1 == index) {
                    return;
                }
                index++;
                imgCommunity.setImageURI(uriList.get(index));
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
                            imgCommunity.setImageURI(uriList.get(0));
                            if (uriList.size() > 1) {
                                fabLeft.setVisibility(View.VISIBLE);
                                fabRight.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            });

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
            showProgress("작성중 입니다...");
            Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
            CommunityApi api = retrofit.create(CommunityApi.class);

            for (int i = 0;i < fileList.size();i++) {
                RequestBody fileBody = RequestBody.create(fileList.get(i), MediaType.parse("image/*"));
                MultipartBody.Part photo = MultipartBody.Part.createFormData("photo", "photo", fileBody);
                multiPartBodyPartList.add(photo);
            }

            RequestBody contentBody = RequestBody.create(txtContent.getText().toString().trim(), MediaType.parse("text/plain"));

            SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
            String accessToken = sp.getString("accessToken", "");

            Call<UserRes> call;
            if (revise) {
                call = api.reviseCommunity("Bearer " + accessToken, getIntent().getIntExtra("postingId", 0), multiPartBodyPartList, contentBody);
            } else {
                call = api.setCommunity("Bearer " + accessToken, multiPartBodyPartList, contentBody);
            }

            call.enqueue(new Callback<UserRes>() {
                @Override
                public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                    dismissProgress();
                    if (response.isSuccessful()) {
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
        int permission = ActivityCompat.checkSelfPermission(CommunityWriteActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
            localBuilder.setTitle("권한 설정")
                    .setMessage("권한을 설정하지 않으면 앱 사용이 불가능합니다.")
                    .setPositiveButton("권한 설정하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            verifyStoragePermissions(CommunityWriteActivity.this);
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

    void showProgress(String message) {
        dialog = new ProgressDialog(CommunityWriteActivity.this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }

    void dismissProgress() {
        dialog.dismiss();
    }
}