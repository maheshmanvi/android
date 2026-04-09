package dev.maarch.mydemo.database;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import dev.maarch.mydemo.api.model.Address;
import dev.maarch.mydemo.api.model.UserDetail;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "users.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, username TEXT, email TEXT, phone TEXT, website TEXT, " +
                "street TEXT, suite TEXT, city TEXT, zipcode TEXT, lat TEXT, lng TEXT, " +
                "company_name TEXT, catch_phrase TEXT, bs TEXT, " +
                "image_path TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public void insertUsers(List<UserDetail> users) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (UserDetail user : users) {
            ContentValues values = new ContentValues();

            values.put("id", user.getId());
            values.put("name", user.getName());
            values.put("username", user.getUsername());
            values.put("email", user.getEmail());
            values.put("phone", user.getPhone());
            values.put("website", user.getWebsite());

            values.put("street", user.getAddress().getStreet());
            values.put("suite", user.getAddress().getSuite());
            values.put("city", user.getAddress().getCity());
            values.put("zipcode", user.getAddress().getZipcode());
            values.put("lat", user.getAddress().getGeo().getLat());
            values.put("lng", user.getAddress().getGeo().getLng());

            values.put("company_name", user.getCompany().getName());
            values.put("catch_phrase", user.getCompany().getCatchPhrase());
            values.put("bs", user.getCompany().getBs());

            db.insert("users", null, values);
        }

        db.close();
    }

    public List<UserDetail> getUsers() {
        List<UserDetail> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM users", null);

        if (cursor.moveToFirst()) {
            do {
                UserDetail user = new UserDetail();
                user.setId(cursor.getInt(0));
                user.setName(cursor.getString(1));
                user.setUsername(cursor.getString(2));
                user.setEmail(cursor.getString(3));
                user.setPhone(cursor.getString(4));
                user.setWebsite(cursor.getString(5));
                user.setImagePath(cursor.getString(15));
                user.setAddress(new Address(cursor.getString(cursor.getColumnIndexOrThrow("lat")),cursor.getString( cursor.getColumnIndexOrThrow("lng"))));

                list.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }


    public void updateUserImage(int id, String path) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("image_path", path);

        db.update("users", values, "id=?", new String[]{String.valueOf(id)});
    }

    public void updateLocation(int id, double lat, double lng) {
        Log.d(TAG, "updateLocation: Saving location to DB" + lat + ", " + lng);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("lat", String.valueOf(lat));
        values.put("lng", String.valueOf(lng));
        values.put("city", "Updated Location");

        db.update("users", values, "id=?", new String[]{String.valueOf(id)});
    }



}