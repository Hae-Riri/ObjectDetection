package org.tensorflow.lite.examples.detection.sos;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import org.tensorflow.lite.examples.detection.R;

public class ObjectPlayer {
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;

    public ObjectPlayer(Context context){
        mContext = context;
        mMediaPlayer = MediaPlayer.create(mContext,R.raw.car);
        mAudioManager=(AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
    }
    public void playAudio(){
        if(mMediaPlayer==null){
            mMediaPlayer=MediaPlayer.create(mContext,R.raw.real_car);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,9,0);
            mMediaPlayer.start();
        }else {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 9, 0);
            mMediaPlayer.start();
        }
    }
    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }

    public void stopAudio() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void closePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
