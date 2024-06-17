package com.example.cafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FourthActivity extends AppCompatActivity {
    private CafeDBHelper helper;
    private EditText etSearch;
    private LinearLayout layoutCafes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        helper = CafeDBHelper.getInstance(this); // 싱글톤 인스턴스 가져오기

        etSearch = findViewById(R.id.editTextSearch);
        layoutCafes = findViewById(R.id.layoutCafes);

        // "뒤로 가기" 이미지뷰 클릭 이벤트 처리
        ImageView backButton = findViewById(R.id.imageView);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 액티비티 종료
            }
        });

        // 액티비티2에서 전달받은 검색어 가져오기
        String searchQuery = getIntent().getStringExtra("searchQuery");
        if (searchQuery != null && !searchQuery.isEmpty()) {
            etSearch.setText(searchQuery);
            searchCafes(searchQuery); // 검색어를 기반으로 카페 검색
        } else {
            loadAllCafes(); // 검색어가 없을 경우 모든 카페 정보 로드
        }
    }

    // 모든 카페 정보를 불러오는 함수
    private void loadAllCafes() {
        SQLiteDatabase sqlDB = null;
        Cursor cursor = null;
        try {
            sqlDB = helper.getReadableDatabase();
            cursor = sqlDB.rawQuery("SELECT cafe_seq, cafe_name, cafe_con, addr, cafe_img FROM tb_cafe", null);

            layoutCafes.removeAllViews(); // 기존의 뷰 제거

            while (cursor.moveToNext()) {
                int cafeSeq = cursor.getInt(0);
                String cafeName = cursor.getString(1);
                String cafeCon = cursor.getString(2);
                String addr = cursor.getString(3);
                String cafeImg = cursor.getString(4);

                View cafeCard = createCafeCard(cafeSeq, cafeName, cafeCon, addr, cafeImg);
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
    private View createCafeCard(int cafeSeq, String cafeName, String cafeCon, String addr, String cafeImg) {
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
                intent.putExtra("cafeSeq", cafeSeq);
                intent.putExtra("cafeName", cafeName);
                intent.putExtra("cafeCon", cafeCon);
                intent.putExtra("addr", addr);
                intent.putExtra("cafeImg", cafeImg);
                startActivity(intent);
            }
        });

        return cardView;
    }

    // 검색 버튼 클릭 시 호출되는 함수
    public void searchOnClick(View view) {
        String query = etSearch.getText().toString();
        searchCafes(query);
    }

    // 검색어를 기반으로 카페를 검색하는 함수
    private void searchCafes(String query) {
        SQLiteDatabase sqlDB = null;
        Cursor cursor = null;
        try {
            sqlDB = helper.getReadableDatabase();
            String sqlQuery = "SELECT DISTINCT tb_cafe.cafe_seq, tb_cafe.cafe_name, tb_cafe.cafe_con, tb_cafe.addr, tb_cafe.cafe_img " +
                    "FROM tb_cafe " +
                    "LEFT JOIN tb_cafe_key ON tb_cafe.cafe_seq = tb_cafe_key.cafe_seq " +
                    "LEFT JOIN tb_key ON tb_cafe_key.key_seq = tb_key.key_seq " +
                    "WHERE tb_cafe.cafe_name LIKE ? OR tb_cafe.cafe_con LIKE ? OR tb_cafe.addr LIKE ? OR tb_key.key_con LIKE ?";
            cursor = sqlDB.rawQuery(sqlQuery, new String[]{"%" + query + "%", "%" + query + "%", "%" + query + "%", "%" + query + "%"});

            layoutCafes.removeAllViews(); // Clear existing views

            while (cursor.moveToNext()) {
                int cafeSeq = cursor.getInt(0);
                String cafeName = cursor.getString(1);
                String cafeCon = cursor.getString(2);
                String addr = cursor.getString(3);
                String cafeImg = cursor.getString(4);

                View cafeCard = createCafeCard(cafeSeq, cafeName, cafeCon, addr, cafeImg);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(15, 20, 15, 30); // Add top margin
                cafeCard.setLayoutParams(layoutParams); // Set layout parameters
                layoutCafes.addView(cafeCard); // Add card to layout
            }
        } catch (Exception e) {
            Log.e("FourthActivity", "Error loading data", e);
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
