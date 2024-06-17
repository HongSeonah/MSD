package com.example.cafe;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class ThirdActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private CafeDBHelper dbHelper;

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private static final String TAG = "ThirdActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        // CafeDBHelper 인스턴스를 가져옵니다.
        dbHelper = CafeDBHelper.getInstance(this);

        // SupportMapFragment를 찾아서 맵을 설정합니다.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // "뒤로 가기" 이미지뷰 클릭 이벤트 처리
        ImageView backButton = findViewById(R.id.imageView);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 액티비티 종료
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 위치 권한이 있는지 확인하고 권한 요청을 합니다.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        // 위치 권한이 허용된 경우 또는 이미 허용된 경우
        setupMap(); // 맵 초기화 및 마커 추가 메서드 호출
    }

    // 맵 초기화 및 현재 위치, 데이터베이스에서 마커 추가
    private void setupMap() {
        mMap.setMyLocationEnabled(true); // 내 위치 계층 활성화

        // 마지막으로 알려진 위치로 지도 카메라 이동
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
            }
        });

        // 데이터베이스에서 카페 위치 정보를 가져와서 마커 추가
        addMarkersFromDatabase();
    }

    // 데이터베이스에서 카페 위치 정보를 가져와서 맵에 마커로 추가하는 메서드
    private void addMarkersFromDatabase() {
        List<CafeDBHelper.Location> locations = dbHelper.getAllCafeLocations();
        Log.d(TAG, "Number of locations fetched: " + locations.size());
        for (CafeDBHelper.Location location : locations) {
            BitmapDescriptor markerIcon = getResizedMarkerIcon(R.drawable.marker, 0.2f); // 0.2배 크기로 조정
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title(location.getCafeName())
                    .icon(markerIcon));
        }
    }

    // 리소스에서 비트맵을 가져와 크기를 조정한 후 비트맵 디스크립터를 반환하는 메서드
    private BitmapDescriptor getResizedMarkerIcon(int resourceId, float scaleFactor) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(resourceId);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        int width = (int) (bitmap.getWidth() * scaleFactor);
        int height = (int) (bitmap.getHeight() * scaleFactor);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }



    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 위치 권한 요청의 결과가 PERMISSIONS_REQUEST_LOCATION일 때 처리합니다.
        if (requestCode == PERMISSIONS_REQUEST_LOCATION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 위치 권한이 허용된 경우 또는 이미 허용된 경우
            setupMap(); // 맵 초기화 및 마커 추가 메서드 호출
        } else {
            // 권한이 거부된 경우
            Log.e(TAG, "Location permission denied");
        }
    }
}
