package com.rwthmcc3;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity{

	private MapView mapView;
	private MapController mapController;
	
	private List<Overlay> mapOverlays;
	private Drawable drawable;
	private PositionMarkerOverlay itemizedOverlay;
	
	private GeoPoint prePoint = null;

	LocationManager lm;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);		
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(12);

		drawable = this.getResources().getDrawable(R.drawable.androidmarker);
		
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new GeoUpdateHandler());

	}	
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	public class GeoUpdateHandler implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);		
			
			GeoPoint point = new GeoPoint(lat, lng);
			OverlayItem overlayitem = new OverlayItem(point, "", "");

			mapOverlays = mapView.getOverlays();
			itemizedOverlay = new PositionMarkerOverlay(prePoint, point, drawable);
			
			itemizedOverlay.addOverlay(overlayitem);
			mapOverlays.add(itemizedOverlay);
			
			mapController.animateTo(point);		
			
			prePoint = point;

		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	
}