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
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{

    private static final String TAG = "PlayService";
    public static final String KEY_PLAY_LIST = "playlist";
    public static final String KEY_POS = "pos";
    public static final String ACTION_PLAY = "com.freeyuyuko.action.PLAY";
    public static final String ACTION_PAUSE = "com.freeyuyuko.action.PAUSE";
    public static final String ACTION_START_FROM_PAUSE = "com.freeyuyuko.action.START_FROM_PAUSE";
    public static final String ACTION_STOP = "com.freeyuyuko.action.STOP";
    public static final String ACTION_NEXT = "com.freeyuyuko.action.NEXT";
    public static final String ACTION_PREV = "com.freeyuyuko.action.PREV";

    private MediaPlayer mMediaPlayer = null;
    private ArrayList<Uri> mPlayList;
    private int mPos = 0;
    public PlayService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "OnCreate.");
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
            if(mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }

            String action = intent.getAction();
            if (action.equals(ACTION_PLAY)) {
                if(mMediaPlayer.isPlaying())
                    mMediaPlayer.stop();
                mPlayList = intent.getExtras().getParcelableArrayList(KEY_PLAY_LIST);
                mPos = intent.getExtras().getInt(KEY_POS);
                Uri uri = mPlayList.get(mPos);
                Log.d(TAG, "Start Uri: " + uri.toString());

                initMediaPlayer(uri);
            }else if(action.equals(ACTION_STOP)) {
                mMediaPlayer.stop();
            }else if(action.equals(ACTION_PAUSE)){
                mMediaPlayer.pause();
            }else if(action.equals(ACTION_START_FROM_PAUSE)){
                mMediaPlayer.start();
            }else if(action.equals(ACTION_NEXT)){

                onNextOrPrev(true);
            }else if(action.equals(ACTION_PREV)){

                onNextOrPrev(false);
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

    @Override
    public void onCompletion(MediaPlayer mp) {
        try{
            mPos++;
            if(mPos >= mPlayList.size())
                mPos = 0;

            Uri uri = mPlayList.get(mPos);
            Log.d(TAG, String.format("Start Uri: %s", uri.toString()));
            initMediaPlayer(uri);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initMediaPlayer(Uri uri){
        try{
            mMediaPlayer.reset();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(getApplicationContext(), uri);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnCompletionListener(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void onNextOrPrev(boolean bNext){
        try{
            if(bNext)
                mPos++;
            else
                mPos--;

            if(mPos >= mPlayList.size())
                mPos = 0;
            else if(mPos < 0)
                mPos = mPlayList.size()-1;

            Uri uri = mPlayList.get(mPos);
            Log.d(TAG, String.format("%s Uri: %s", bNext?"Next":"Prev", uri.toString()));
            initMediaPlayer(uri);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
