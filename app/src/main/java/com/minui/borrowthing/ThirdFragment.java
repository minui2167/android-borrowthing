package com.minui.borrowthing;

import static android.content.Context.MODE_PRIVATE;

import static com.minui.borrowthing.MainActivity.context;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.minui.borrowthing.adapter.CommunityAdapter;
import com.minui.borrowthing.api.CommunityApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.Community;
import com.minui.borrowthing.model.CommunityResult;
import com.minui.borrowthing.model.UserRes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // 리사이클러 뷰 관련 멤버변수
    RecyclerView recyclerView;
    CommunityAdapter adapter;
    ArrayList<Community> communityList = new ArrayList<>();

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    // 페이징에 필요한 변수
    int offset = 0;
    int limit = 10;
    int count = 0;

    // ui
    Button btnPopularity;
    Button btnLatest;
    FloatingActionButton fab;

    // 변수
    String accessToken;

    public ThirdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_third, container, false);

        btnPopularity = rootView.findViewById(R.id.btnPopularity);
        btnLatest = rootView.findViewById(R.id.btnLatest);
        fab = rootView.findViewById(R.id.fab);

        btnPopularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(communityList, new Comparator<Community>() {
                    @Override
                    public int compare(Community community1, Community community2) {
                        if (community1.getLikesCount() == community2.getLikesCount())
                        {
                            if (community1.getCommentCount() == community2.getCommentCount()) {
                                return community2.getCreatedAt().compareTo(community1.getCreatedAt());
                            }
                            return community2.getCommentCount() - community1.getCommentCount();
                        }
                        return community2.getLikesCount() - community1.getLikesCount();
                    }
                });
                adapter.notifyDataSetChanged();
            }
        });

        btnLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(communityList, new Comparator<Community>() {
                    @Override
                    public int compare(Community community1, Community community2) {
                        return community2.getCreatedAt().compareTo(community1.getCreatedAt());
                    }
                });
                adapter.notifyDataSetChanged();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((MainActivity) context).isLogin()) {
                    ((MainActivity) context).login();
                    return;
                }
                Intent intent = new Intent(getContext(), CommunityWriteActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getNetworkData();
    }

    private void getNetworkData() {
        communityList.clear();

        offset = 0;
        limit = 10;
        count = 0;

        showProgress("게시물 가져오는중...");
        SharedPreferences sp = getActivity().getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        CommunityApi communityApi = retrofit.create(CommunityApi.class);

        Call<CommunityResult> call;
        if (accessToken.isEmpty()) {
            call = communityApi.getCommunityList(offset, limit);
        } else {
            call = communityApi.getCommunityList(offset, limit, "Bearer " + accessToken);
        }

        call.enqueue(new Callback<CommunityResult>() {
            @Override
            public void onResponse(Call<CommunityResult> call, Response<CommunityResult> response) {
                dismissProgress();
                CommunityResult communityResult = response.body();
                count = communityResult.getCount();
                communityList.addAll(communityResult.getItems());
                offset = offset + count;

                adapter = new CommunityAdapter(getContext(), communityList, "thirdFragment");
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<CommunityResult> call, Throwable t) {
                dismissProgress();
            }
        });
    }

    private void addNetworkData() {
        showProgress("게시물 가져오는중...");
        SharedPreferences sp = getActivity().getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        CommunityApi communityApi = retrofit.create(CommunityApi.class);

        Call<CommunityResult> call;
        if (accessToken.isEmpty()) {
            call = communityApi.getCommunityList(offset, limit);
        } else {
            call = communityApi.getCommunityList(offset, limit, "Bearer " + accessToken);
        }

        call.enqueue(new Callback<CommunityResult>() {
            @Override
            public void onResponse(Call<CommunityResult> call, Response<CommunityResult> response) {
                dismissProgress();
                CommunityResult communityResult = response.body();
                count = communityResult.getCount();
                communityList.addAll(communityResult.getItems());
                offset = offset + count;

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CommunityResult> call, Throwable t) {
                dismissProgress();
            }
        });
    }

    public void setLike(int index) {
        if (!((MainActivity) context).isLogin()) {
            ((MainActivity) context).login();
            return;
        }
        showProgress("추천 하는중...");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        CommunityApi communityApi = retrofit.create(CommunityApi.class);
        SharedPreferences sp = getActivity().getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");

        if (communityList.get(index).getIsLike() == 0) {
            Call<UserRes> call = communityApi.setLike(communityList.get(index).getId(), "Bearer " + accessToken);
            call.enqueue(new Callback<UserRes>() {
                @Override
                public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                    dismissProgress();
                    if(response.isSuccessful()) {
                        communityList.get(index).setIsLike(1);
                        communityList.get(index).setLikesCount(response.body().getLikesCount());
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<UserRes> call, Throwable t) {
                    dismissProgress();
                }
            });
        } else {
            Call<UserRes> call = communityApi.setLikeCancel(communityList.get(index).getId(), "Bearer " + accessToken);
            call.enqueue(new Callback<UserRes>() {
                @Override
                public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                    dismissProgress();
                    if(response.isSuccessful()) {
                        communityList.get(index).setIsLike(0);
                        communityList.get(index).setLikesCount(response.body().getLikesCount());
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
}