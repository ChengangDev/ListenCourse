package com.freeyuyuko.listencourse;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * Created by chengang on 16-1-3.
 */
public class RefreshDbTask extends AsyncTask<String, Integer, Boolean> {

    private static final String TAG = "RefreshDbTask";

    private final Activity mAct;

    public RefreshDbTask(Activity activity) {
        mAct = activity;
    }

    @Override
    protected final Boolean doInBackground(String... params) {
        Log.d(TAG, String.format("Start RefreshDbTask:%s", Arrays.toString(params)));
        try{
            for(int i = 0; i < params.length; ++i)
            {
                if( params[i] == null )
                    continue;

                File dir = new File(params[i]);
                if( !dir.exists() ){
                    Log.e(TAG, String.format("%s not exits.", params[i]));
                    continue;
                }

                if( dir.isFile() )
                    continue;

                File[] videos = dir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.toLowerCase().endsWith(".mp4");
                    }
                });

                Log.d(TAG, String.format("Videos:%s", Arrays.toString(videos)));
                DbOperator dbOperator = new DbOperator(mAct);
                for(int j = 0; j < videos.length; ++j ){
                    File file = videos[j];
                    String rawName = file.getName();
                    if( dbOperator.isVideoExist(rawName))
                        continue;
                    dbOperator.addVideo(rawName, "", "", "");
                }
            }
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
        Log.d(TAG, "RefreshDbTask finished.");
        return true;
    }
}
