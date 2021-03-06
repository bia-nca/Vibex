package de.girlsgeek.vibex;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

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

public class SoundActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_sound);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startUserByCityCall();
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

                startTrackRequest((int)userIDs.remove(0));

            }
            else if(callType.equals(GET_TRACKS_CALL) && userTrackList != null){
                for (int i = 0; i < userTrackList.size(); i++) {
                    Log.d("SONG URLS", "URL: " + userTrackList.get(i));

                }

                if(userTrackList.size() < LIMIT){
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

}
