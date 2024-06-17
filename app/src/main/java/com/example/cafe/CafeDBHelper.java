package com.example.cafe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CafeDBHelper extends SQLiteOpenHelper {
    private static CafeDBHelper instance;

    private static final String TAG = "CafeDBHelper";

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
        try {
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
                    "cafe_img TEXT)");

            db.execSQL("CREATE TABLE tb_review (" +
                    "review_seq INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "cafe_seq INTEGER," +
                    "user_seq INTEGER," +
                    "content TEXT," +
                    "review_img TEXT," +
                    "review_date TEXT DEFAULT (datetime('now','localtime'))," +
                    "FOREIGN KEY (cafe_seq) REFERENCES tb_cafe (cafe_seq)," +
                    "FOREIGN KEY (user_seq) REFERENCES tb_user (user_seq));");

            db.execSQL("CREATE TABLE tb_key (" +
                    "key_seq INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "key_con VARCHAR(255)," +
                    "dtime DATETIME DEFAULT (datetime('now','localtime')));");

            db.execSQL("CREATE TABLE tb_cafe_key (" +
                    "cafe_key_seq INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "cafe_seq INTEGER," +
                    "key_seq INTEGER," +
                    "FOREIGN KEY (cafe_seq) REFERENCES tb_cafe (cafe_seq)," +
                    "FOREIGN KEY (key_seq) REFERENCES tb_key (key_seq));");

            // Insert initial data
            db.execSQL("INSERT INTO tb_user (user_id, user_pw) VALUES ('12345678', '12345678');");

            db.execSQL("INSERT INTO tb_cafe (cafe_name, cafe_con, cafe_phone, open_time, close_time, addr, latitude, longitude, cafe_img) VALUES " +
                    "('에스프레소플래닛', '생화 보는 즐거움이 있는 커피가 맛있는 카페', '031-432-0998', '10:00', '23:00', '경기 시흥시 마유로418번길 10', 37.351402, 126.741839, 'espressoplanet')," +
                    "('스타벅스 시화로데오점', '커피 한 잔하며 작업하기 좋은 쾌적한 카페', '1522-3232', '07:00', '23:30', '경기 시흥시 중심상가2길 20-5', 37.344625, 126.737398, 'starbucks')," +
                    "('읍천리382 시흥정왕점', '옛날 느낌 물씬 나는 분위기 있는 카페', '0507-1350-1633', '08:30', '23:00', '경기 시흥시 중심상가로 180 108호, 109호', 37.344725, 126.735242, 'eupcheonri')," +
                    "('49번가디저트카페', '커피가 저렴하고 마카롱과 다쿠아즈가 맛있는 카페', '0507-2093-4341', '10:30', '23:00', '경기 시흥시 중심상가로 187 진명프라자 103호', 37.344637, 126.736034, 'treetdesertcafe49')," +
                    "('재리스토스트카페 정왕직영점', '인테리어가 힙하고 귀여운 카페', '010-9943-2571', '11:00', '23:30', '경기 시흥시 중심상가2길 12-6 2층 204호', 37.34528, 126.736263, 'jaerytoastcafe')," +
                    "('타르트봉봉 정왕본점', '타르트와 커피가 맛있는 카페', '0507-1386-3554', '11:00', '23:00', '경기 시흥시 중심상가로 192 108호 타르트봉봉 정왕본점', 37.344072, 126.736428, 'tartbongbong')," +
                    "('이디야커피 시화로데오점', '오붓하게 얘기하기 좋고 커피가 맛있고 사장님이 친절한 카페', '0507-1384-0872', '08:00', '23:55', '경기 시흥시 중심상가2길 20-13', 37.344568, 126.737074, 'ediya')," +
                    "('커피베이 정왕중심상가점', '탁 트여 여유롭게 이야기하기 좋은 오픈 카페', '0507-1355-5882', '09:00', '23:00', '경기 시흥시 중심상가3길 22 1층 107호', 37.34431, 126.735905, 'coffeebay');");

            db.execSQL("INSERT INTO tb_review (cafe_seq, user_seq, content, review_img) VALUES (1, 1, '정말 훌륭한 카페입니다! 커피도 맛있고 분위기도 좋아요.', 'review_img');");

            // Insert keywords
            db.execSQL("INSERT INTO tb_key (key_con) VALUES ('작업하기 좋은');");
            db.execSQL("INSERT INTO tb_key (key_con) VALUES ('가장 저렴한');");
            db.execSQL("INSERT INTO tb_key (key_con) VALUES ('커피가 맛있는');");

            // Associate keywords with cafes
            db.execSQL("INSERT INTO tb_cafe_key (cafe_seq, key_seq) VALUES (1, 1);"); // 에스프레소플래닛 - 작업하기 좋은
            db.execSQL("INSERT INTO tb_cafe_key (cafe_seq, key_seq) VALUES (1, 3);"); // 에스프레소플래닛 - 커피가 맛있는
            db.execSQL("INSERT INTO tb_cafe_key (cafe_seq, key_seq) VALUES (2, 1);"); // 스타벅스 시화로데오점 - 작업하기 좋은
            db.execSQL("INSERT INTO tb_cafe_key (cafe_seq, key_seq) VALUES (2, 3);"); // 스타벅스 시화로데오점 - 커피가 맛있는
            db.execSQL("INSERT INTO tb_cafe_key (cafe_seq, key_seq) VALUES (3, 3);"); // 읍천리 - 커피가 맛있는
            db.execSQL("INSERT INTO tb_cafe_key (cafe_seq, key_seq) VALUES (4, 2);"); // 49번가디저트카페 - 가장 저렴한
            db.execSQL("INSERT INTO tb_cafe_key (cafe_seq, key_seq) VALUES (5, 1);"); // 이디야 - 작업하기 좋은
            db.execSQL("INSERT INTO tb_cafe_key (cafe_seq, key_seq) VALUES (6, 3);"); // 타르트봉봉 정왕본점 - 커피가 맛있는

            Log.d(TAG, "Database tables created successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error creating database tables", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tb_user");
        db.execSQL("DROP TABLE IF EXISTS tb_cafe");
        db.execSQL("DROP TABLE IF EXISTS tb_review");
        db.execSQL("DROP TABLE IF EXISTS tb_key");
        db.execSQL("DROP TABLE IF EXISTS tb_cafe_key");
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    public List<Location> getAllCafeLocations() {
        List<Location> locations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT latitude, longitude, cafe_name FROM tb_cafe", null);
            if (cursor != null && cursor.moveToFirst()) {
                int latitudeIndex = cursor.getColumnIndex("latitude");
                int longitudeIndex = cursor.getColumnIndex("longitude");
                int cafeNameIndex = cursor.getColumnIndex("cafe_name");
                do {
                    double latitude = cursor.getDouble(latitudeIndex);
                    double longitude = cursor.getDouble(longitudeIndex);
                    String cafeName = cursor.getString(cafeNameIndex);
                    locations.add(new Location(latitude, longitude, cafeName));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching cafe locations", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return locations;
    }

    public void insertKeyword(String keyword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("key_con", keyword);
        db.insert("tb_key", null, values);
    }

    public void associateKeywordWithCafe(int cafeSeq, int keySeq) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cafe_seq", cafeSeq);
        values.put("key_seq", keySeq);
        db.insert("tb_cafe_key", null, values);
    }

    public List<String> getKeywordsForCafe(int cafeSeq) {
        List<String> keywords = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT key_con FROM tb_key " +
                    "INNER JOIN tb_cafe_key ON tb_key.key_seq = tb_cafe_key.key_seq " +
                    "WHERE tb_cafe_key.cafe_seq = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(cafeSeq)});
            if (cursor != null && cursor.moveToFirst()) {
                int keyConIndex = cursor.getColumnIndex("key_con");
                do {
                    String keyword = cursor.getString(keyConIndex);
                    keywords.add(keyword);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching keywords for cafe", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return keywords;
    }

    public static class Location {
        private double latitude;
        private double longitude;
        private String cafeName;

        public Location(double latitude, double longitude, String cafeName) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.cafeName = cafeName;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getCafeName() {
            return cafeName;
        }
    }
}
