package com.rwthmcc103;

import android.os.Bundle;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class MMMapView extends MapActivity {
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.mmmapview);
        
        MapView mapView;
        
        mapView = (MapView) findViewById(R.id.mmmap);
        mapView.setBuiltInZoomControls(true);
    }
    
    
}
