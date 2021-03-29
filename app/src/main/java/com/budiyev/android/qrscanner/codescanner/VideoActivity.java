package com.budiyev.android.qrscanner.codescanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.qrscanner.R;

public class VideoActivity extends AppCompatActivity {
        VideoView videoView;
        MediaController mediaControls;
        String language;
        String videoFilter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_video);
            Intent audioIntent = getIntent();
            language = audioIntent.getStringExtra("videoLang");
            videoFilter = audioIntent.getStringExtra("videoFilter");
            // Find your VideoView in your video_main.xml layout
            videoView = (VideoView) findViewById(R.id.video);

            if (mediaControls == null) {
                // create an object of media controller class
                mediaControls = new MediaController(VideoActivity.this);
                mediaControls.setAnchorView(videoView);
            }
            // set the media controller for video view
            videoView.setMediaController(mediaControls);
            // set the uri for the video view
            String videoUrl = "https://github.com/sandeepbyathagururajarao/video/blob/main/"+videoFilter+"_"+language+".mp4?raw=true";
            videoView.setVideoURI(Uri.parse(videoUrl));
            // start a video
            videoView.start();

            // implement on completion listener on video view
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Toast.makeText(getApplicationContext(), "Thank for watching", Toast.LENGTH_LONG).show(); // display a toast when an video is completed
                }
            });
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
                    return false;
                }
            });
        }
    }

