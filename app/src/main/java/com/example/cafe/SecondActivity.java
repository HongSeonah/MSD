package com.example.cafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    private EditText searchEditText;
    private SharedPreferences sharedPreferences;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // 검색어 입력창
        searchEditText = findViewById(R.id.editTextSearch);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // 검색 버튼 클릭 이벤트 처리
        ImageView searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 검색어 가져오기
                String query = searchEditText.getText().toString().trim();

                // FourthActivity로 검색어 전달
                Intent intent = new Intent(SecondActivity.this, FourthActivity.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);
            }
        });

        // 지도 이미지 클릭 이벤트 처리
        ImageView mapImageView = findViewById(R.id.map);
        mapImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                startActivity(intent);
            }
        });

        // 로그인 이미지 클릭 이벤트 처리
        ImageView loginImageView = findViewById(R.id.login);
        loginImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoggedIn) {
                    // 로그아웃 처리
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.putInt("userSeq", -1);
                    editor.apply();
                    isLoggedIn = false;
                    Toast.makeText(SecondActivity.this, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
                    // Update UI if necessary (e.g., change login image)
                } else {
                    // 로그인 페이지로 이동
                    Intent intent = new Intent(SecondActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        // 메뉴 이미지 클릭 이벤트 처리
        ImageView menuImageView = findViewById(R.id.menu);
        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, FourthActivity.class); // 이 부분은 메뉴로 이동하는 액티비티가 무엇인가요? 확인 부탁드립니다.
                startActivity(intent);
            }
        });
    }
}
