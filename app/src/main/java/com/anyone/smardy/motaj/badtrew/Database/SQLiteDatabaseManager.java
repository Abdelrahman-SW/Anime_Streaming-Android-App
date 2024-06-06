package com.anyone.smardy.motaj.badtrew.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anyone.smardy.motaj.badtrew.model.Cartoon;
import com.anyone.smardy.motaj.badtrew.model.Download;
import com.anyone.smardy.motaj.badtrew.model.Favorite;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabaseManager {

    SQLHelper helper;

    public SQLiteDatabaseManager(Context context) {
        helper = new SQLHelper(context);
    }

    //---------------------------------//

    public long insertFavorite(Favorite favorite){
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLHelper.ID, favorite.getId());
        contentValues.put(SQLHelper.IMG_URL, favorite.getImgUrl());
        contentValues.put(SQLHelper.TITLE, favorite.getTitle());
        contentValues.put(SQLHelper.PLAYLIST_TITLE, favorite.getPlaylistTitle());
        contentValues.put(SQLHelper.CARTOON_TITLE, favorite.getCartoonTitle());
        contentValues.put(SQLHelper.VIDEO_URL, favorite.getVideoUrl());


        long id = db.insert(SQLHelper.TABLE_Name, null, contentValues);
        db.close();

        //-----------Will return -1 if not success --------//
        return id;
    }

    public boolean isFavorite(int id){

        SQLiteDatabase db = helper.getReadableDatabase();

        String[] columns = {SQLHelper.ID};
        String[] args = {String.valueOf(id)};

        Cursor cursor = db.query(SQLHelper.TABLE_Name, columns, SQLHelper.ID + " = ? ", args, null, null, null);

        boolean isFavorite = cursor.moveToFirst() && cursor.getCount() >= 1;

        cursor.close();

        return isFavorite;

    }

    public int deleteFavorite(int id){

        SQLiteDatabase db = helper.getWritableDatabase();

        String[] args = {String.valueOf(id)};

        int count = db.delete(SQLHelper.TABLE_Name, SQLHelper.ID + " = ? ", args);

        db.close();

        return count;
    }

    public List<Favorite> getFavoriteData(){

        List<Favorite> favoriteList = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(SQLHelper.TABLE_Name, null, null, null, null, null, null);

        while (cursor.moveToNext()){

            favoriteList.add(new Favorite(
                    cursor.getInt(cursor.getColumnIndex(SQLHelper.ID)),
                    cursor.getString(cursor.getColumnIndex(SQLHelper.TITLE)),
                    cursor.getString(cursor.getColumnIndex(SQLHelper.PLAYLIST_TITLE)),
                    cursor.getString(cursor.getColumnIndex(SQLHelper.CARTOON_TITLE)),
                    cursor.getString(cursor.getColumnIndex(SQLHelper.IMG_URL)),
                    cursor.getString(cursor.getColumnIndex(SQLHelper.VIDEO_URL))));
        }

        cursor.close();

        return favoriteList;
    }



    //----------For Cartoons---------//

    public long insertFavoriteCartoon(Cartoon cartoon){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLHelper.ID, cartoon.getId());
        contentValues.put(SQLHelper.IMG_URL, cartoon.getThumb());
        contentValues.put(SQLHelper.TITLE, cartoon.getTitle());
        contentValues.put(SQLHelper.TYPE, cartoon.getType());


        long id = db.insert(SQLHelper.TABLE_Name2, null, contentValues);
        db.close();

        //-----------Will return -1 if not success --------//
        return id;
    }

    public void deleteAllFavouriteCartoons() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(SQLHelper.TABLE_Name2 , null , null);
        db.close();
    }

    public boolean isCartoonFavorite(int id){
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] columns = {SQLHelper.ID};
        String[] args = {String.valueOf(id)};

        Cursor cursor = db.query(SQLHelper.TABLE_Name2, columns, SQLHelper.ID + " = ? ", args, null, null, null);

        boolean isSeen = cursor.moveToFirst() && cursor.getCount() >= 1;

        cursor.close();

        return isSeen;
    }

    public int deleteFavoriteCartoon(int id){

        SQLiteDatabase db = helper.getWritableDatabase();

        String[] args = {String.valueOf(id)};

        int count = db.delete(SQLHelper.TABLE_Name2, SQLHelper.ID + " = ? ", args);

        db.close();

        return count;
    }

    public List<Cartoon> getCartoonsFavoriteData(){

        List<Cartoon> cartoonList = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(SQLHelper.TABLE_Name2, null, null, null, null, null, null);

        while (cursor.moveToNext()){

            cartoonList.add(new Cartoon(
                    cursor.getInt(cursor.getColumnIndex(SQLHelper.ID)),
                    cursor.getString(cursor.getColumnIndex(SQLHelper.TITLE)),
                    cursor.getString(cursor.getColumnIndex(SQLHelper.IMG_URL)),
                    cursor.getInt(cursor.getColumnIndex(SQLHelper.TYPE))));
        }

        cursor.close();

        return cartoonList;
    }


    //-----------------For seen Episodes-------------------//

    public long insertSeenEpisode(int episodeId){
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLHelper.ID, episodeId);

        long id = db.insert(SQLHelper.TABLE_Name3, null, contentValues);
        db.close();

        //-----------Will return -1 if not success --------//
        return id;
    }

    public void deleteAllSeenEpisode() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(SQLHelper.TABLE_Name3 , null , null);
        db.close();
    }

    public boolean isEpisodeSeen(int id){
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] columns = {SQLHelper.ID};
        String[] args = {String.valueOf(id)};

        Cursor cursor = db.query(SQLHelper.TABLE_Name3, columns, SQLHelper.ID + " = ? ", args, null, null, null);

        boolean isSeen = cursor.moveToFirst() && cursor.getCount() >= 1;

        cursor.close();

        return isSeen;
    }


    //-----------------For Downloaded Videos-------------------//

    public long insertDownload(String title, String path){
        Log.i("ab_do" , "insertDownload");
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLHelper.TITLE, title);
        contentValues.put(SQLHelper.PATH, path);

        long id = db.insert(SQLHelper.TABLE_Name4, null, contentValues);
        db.close();

        //-----------Will return -1 if not success --------//
        return id;
    }


    public List<Download> getDownloads(){

        List<Download> downloadList = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(SQLHelper.TABLE_Name4, null, null, null, null, null, null);

        while (cursor.moveToNext()){

            downloadList.add(new Download(
                    cursor.getInt(cursor.getColumnIndex(SQLHelper.ID)),
                    cursor.getString(cursor.getColumnIndex(SQLHelper.TITLE)),
                    cursor.getString(cursor.getColumnIndex(SQLHelper.PATH))));
        }

        cursor.close();

        return downloadList;
    }

    public int deleteDownload(int id){

        SQLiteDatabase db = helper.getWritableDatabase();

        String[] args = {String.valueOf(id)};

        int count = db.delete(SQLHelper.TABLE_Name4, SQLHelper.ID + " = ? ", args);

        db.close();

        return count;
    }



    //--------------------------------//

    public class SQLHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "favoritesDatabase";
        private static final int DATABASE_VERSION = 2;

        private static final String TABLE_Name = "favorites";

        private static final String ID = "id";
        private static final String IMG_URL = "img_url";
        private static final String TITLE = "title";
        private static final String CARTOON_TITLE = "cartoon_title";
        private static final String PLAYLIST_TITLE = "playlist_title";
        private static final String VIDEO_URL = "video_id";

        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_Name + " (" +
                ID + " INTEGER PRIMARY KEY, " +
                IMG_URL + " VARCHAR(250), " +
                TITLE + " VARCHAR(250), " +
                PLAYLIST_TITLE + " VARCHAR(250), " +
                CARTOON_TITLE + " VARCHAR(250), " +
                VIDEO_URL + " VARCHAR(250));";

