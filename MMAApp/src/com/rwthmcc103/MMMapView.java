package com.rwthmcc103;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class MMMapView extends MapActivity {
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	List<Overlay> mapOverlays;
	Drawable drawable;
	MapView mapView;
	MMItemizedOverlay itemizedOverlay;
	MapController mapController;
	GeoPoint thePoint;
	Bundle extras;
	
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.mmmapview);
                        
        mapView = (MapView) findViewById(R.id.mmmap);
        mapView.setBuiltInZoomControls(true);
        
        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.marker);
        itemizedOverlay = new MMItemizedOverlay(drawable);
        mapController = mapView.getController();
                
        extras = this.getIntent().getExtras();

        // center point
        thePoint = new GeoPoint(	(int)(Double.valueOf(extras.getString("lat"))*1000000),
        		(int)(Double.valueOf(extras.getString("lon"))*1000000));
        
        mapController.setCenter(thePoint);
        OverlayItem overlayitem = new OverlayItem(thePoint, "", "");

        itemizedOverlay.removeMarkers();
        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedOverlay);
    }      
            
    @Override
	public void onBackPressed() {    	    	
    	if (extras.getBoolean("edit")) {
    		Intent result = new Intent();
    		result.putExtra("lat", Double.toString(thePoint.getLatitudeE6()/1000000.0));
    		result.putExtra("lon", Double.toString(thePoint.getLongitudeE6()/1000000.0));
    	    	
    		setResult(RESULT_OK, result);
    	}
    	
		super.onBackPressed();
	}

	public void onLocationChanged(Location location) {
    	if (extras.getBoolean("edit")) {    	
	    	if (location != null) {
	              double lat = location.getLatitude();
	              double lng = location.getLongitude();
	              thePoint = new GeoPoint((int) lat * 1000000, (int) lng * 1000000);
	              
	              mapController.animateTo(thePoint);
	                            
	              OverlayItem overlayitem = new OverlayItem(thePoint, "", "");              
	              itemizedOverlay.removeMarkers();
	              itemizedOverlay.addOverlay(overlayitem);              
	        }
    	}
	}
	
	public class MMItemizedOverlay extends ItemizedOverlay {
    	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    	
    	public MMItemizedOverlay(Drawable defaultMarker) {
    		super(boundCenterBottom(defaultMarker));
    	}

    	public void addOverlay(OverlayItem overlay) {
    	    mOverlays.add(overlay);
    	    populate();
    	}
    	
    	public void removeMarkers() {
    		mOverlays.clear();
    	}
    	
    	@Override
    	protected OverlayItem createItem(int i) {
    	  return mOverlays.get(i);
    	}

    	@Override
    	public int size() {
    		return mOverlays.size();
    	}
    }

}
