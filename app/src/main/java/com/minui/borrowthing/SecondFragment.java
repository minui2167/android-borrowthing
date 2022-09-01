package com.minui.borrowthing;


import static android.content.Context.MODE_PRIVATE;
import static com.minui.borrowthing.MainActivity.context;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.minui.borrowthing.adapter.CommunityAdapter;
import com.minui.borrowthing.api.CommunityApi;
import com.minui.borrowthing.api.UserApi;
import com.minui.borrowthing.config.Config;
import com.minui.borrowthing.config.NetworkClient;
import com.minui.borrowthing.model.AreaInfo;
import com.minui.borrowthing.model.AreaRes;
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
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment extends Fragment implements OnMapReadyCallback{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MapView googlemap = null;

    int count;
    List<AreaInfo> areaList = new ArrayList<>();
    Boolean isSuccess = false;

    // 변수
    String accessToken;

    // 네트워크 처리 보여주는 프로그램 다이얼로그
    ProgressDialog dialog;

    public SecondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_second, container, false);

        googlemap = (MapView) rootView.findViewById(R.id.mapView);

        areaList.clear();

        showProgress("활동 범위내 동네 가져오는중...");
        SharedPreferences sp = getActivity().getApplication().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        Retrofit retrofit = NetworkClient.getRetrofitClient(Config.BASE_URL);
        UserApi userApi = retrofit.create(UserApi.class);

        Call<AreaRes> call;

        call = userApi.getActivityAreaList("Bearer " + accessToken);


        call.enqueue(new Callback<AreaRes>() {
            @Override
            public void onResponse(Call<AreaRes> call, Response<AreaRes> response) {
                dismissProgress();
                if (response.isSuccessful()){
                    AreaRes areaRes = response.body();
                    count = areaRes.getCount();
                    areaList.addAll(areaRes.getItems());

                    googlemap.onCreate(savedInstanceState);
                    googlemap.onResume();
                }

            }

            @Override
            public void onFailure(Call<AreaRes> call, Throwable t) {
                dismissProgress();
            }
        });

        googlemap.getMapAsync(this);


        return rootView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        LatLng myLocation = new LatLng(((MainActivity) context).latitude, ((MainActivity) context).longitude);
        Marker myMarker;
        myMarker = googleMap.addMarker(new MarkerOptions().position(myLocation));
        myMarker.setTag(0);
        if(areaList.size() > 0){
            List<Marker> markerList = new ArrayList<>();

            markerList.add(myMarker);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18));
            int i = 1;
            for(AreaInfo areaInfo : areaList){
                double lat = areaInfo.getLatitude();
                double lng = areaInfo.getLongitude();
                LatLng location = new LatLng(lat, lng);
                String name = areaInfo.getName();
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(name));
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                marker.setTag(areaInfo.getSidoAreaId() + "," +areaInfo.getSiggAreaId()+ ","+ areaInfo.getId());
                markerList.add(marker);
                i++;
            }
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    Intent intent = new Intent(getActivity(), BorrowListByAreaActivity.class);
                    if(marker.getTag().equals(0)){
                        Toast.makeText(getActivity(), "현재 나의 위치입니다.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    intent.putExtra("title", marker.getTitle());
                    intent.putExtra("tag", marker.getTag().toString());
                    startActivity(intent);
                    return false;

                }
            });
        }
        else {
            Toast.makeText(getContext(), "활동범위 내 동네가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
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