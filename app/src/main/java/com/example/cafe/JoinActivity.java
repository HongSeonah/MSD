package com.example.cafe;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
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
import android.widget.Toast;

public class JoinActivity extends AppCompatActivity {

    private static final String TAG = "JoinActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        CafeDBHelper helper = new CafeDBHelper(this);

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
                    // ID가 이미 존재하는지 확인
                    SQLiteDatabase db = helper.getReadableDatabase();
                    Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM tb_user WHERE user_id = ?", new String[]{id});
                    cursor.moveToFirst();
                    int count = cursor.getInt(0);
                    cursor.close();

                    if (count > 0) {
                        Toast.makeText(JoinActivity.this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 아이디와 비밀번호가 모두 유효한 경우 데이터베이스에 삽입
                        SQLiteDatabase dbWrite = helper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("user_id", id);
                        values.put("user_pw", pw);
                        long newRowId = dbWrite.insert("tb_user", null, values);
                        dbWrite.close();

                        if (newRowId != -1) {
                            // 삽입 성공 시 로그인 화면으로 이동
                            Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(JoinActivity.this, "아이디가 생성 되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            // 삽입 실패 시 메시지 출력
                            Toast.makeText(JoinActivity.this, "회원가입에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    db.close();
                } else {
                    // 유효하지 않은 입력값에 대한 예외처리
                    if (id.length() < 8) {
                        Toast.makeText(JoinActivity.this, "아이디는 최소 8자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                    } else if (!validID) {
                        Toast.makeText(JoinActivity.this, "아이디에 특수문자를 포함할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    } else if (pw.length() < 8) {
                        Toast.makeText(JoinActivity.this, "비밀번호는 최소 8자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                    } else if (pw.length() > 20) {
                        Toast.makeText(JoinActivity.this, "비밀번호는 최대 20자까지만 가능합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(JoinActivity.this, "비밀번호에 특수문자를 포함할 수 없습니다.", Toast.LENGTH_SHORT).show();
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
        return pw.length() >= 8 && pw.length() <= 20 && pw.matches("[a-zA-Z0-9]+") && !pw.matches("[^a-zA-Z0-9]+");
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
