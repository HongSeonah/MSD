package com.example.cafe;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        helper = new CafeDBHelper(this);

        TextView signUpText = findViewById(R.id.textView6);
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

    // SQLite 데이터베이스 도우미 클래스
    static class CafeDBHelper extends SQLiteOpenHelper {
        public CafeDBHelper(Context context) {
            super(context, "cafe.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE tb_user (" +
                    "user_seq INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id VARCHAR(50)," +
                    "user_pw VARCHAR(50));");
            Log.d(TAG, "User table created.");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS tb_user");
            onCreate(db);
        }
    }
}
