package com.minui.borrowthing;

import static android.content.Context.MODE_PRIVATE;

import static com.minui.borrowthing.MainActivity.context;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.minui.borrowthing.adapter.BorrowAdapter;
import com.minui.borrowthing.adapter.BorrowRecommendAdapter;
import com.minui.borrowthing.adapter.CommunityAdapter;
import com.minui.borrowthing.api.BorrowApi;
import com.minui.borrowthing.api.CommunityApi;
import com.minui.borrowthing.api.UserApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.Area;
import com.minui.borrowthing.model.AreaInfo;
import com.minui.borrowthing.model.AreaRes;
import com.minui.borrowthing.model.BorrowResult;
import com.minui.borrowthing.model.CommunityResult;
import com.minui.borrowthing.model.UserRes;
import com.minui.borrowthing.model.item;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // 리사이클러 뷰 관련 멤버변수
    RecyclerView recyclerView;
    BorrowAdapter adapter;
    ArrayList<item> itemList = new ArrayList<>();
    RecyclerView recyclerViewRecommend;
    BorrowRecommendAdapter adapterRecommend;

    ActionBar ac; // 액션바

    ArrayList<item> itemRecommendList = new ArrayList<>();

    ArrayList<AreaInfo> areaList = new ArrayList<>();
    // 페이징에 필요한 변수
    int offset = 0;
    int limit = 20;
    int count = 0;
    int offsetRecommend = 0;
    int limitRecommend = 20;
    int countRecommend = 0;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;
    ProgressDialog dialogRecommend;

    // ui
    FloatingActionButton fab;
    TextView txtNickname;

    boolean isMyArea = false;
    int contextStatus = 0;

    public FirstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment newInstance(String param1, String param2) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_first, container, false);

        ac = ((AppCompatActivity)getActivity()).getSupportActionBar();

        fab = rootView.findViewById(R.id.fab);
        txtNickname = rootView.findViewById(R.id.txtNickname);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewRecommend = rootView.findViewById(R.id.recyclerViewRecommend);
        recyclerViewRecommend.setHasFixedSize(true);
        recyclerViewRecommend.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((MainActivity) context).isLogin()) {
                    ((MainActivity) context).login();
                    return;
                }

                // 우리동네 불러오기 API 결과의 items 가 null 이면 활동범위를 설정하라는 토스트 메시지 출력
                contextStatus = 1;
                if(!getMyLocation()){
                    return;
                }

            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        txtNickname.setVisibility(View.GONE);
        getNetworkData();
        SharedPreferences sp = getActivity().getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        int ratingCount = sp.getInt("ratingCount", 0);
        if (ratingCount > 2)
            getRecommendData();
    }


    public boolean getMyLocation() {
        areaList.clear();
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        UserApi userApi = retrofit.create(UserApi.class);
        SharedPreferences sp = getActivity().getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Call<AreaRes> call = userApi.getMyLocation("Bearer " + accessToken);
        call.enqueue(new Callback<AreaRes>() {
            @Override
            public void onResponse(Call<AreaRes> call, Response<AreaRes> response) {
                if(response.isSuccessful()) {
                    AreaRes areaRes = response.body();
                    areaList.addAll(areaRes.getItems());
//                    Log.i("test", areaList.get(0).getEmd());
                    if(areaList.isEmpty()){
                        isMyArea = false;
                        if(contextStatus == 1){
                            Toast.makeText(getActivity(), "동네 인증 후 게시글 등록 가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                        ac.setTitle("");
                    } else{
                        isMyArea = true;
                        Log.i("teste", areaList.get(0).getEmd());
                        if (contextStatus == 1){
                            Intent intent = new Intent(getContext(), BorrowWriteActivity.class);
                            startActivity(intent);
                        }
                        ac.setTitle(areaList.get(0).getEmd());

                    }

                }
            }

            @Override
            public void onFailure(Call<AreaRes> call, Throwable t) {

            }
        });
        return isMyArea;
    }

    private void getRecommendData() {
        SharedPreferences sp = getActivity().getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        if (accessToken.isEmpty()) {
            return;
        }

        itemRecommendList.clear();

        offsetRecommend = 0;
        limitRecommend = 10;
        countRecommend = 0;

        showRecommendProgress("게시물 가져오는중...");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        BorrowApi borrowApi = retrofit.create(BorrowApi.class);
        Call<BorrowResult> call = borrowApi.getRecommend("Bearer " + accessToken, offsetRecommend, limitRecommend);

        call.enqueue(new Callback<BorrowResult>() {
            @Override
            public void onResponse(Call<BorrowResult> call, Response<BorrowResult> response) {
                dismissRecommendProgress();
                if (response.isSuccessful()) {
                    BorrowResult borrowResult = response.body();
                    countRecommend = borrowResult.getCount();
                    itemRecommendList.addAll(borrowResult.getItems());
                    offsetRecommend = offsetRecommend + countRecommend;
                    adapterRecommend = new BorrowRecommendAdapter(getContext(), itemRecommendList);
                    recyclerViewRecommend.setAdapter(adapterRecommend);
                    txtNickname.setVisibility(View.VISIBLE);
                    SharedPreferences sp = getActivity().getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                    String nickName = sp.getString("nickName", "");
                    txtNickname.setText(nickName + "님의 추천 상품");
                }
            }

            @Override
            public void onFailure(Call<BorrowResult> call, Throwable t) {
                dismissRecommendProgress();
            }
        });


    }

    private void getNetworkData() {
        itemList.clear();

        offset = 0;
        limit = 20;
        count = 0;

        showProgress("게시물 가져오는중...");
        SharedPreferences sp = getActivity().getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        BorrowApi borrowApi = retrofit.create(BorrowApi.class);

        Call<BorrowResult> call;
        if (accessToken.isEmpty()) {
            call = borrowApi.getGoods(offset, limit, 0);
        } else {
            call = borrowApi.getGoods(offset, limit, 0, "Bearer " + accessToken);
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

                    adapter = new BorrowAdapter(getContext(), itemList, "firstFragment");
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
        SharedPreferences sp = getActivity().getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        BorrowApi borrowApi = retrofit.create(BorrowApi.class);

        Call<BorrowResult> call;
        if (accessToken.isEmpty()) {
            call = borrowApi.getGoods(offset, limit, 0);
        } else {
            call = borrowApi.getGoods(offset, limit, 0, "Bearer " + accessToken);
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

    public void setConcerned(int index) {
        if (!((MainActivity) context).isLogin()) {
            ((MainActivity) context).login();
            return;
        }
        showProgress("관심상품 등록중...");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        BorrowApi borrowApi = retrofit.create(BorrowApi.class);
        SharedPreferences sp = getActivity().getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(message);
        builder.setPositiveButton("확인", null);
        builder.show();
    }

    void showProgress(String message) {
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }

    void dismissProgress() {
        dialog.dismiss();
    }

    void showRecommendProgress(String message) {
        dialogRecommend = new ProgressDialog(getContext());
        dialogRecommend.setCancelable(false);
        dialogRecommend.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogRecommend.setMessage(message);
        dialogRecommend.show();
    }

    void dismissRecommendProgress() {
        dialogRecommend.dismiss();
    }
}