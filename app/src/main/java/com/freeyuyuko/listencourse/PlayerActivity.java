package com.freeyuyuko.listencourse;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerActivity extends AppCompatActivity
    implements SingleSelectDialog.CallBackSingleSelectFinished{

    private static final String TAG = "PlayerActivity";

    private String mRawName;
    private List<Map<String,String>> mPlayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate.");

        //new Exception("Stack Trace.").printStackTrace();

        setContentView(R.layout.activity_player);
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

        Bundle bundle = getIntent().getExtras();
        mRawName = bundle.getString(CourseMap.Videos.COL_RAW_NAME);
        mPlayList = (List<Map<String,String>>)bundle.getSerializable("list");

        try {
            int pos = 0;
            ArrayList<Uri> list = new ArrayList<>();
            for (int i = 0; i < mPlayList.size(); ++i) {
                String rawName = mPlayList.get(i).get(CourseMap.Videos.COL_RAW_NAME);
                if (rawName.equals(mRawName))
                    pos = i;
                String path = CourseApplication.getInstance().getCourseraPath()
                        + File.separator + rawName;
                Uri uri = Uri.fromFile(new File(path));
                list.add(uri);
            }
            playVideo(list, pos);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "OnRestart.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "OnPause.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "OnStop.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OnDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.d(TAG, "OnSaveInstanceState.");
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_move) {
            showSelectCourse(mRawName);
            return true;
        }else if( id == R.id.action_delete_video ){
            deleteFromList(mRawName);
            //finish();
            return true;
        }else if( id == android.R.id.home ){
            Log.d(TAG, "On action home pressed.");
            //finish();
            //return true; //will not be handled
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.d(TAG, "OnBackPressed.");
        finish();
    }

    @Override
    public void OnSingleSelectFinished(String select, boolean bOk) {

        if(!bOk)
            return;

        Log.d(TAG, String.format("SingleSelectDialog return:%s", select));
        try{
            if(select == null){
                Toast.makeText(this, "No course is selected.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(select.equalsIgnoreCase("Others"))
                select = "";

            //thread be better
            DbOperator operator = new DbOperator(this);
            operator.moveVideoTo(mRawName, select, operator.getCourseCount(
                    operator.getCourseNameOfVideo(mRawName)));
            //return
            finish();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    private void playVideo(ArrayList<Uri> list, int pos){
        Intent intent = new Intent(this, PlayService.class);
        intent.setAction(PlayService.ACTION_PLAY);
        intent.putExtra(PlayService.KEY_POS, pos);
        intent.putExtra(PlayService.KEY_PLAY_LIST, list);
        startService(intent);
    }

    private void showSelectCourse(String rawName){
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

    private void deleteFromList(String rawName){
        try{

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
