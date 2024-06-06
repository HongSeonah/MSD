package com.example.cafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        Button button = findViewById(R.id.button3);
        EditText editTextID = findViewById(R.id.editTextID);
        EditText editTextPW = findViewById(R.id.editTextPW);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = editTextID.getText().toString().trim();
                String pw = editTextPW.getText().toString().trim();

                boolean validID = isValidID(id);
                boolean validPW = isValidPW(pw);

                if (validID && validPW) {
                    // 아이디와 비밀번호가 모두 유효한 경우 로그인 화면으로 이동
                    Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(JoinActivity.this,"아이디가 생성 되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 유효하지 않은 입력값에 대한 예외처리
                    if (id.length() < 8) {
                        Toast.makeText(JoinActivity.this, "아이디는 최소 8자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                    } else if (!validID) {
                        Toast.makeText(JoinActivity.this,"아이디에 특수문자를 포함할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    } else if (pw.length() < 8) {
                        Toast.makeText(JoinActivity.this,"비밀번호는 최소 8자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                    } else if (pw.length() > 20) {
                        Toast.makeText(JoinActivity.this,"비밀번호는 최대 20자까지만 가능합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(JoinActivity.this,"비밀번호에 특수문자를 포함할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // 아이디 유효성 검사 메서드
    private boolean isValidID(String id) {
        return id.length() >= 8 && id.matches("[a-zA-Z0-9]+");
    }

    // 비밀번호 유효성 검사 메서드
    private boolean isValidPW(String pw) {
        // 비밀번호는 8~20자의 영어와 숫자로만 구성되어야 함
        // 특수문자 포함 여부 확인
        return pw.length() >= 8 && pw.length() <= 20 && pw.matches("[a-zA-Z0-9]+") && !pw.matches("[^a-zA-Z0-9]+");
    }
}
