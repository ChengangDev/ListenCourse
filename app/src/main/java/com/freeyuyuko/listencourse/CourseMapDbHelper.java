package com.freeyuyuko.listencourse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.freeyuyuko.listencourse.CourseMap.Courses;
import com.freeyuyuko.listencourse.CourseMap.Videos;

/**
 * Created by chengang on 16-1-1.
 */
public class CourseMapDbHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 2;
    public static final String DB_NAME = "course_map.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String DATE_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_VIDEOS =
            "CREATE TABLE " + Videos.TABLE_NAME + " (" +
                    Videos._ID + " INTEGER PRIMARY KEY," +
                    Videos.COL_COURSE_NAME + TEXT_TYPE + COMMA_SEP +
                    Videos.COL_RAW_NAME + TEXT_TYPE + COMMA_SEP +
                    Videos.COL_SCHEDULE + INT_TYPE + COMMA_SEP +
                    Videos.COL_LESSON_NAME + TEXT_TYPE + COMMA_SEP +
                    Videos.COL_CREATE_TIME + DATE_TYPE + COMMA_SEP +
                    Videos.COL_TAGS + TEXT_TYPE +
                    " )";
    private static final String SQL_DELETE_VIDEOS =
            "DROP TABLE IF EXISTS " + Videos.TABLE_NAME;

    private static final String SQL_CREATE_COURSES =
            "CREATE TABLE " + Courses.TABLE_NAME + " (" +
                    Courses._ID + " INTEGER PRIMARY KEY," +
                    Courses.COL_COURSE_NAME + TEXT_TYPE + COMMA_SEP +
                    Courses.COL_CREATE_TIME + DATE_TYPE + COMMA_SEP +
                    Courses.COL_TAGS + TEXT_TYPE + COMMA_SEP +
                    Courses.COL_COUNT + INT_TYPE +
                    " )";
    private static final String SQL_DELETE_COURSES =
            "DROP TABLE IF EXISTS " + Courses.TABLE_NAME;



    public CourseMapDbHelper(Context context){

        super(context, CourseApplication.getInstance().getDbPath(),
                null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_COURSES);
        db.execSQL(SQL_CREATE_VIDEOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_COURSES);
        db.execSQL(SQL_DELETE_VIDEOS);
        onCreate(db);
    }
}
