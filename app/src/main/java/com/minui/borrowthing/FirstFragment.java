package com.minui.borrowthing;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minui.borrowthing.adapter.BorrowAdapter;
import com.minui.borrowthing.adapter.CommunityAdapter;
import com.minui.borrowthing.api.BorrowApi;
import com.minui.borrowthing.api.CommunityApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.BorrowResult;
import com.minui.borrowthing.model.CommunityResult;
import com.minui.borrowthing.model.item;

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

    // 리사이클러 뷰 관련 멤버변수 3개
    RecyclerView recyclerView;
    BorrowAdapter adapter;
    ArrayList<item> itemList = new ArrayList<>();

    // 페이징에 필요한 변수
    int offset = 0;
    int limit = 20;
    int count = 0;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;


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

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getNetworkData();

        return rootView;
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
//        if (accessToken.isEmpty()) {
//            call = borrowApi.getGoods(offset, limit);
//        } else {
//            call = borrowApi.getGoods(offset, limit, "Bearer " + accessToken);
//        }
        call = borrowApi.getGoods(offset, limit);

        call.enqueue(new Callback<BorrowResult>() {
            @Override
            public void onResponse(Call<BorrowResult> call, Response<BorrowResult> response) {
                dismissProgress();
                BorrowResult borrowResult = response.body();
                count = borrowResult.getCount();
                itemList.addAll(borrowResult.getItems());
                offset = offset + count;

                adapter = new BorrowAdapter(getContext(), itemList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<BorrowResult> call, Throwable t) {
                dismissProgress();
            }
        });
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