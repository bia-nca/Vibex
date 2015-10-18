package de.girlsgeek.vibex;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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

    final String GET_USERS_BY_CITY_CALL = "getUsersByCity";
    final String GET_TRACKS_CALL = "getTracks";
    final String CLIENT_ID = "0b0263b59a2c75be631fecf6c8c95dd1";
    final String CITY = "Berlin";
    final int LIMIT = 20;

    String city = "berlin";

    List userIDs;
    List userTrackList;
    String callType;
    int currentTrack = 0;
    boolean instantPlay = false;
    boolean isPlaying = false;
    boolean wasStopped = false;
    ImageButton buttonPlay;
    ImageButton buttonPause;
    String[] trackInfo;
    private MediaPlayer mp;
    private boolean playerIsReady = false;
    private boolean playerHasTrack = false;

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibex);

        city = getIntent().getExtras().getString("city");
        Log.d("CITY", "STADT: " + city);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.back_button);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(city);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        buttonPlay = (ImageButton) findViewById(R.id.play);
        buttonPause = (ImageButton) findViewById(R.id.pause);
        final ImageButton buttonStop = (ImageButton) findViewById(R.id.stop);
        final ImageButton buttonNext = (ImageButton) findViewById(R.id.next);
        buttonPause.setVisibility(View.INVISIBLE);

        View.OnClickListener playpauseListener = new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("TAG", "button");
                if (playerIsReady) {
                    if (isPlaying) {
                        pauseSound();
                    } else {
                        playSound();
                    }
                } else if(wasStopped){
                    playSound();
                }
            }
        };

        buttonPlay.setOnClickListener(playpauseListener);
        buttonPause.setOnClickListener(playpauseListener);

        buttonStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isPlaying) {
                    stopSound();
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                playerIsReady = false;
                instantPlay = true;
                currentTrack++;

                // RESET currentTrackNr if larger than Tracklist
                if (currentTrack == userTrackList.size()) {
                    currentTrack = 0;
                }

                trackInfo = (String[]) userTrackList.get(currentTrack);
                sendToPlayer(trackInfo);
            }
        });

        // LISTVIEW

        ListView list = (ListView) findViewById(R.id.listView);
        String[] names = new String[]{"Unheilig", "Indecks at Brunnen70", "Clekclekboom at Berghain/Panorama Bar", "THE SAY HIGHS- Adam Fuge- Vanterra- Bastian Bandt", "Nana K. - Motion: w/ Mantu"};
        String[] locations = new String[]{"O2 World", "Brunnen70", "BERGHAIN/PANORAMA BAR", "CZAR HAGESTOLZ OPEN AIR", "60Hz Berlin"};
        String[] urls = new String[]{"http://berlin.eventful.com/events/unheilig-/E0-001-079518031-9?utm_source=apis&amp;utm_medium=apim&amp;utm_campaign=apic", "http://berlin.eventful.com/events/indecks-brunnen70-/E0-001-087898367-3?utm_source=apis&amp;utm_medium=apim&amp;utm_campaign=apic", "http://berlin.eventful.com/events/clekclekboom-berghainpanorama-bar-/E0-001-087472373-6?utm_source=apis&amp;utm_medium=apim&amp;utm_campaign=apic", "http://berlin.eventful.com/venues/czar-hagestolz-open-air-/V0-001-008166680-4?utm_source=apis&amp;utm_medium=apim&amp;utm_campaign=apic", "http://berlin.eventful.com/events/nana-k-motion-w-mantu-get-physical-dayne-s-exp-/E0-001-086018654-9?utm_source=apis&amp;utm_medium=apim&amp;utm_campaign=apic"};
        list.setAdapter(new EventListAdapter(this, names, locations, urls));

    }

    @Override
    public void onStart(){
        super.onStart();

        mp = new MediaPlayer();

        mp.setOnPreparedListener(this);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

        startUserByCityCall();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        wasStopped = false;
        playerIsReady = true;
        Log.d("TAG", "on Prepared!");

        if(instantPlay){
            playSound();
            instantPlay = false;
        }
    }

    private void playSound(){
        buttonPause.setVisibility(View.VISIBLE);
        buttonPlay.setVisibility(View.INVISIBLE);
        isPlaying = true;
        if(wasStopped){
            instantPlay = true;
            mp.reset();
            mp.prepareAsync();

        } else {
            resetArtistSongText(trackInfo[1], trackInfo[2]);
            mp.start();
        }
    }

    private void pauseSound(){
        buttonPlay.setVisibility(View.VISIBLE);
        buttonPause.setVisibility(View.INVISIBLE);
        isPlaying = false;
        mp.pause();
    }

    private void stopSound(){
        buttonPlay.setVisibility(View.VISIBLE);
        buttonPause.setVisibility(View.INVISIBLE);
        isPlaying = false;
        playerIsReady = false;
        wasStopped = true;
        mp.stop();
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

        if(jsonArray.length() > 0) {
            JSONObject userObject = jsonArray.getJSONObject(0);
            String trackURL = userObject.getString("stream_url");
            JSONObject user = userObject.getJSONObject("user");
            String username = user.getString("username");
            String title = userObject.getString("title");
            String[] track = new String[]{trackURL, username, title};

            userTrackList.add(track);
        }
    }

    private void startUserByCityCall(){
        String cityUserListCall = "http://api.soundcloud.com/users?q=" + CITY + "&limit=" + LIMIT + "&client_id=" + CLIENT_ID;
        new CallAPI().execute(cityUserListCall, GET_USERS_BY_CITY_CALL);
    }

//    Start API Calls

    private void startTrackRequest(int userId){
        String userTrackCall = "http://api.soundcloud.com/tracks?filter=public&user_id=" + userId + "&client_id=" + CLIENT_ID;
        new CallAPI().execute(userTrackCall, GET_TRACKS_CALL);
    }

    private void sendToPlayer(String[] track){
        String url = track[0];
        String artist = track[1];
        String title = track[2];

        Log.d("TRACK URL: ", url);
        try{
            if(playerHasTrack){
                mp.reset();
            }
            mp.setDataSource(url + "?client_id=" + CLIENT_ID);

        } catch (IllegalArgumentException e){
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        resetArtistSongText(artist, title);
        playerHasTrack = true;
        mp.prepareAsync();
    }

    private void resetArtistSongText(String artist, String track){
        TextView artistSong = (TextView) findViewById(R.id.artistSong);
        artistSong.setText(artist + " - " + track);
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
                    userTrackList = new ArrayList<String[]>();

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

                    trackInfo = (String[]) userTrackList.get(0);

                    String userTrack = trackInfo[0];
                    Log.d("SONG URLS", "URL: " + userTrack);
                    sendToPlayer(trackInfo);
                }

                // GET NEW TRACKS
                if(userTrackList.size() < LIMIT && userIDs.size() > 0){
                    startTrackRequest((int)userIDs.remove(0));
                }
            }
        }
    }


}
