package com.freeyuyuko.listencourse;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class PlayService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener{

    private static final String TAG = "PlayService";
    public static final String KEY_PLAY_LIST = "playlist";
    public static final String KEY_POS = "pos";
    public static final String ACTION_PLAY = "com.freeyuyuko.action.PLAY";
    public static final String ACTION_STOP = "com.freeyuyuko.action.STOP";
    public static final String ACTION_NEXT = "com.freeyuyuko.action.NEXT";
    public static final String ACTION_PREV = "com.freeyuyuko.action.PREV";

    private MediaPlayer mMediaPlayer = null;
    private ArrayList<Uri> mPlayList;
    private int mPos = 0;
    public PlayService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "OnStart.");
        if( mMediaPlayer != null )
            mMediaPlayer.release();
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(TAG, "OnStartCommand.");
        try {
            mPlayList = intent.getExtras().getParcelableArrayList(KEY_PLAY_LIST);
            mPos = intent.getExtras().getInt(KEY_POS);
            Uri uri = mPlayList.get(mPos);
            Log.d(TAG, "Start Uri: " + uri.toString());

            mMediaPlayer = new MediaPlayer();

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(getApplicationContext(), uri);

            if (intent.getAction().equals(ACTION_PLAY)) {

                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.prepareAsync();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }

    public void onPrepared(MediaPlayer player){
        Log.d(TAG, "OnPrepared.");
        player.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        return false;
    }
}
