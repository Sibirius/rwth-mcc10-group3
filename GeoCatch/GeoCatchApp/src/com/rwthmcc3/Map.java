package com.rwthmcc3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class Map extends MapActivity{

	private static String LOGTAG = "Map";
	
	private MapView mapView;
	private MapController mapController;
	
	private String provider = "";
	
	private MyOverlay myPositionOverlay = null;
	private MyOverlay myStartPositionOverlay = null;
	private MyOverlay myTargetPositionOverlay = null;
	private MyOverlay myHunterPositionOverlay = null;
	private MyOverlay myPowerUpPositionOverlay = null;
	
	final int NUMOFPOWERUPS = 4;
	private boolean[] powerUpEnabled = new boolean[NUMOFPOWERUPS]; 
	private long[] powerUpTime = new long[NUMOFPOWERUPS]; 
	// 0 - show hunter
	// 1 - hide target

	private String[] powerUpMessage = new String[NUMOFPOWERUPS];
	
	private GeoPoint prePoint = null;
	private GeoPoint targetPoint = null;
	private GeoPoint powerupPoint = null;
	private GeoPoint hunterPoint = null;
	
	private Player player;
	private boolean gameFinished;	
	
	private LocationManager lm;	
	private GeoUpdateHandler geoUpdater;
	
	private long startTimeMillis;
	
	private GeoPointList pointList; 
	
	// MapView canvas should be redrawn in the UI thread only
    // Need handler for callbacks to the UI thread
    final Handler mHandler = new Handler();

    // Create runnable for updating markers
    final Runnable mUpdateMarkers = new Runnable() {
        public void run() {
            updateMarkers();
        }
    };
    
    // Create runnable for closing mapview when game has finished
    final Runnable mWinLoose = new Runnable() {
        public void run() {
            winLooseAlert();
        }
    };
    
    // Create runnable for closing mapview when game has stopped
    final Runnable mClose = new Runnable(){
    	public void run(){
    		gameClosedAlert();
    	}
    };
    
    // Create runnable for disabling powerUps
    final Runnable mDisablePowerUp = new Runnable(){
    	public void run(){
    		SystemClock.sleep(185000);
    		long time = System.currentTimeMillis() - 180000;
    		for(int i = 0; i < NUMOFPOWERUPS; i++){
    			if(powerUpTime[i] <= time) powerUpEnabled[i] = false;
    		}
    	}
    };
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) && !gameFinished) {
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);	
		
		//init map
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);
		mapController = mapView.getController();
		mapController.setZoom(18);

		//Player sample data
		player = Player.getPlayer();

		//Toast.makeText(getApplicationContext(), player.getName(), Toast.LENGTH_LONG).show();
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		
		if(player != null){
			
			//init LocationManger
			lm = (LocationManager) getSystemService(LOCATION_SERVICE);

			if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
				provider = LocationManager.GPS_PROVIDER;
			} else {
				provider = LocationManager.NETWORK_PROVIDER;			
			}
			
			geoUpdater = new GeoUpdateHandler();
			lm.getLastKnownLocation(provider);
			lm.requestLocationUpdates(provider, 0, 0, geoUpdater);
			
			//init powerUps
			powerUpMessage[0] = "Du kannst jetzt für drei Minuten deinen Jäger in gelb sehen!";
			powerUpMessage[1] = "Du kannst dein Ziel nicht mehr sehen!";
			
			gameLoop();
			
		} else {
			Toast.makeText(getApplicationContext(), "Übertragung der Spieldaten schlug fehl!", Toast.LENGTH_LONG).show();
			Map.this.finish();
		}
	}	
	
	//background thread updating player/target position
    protected void gameLoop() {

    	startTimeMillis = System.currentTimeMillis();
    	welcomeAlert();
    	pointList = new GeoPointList();
    	gameFinished = false;
    	
        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread t = new Thread() {
            public void run() {
            	while(true){
            		
            		if(gameFinished) break;
            		
            		synchronized(player){
		            	if(player != null) {
		            		if(Integrator.playerUpdateState(player)){ 
			            		if(player.getTargetLat() != 0.0 || player.getTargetLong() != 0.0){
			            			targetPoint = new GeoPoint((int) ((player.getTargetLat()) * 1E6),(int) ((player.getTargetLong()) * 1E6));		            		
			            		}
			            		if(player.getHunterLat() != 0.0 || player.getHunterLong() != 0.0){
			            			hunterPoint = new GeoPoint((int) ((player.getHunterLat()) * 1E6), (int) ((player.getHunterLong()) * 1E6));
			            		}
		            		}
		            	}
            		}
            		
	                mHandler.post(mUpdateMarkers);

	                synchronized(player){
	            		if(player != null && player.getMyGame() != null && player.getMyGame().getState() == 3){ //game has finished
	            			mHandler.post(mWinLoose);
	            			break;
	            		}
	            		
	            		if(player != null && player.getMyGame() != null && player.getMyGame().getState() == 2){ //game has stopped
	            			mHandler.post(mClose);
	            			break;
	            		}
	                }
	                
            		SystemClock.sleep(30000);
            		
            	}
            	
            }
        };
        t.start();
        
    }
			
    //redraw markers on map
	private void updateMarkers(){
		Log.d(LOGTAG, "updateMarkers()");
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		
		synchronized(player){
			
			if(player.getLatitude() != 0.0 || player.getLongitude()!= 0.0){
				
				//TODO: if game mode == 1
				if(powerupPoint == null) setNewPowerUpMarker();
				
				GeoPoint point = new GeoPoint((int) (player.getLatitude() * 1E6),(int) (player.getLongitude() * 1E6));
				
				if(pointList.size() == 0){
					pointList.add(point);
				} else {
					if(pointList.get(pointList.size()-1).getLatitudeE6() != point.getLatitudeE6() || pointList.get(pointList.size()-1).getLongitudeE6() != point.getLongitudeE6()){
						pointList.add(point);
					}
				}
				
				if(prePoint == null || point.getLatitudeE6() != prePoint.getLatitudeE6() || point.getLongitudeE6() != prePoint.getLongitudeE6()){
					if(myStartPositionOverlay == null || prePoint == null){
						myStartPositionOverlay = new MyOverlay(point,null,R.drawable.point_blue);
						mapOverlays.add(myStartPositionOverlay);
						
						prePoint = point;
					} else if(myPositionOverlay == null){
						mapOverlays.remove(myStartPositionOverlay);
						mapOverlays.add(new MyOverlay(prePoint,point,R.drawable.start));
						myPositionOverlay = new MyOverlay(point,null,R.drawable.point_blue);
						mapOverlays.add(myPositionOverlay);
						
						prePoint = point;
					} else {
						mapOverlays.remove(myPositionOverlay);
						mapOverlays.add(new MyOverlay(prePoint,point,R.drawable.point));
						myPositionOverlay = new MyOverlay(point,null,R.drawable.point_blue);
						mapOverlays.add(myPositionOverlay);	
						
						prePoint = point;						
					}
					
					//mapView.getController().animateTo(point);
					
					if(!powerUpEnabled[1] && targetPoint != null){
						if(myTargetPositionOverlay != null) mapOverlays.remove(myTargetPositionOverlay);
						myTargetPositionOverlay = new MyOverlay(targetPoint,null,R.drawable.point_red);
						mapOverlays.add(myTargetPositionOverlay);
					}
					
					if(powerupPoint != null && myPowerUpPositionOverlay != null) mapOverlays.remove(myPowerUpPositionOverlay);
						myPowerUpPositionOverlay = new MyOverlay(powerupPoint,null,R.drawable.point_green);
						mapOverlays.add(myPowerUpPositionOverlay);
					
					if(hunterPoint != null && powerUpEnabled[0]){
						if(myHunterPositionOverlay != null) mapOverlays.remove(myHunterPositionOverlay);
						myHunterPositionOverlay = new MyOverlay(hunterPoint,null,R.drawable.point_yellow);
						mapOverlays.add(myHunterPositionOverlay);
					}
					
					mapView.postInvalidate();
					
				}
			}
		}
	}
	
	private String getFormattetTotalGameTime(){
		int time = (int) (System.currentTimeMillis() - startTimeMillis) / 1000;
		int mins = time / 60;
		int secs = time % 60;
		
		return mins + " Min, " + secs + " Sek";
	}
	
	private void welcomeAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  builder.setMessage("Hallo " + player.getName() + "!\n" +
		  		"Willkommen im Spiel!\n\n" +
		  		"Der >blaue< Punkt auf der Karte ist deine aktuelle Position. " +
		  		"Diese wird laufend aktualisiert! " +
		  		((player != null && player.getMyGame() != null && player.getMyGame().getMode() == 1) ?
		  		"Deine Aufgabe ist es nun, die Person, die " +
		  		"sich hinter dem >roten< Punkt auf der Karte verbirgt, zu fangen!\n" + 
		  		"Aber Achtung:\n" +
		  		"Auch du wirst gejagt!" : "Deine Aufgabe ist es nun, dich zur Stelle, auf die " +
		  		"der >rote< Punkt zeigt, zu bewegen!\n"))
		         .setCancelable(false)
		         .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int id) {
		                  dialog.cancel();
		             }
		         });
		  final AlertDialog welcomeAlert = builder.create();
		  welcomeAlert.show();
	}
	
	private void winLooseAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  builder.setMessage("Du hast " + (player.isHasWin() ? "gewonnen" : "verloren") + "\n" +
		  		"\n" +
		  		"Zurückgelegte Distanz: " + pointList.getDistance() + "m\n" +
		  		"Spielzeit: " + getFormattetTotalGameTime())
		         .setCancelable(false)
		         .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int id) {
		                  dialog.cancel();
		                  lm.removeUpdates(geoUpdater);
		                  gameFinished = true;
		             }
		         });
		  final AlertDialog winLooseAlert = builder.create();
		  winLooseAlert.show();
	}
	
	private void gameClosedAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  builder.setMessage("Das Spiel wurde beendet!")
		         .setCancelable(false)
		         .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int id) {
		                  dialog.cancel();
		                  lm.removeUpdates(geoUpdater);
		                  gameFinished = true;
		             }
		         });
		  final AlertDialog winLooseAlert = builder.create();
		  winLooseAlert.show();
	}
	
	private void closeMapView(){
		if(!gameFinished) lm.removeUpdates(geoUpdater);
		gameFinished = true;
        Map.this.finish();	
	}
	
	/**********************************************************************/
	/**                           in game menu                           **/
	/**********************************************************************/
	
	/**
	 *  Creates the menu items. 
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.in_game_options_menu, menu);
	    return true;
	}

	/** 
	 * Handles item selections 
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.in_game_options_menu_leave:
			Integrator.leaveGame(player);
			Toast.makeText(getApplicationContext(), "Du hast das Spiel verlassen", Toast.LENGTH_LONG).show();
			closeMapView();
			return true;						
		
		case R.id.in_game_options_menu_help:
			startActivityForResult(new Intent(this.getApplicationContext(),
					com.rwthmcc3.Help.class), 0);
			overridePendingTransition(R.anim.fade, R.anim.hold);
			return true;
		}
		return true;
	}

	/**********************************************************************/
	/**                       powerUp functions                          **/
	/**********************************************************************/
	
	private boolean gotPowerUp(){
		Log.d(LOGTAG, "gotPowerUP()");
		//float[] results = new float[1];
		//Location.distanceBetween(powerUpLat / 1E6, powerUpLng / 1E6, player.getLatitude() / 1E6, player.getLongitude() / 1E6, results);
		//if( results[0] < 15f) {
		//	return true;
		//}
		if( (int) ( powerupPoint.getLatitudeE6()  / 10 + 5) == (int) (player.getLatitude()  * 1E5 + 5) &&
			(int) ( powerupPoint.getLongitudeE6() / 10 + 5) == (int) (player.getLongitude() * 1E5 + 5) ){
			return true;
		}
		return false;
	}
	
	private void setNewPowerUpMarker(){
		Random randomGenerator = new Random();
		double[] res = Integrator.snapToStreet( player.getLatitude()  + (randomGenerator.nextDouble() - 0.5) / 100,
												player.getLongitude() + (randomGenerator.nextDouble() - 0.5) / 100);
		if(res != null){
			powerupPoint = new GeoPoint((int) (res[0] * 1E6),(int) (res[1] * 1E6));
			Log.d(LOGTAG, "setNewPowerUP(): " + res[0] + " " + res[1]);
		}
	
		
	}
	
	/**********************************************************************/
	/**                         additional classes                       **/
	/**********************************************************************/
	
	/*
	 * GeoLocation Update Handler
	 */
	public class GeoUpdateHandler implements LocationListener {

		public void onLocationChanged(Location location) {
			if(location != null){
				double lat = location.getLatitude();
				double lng = location.getLongitude();		
				synchronized (player){
					player.setLatitude(lat);
					player.setLongitude(lng);
					
					//TODO: if game mode == 1 && 
            		if(gotPowerUp()){
            			setNewPowerUpMarker();
            			Random randomGenerator = new Random();
            			int selectedPowerUp = randomGenerator.nextInt(NUMOFPOWERUPS);	            				
            			powerUpEnabled[selectedPowerUp] = true;	
            			powerUpTime[selectedPowerUp] = System.currentTimeMillis();
            			if(selectedPowerUp == 0){ //show hunter
            				Integrator.activatePowerup(0);
            			}
            			mHandler.post(mDisablePowerUp);
            			Toast.makeText(getApplicationContext(), "PowerUp erhalten: " + powerUpMessage[selectedPowerUp], Toast.LENGTH_LONG).show();       				
            		}
					
            		//mHandler.post(mUpdateMarkers);
            		
            		//Toast.makeText(getApplicationContext(), "Lat: "+lat+"\nLng: "+lng, Toast.LENGTH_LONG).show();
				}
			}
		}
	

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
	
	/*
	 * custom overlay item class
	 */
	private class MyOverlay extends Overlay{
		int markerId;
		GeoPoint point1;
		GeoPoint point2;
		
		public MyOverlay(GeoPoint gP1, GeoPoint gP2, int mId){
			markerId = mId;
			point1 = gP1;
			point2 = gP2;
		}
		
	    @Override
	    public void draw(Canvas canvas, MapView mapView, boolean shadow) {

	        super.draw(canvas, mapView, shadow);
	    	
	        if(point1 == null) return;
	        
	        Paint paint=new Paint();
	        paint.setDither(true);
	        paint.setColor(Color.BLACK);
	        paint.setStrokeWidth(2);
	        DrawFilter drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG);
	        canvas.setDrawFilter(drawFilter);
	        
	        Point p1=new Point();	
	        mapView.getProjection().toPixels(point1, p1);
	        int x1=p1.x;
	        int y1=p1.y;
	        
	        if(point2 != null){
		          
		        Point p2=new Point();
		        mapView.getProjection().toPixels(point2, p2);
		        int x2=p2.x;
		        int y2=p2.y;
        
		        canvas.drawLine(x1, y1, x2, y2, paint);
		        
	        }
	        Bitmap bmp = BitmapFactory.decodeResource(getResources(), markerId);            
	        canvas.drawBitmap(bmp, x1-5, y1-5, null);
	    }		
	}
	
	/*
	 * GeoPointList contains all geolocations visited by player
	 * also calculates distance between first and last location in the List
	 */
	private class GeoPointList extends ArrayList<GeoPoint>{

		private static final long serialVersionUID = 1L;
		private float distance = 0f;
		
		public boolean add(GeoPoint g){
			
			if(g != null){
				if(this.size() > 0){
					float[] results = new float[1];
					Location.distanceBetween(g.getLatitudeE6() / 1E6, g.getLongitudeE6() / 1E6, this.get(this.size()-1).getLatitudeE6() / 1E6, this.get(this.size()-1).getLongitudeE6() / 1E6, results);
					distance += results[0];
				}
				return super.add(g);
			} else {
				return false;
			}
			
		}
		
		public float getDistance(){
			return distance;
		}
		
	}
	
}