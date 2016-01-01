package com.freeyuyuko.listencourse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.freeyuyuko.listencourse.CourseMap.*;

/**
 * Created by chengang on 16-1-1.
 */
public class DbOperator {
    private static final String TAG = "DbOperator";

    private CourseMapDbHelper mCourseMapDbHelper;

    public DbOperator(Context context){
        mCourseMapDbHelper = new CourseMapDbHelper(context);
    }

    /**
     *
     * @param course
     * @return
     */
    public  boolean addCourse(String course, String tags) {
        SQLiteDatabase db = mCourseMapDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Courses.COL_COURSE_NAME, course);
        values.put(Courses.COL_COUNT, 0);
        values.put(Courses.COL_CREATE_TIME, getCurDateTime());
        values.put(Courses.COL_TAGS, tags);
        db.insertOrThrow(Courses.TABLE_NAME, null, values);
        return true;
    }

    public int deleteCourse(String course){

        SQLiteDatabase db = mCourseMapDbHelper.getWritableDatabase();
        String[] selectionArgs = {course};
        String selection = Courses.COL_COURSE_NAME + " = ?";

        return db.delete(Courses.TABLE_NAME, selection, selectionArgs);
    }

    public int getCourseCount(String course){
        SQLiteDatabase db = mCourseMapDbHelper.getReadableDatabase();
        String[] selectionArgs = {course};
        String selection = Courses.COL_COURSE_NAME + " = ?";
        String[] projectioin = {Courses.COL_COUNT};
        Cursor c = db.query(Courses.TABLE_NAME,
                projectioin,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if(c.getCount() == 0)
            return 0;

        c.moveToFirst();
        int count = c.getInt(c.getColumnIndex(Courses.COL_COUNT));
        return count;
    }

    public int setCourseCount(String course, int count){
        SQLiteDatabase db = mCourseMapDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(CourseMap.Courses.COL_COURSE_NAME, course);
        values.put(Courses.COL_COUNT, count);
        String[] selectionArgs = {course};
        String selection = Courses.COL_COURSE_NAME + " = ?";
        return db.update(Courses.TABLE_NAME, values, selection, selectionArgs);
    }
    /**
     *
     * @param course
     * @return
     */
    public boolean isCourseExist(String course){

        SQLiteDatabase db = mCourseMapDbHelper.getReadableDatabase();
        String selection = Courses.COL_COURSE_NAME + " = ?";
        String[] selectionArgs = {course};
        String[] projection = {Courses.COL_COURSE_NAME};

        Cursor c = db.query(Courses.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        return c.getCount() > 0;
    }

    public boolean isVideoExist(String rawName){
        SQLiteDatabase db = mCourseMapDbHelper.getReadableDatabase();
        String[] projection = {Videos.COL_RAW_NAME};
        String selection = Videos.COL_RAW_NAME + " = ?";
        String[] selectionArgs = {rawName};

        Cursor c = db.query(Videos.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        return c.getCount()>0;
    }

    /**
     * add a new video to the tail
     * @param rawName
     * @param courseName
     * @param lessonName
     * @param tags
     */
    public void addVideo(String rawName, String courseName, String lessonName,
                         String tags){
        SQLiteDatabase db = mCourseMapDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Videos.COL_RAW_NAME, rawName);
        values.put(Videos.COL_COURSE_NAME, courseName);
        values.put(Videos.COL_LESSON_NAME, lessonName);
        values.put(Videos.COL_SCHEDULE, countVideoOfCourse(courseName)+1);
        values.put(Videos.COL_TAGS, tags);
        values.put(Videos.COL_CREATE_TIME, getCurDateTime());

        db.insertOrThrow(Videos.TABLE_NAME, null, values);

        //update count in course
        setCourseCount(courseName, countVideoOfCourse(courseName));
    }

    public void deleteVideo (String rawName) throws Exception {
        SQLiteDatabase db = mCourseMapDbHelper.getWritableDatabase();

        String selection = Videos.COL_RAW_NAME + " = ?";
        String[] selectionArgs = {rawName};

        String courseName = getCourseNameOfVideo(rawName);
        db.delete(Videos.TABLE_NAME,
                selection, selectionArgs);
        //keep order
        vacuumVideoOf(courseName);

        //update count in course
        setCourseCount(courseName, countVideoOfCourse(courseName));
    }

    /**
     *
     * @param rawName
     * @param toCourseName
     * @param schedule
     */
    public void moveVideoTo(String rawName, String toCourseName,
                            int schedule)throws Exception{
        if( !isVideoExist(rawName))
            throw new Exception(String.format("%s is not existed.", rawName));

        //remove from old course and vacuum old schedule
        String fromCourseName = getCourseNameOfVideo(rawName);
        setVideo(rawName, Videos.COL_COURSE_NAME, "");
        vacuumVideoOf(fromCourseName);
        setCourseCount(fromCourseName, countVideoOfCourse(fromCourseName));

        //add to new course
        setVideo(rawName, Videos.COL_COURSE_NAME, toCourseName);
        setVideo(rawName, Videos.COL_SCHEDULE, String.valueOf(schedule));
        stretchVideoOf(toCourseName, rawName);
        setCourseCount(toCourseName, countVideoOfCourse(toCourseName));
    }

    /**
     * make schedule one after one, keep order
     * @param courseName
     */
    public void vacuumVideoOf(String courseName){
        SQLiteDatabase db = mCourseMapDbHelper.getWritableDatabase();
        String[] projection = {Videos.COL_RAW_NAME};
        String selection = Videos.COL_COURSE_NAME + " = ?";
        String[] selectionArgs = {courseName};
        String sortOrder = Videos.COL_SCHEDULE + " ASC";

        Cursor c = db.query(Videos.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        c.moveToFirst();
        int i = 1;
        do{
            String rawName = c.getString(
                    c.getColumnIndexOrThrow(Videos.COL_RAW_NAME));
            setVideo(rawName, Videos.COL_SCHEDULE, Integer.toString(i));
            ++i;
        }while(c.moveToNext());

    }

    /**
     * Make schedule which contains conflic be one after one and keep order.
     * When rawNamePreferred conflics, its schedule is preferred to be smaller.
     * @param courseName
     */
    public void stretchVideoOf(String courseName, String rawNamePreferred)
        throws Exception{
        SQLiteDatabase db = mCourseMapDbHelper.getWritableDatabase();

        int pivot = getScheduleOfVideo(rawNamePreferred);
        String rawSql = String.format(
                "update %s set %s = %s + 1 where " +
                        "%s = %s and " +
                        "%s >= %d and " +
                        "%s != %s",
                Videos.TABLE_NAME, Videos.COL_SCHEDULE, Videos.COL_SCHEDULE,
                Videos.COL_COURSE_NAME, courseName,
                Videos.COL_SCHEDULE, pivot,
                Videos.COL_RAW_NAME, rawNamePreferred);
        Log.d(TAG, String.format("stretchVideoOf: %s", rawSql));
        db.execSQL(rawSql);

        vacuumVideoOf(courseName);
    }

    public int countVideoOfCourse(String courseName){
        SQLiteDatabase db = mCourseMapDbHelper.getReadableDatabase();
        String[] projection = {Videos.COL_RAW_NAME};
        String selection = Videos.COL_COURSE_NAME + " = ?";
        String[] selectionArgs = {courseName};
        Cursor c = db.query(true,
                Videos.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null);
        return c.getCount();
    }

    public String getCourseNameOfVideo(String rawName) throws Exception {
        return getVideo(rawName, Videos.COL_COURSE_NAME);
    }

    public int getScheduleOfVideo(String rawName) throws Exception{
        return Integer.valueOf(getVideo(rawName, Videos.COL_SCHEDULE));
    }

    public int setLessonNameOfVideo(String rawName, String lessonName){
        return setVideo(rawName, Videos.COL_LESSON_NAME, lessonName);
    }

    /**
     * when set course_name or schedule, sort order may be broken, this function
     * does not resort schedule
     * @param rawName
     * @param key
     * @param value
     * @return
     */
    private int setVideo(String rawName, String key, String value){

        SQLiteDatabase db = mCourseMapDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(key, value);
        String selection = Videos.COL_RAW_NAME + " = ?";
        String[] selectionArgs = { rawName };
        return db.update(Videos.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    private String getVideo(String rawName, String key) throws Exception {
        SQLiteDatabase db = mCourseMapDbHelper.getReadableDatabase();
        String[] projection = {key};
        String selection = Videos.COL_RAW_NAME + " = ?";
        String[] selectionArgs = {rawName};

        Cursor c = db.query(Videos.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if(c.getCount() < 1) {
            throw new Exception(String.format("%s is not existed.", rawName));
        }

        c.moveToFirst();
        return c.getString(c.getColumnIndexOrThrow(key));
    }

    public static String getCurDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }



}
