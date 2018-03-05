package com.dayal.mediaplayer;

import java.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{  //interface for overriding onClick method
    private MediaPlayer mediaPlayer;
    private Thread thread;
    private Button prevBtn,playBtn,nextBtn;
    private TextView artist,author,leftTime,rightTime;
    private SeekBar mSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpUI();
        mSeekBar.setMax(mediaPlayer.getDuration());

     //    if(mediaPlayer.isPlaying()) mSeekBar.setProgress(mediaPlayer.getCurrentPosition());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress); //seek the progress
                    }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                int duration=mp.getDuration();
                String mDuration=String.valueOf(duration/1000);
                Toast.makeText(getApplicationContext(),"Duration "+mDuration+" s", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setUpUI(){
        playBtn=(Button)findViewById(R.id.play_btn);
        prevBtn=(Button)findViewById(R.id.prev_btn);
        nextBtn=(Button)findViewById(R.id.next_btn);
        leftTime=(TextView)findViewById(R.id.start_time);
        rightTime=(TextView)findViewById(R.id.end_time);
        mediaPlayer=new MediaPlayer();
        mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.clapp);
        mSeekBar=(SeekBar)findViewById(R.id.seekBar);

        prevBtn.setOnClickListener(this);   // register for the click
        playBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
    }

    public void updateThread() {
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //code for seekbar & timer
                                int duration=mediaPlayer.getDuration();
                                double currPos=mediaPlayer.getCurrentPosition();
                                Log.i("MyMsg",""+currPos);
                                mSeekBar.setMax(duration);
                                mSeekBar.setProgress((int)currPos);
                                SimpleDateFormat dateFormat=new SimpleDateFormat("mm:ss");  //requires min sdk 24
                                //Date date=new Date(currPos);
                               //  String p=String.format( TimeUnit.MILLISECONDS.toMinutes((long)currPos));
                                //String d=dateFormat.format(duration);

                               // leftTime.setText(String.valueOf(dateFormat.format(new Date((long)currPos))));
                               leftTime.setText( String.format("%d min, %d sec",
                                       TimeUnit.MILLISECONDS.toMinutes((long) currPos),
                                       TimeUnit.MILLISECONDS.toSeconds((long) currPos) -
                                               TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                       toMinutes((long) currPos)))
                               );;
                                Log.i("MyMsg","leftTime= "+leftTime);
                                rightTime.setText(String.valueOf(dateFormat.format(new Date((int)(duration-currPos)))));
                            }
                        });
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }



        };thread.start();
    }
    @Override
    public void onClick(View view) {
         switch(view.getId()){
             case R.id.prev_btn:
                 //code
                 break;
             case R.id.play_btn:
                 //code
                 if(mediaPlayer.isPlaying()){
                     pauseMusic();
                 }else{
                     startMusic();
                 }
                 break;
             case R.id.next_btn:
                 //code
                 break;

         }
    }

    public void pauseMusic(){
        if(mediaPlayer!=null){
            mediaPlayer.pause();
            playBtn.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }

    public void startMusic(){
        if(mediaPlayer!=null){
            mediaPlayer.start();
            playBtn.setBackgroundResource(android.R.drawable.ic_media_pause);
            updateThread();
        }
    }

    @Override
    protected void onDestroy() {
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();;
            mediaPlayer.release();
            mediaPlayer=null;
        }
        thread.interrupt();
        thread=null;

        super.onDestroy();
    }


}
