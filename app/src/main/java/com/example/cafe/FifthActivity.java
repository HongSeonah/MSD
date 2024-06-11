package com.example.cafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FifthActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fifth);

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        // Intent로부터 데이터를 가져옴
        Intent intent = getIntent();
        String cafeName = intent.getStringExtra("cafeName");
        String cafeCon = intent.getStringExtra("cafeCon");
        String addr = intent.getStringExtra("addr");
        String cafeImg = intent.getStringExtra("cafeImg");
        // 가져온 데이터를 레이아웃의 뷰에 설정
        TextView tvCafeNameHeader = findViewById(R.id.tvCafeNameHeader);
        TextView tvCafeName = findViewById(R.id.tvCafeName);
        TextView tvCafeCon = findViewById(R.id.tvCafeCon);
        TextView tvAddr = findViewById(R.id.tvAddr);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvOpenTime = findViewById(R.id.tvOpenTime);
        TextView tvCloseTime = findViewById(R.id.tvCloseTime);
        ImageView ivCafeImg = findViewById(R.id.ivCafeImg);
        TextView testText = findViewById(R.id.testText); // 추가

        tvCafeNameHeader.setText(cafeName); // 헤더 텍스트뷰에 카페 이름 설정
        tvCafeName.setText(cafeName);
        tvCafeCon.setText(cafeCon);
        tvAddr.setText(addr);

        // 이미지 리소스 설정
        int resId = getResources().getIdentifier(cafeImg, "drawable", getPackageName());
        ivCafeImg.setImageResource(resId);

        // 데이터베이스에서 번호와 시간 정보 가져오기
        loadPhoneAndTimeFromDatabase(cafeName);

        // 로그인 여부에 따라서 testText 보이기/숨기기
        if (isLoggedIn()) {
            testText.setVisibility(View.VISIBLE);
        } else {
            testText.setVisibility(View.GONE);
        }
    }

    // SharedPreferences를 이용한 로그인 상태 확인
    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    public void openOrderActivity(View view) {
        Intent intent = new Intent(FifthActivity.this, OrderActivity.class);
        startActivity(intent);
    }

    // 데이터베이스에서 번호와 시간 정보를 가져와서 뷰에 설정하는 함수
    private void loadPhoneAndTimeFromDatabase(String cafeName) {
        SQLiteDatabase sqlDB = null;
        Cursor cursor = null;
        try {
            CafeDBHelper helper = CafeDBHelper.getInstance(this);
            sqlDB = helper.getReadableDatabase();
            cursor = sqlDB.rawQuery("SELECT cafe_phone, open_time, close_time FROM tb_cafe WHERE cafe_name=?", new String[]{cafeName});

            if (cursor.moveToFirst()) {
                String cafePhone = cursor.getString(0);
                String openTime = cursor.getString(1);
                String closeTime = cursor.getString(2);

                // 가져온 번호와 시간 정보를 레이아웃의 뷰에 설정
                TextView tvPhone = findViewById(R.id.tvPhone);
                TextView tvOpenTime = findViewById(R.id.tvOpenTime);
                TextView tvCloseTime = findViewById(R.id.tvCloseTime);

                tvPhone.setText(cafePhone);
                tvOpenTime.setText("Open: " + openTime);
                tvCloseTime.setText("Close: " + closeTime);
            }
        } catch (Exception e) {
            Log.e("FifthActivity", "데이터를 불러오는 중 오류 발생", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (sqlDB != null) {
                sqlDB.close();
            }
        }
    }
}