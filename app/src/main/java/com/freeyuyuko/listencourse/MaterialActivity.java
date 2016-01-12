package com.freeyuyuko.listencourse;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.freeyuyuko.listencourse.CourseMap.Courses;
import com.freeyuyuko.listencourse.CourseMap.Videos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialActivity extends AppCompatActivity implements
        SingleSelectDialog.CallBackSingleSelectFinished{

    private static final String TAG = "MaterialActivity";

    private String mCourseName;
    private List<Map<String, String>> mVideos;
    private ListView mListViewVideo;

    private UpdateMaterialListTask mTask;
    private MoveVideoTask mMoveTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate.");
        setContentView(R.layout.activity_material);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListViewVideo = (ListView)findViewById(R.id.list_videos);


        Intent intent = getIntent();
        mCourseName = intent.getExtras()
                .getString(Courses.COL_COURSE_NAME);
        ((TextView)findViewById(R.id.text_course_name)).setText(mCourseName);
        if(mCourseName.equalsIgnoreCase("others"))
            mCourseName = "";

        updateMaterialList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "OnRestart.");
        updateMaterialList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OnDestory.");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.d(TAG, "OnSaveInstanceState.");
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_material, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateMaterialList();
            return true;
        }else if( id == R.id.action_add_video ){
            //showAddCourse("");
            return true;
        }else if( id == android.R.id.home ){
            Log.d(TAG, "On action home pressed.");
            finish();
            return true;
        }else if( id == R.id.action_move_all ){
            showSelectCourse();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void updateMaterialList(){
        mListViewVideo = (ListView)findViewById(R.id.list_videos);

        if(mTask != null){
            mTask.cancel(true);
        }
        mTask = new UpdateMaterialListTask();
        mTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, null);
    }



    private class UpdateMaterialListTask extends AsyncTask<Void, Void, Boolean>{

        private static final String TAG = "UpdateMaterialListTask";


        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                DbOperator dbOperator = new DbOperator(MaterialActivity.this);
                mVideos = dbOperator.getVideosList(mCourseName);
                for(int i = 0; i < mVideos.size(); ++i){
                    String lessonName = mVideos.get(i).get(Videos.COL_LESSON_NAME);
                    if( lessonName == null || lessonName.isEmpty() )
                        mVideos.get(i).put(Videos.COL_LESSON_NAME,
                                mVideos.get(i).get(Videos.COL_RAW_NAME));
                }
            }catch(final Exception e){
                e.printStackTrace();
                MaterialActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MaterialActivity.this, e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if( !aBoolean )
                return;

            try{
                SimpleAdapter adapter = new SimpleAdapter(
                        MaterialActivity.this,
                        mVideos,
                        R.layout.item_of_videos_list,
                        new String[]{
                                Videos.COL_SCHEDULE,
                                Videos.COL_LESSON_NAME
                        },
                        new int[]{
                                R.id.text_video_index,
                                R.id.text_video_name
                        });
                mListViewVideo.setAdapter(adapter);
                mListViewVideo.setOnItemClickListener(new AdapterView
                        .OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        Intent intent = new Intent(MaterialActivity.this,
                                PlayerActivity.class);
                        intent.putExtra("position", position);
                        ArrayList<HashMap<String,String>> list = new ArrayList<>();
                        for(int i = 0; i < mVideos.size(); ++i)
                            list.add(new HashMap<String, String>(mVideos.get(i)));
                        intent.putExtra("list", list);
                        for(int i = 0; i < mVideos.size(); ++i)

                        MaterialActivity.this.startActivity(intent);

                    }
                });
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(MaterialActivity.this,
                        e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showSelectCourse(){
        try {
            Bundle bundle = new Bundle();
            bundle.putString("title", "Select Course");

            DbOperator operator = new DbOperator(this);
            List<Map<String,String>> listMap = operator.getCoursesList();
            ArrayList<String> list = new ArrayList<>();
            for(int i = 0; i < listMap.size(); ++i ){
                list.add(listMap.get(i).get(CourseMap.Courses.COL_COURSE_NAME));
            }
            bundle.putStringArrayList("list", list);
            SingleSelectDialog dlg = new SingleSelectDialog();
            dlg.setArguments(bundle);
            dlg.show(getFragmentManager(), "Select Course");
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnSingleSelectFinished(String select, boolean bOk) {
        if(mMoveTask != null){
            Toast.makeText(this, "Already have a move task.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(select.equalsIgnoreCase("Others"))
            select = "";

        if(select.equals(mCourseName))
            return;

        mMoveTask = new MoveVideoTask(this, mVideos, select);
        mMoveTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, null);
    }

    public class MoveVideoTask extends AsyncTask<Void, Integer, Boolean>{
        private static final String TAG = "MoveVideoTask";

        private final Activity mAct;
        private final List<Map<String,String>> mList;
        private final String mCourseName;

        public MoveVideoTask(Activity act, List<Map<String,String>> list, String courseName){
            mAct = act;
            mList = list;
            mCourseName = courseName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, "Do move task.");
            try{
                DbOperator operator = new DbOperator(mAct);

                for(int i = 0; i < mList.size(); ++i){
                    String rawName = mVideos.get(i).get(CourseMap.Videos.COL_RAW_NAME);
                    String oldCourseName = operator.getCourseNameOfVideo(rawName);
                    operator.moveVideoTo(rawName, mCourseName, operator.getCourseCount(
                            oldCourseName));
                    Log.d(TAG, String.format("%d)Move %s from %s to %s",
                            i+1, rawName, oldCourseName, mCourseName));
                }
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mMoveTask = null;
            if(aBoolean){
                Toast.makeText(mAct, "Move Success.", Toast.LENGTH_SHORT);
                updateMaterialList();
            }
        }
    }
}
