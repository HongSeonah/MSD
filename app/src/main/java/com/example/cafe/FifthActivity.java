package com.example.cafe;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FifthActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText etReviewInput;
    private ImageView ivSelectedImage;
    private String currentPhotoPath;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fifth);

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        // Initialize views
        etReviewInput = findViewById(R.id.etReviewInput);
        Button btnSubmitReview = findViewById(R.id.btnSubmitReview);

        // "뒤로 가기" 이미지뷰 클릭 이벤트 처리
        ImageView backButton = findViewById(R.id.imageView);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 액티비티 종료
            }
        });

        // Set click listener for submit button
        btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get review content
                String reviewContent = etReviewInput.getText().toString().trim();

                if (!reviewContent.isEmpty()) {
                    // Get cafeSeq from intent extra
                    Intent intent = getIntent();
                    int cafeSeq = intent.getIntExtra("cafeSeq", 0);

                    // Get userSeq from SharedPreferences or your session management
                    int userSeq = getUserSeqFromPreferences(); // Implement this method to get userSeq

                    // Insert into tb_review table
                    insertReviewIntoDatabase(cafeSeq, userSeq, reviewContent);
                } else {
                    Toast.makeText(FifthActivity.this, "리뷰를 작성하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Intent로부터 데이터를 가져옴
        Intent intent = getIntent();
        String cafeName = intent.getStringExtra("cafeName");
        String cafeCon = intent.getStringExtra("cafeCon");
        String addr = intent.getStringExtra("addr");
        String cafeImg = intent.getStringExtra("cafeImg");
        int cafeSeq = intent.getIntExtra("cafeSeq", 0); // 카페 시퀀스 추가

        // 가져온 데이터를 레이아웃의 뷰에 설정
        TextView tvCafeNameHeader = findViewById(R.id.tvCafeNameHeader);
        TextView tvCafeName = findViewById(R.id.tvCafeName);
        TextView tvCafeCon = findViewById(R.id.tvCafeCon);
        TextView tvAddr = findViewById(R.id.tvAddr);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvOpenTime = findViewById(R.id.tvOpenTime);
        TextView tvCloseTime = findViewById(R.id.tvCloseTime);
        ImageView ivCafeImg = findViewById(R.id.ivCafeImg);
        LinearLayout reviewWriteContainer = findViewById(R.id.reviewWriteContainer);

        tvCafeNameHeader.setText(cafeName); // 헤더 텍스트뷰에 카페 이름 설정
        tvCafeName.setText(cafeName);
        tvCafeCon.setText(cafeCon);
        tvAddr.setText(addr);

        // 이미지 리소스 설정
        int resId = getResources().getIdentifier(cafeImg, "drawable", getPackageName());
        ivCafeImg.setImageResource(resId);

        // 데이터베이스에서 번호와 시간 정보 가져오기
        loadPhoneAndTimeFromDatabase(cafeName);

        // 리뷰를 데이터베이스에서 가져와서 표시하기
        loadReviewsFromDatabase(cafeSeq);

        // 로그인 여부에 따라서 리뷰 작성 컨테이너 보이기/숨기기
        if (isLoggedIn()) {
            reviewWriteContainer.setVisibility(View.VISIBLE);
        } else {
            reviewWriteContainer.setVisibility(View.GONE);
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

    // Method to retrieve userSeq from SharedPreferences
    private int getUserSeqFromPreferences() {
        return sharedPreferences.getInt("userSeq", 0); // Assuming userSeq is stored as an integer
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

    // 리뷰를 tb_review 테이블에 삽입하는 메서드
    private void insertReviewIntoDatabase(int cafeSeq, int userSeq, String reviewContent) {
        SQLiteDatabase sqlDB = null;
        try {
            CafeDBHelper helper = CafeDBHelper.getInstance(this);
            sqlDB = helper.getWritableDatabase();

            // 삽입할 ContentValues 준비
            ContentValues values = new ContentValues();
            values.put("cafe_seq", cafeSeq);
            values.put("user_seq", userSeq);
            values.put("content", reviewContent);

            // 선택된 이미지가 있는지 확인
            if (currentPhotoPath != null) {
                values.put("review_img", currentPhotoPath); // 이미지 경로를 데이터베이스에 저장
            } else {
                values.putNull("review_img"); // 이미지가 없을 경우 null로 저장
            }

            // tb_review 테이블에 삽입
            long newRowId = sqlDB.insert("tb_review", null, values);

            if (newRowId != -1) {
                Toast.makeText(this, "리뷰가 등록되었습니다", Toast.LENGTH_SHORT).show();

                // 리뷰 입력 및 선택된 이미지 초기화
                etReviewInput.setText("");
                ivSelectedImage.setVisibility(View.GONE);

                // 필요 시 리뷰 목록 새로고침
                loadReviewsFromDatabase(cafeSeq); // 삽입 후 리뷰 다시 로드
            } else {
                Toast.makeText(this, "리뷰 등록에 실패했습니다", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("FifthActivity", "리뷰를 저장하는 중 오류 발생", e);
        } finally {
            if (sqlDB != null) {
                sqlDB.close();
            }
        }
    }


    // 데이터베이스에서 리뷰를 가져와서 뷰에 설정하는 함수
    private void loadReviewsFromDatabase(int cafeSeq) {
        SQLiteDatabase sqlDB = null;
        Cursor cursor = null;
        try {
            CafeDBHelper helper = CafeDBHelper.getInstance(this);
            sqlDB = helper.getReadableDatabase();

            // Fetch reviews ordered by review_date DESC
            cursor = sqlDB.rawQuery("SELECT content, review_img, review_date, user_seq FROM tb_review WHERE cafe_seq=? ORDER BY review_date DESC", new String[]{String.valueOf(cafeSeq)});

            LinearLayout reviewContainer = findViewById(R.id.reviewContainer);
            reviewContainer.removeAllViews(); // Clear existing reviews

            while (cursor.moveToNext()) {
                String reviewContent = cursor.getString(0);
                String reviewImg = cursor.getString(1);
                String reviewDate = cursor.getString(2);
                int userSeq = cursor.getInt(3);

                // Fetch username using user_seq
                Cursor userCursor = sqlDB.rawQuery("SELECT user_id FROM tb_user WHERE user_seq=?", new String[]{String.valueOf(userSeq)});
                String reviewAuthor = "";
                if (userCursor.moveToFirst()) {
                    reviewAuthor = userCursor.getString(0);
                }
                userCursor.close();

                // Inflate review_item layout and set data
                View reviewView = getLayoutInflater().inflate(R.layout.review_item, null);
                TextView tvReviewContent = reviewView.findViewById(R.id.tvReviewContent);
                ImageView ivReviewImg = reviewView.findViewById(R.id.ivReviewImg);
                TextView tvReviewDate = reviewView.findViewById(R.id.tvReviewDate);
                TextView tvReviewAuthor = reviewView.findViewById(R.id.tvReviewAuthor);

                tvReviewContent.setText(reviewContent);
                if (reviewImg != null && !reviewImg.isEmpty()) {
                    int resId = getResources().getIdentifier(reviewImg, "drawable", getPackageName());
                    ivReviewImg.setImageResource(resId);
                } else {
                    ivReviewImg.setVisibility(View.GONE);
                }

                tvReviewDate.setText("작성일: " + reviewDate);
                tvReviewAuthor.setText("작성자: " + reviewAuthor);

                reviewContainer.addView(reviewView);
            }
        } catch (Exception e) {
            Log.e("FifthActivity", "리뷰를 불러오는 중 오류 발생", e);
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

