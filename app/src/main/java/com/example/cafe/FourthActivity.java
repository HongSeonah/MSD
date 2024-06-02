package com.example.cafe;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FourthActivity extends AppCompatActivity {
    CafeDBHelper helper;
    EditText etSearch;
    LinearLayout layoutCafes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        helper = new CafeDBHelper(this);

        etSearch = findViewById(R.id.editTextSearch);
        layoutCafes = findViewById(R.id.layoutCafes);

        // 앱 시작 시 모든 카페 정보를 불러옴
        loadAllCafes();
    }

    // 모든 카페 정보를 불러오는 함수
    private void loadAllCafes() {
        SQLiteDatabase sqlDB = null;
        Cursor cursor = null;
        try {
            sqlDB = helper.getReadableDatabase();
            cursor = sqlDB.rawQuery("SELECT cafe_name, cafe_con, addr, cafe_img FROM tb_cafe", null);

            layoutCafes.removeAllViews(); // 기존의 뷰 제거

            while (cursor.moveToNext()) {
                String cafeName = cursor.getString(0);
                String cafeCon = cursor.getString(1);
                String addr = cursor.getString(2);
                String cafeImg = cursor.getString(3);

                View cafeCard = createCafeCard(cafeName, cafeCon, addr, cafeImg);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(15, 20, 15, 30); // 상단 마진 추가
                cafeCard.setLayoutParams(layoutParams); // 레이아웃 파라미터 설정
                layoutCafes.addView(cafeCard);
            }
        } catch (Exception e) {
            Log.e("FourthActivity", "데이터를 불러오는 중 오류 발생", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (sqlDB != null) {
                sqlDB.close();
            }
        }
    }

    // 카페 정보를 담은 CardView를 생성하는 함수
    private View createCafeCard(String cafeName, String cafeCon, String addr, String cafeImg) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.cafe_card, null);

        TextView tvCafeName = cardView.findViewById(R.id.tvCafeName);
        TextView tvCafeCon = cardView.findViewById(R.id.tvCafeCon);
        TextView tvAddr = cardView.findViewById(R.id.tvAddr);
        ImageView ivCafeImg = cardView.findViewById(R.id.ivCafeImg);

        tvCafeName.setText(cafeName);
        tvCafeCon.setText(cafeCon);
        tvAddr.setText(addr);

        // 이미지 리소스 설정
        int resId = getResources().getIdentifier(cafeImg, "drawable", getPackageName());
        ivCafeImg.setImageResource(resId);

        // 카드뷰에 클릭 리스너 추가
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭된 카드뷰의 정보를 가져와서 상세 정보 액티비티로 전달
                Intent intent = new Intent(FourthActivity.this, FifthActivity.class);
                intent.putExtra("cafeName", cafeName);
                intent.putExtra("cafeCon", cafeCon);
                intent.putExtra("addr", addr);
                intent.putExtra("cafeImg", cafeImg);
                startActivity(intent);
            }
        });

        return cardView;
    }

    // SQLite 데이터베이스 도우미 클래스
    static class CafeDBHelper extends SQLiteOpenHelper {
        public CafeDBHelper(Context context) {
            super(context, "cafe.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE tb_cafe (" +
                    "cafe_seq INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "cafe_name TEXT," +
                    "cafe_con TEXT," +
                    "cafe_phone TEXT," +
                    "open_time TEXT," +
                    "close_time TEXT," +
                    "addr TEXT," +
                    "latitude DECIMAL(16,14)," +
                    "longitude DECIMAL(18,15)," +
                    "cafe_img TEXT," +
                    "dtime DATETIME);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS tb_cafe");
            onCreate(db);
        }
    }
}