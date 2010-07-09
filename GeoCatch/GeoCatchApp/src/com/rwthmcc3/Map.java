package com.rwthmcc3;

import java.util.ConcurrentModificationException;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity{

	private static String LOGTAG = "Map";
	
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
	
	private Player player;
	
	Handler mHandler;
	
	LocationManager lm;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);	
				
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(5);
		mapView.invalidate();
		mapOverlays = mapView.getOverlays();

		//Player sample data
		player = Player.getPlayer();
		if (player == null){
			player.setKey("ahFyd3RoLW1jYzEwLWdyb3VwM3IOCxIGUGxheWVyGMncAww");
			player.setMac("34:34:34:24:24:24");
			player.setName("tesspieler");
			player.setCreator(true);
			Game myGame = new Game();
			myGame.setKey("ahFyd3RoLW1jYzEwLWdyb3VwM3IMCxIER2FtZRix5AMM");
			myGame.setName("tollestestspiel");
			myGame.setMaxPlayersCount(1);
			myGame.setVersion(1);
			myGame.setState(1); //game started
			myGame.setMode(0); //single player
			player.setMyGame(myGame);
		}
		Toast.makeText(getApplicationContext(), player.getKey(), Toast.LENGTH_LONG).show();
		
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
		mHandler = new Handler();
		lm.getLastKnownLocation(provider);
		lm.requestLocationUpdates(provider, 0, 0, new GeoUpdateHandler());
		if(player != null){
			gameLoop();
		} else {
			Map.this.finish();
		}
	}	
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	private void gameLoop(){
		
		// create a thread for updating target location
		final Thread background = new Thread (new Runnable() {
			public void run() {
				try {
					 
					while(true){
						if(player != null) Integrator.playerUpdateState(player);
						GeoPoint targetPoint = new GeoPoint((int) ((player.getTargetLat()) * 1E6),(int) ((player.getTargetLong()) * 1E6));
						updateTargetPosition(targetPoint);
						
						//TODO: Get powerup Position (and Type) from Server
						//GeoPoint powerupPoint = new GeoPoint(5454321,654321);
						//updatePowerUpPosition(powerupPoint);

						if(player != null && player.getMyGame() != null && player.getMyGame().getState() == 3){ //game finished
							winLooseAlert();
							break;
						}
						
						Thread.sleep(5000); //sleep 5 secs 
					}
	                 	
				} catch (java.lang.InterruptedException e) {
	                 // if something fails do something smart
				}
			}
	         
		});
		
	    mHandler.post(new Runnable() { // implement the Runnable interface
            public void run() {
            	background.start();
            }
        }); 
		
	    
	}
	
	private void winLooseAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  builder.setMessage("Du hast " + (player.getName().equals("gewonnen") ? "gewonnen" : "verloren"))
		         .setCancelable(false)
		         .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int id) {
		                  dialog.cancel();
		                  Map.this.finish();
		             }
		         });
		  final AlertDialog winLooseAlert = builder.create();
	}
	
	private void updateTargetPosition(GeoPoint point){
		Log.d(LOGTAG, "updateTargetPosition()");
		synchronized (mapOverlays) {
			if(targetOverlay != null) mapOverlays.remove(targetOverlay);
			targetOverlay = new PositionMarkerOverlay(null, null, targetMarker);
			targetOverlay.addOverlay(new OverlayItem(point, "", ""));
			mapOverlays.add(targetOverlay);
			mapView.postInvalidate();
		}
	}
	
	private void updatePowerUpPosition(GeoPoint point){
		synchronized (mapOverlays) {
			if(powerUpOverlay != null) mapOverlays.remove(powerUpOverlay);
			powerUpOverlay = new PositionMarkerOverlay(null, null, powerUpMarker);
			powerUpOverlay.addOverlay(new OverlayItem(point, "", ""));
			mapOverlays.add(powerUpOverlay);	
			mapView.postInvalidate();			
		}
	}	
	
	public class GeoUpdateHandler implements LocationListener {

		public void onLocationChanged(Location location) {
			if(location != null){
				int lat = (int) (location.getLatitude() * 1E6);
				int lng = (int) (location.getLongitude() * 1E6);		
				
				GeoPoint point = new GeoPoint(lat, lng);
				player.setLatitude(lat);
				player.setLongitude(lng);
				if(player != null) Integrator.playerUpdateState(player);
				Toast.makeText(getApplicationContext(), "Lat: "+lat+"\nLng: "+lng, Toast.LENGTH_LONG).show();
				
				synchronized (mapOverlays) {
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
					mapView.postInvalidate();
				}
				
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