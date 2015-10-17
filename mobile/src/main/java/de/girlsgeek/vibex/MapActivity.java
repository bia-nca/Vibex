package de.girlsgeek.vibex;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapLabeledMarker;
import com.here.android.mpa.mapping.MapMarker;


public class MapActivity extends Activity {

    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private MapFragment mapFragment = null;


    private MapMarker marker = null;
    private MapLabeledMarker labeledMarker = null;

    private int markerCounter = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Search for the map fragment to finish setup by calling init().
        mapFragment = (MapFragment)getFragmentManager().findFragmentById(
                R.id.mapfragment);
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(
                    OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the map fragment
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();

                    GeoCoordinate geo = new GeoCoordinate(52.520007, 13.404954);
                    GeoCoordinate geo2 = new GeoCoordinate(53.520007, 11.404954);
                    Image img = new Image();

                    Drawable drawable = getResources().getDrawable(R.mipmap.twitter);;

                    if(markerCounter > 10) {
                        drawable = getResources().getDrawable(R.mipmap.twitter);
                    }else if(markerCounter > 30){
                        drawable = getResources().getDrawable(R.mipmap.twitter);
                    }else if(markerCounter > 50){
                        drawable = getResources().getDrawable(R.mipmap.twitter);
                    }else if(markerCounter > 100){
                        drawable = getResources().getDrawable(R.mipmap.twitter);
                    }

                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    img.setBitmap(bitmap);

                    Textma
                    marker = new MapMarker(geo, img);
                    marker.addOnClickListener("onclick", new View.OnClickListener(){

                    });

                    map.addMapObject(marker);

                    // Set the map center to the Berlin regio^n (no animation)
                    map.setCenter(geo, Map.Animation.NONE);
                    // Set the zoom level to the average between min and max
                    map.setZoomLevel(
                            (map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }
            }
        });
    }

}
