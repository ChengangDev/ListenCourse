package com.freeyuyuko.listencourse;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.freeyuyuko.listencourse.CourseMap.Courses;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ListView mListViewCourse = null;
    private UpdateCourseListTask mTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action",
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mListViewCourse = (ListView)findViewById(R.id.list_courses);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class UpdateCourseListTask extends
            AsyncTask<Void, Integer, Boolean>{
        private Activity mAct;
        private List<Map<String, String>> mCourses;

        public UpdateCourseListTask(Activity act){
            mAct = act;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                DbOperator dbOperator = new DbOperator(mAct);
                mCourses = dbOperator.getCoursesList();
            }catch (final Exception e){
                e.printStackTrace();
                mAct.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mAct, e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if( !aBoolean ){
                Toast.makeText(mAct, "Failed to get courses.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try{
                SimpleAdapter adpter = new SimpleAdapter(
                        mAct, mCourses, R.layout.item_of_courses_list,
                        new String[]{
                                Courses._ID,
                                Courses.COL_COURSE_NAME,
                                Courses.COL_COUNT
                        },
                        new int[]{
                                R.id.text_course_index,
                                R.id.text_course_name,
                                R.id.text_material_count
                        }
                );
                mListViewCourse.setAdapter(adpter);
                mListViewCourse.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {

                    }
                });
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(mAct, e.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        }
    }
}
