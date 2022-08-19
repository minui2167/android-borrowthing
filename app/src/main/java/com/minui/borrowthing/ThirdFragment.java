package com.minui.borrowthing;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.minui.borrowthing.adapter.CommunityAdapter;
import com.minui.borrowthing.api.CommunityApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.Community;
import com.minui.borrowthing.model.CommunityResult;

import java.util.ArrayList;
import java.util.List;

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
    ArrayList<Community> communities = new ArrayList<>();

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    // ui
    Button btnPopularity;
    Button btnLatest;
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    ImageView imgLeft;
    ImageView imgRight;
    ImageView imgAdd;

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

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        CommunityApi communityApi = retrofit.create(CommunityApi.class);
        Call<CommunityResult> call = communityApi.getCommunityList(0, 21);
        showProgress("게시물 가져오는중...");
        call.enqueue(new Callback<CommunityResult>() {
            @Override
            public void onResponse(Call<CommunityResult> call, Response<CommunityResult> response) {
                dismissProgress();
                CommunityResult communityResult = response.body();
                communityList.addAll(communityResult.getItems());
                communities.clear();
                for (int i = 0; i < 3; i++) {
                    communities.add(communityList.get(i));
                }

                adapter = new CommunityAdapter(getContext(), communities);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<CommunityResult> call, Throwable t) {
                dismissProgress();
            }
        });

        return rootView;
    }

    void showProgress(String message) {
        dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }

    void dismissProgress() {
        dialog.dismiss();
    }
}