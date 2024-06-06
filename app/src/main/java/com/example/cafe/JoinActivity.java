package com.example.cafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        Button button = findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그인으로 이동
                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(JoinActivity.this,"아이디가 생성 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });



    }
}
