package com.minui.borrowthing;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.minui.borrowthing.adapter.ChatRoomAdapter;
import com.minui.borrowthing.api.ChatApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.ChatRoom;
import com.minui.borrowthing.model.ChatRoomRes;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // ui
    RecyclerView recyclerView;

    List<ChatRoom> chatRoomList = new ArrayList<>();
    ChatRoomAdapter chatRoomAdapter;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment chatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
        // ui
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chatRoomList.clear();
        showProgress("채팅방 가져오는중...");
        SharedPreferences sp = getActivity().getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);

        ChatApi api = retrofit.create(ChatApi.class);

        Call<ChatRoomRes> call = api.getChatRoomList("Bearer " + accessToken);
        // 실제 api 호출
        call.enqueue(new Callback<ChatRoomRes>() {
            @Override
            public void onResponse(Call<ChatRoomRes> call, Response<ChatRoomRes> response) {
//                progressBar.setVisibility(View.INVISIBLE);
                // 200 OK
                if(response.isSuccessful()){
                    dismissProgress();
                    // 정상으로 데이터 받아왔으니, 리사이클러뷰에 표시
                    ChatRoomRes chatRoomRes = response.body();

                    chatRoomList.addAll(chatRoomRes.getItems());
                    Log.i("myChatRoom", "id :" + chatRoomRes.getResult());
                    chatRoomAdapter = new ChatRoomAdapter(getContext(), chatRoomList);
                    recyclerView.setAdapter(chatRoomAdapter);

                } else{
                    Toast.makeText(getContext(), "에러발생 : " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    dismissProgress();
                }
            }

            @Override
            public void onFailure(Call<ChatRoomRes> call, Throwable t) {
                // 네트워크 자체 문제로 실패
                Toast.makeText(getContext(), "네트워크에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
                dismissProgress();
//                progressBar.setVisibility(View.INVISIBLE);
            }
        });
//        textInputEditText = rootView.findViewById(R.id.txtTitle);
//
//        // 클릭하면 전송
//        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i("test", textInputEditText.getText().toString() );
//            }
//        });
//
//        // 포커스되면 색 바꾸기
//        textInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                int color = b ? Color.parseColor("#3498db"): Color.GRAY;
//                textInputLayout.setEndIconTintList(ColorStateList.valueOf(color));
//            }
//        });
        return rootView;
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