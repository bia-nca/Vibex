package de.girlsgeek.vibex;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mp;
    private String audioSource; // get the audio source from an intend
    private MediaPlayer.TrackInfo trackInfo[];

    @Override
    public void onPrepared(MediaPlayer mp) {
        //mp.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button buttonPlayPause = (Button) findViewById(R.id.play_pause);
        final Button buttonNext = (Button) findViewById(R.id.next);



        buttonPlayPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if(mp.isPlaying()) {
                    mp.pause();
                } else {
                    mp.start();
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                for (MediaPlayer.TrackInfo info: trackInfo) {
                    System.out.println(info.toString());
                }
            }
        });

        mp = new MediaPlayer();

        mp.setOnPreparedListener(this);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try{
            mp.setDataSource("https://api.soundcloud.com/tracks/201059229/stream?client_id=0b0263b59a2c75be631fecf6c8c95dd1");
            trackInfo = mp.getTrackInfo();
        } catch (IllegalArgumentException e){
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }

        mp.prepareAsync();
    }
}
