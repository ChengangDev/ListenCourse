package com.freeyuyuko.listencourse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.freeyuyuko.listencourse.CourseMap.Courses;
import com.freeyuyuko.listencourse.CourseMap.Videos;

import java.util.List;
import java.util.Map;

public class MaterialActivity extends AppCompatActivity {

    private static final String TAG = "MaterialActivity";

    private String mCourseName;
    private ListView mListViewVideo;

    private UpdateMaterialListTask mTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if(mCourseName.equalsIgnoreCase("others"))
            mCourseName = "";

        updateMaterialList();
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
            return true;
        }else if( id == R.id.action_add_video ){
            //showAddCourse("");
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
        private List<Map<String, String>> mVideos;

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                DbOperator dbOperator = new DbOperator(MaterialActivity.this);
                mVideos = dbOperator.getVideosList(mCourseName);
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
                                Videos.COL_RAW_NAME
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
}
