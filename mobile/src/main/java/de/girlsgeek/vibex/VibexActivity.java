package de.girlsgeek.vibex;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VibexActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mp;
    private String audioSource; // get the audio source from an intent
    private MediaPlayer.TrackInfo trackInfo[];
    private boolean playerIsReady = false;
    private boolean playerHasTrack = false;

    final String GET_USERS_BY_CITY_CALL = "getUsersByCity";
    final String GET_TRACKS_CALL = "getTracks";

    final String CLIENT_ID = "0b0263b59a2c75be631fecf6c8c95dd1";
    final String CITY = "berlin";
    final int LIMIT = 20;

    List userIDs;
    List userTrackList;
    String callType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibex);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final AppCompatImageButton buttonPlay = (AppCompatImageButton) findViewById(R.id.play);
        final AppCompatImageButton buttonNext = (AppCompatImageButton) findViewById(R.id.next);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(playerIsReady){
                    if(mp.isPlaying()) {
                        mp.pause();
                    } else {
                        mp.start();
                    }
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                for (MediaPlayer.TrackInfo info : trackInfo) {
                    System.out.println(info.toString());
                }
            }
        });


        mp = new MediaPlayer();

        mp.setOnPreparedListener(this);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);


        startUserByCityCall();

    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d("PLAYER", "IS READY!!!!");
        playerIsReady = true;
    }

    private class CallAPI extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            String urlString=params[0]; // URL to call
            callType = params[1]; // call type

            String resultToDisplay = "";
            InputStream in;

            // HTTP Get
            try {

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
                String parsedResult = convertInputStreamToString(in);
                Log.d("PARSED", parsedResult);

                if(callType.equals(GET_USERS_BY_CITY_CALL)){
                    // reset tracklist
                    userTrackList = new ArrayList();

                    // get city results
                    fetchUserIDs(parsedResult);
                }
                else if(callType.equals(GET_TRACKS_CALL)){
                    fetchTrackURLS(parsedResult);
                }

            } catch (Exception e ) {
                System.out.println(e.getMessage());
                return e.getMessage();
            }

            return resultToDisplay;
        }

        protected void onPostExecute(String result) {
            if(callType.equals(GET_USERS_BY_CITY_CALL) && userIDs != null){

                if(userIDs.size() > 0){
                    startTrackRequest((int)userIDs.remove(0));
                }

            }
            else if(callType.equals(GET_TRACKS_CALL) && userTrackList != null){

                // SEND TO PLAYER ONCE
                if(userTrackList.size() > 0 && !playerHasTrack){
                    String userTrack = userTrackList.remove(0).toString();
                    Log.d("SONG URLS", "URL: " + userTrack);
                    sendToPlayer(userTrack);
                }

                // GET NEW TRACKS
                if(userTrackList.size() < LIMIT && userIDs.size() > 0){
                    startTrackRequest((int)userIDs.remove(0));
                }

            }
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

    private void fetchUserIDs(String result) throws JSONException {
        // get List of user ids out of SoundCloud API result
        userIDs = new ArrayList();

        JSONArray jsonArray = new JSONArray(result);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject userObject = jsonArray.getJSONObject(i);
            int userID = userObject.getInt("id");
            userIDs.add(userID);
        }
    }

    private void fetchTrackURLS(String result) throws JSONException {
        // add track url out of SoundCloud API result to userTrackList
        JSONArray jsonArray = new JSONArray(result);

        if(jsonArray.length()>0){
            JSONObject userObject = jsonArray.getJSONObject(0);
            String trackURL = userObject.getString("stream_url");
            userTrackList.add(trackURL);
        }
    }

//    Start API Calls

    private void startUserByCityCall(){
        String cityUserListCall = "http://api.soundcloud.com/users?q=" + CITY + "&limit=" + LIMIT + "&client_id=" + CLIENT_ID;
        new CallAPI().execute(cityUserListCall, GET_USERS_BY_CITY_CALL);
    }

    private void startTrackRequest(int userId){
        String userTrackCall = "http://api.soundcloud.com/tracks?filter=public&user_id=" + userId + "&client_id=" + CLIENT_ID;
        new CallAPI().execute(userTrackCall, GET_TRACKS_CALL);
    }

    private void sendToPlayer(String url){
        Log.d("TRACK URL: ", url);
        try{
            mp.setDataSource(url + "?client_id=" + CLIENT_ID);
            trackInfo = mp.getTrackInfo();
        } catch (IllegalArgumentException e){
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        playerHasTrack = true;
        mp.prepareAsync();
    }


}
