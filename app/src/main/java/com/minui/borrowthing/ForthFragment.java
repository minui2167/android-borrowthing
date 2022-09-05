package com.minui.borrowthing;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import static com.minui.borrowthing.MainActivity.context;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.minui.borrowthing.api.UserApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.LocationApi;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.MyLocation;
import com.minui.borrowthing.model.Result;
import com.minui.borrowthing.model.UserRes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForthFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button btnInformation;
    Button btnInterestsList;
    Button btnMyWrote;
    Button btnMyLocation;
    Button btnTransaction;
    Button btnLogout;
    ImageView imgUser;
    TextView txtNickname;

    LocationManager locationManager;

    double latitude;
    double longitude;

    Result result = new Result(); // 지역받기
    String region; // 지역명

    MyLocation myLocation;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    public ForthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForthFragment newInstance(String param1, String param2) {
        ForthFragment fragment = new ForthFragment();
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_forth, container, false);

        imgUser = rootView.findViewById(R.id.imgUser);
        txtNickname = rootView.findViewById(R.id.txtNickname);
        btnInformation = rootView.findViewById(R.id.btnInformation);
        btnInterestsList = rootView.findViewById(R.id.btnInterestsList);
        btnMyWrote = rootView.findViewById(R.id.btnMyWrote);
        btnMyLocation = rootView.findViewById(R.id.btnMyLocation);
        btnTransaction = rootView.findViewById(R.id.btnTransaction);
        btnLogout = rootView.findViewById(R.id.btnLogout);

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        SharedPreferences sp = getActivity().getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String nickName = sp.getString("nickName", "");
        if (!nickName.equals(""))
            txtNickname.setText(nickName + " 님");




        btnInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo 내정보 수정하는 화면 구성
                Intent intent = new Intent(getActivity(),MyReviseActivity.class);
                startActivity(intent);

            }
        });

        btnInterestsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo 내 관심 목록 불러오기
                Intent intent = new Intent(getContext(),InterestListActivity.class);
                startActivity(intent);
            }
        });

        btnMyWrote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo 내가 쓴 글 목록 불러오기
                Intent intent = new Intent(getContext(), MyCommunityListActivity.class);
                startActivity(intent);
            }
        });

        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 내 현재 위치 설정
                Intent intent = new Intent(getActivity(), SetAreaActivity.class);
                startActivity(intent);

            }
        });

        btnTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo 거래내역 불러오기
                Intent intent = new Intent(getActivity(), TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgress("로그아웃 중입니다.");
                Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
                UserApi userApi = retrofit.create(UserApi.class);

                SharedPreferences sp = getActivity().getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String accessToken = sp.getString("accessToken", "");

                Call<UserRes> call = userApi.logout("Bearer " + accessToken);
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        dismissProgress();
                        if(response.isSuccessful()) {
                            SharedPreferences sp = getActivity().getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("accessToken", "");
                            editor.putString("nickName", "");
                            editor.putInt("ratingCount", 0);
                            editor.apply();
                            ((MainActivity) context).loadFragment(((MainActivity) context).firstFragment);
                            ((MainActivity) context).navigationView.setSelectedItemId(R.id.firstFragment);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {
                        dismissProgress();
                    }
                });
            }
        });

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

    public void setMyLocation() {
        if (myLocation == null) {
            return;
        }
        showProgress("동네 설정 중입니다.");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        UserApi userApi = retrofit.create(UserApi.class);
        SharedPreferences sp = getActivity().getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Call<UserRes> call = userApi.setLocation("Bearer " + accessToken, myLocation);
        call.enqueue(new Callback<UserRes>() {
            @Override
            public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                dismissProgress();
                Log.i("test", response.code()+"");
                if(response.isSuccessful()) {
                    Toast.makeText(getActivity().getApplication(), "동네 설정이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                } else if(response.code() == 400){
                    Toast.makeText(getActivity().getApplication(), "지원하는 동네가 아닙니다.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UserRes> call, Throwable t) {
                dismissProgress();
            }
        });
    }
}