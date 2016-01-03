package com.freeyuyuko.listencourse;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by chengang on 16-1-2.
 */
public class CourseApplication extends Application {
    private static final String TAG = "CourseApplication";
    private static CourseApplication ourInstance;

    private String mHomePath;

    private String mCourseraPath;

    public static CourseApplication getInstance() {
        return ourInstance;
    }

    public CourseApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ourInstance = this;
        mHomePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/ListenCourse";

        Log.d(TAG, String.format("HomePath: %s", mHomePath));

        mCourseraPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/org.coursera.android/files/Download";
    }

    public String getHomePath(){
        File dir = new File(mHomePath);
        if(!dir.exists())
            dir.mkdirs();
        return mHomePath;
    }

    public String getDbPath(){
        String path = getHomePath() + "/"
                + CourseMapDbHelper.DB_NAME;
        return path;
    }

    public String getCourseraPath(){
        return mCourseraPath;
    }

    public String getCoursesPath(){
        String coursesPath = getHomePath() + "/Courses";
        File dir = new File(coursesPath);
        if( !dir.exists() )
            dir.mkdirs();
        return coursesPath;
    }
}
