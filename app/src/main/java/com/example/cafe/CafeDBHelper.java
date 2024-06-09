package com.example.cafe;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CafeDBHelper extends SQLiteOpenHelper {
    private static CafeDBHelper instance;

    private CafeDBHelper(Context context) {
        super(context, "cafe.db", null, 1);
    }

    public static synchronized CafeDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CafeDBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE tb_user (" +
                "user_seq INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id VARCHAR(50)," +
                "user_pw VARCHAR(50));");

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

        // 임의의 데이터 삽입
        db.execSQL("INSERT INTO tb_cafe (cafe_name, cafe_con, cafe_phone, open_time, close_time, addr, latitude, longitude, cafe_img, dtime) VALUES (" +
                "'Example Cafe'," +
                "'A cozy cafe with a great atmosphere.'," +
                "'123-456-7890'," +
                "'08:00'," +
                "'22:00'," +
                "'123 Cafe Street, City'," +
                "37.7749," +
                "-122.4194," +
                "'cafe_img'," +
                "CURRENT_TIMESTAMP);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tb_user");
        db.execSQL("DROP TABLE IF EXISTS tb_cafe");
        onCreate(db);
    }
}