//-------------------------------------------------------------------------//
        private static final String TABLE_Name2 = "favorites_cartoons";

        private static final String TYPE = "type";

        private static final String CREATE_TABLE2 = "CREATE TABLE " + TABLE_Name2 + " (" +
                ID + " INTEGER PRIMARY KEY, " +
                IMG_URL + " VARCHAR(250), " +
                TITLE + " VARCHAR(250), " +
                TYPE + " INTEGER);";


//-----------------------------------------------------------------------------//
        private static final String TABLE_Name3 = "episodes_seen";

        private static final String CREATE_TABLE3 = "CREATE TABLE " + TABLE_Name3 + " (" +
                ID + " INTEGER PRIMARY KEY);";

//-----------------------------------------------------------------------------//
        private static final String TABLE_Name4 = "downloads";

        private static final String PATH = "path";

        private static final String CREATE_TABLE4 = "CREATE TABLE " + TABLE_Name4 + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE + " VARCHAR(250), " +
                PATH + " TEXT);";

        Context context;

        public SQLHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
            db.execSQL(CREATE_TABLE2);
            db.execSQL(CREATE_TABLE3);
            db.execSQL(CREATE_TABLE4);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(CREATE_TABLE2);
            db.execSQL(CREATE_TABLE3);
            db.execSQL(CREATE_TABLE4);
        }
    }

}
