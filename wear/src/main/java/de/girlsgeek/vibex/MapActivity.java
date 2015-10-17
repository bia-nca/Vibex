package de.girlsgeek.vibex;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapMarker;

public class MapActivity extends Activity {

    private TextView mTextView;

    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private MapFragment mapFragment = null;

    private MapMarker marker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);

                // Search for the map fragment to finish setup by calling init().
                mapFragment = (MapFragment) getFragmentManager().findFragmentById(
                        R.id.mapfragment);
                mapFragment.init(new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(
                            OnEngineInitListener.Error error) {
                        if (error == OnEngineInitListener.Error.NONE) {
                            System.out.println("OK: It works: " + error);



                            // retrieve a reference of the map from the map fragment
                            map = mapFragment.getMap();

                            GeoCoordinate geo = new GeoCoordinate(52.520007, 13.404954);
                            Image img = new Image();
                            Drawable drawable = getResources().getDrawable(R.mipmap.twitter);
                            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                            img.setBitmap(bitmap);


                            marker = new MapMarker(geo, img);

                            map.addMapObject(marker);
                            // Set the map center to the Berlin regio^n (no animation)
                            map.setCenter(geo, Map.Animation.NONE);

                            // Set the zoom level to the average between min and max
                            map.setZoomLevel(
                                    (map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                        } else {
                            System.out.println("ERROR: Cannot initialize Map Fragment + " + error);
                        }
                    }
                });
            }
        });





    }
}
