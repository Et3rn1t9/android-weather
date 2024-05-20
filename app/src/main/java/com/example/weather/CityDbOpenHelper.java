package com.example.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.amap.api.maps.offlinemap.City;
import com.example.weather.data.MyCity;

import java.util.ArrayList;
import java.util.List;

public class CityDbOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "citySQLite.db";
    private static final String TABLE_CITY = "city";
    private static final String COLUMN_ID = "city_id";
    private static final String COLUMN_NAME = "city_name";

    // SQL statement to create the city table
    private static final String DATABASE_CREATE = "create table "
            + TABLE_CITY + "(id integer primary key autoincrement, " + COLUMN_NAME
            + " text not null, "  + COLUMN_ID + " text not null);";

    public CityDbOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertData(MyCity myCity) {
        SQLiteDatabase db = getWritableDatabase();
        // 检查是否已存在相同的记录
        if (isCityExists(db, myCity.getCity_id())) {
            // 城市已存在，返回-1表示插入失败
            return -1;
        }
        ContentValues values = new ContentValues();
        values.put("city_name", myCity.getCity_name());
        values.put("city_id", myCity.getCity_id());

        // 返回插入的结果
        return db.insert(TABLE_CITY, null, values);
    }

    private boolean isCityExists(SQLiteDatabase db, String cityName) {
        String query = "SELECT * FROM " + TABLE_CITY + " WHERE city_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{cityName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public int deleteFromDbById(String id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_CITY, "city_id like ?", new String[]{id});
    }

    public int updateData(MyCity myCity) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("city_name", myCity.getCity_name());
        values.put("city_id", myCity.getCity_id());
        return db.update(TABLE_CITY, values, "id like ?", new String[]{myCity.getCity_id()});
    }

    public List<MyCity> queryAllFromDb() {

        SQLiteDatabase db = getWritableDatabase();
        List<MyCity> myCityList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_CITY, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String city_name = cursor.getString(cursor.getColumnIndexOrThrow("city_name"));
                String city_id = cursor.getString(cursor.getColumnIndexOrThrow("city_id"));

                MyCity myCity = new MyCity(city_name,  city_id);
                myCityList.add(myCity);
            }
            cursor.close();
        }

        return myCityList;

    }
}
