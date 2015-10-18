package de.girlsgeek.vibex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;

import java.util.List;


public class MapActivity extends Activity implements MapGesture.OnGestureListener, MapMarker.OnDragListener {


    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private MapFragment mapFragment = null;


    private MapMarker marker = null;

    private int markerCounter = 10;
    private GeoCoordinate geo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ImageView berlinImg = (ImageView) findViewById(R.id.berlinImg);
        ImageView potsdamImg = (ImageView) findViewById(R.id.potsdamImg);

        View.OnClickListener picClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), VibexActivity.class);
                if(v.getId() == R.id.berlinImg){
                    intent.putExtra("city", "Berlin");
                } else if(v.getId() == R.id.potsdamImg){
                    intent.putExtra("city", "Potsdam");
                }
                startActivity(intent);
            }
        };

        berlinImg.setOnClickListener(picClickListener);
        potsdamImg.setOnClickListener(picClickListener);


        // Search for the map fragment to finish setup by calling init().
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(
                R.id.mapfragment);

        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(
                    OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the map fragment
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();

                    GeoCoordinate geo_1 = new GeoCoordinate(52.53, 13.38);
                    GeoCoordinate geo_2 = new GeoCoordinate(52.396070, 13.057117);
                    GeoCoordinate geo_3 = new GeoCoordinate(52.748615, 13.251458);
                    Image img = new Image();
                    Drawable drawable = getResources().getDrawable(R.drawable.loc_picker);


                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    img.setBitmap(bitmap);

                    marker = new MapMarker(geo_1, img);
                    marker.setTitle("Berlin");
                    map.addMapObject(marker);

                    marker = new MapMarker(geo_2, img);
                    marker.setTitle("Potsdam");
                    map.addMapObject(marker);

                    marker = new MapMarker(geo_3, img);
                    marker.setTitle("Oranienburg");
                    map.addMapObject(marker);

                    // Set the map center to the Berlin region (no animation)
                    map.setCenter(geo_1, Map.Animation.NONE);

                    // Set the zoom level to the average between min and max
                    map.setZoomLevel(
                            (map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);

                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }
            }
        });

    }

    @Override
    public void onPanStart() {

    }

    @Override
    public void onPanEnd() {

    }

    @Override
    public void onMultiFingerManipulationStart() {

    }

    @Override
    public void onMultiFingerManipulationEnd() {

    }

    @Override
    public boolean onMapObjectsSelected(List<ViewObject> objects) {
        // TODO Auto-generated method stub
        System.out.println("onMapObjectsSelected");
        for (ViewObject viewObject : objects) {
            if (viewObject.getBaseType() == ViewObject.Type.USER_OBJECT) {
                System.out.println("onMapObjectsSelected Object");
                MapObject mapObject = (MapObject) viewObject;

                if (mapObject.getType() == MapObject.Type.MARKER) {
                    System.out.println("onMapObjectsSelected Marker");
                    MapMarker marker = (MapMarker) mapObject;
                    if (marker != null) {
                        marker.showInfoBubble();
                    }
                    return false;
                }
            }
        }

        return false;
    }

    @Override
    public boolean onTapEvent(PointF pointF) {
        System.out.println("onTapEvent");
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(PointF pointF) {
        System.out.println("onDoubleTapEvent");
        return false;
    }

    @Override
    public void onPinchLocked() {

    }

    @Override
    public boolean onPinchZoomEvent(float v, PointF pointF) {
        return false;
    }

    @Override
    public void onRotateLocked() {

    }

    @Override
    public boolean onRotateEvent(float v) {
        return false;
    }

    @Override
    public boolean onTiltEvent(float v) {
        return false;
    }

    @Override
    public boolean onLongPressEvent(PointF pointF) {
        return false;
    }

    @Override
    public void onLongPressRelease() {

    }

    @Override
    public boolean onTwoFingerTapEvent(PointF pointF) {
        return false;
    }

    private void callNextActivity(String city) {
        Intent intent = new Intent(this, VibexActivity.class);
        intent.putExtra("city", city);
        startActivity(intent);
    }

    @Override
    public void onMarkerDrag(MapMarker mapMarker) {
        System.out.println("onMarkerDrag");
    }

    @Override
    public void onMarkerDragEnd(MapMarker mapMarker) {
        System.out.println("onMarkerDragEnd");
    }

    @Override
    public void onMarkerDragStart(MapMarker mapMarker) {
        System.out.println("onMarkerDragStart");
    }
}
