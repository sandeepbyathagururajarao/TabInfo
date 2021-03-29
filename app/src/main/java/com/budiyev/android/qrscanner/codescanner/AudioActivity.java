package com.budiyev.android.qrscanner.codescanner;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.qrscanner.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AudioActivity extends AppCompatActivity {

    // creating a variable for  
    // button and media player 
    Button playBtn, pauseBtn;
    MediaPlayer mediaPlayer;
    String language;
    String audioFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent audioIntent = getIntent();
        language = audioIntent.getStringExtra("audioLang");
        audioFilter = audioIntent.getStringExtra("audioFilter");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        // initializing our buttons 
        playBtn = findViewById(R.id.idBtnPlay);
        pauseBtn = findViewById(R.id.idBtnPause);

        // setting on click listener for our play and pause buttons. 
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling method to play audio. 
                playAudio();
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking the media player  
                // if the audio is playing or not. 
                if (mediaPlayer.isPlaying()) {
                    // pausing the media player if media player  
                    // is playing we are calling below line to 
                    // stop our media player. 
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();

                    // below line is to display a message  
                    // when media player is paused. 
                    Toast.makeText(AudioActivity.this, "Audio has been paused", Toast.LENGTH_SHORT).show();
                } else {
                    // this method is called when media  
                    // player is not playing. 
                    Toast.makeText(AudioActivity.this, "Audio has not played", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void playAudio() {

        String audioUrl = "https://github.com/sandeepbyathagururajarao/audio/blob/main/"+audioFilter+"_"+language+".mp3?raw=true";
        // initializing media player 
        mediaPlayer = new MediaPlayer();

        // below line is use to set the audio  
        // stream type for our media player. 
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // below line is use to set our  
        // url to our media player. 
        try {
            mediaPlayer.setDataSource(audioUrl);
            // below line is use to prepare 
            // and start our media player. 
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        // below line is use to display a toast message. 
        Toast.makeText(this, "Audio started playing..", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
    }
}