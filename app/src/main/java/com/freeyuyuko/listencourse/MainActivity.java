package com.freeyuyuko.listencourse;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.freeyuyuko.listencourse.CourseMap.Courses;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements InputDialog.CallBackInputFinished{

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

        updateCourseList();
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
        }else if( id == R.id.action_add_course ){
            showAddCourse("");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnInputFinished(String input, int which) {
        if( which == DialogInterface.BUTTON_POSITIVE ){
            if( input == null || input.isEmpty() ) {
                Toast.makeText(this, "Course name can not be empty.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try{
                if( input.contains("(") || input.contains(")")){
                    Toast.makeText(this, "( and ) is illegel.",
                            Toast.LENGTH_SHORT).show();
                    showAddCourse(input);
                    return;
                }

                String yearMonth = DbOperator.getCurDateTime().substring(0, 7);
                String courseName = input + "(" + yearMonth + ")";

                //thread be better
                DbOperator dbOperator = new DbOperator(this);
                if( dbOperator.isCourseExist(courseName) ){
                    Toast.makeText(this, String.format("%s is exist", courseName),
                            Toast.LENGTH_SHORT).show();
                    showAddCourse(input);
                    return;
                }
                dbOperator.addCourse(courseName, "");
                Log.d(TAG, String.format("Add Course %s.", courseName));
                updateCourseList();

            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void updateCourseList(){
        mListViewCourse = (ListView)findViewById(R.id.list_courses);

        if( mTask != null )
            mTask.cancel(true);

        mTask = new UpdateCourseListTask(this);
        mTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, null);

    }

    private void showAddCourse(String input){
        try {
            Bundle bundle = new Bundle();
            bundle.putString("title", "Add Course");
            bundle.putString("viewName", EditText.class.getName());
            bundle.putString("input", input);
            InputDialog dlg = new InputDialog();
            dlg.setArguments(bundle);
            dlg.show(getFragmentManager(), "AddCourse");
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
