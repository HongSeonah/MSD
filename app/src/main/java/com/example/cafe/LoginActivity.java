package com.example.cafe;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private CafeDBHelper helper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        helper = CafeDBHelper.getInstance(this);

        TextView signUpText = findViewById(R.id.textView6);
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE); // SharedPreferences 초기화

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to JoinActivity for sign-up
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });

        Button loginButton = findViewById(R.id.button2);
        EditText editTextID = findViewById(R.id.editTextLoginID);
        EditText editTextPW = findViewById(R.id.editTextLoginPW);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = editTextID.getText().toString().trim();
                String pw = editTextPW.getText().toString().trim();

                if (isValidLogin(id, pw)) {
                    // 로그인 성공 시 SharedPreferences에 로그인 상태 저장
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, SecondActivity.class);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "로그인에 실패했습니다. 아이디나 패스워드를 확인해 보세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidLogin(String id, String pw) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM tb_user WHERE user_id = ? AND user_pw = ?", new String[]{id, pw});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();

        return count > 0;
    }
}
