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
	
	private String provider = "";
	
	private List<Overlay> mapOverlays;
	private Drawable startMarker;
	private Drawable marker;
	private Drawable curPosMarker;
	private Drawable targetMarker;
	private Drawable powerUpMarker;
	private PositionMarkerOverlay itemizedOverlay;
	private PositionMarkerOverlay targetOverlay;
	private PositionMarkerOverlay powerUpOverlay;	
	
	private GeoPoint prePoint = null;
	private GeoPoint prePrePoint = null;
	
	LocationManager lm;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);		
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(12);

		startMarker = this.getResources().getDrawable(R.drawable.start);
		marker = this.getResources().getDrawable(R.drawable.point);
		curPosMarker = this.getResources().getDrawable(R.drawable.point_blue);
		targetMarker = this.getResources().getDrawable(R.drawable.point_red);
		powerUpMarker = this.getResources().getDrawable(R.drawable.point_yellow);
		
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);

		if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			provider = LocationManager.GPS_PROVIDER;
		} else {
			provider = LocationManager.NETWORK_PROVIDER;			
		}
		
		lm.getLastKnownLocation(provider);
		lm.requestLocationUpdates(provider, 0, 0, new GeoUpdateHandler());
		gameLoop();
	}	
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	private void gameLoop(){
		updateTargetPosition(54654321,6654321);
		updatePowerUpPosition(54123456,6123456);	
	}
	
	private void updateTargetPosition(int lat, int lng){
		//TODO: Get target position from server
		GeoPoint point = new GeoPoint(lat, lng);
		
		mapOverlays = mapView.getOverlays();
		if(targetOverlay != null) mapOverlays.remove(targetOverlay);
		targetOverlay = new PositionMarkerOverlay(null, null, targetMarker);
		targetOverlay.addOverlay(new OverlayItem(point, "", ""));
		mapOverlays.add(targetOverlay);		
	}
	
	private void updatePowerUpPosition(int lat, int lng){
		//TODO: Get powerup position from server

		GeoPoint point = new GeoPoint(lat, lng);
		
		mapOverlays = mapView.getOverlays();
		if(powerUpOverlay != null) mapOverlays.remove(powerUpOverlay);
		powerUpOverlay = new PositionMarkerOverlay(null, null, powerUpMarker);
		powerUpOverlay.addOverlay(new OverlayItem(point, "", ""));
		mapOverlays.add(powerUpOverlay);		
	}	
	
	public class GeoUpdateHandler implements LocationListener {

		public void onLocationChanged(Location location) {
			if(location != null){
				int lat = (int) (location.getLatitude() * 1E6);
				int lng = (int) (location.getLongitude() * 1E6);		
				
				GeoPoint point = new GeoPoint(lat, lng);
				//TODO: Save location in player class / send it to server
				
				mapOverlays = mapView.getOverlays();
				if(prePoint == null ){
					itemizedOverlay = new PositionMarkerOverlay(null, null, startMarker);
				} else {
					if(prePrePoint != null){
						if(itemizedOverlay != null) mapOverlays.remove(itemizedOverlay);
						PositionMarkerOverlay itemizedOverlay2 = new PositionMarkerOverlay(prePrePoint, prePoint, marker);
						itemizedOverlay2.addOverlay(new OverlayItem(prePoint, "", ""));
						mapOverlays.add(itemizedOverlay2);
					}
					itemizedOverlay = new PositionMarkerOverlay(prePoint, point, curPosMarker);
				}
				itemizedOverlay.addOverlay(new OverlayItem(point, "", ""));
				mapOverlays.add(itemizedOverlay);
				
				mapController.animateTo(point);		
				
				prePrePoint = prePoint;
				prePoint = point;
			}
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	
}