package com.freeyuyuko.listencourse;

import android.provider.BaseColumns;

/**
 * Created by chengang on 16-1-1.
 */
public final class CourseMap {

    private static final String TAG = "CourseMap";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INT";
    private static final String DATE_TYPE = " DATE";
    private static final String COMMA_SEP = ",";

    public CourseMap(){}

    public static abstract class Videos implements BaseColumns{
        public static final String TABLE_NAME = "videos";
        //public static final String COL_ID = "video_id";
        public static final String COL_RAW_NAME = "raw_name";
        public static final String COL_COURSE_NAME = "course_name";
        public static final String COL_SCHEDULE = "schedule";
        public static final String COL_CREATE_TIME = "create_time";

        public static final String COL_LESSON_NAME = "lesson_name";
        public static final String COL_VIDEO_NAME = "video_name";
        public static final String COL_TYPE = "type";
        public static final String COL_COUNT = "count";
        public static final String COL_OTHER = "other";
        public static final String COL_TAGS = "tags";


    }

    public static abstract class Courses implements BaseColumns{
        public static final String TABLE_NAME = "courses";
        //public static final String COL_ID = "_ID";
        public static final String COL_COURSE_NAME = "course_name";
        public static final String COL_CREATE_TIME = "create_time";

        public static final String COL_TYPE = "type";
        public static final String COL_LANGUAGE = "language";
        public static final String COL_TAGS = "tags";
        public static final String COL_OTHER = "other";
        public static final String COL_COUNT = "count";
    }
}
