package com.freeyuyuko.listencourse;

import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerActivity extends AppCompatActivity
    implements SingleSelectDialog.CallBackSingleSelectFinished,
        InputDialog.CallBackInputFinished{

    private static final String TAG = "PlayerActivity";

    private int mPos;
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
        mPos = bundle.getInt("position");
        mPlayList = (List<Map<String,String>>)bundle.getSerializable("list");

        try {

            ArrayList<Uri> list = new ArrayList<>();
            for (int i = 0; i < mPlayList.size(); ++i) {
                String rawName = mPlayList.get(i).get(CourseMap.Videos.COL_RAW_NAME);

                String path = CourseApplication.getInstance().getCourseraPath()
                        + File.separator + rawName;
                Uri uri = Uri.fromFile(new File(path));
                list.add(uri);
            }
            playVideo(list, mPos);
        }catch (Exception e){
            e.printStackTrace();
        }

        initButtons();
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
            showSelectCourse();
            return true;
        }else if( id == R.id.action_delete_video ){
            deleteFromList();
            //finish();
            return true;
        }else if( id == android.R.id.home ){
            Log.d(TAG, "On action home pressed.");
            //finish();
            //return true; //will not be handled
        }else if( id == R.id.action_rename_video ){
            showRenameDialog();
            return true;
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

            String rawName = mPlayList.get(mPos).get(CourseMap.Videos.COL_RAW_NAME);
            //thread be better
            DbOperator operator = new DbOperator(this);
            operator.moveVideoTo(rawName, select, operator.getCourseCount(
                    operator.getCourseNameOfVideo(rawName)));
            //return
            finish();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    private void initButtons(){
        findViewById(R.id.button_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, PlayService.class);
                intent.setAction(PlayService.ACTION_NEXT);
                startService(intent);
            }
        });

        findViewById(R.id.button_prev).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, PlayService.class);
                intent.setAction(PlayService.ACTION_PREV);
                startService(intent);
            }
        });

        final Button btn = (Button)findViewById(R.id.button_status);
        btn.setText("Playing");
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String oldStatus = btn.getText().toString();
                Intent intent = new Intent(PlayerActivity.this, PlayService.class);
                if (oldStatus.equalsIgnoreCase("Playing")) {
                    intent.setAction(PlayService.ACTION_PAUSE);
                    btn.setText("Pause");
                } else if (oldStatus.equalsIgnoreCase("Pause")) {
                    intent.setAction(PlayService.ACTION_START_FROM_PAUSE);
                    btn.setText("Playing");
                }
                startService(intent);
            }
        });
    }

    private void playVideo(ArrayList<Uri> list, int pos){
        Intent intent = new Intent(this, PlayService.class);
        intent.setAction(PlayService.ACTION_PLAY);
        intent.putExtra(PlayService.KEY_POS, pos);
        intent.putExtra(PlayService.KEY_PLAY_LIST, list);
        startService(intent);
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

    private void deleteFromList(){
        try{

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showRenameDialog(){
        try{
            Bundle bundle = new Bundle();
            bundle.putString("title", "Set Lesson Name");
            bundle.putString("viewName", EditText.class.getName());
            bundle.putString("input", mPlayList.get(mPos).get(CourseMap.Videos.COL_LESSON_NAME));
            InputDialog dlg = new InputDialog();
            dlg.setArguments(bundle);
            dlg.show(getFragmentManager(), "SetLessonName");
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
                //thread be better
                DbOperator dbOperator = new DbOperator(this);
                String rawName = mPlayList.get(mPos).get(CourseMap.Videos.COL_RAW_NAME);
                dbOperator.setLessonNameOfVideo(rawName, input);

                Log.d(TAG, String.format("Set Lesson Name %s.", input));


            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }

        }
    }
}
