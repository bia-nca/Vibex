package de.girlsgeek.vibex;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SoundActivity extends AppCompatActivity {

    String clientId = "0b0263b59a2c75be631fecf6c8c95dd1";
    String city = "berlin";
    int limit = 20;

    boolean playerPrepared = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String soundCloudURL = "http://api.soundcloud.com/users?q=" + city + "&limit=" + limit + "&client_id=" + clientId;

        new CallAPI().execute(soundCloudURL);


    }

    private void startMediaPlayer(){
        try {
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource("http://www.stephaniequinn.com/Music/Handel%20-%20Entrance%20of%20the%20Queen%20of%20Sheba.mp3");
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    playerPrepared = true;
                }
            });
            player.prepareAsync();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private class CallAPI extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            String urlString=params[0]; // URL to call

            String resultToDisplay = "";

            InputStream in = null;

            // HTTP Get
            try {

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
                String parsedResult = convertInputStreamToString(in);
                Log.d("PARSED", parsedResult);

                startMediaPlayer();

            } catch (Exception e ) {

                System.out.println(e.getMessage());
                return e.getMessage();

            }

            return resultToDisplay;
        }

        protected void onPostExecute(String result) {
            Log.d("RESULT", result);
        }



    } // end CallAPI

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}
