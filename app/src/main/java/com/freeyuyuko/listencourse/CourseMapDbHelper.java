package com.freeyuyuko.listencourse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by chengang on 16-1-1.
 */
public class CourseMapDbHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "course_map.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String DATE_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_VIDEOS =
            "CREATE TABLE " + CourseMap.Videos.TABLE_NAME + " (" +
                    CourseMap.Videos._ID + " INTEGER PRIMARY KEY," +
                    CourseMap.Videos.COL_COURSE_NAME + TEXT_TYPE + COMMA_SEP +
                    CourseMap.Videos.COL_RAW_NAME + TEXT_TYPE + COMMA_SEP +
                    CourseMap.Videos.COL_SCHEDULE + INT_TYPE + COMMA_SEP +
                    CourseMap.Videos.COL_LESSON_NAME + TEXT_TYPE + COMMA_SEP +
                    CourseMap.Videos.COL_CREATE_TIME + DATE_TYPE + COMMA_SEP +
                    CourseMap.Videos.COL_TAGS + TEXT_TYPE + COMMA_SEP +
                    " )";
    private static final String SQL_DELETE_VIDEOS =
            "DROP TABLE IF EXISTS " + CourseMap.Videos.TABLE_NAME;

    private static final String SQL_CREATE_COURSES =
            "CREATE TABLE " + CourseMap.Videos.TABLE_NAME + " (" +
                    CourseMap.Courses._ID + " INTEGER PRIMARY KEY," +
                    CourseMap.Courses.COL_COURSE_NAME + TEXT_TYPE + COMMA_SEP +
                    CourseMap.Courses.COL_CREATE_TIME + DATE_TYPE + COMMA_SEP +
                    CourseMap.Courses.COL_TAGS + TEXT_TYPE + COMMA_SEP +
                    CourseMap.Courses.COL_COUNT + INT_TYPE + COMMA_SEP +
                    " )";
    private static final String SQL_DELETE_COURSES =
            "DROP TABLE IF EXISTS " + CourseMap.Courses.TABLE_NAME;



    public CourseMapDbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
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
