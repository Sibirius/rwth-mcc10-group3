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
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/*
 * Map Activity
 */
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
	
	final int NUMOFPOWERUPS = 2;
	private boolean[] powerUpEnabled = new boolean[NUMOFPOWERUPS]; 
	private long[] powerUpTime = new long[NUMOFPOWERUPS]; 
	// 0 - show hunter
	// 1 - hide target

	private String[] powerUpMessage = new String[NUMOFPOWERUPS];
	
	private int selectedPowerUp = 0;
	private long powerUpSetTime = 0;
	
	private GeoPoint prePoint = null;
	private GeoPoint targetPoint = null;
	private GeoPoint powerUpPoint = null;
	private GeoPoint hunterPoint = null;
	
	private Player player;
	private boolean gameFinished = false;	
	
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
    
    // Create runnable for creating a powerup enabled notification
    final Runnable mPowerUpEnabledNotification = new Thread(){
    	public void run(){
    		//Toast.makeText(getApplicationContext(), "PowerUp erhalten: " + powerUpMessage[selectedPowerUp], Toast.LENGTH_LONG).show();
    		powerUpAlert();
    	}
    };
    
    // Create runnable for creating a powerup disabled notification
    final Runnable mPowerUpDisabledNotification = new Thread(){
    	public void run(){
    		Toast.makeText(getApplicationContext(), "PowerUp wird deaktiviert!", Toast.LENGTH_LONG).show();		
    	}
    };

    // Create runnable for creating a powerup reset notification
    final Runnable mPowerUpResetNotification = new Thread(){
    	public void run(){
    		Toast.makeText(getApplicationContext(), "PowerUp wird verschoben!", Toast.LENGTH_LONG).show();		
    	}
    };
    
    /*
     * key events
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	//disable back key
        if (keyCode == KeyEvent.KEYCODE_BACK && !gameFinished) {
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    
	/*
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	
	/* 
	 * initiates map and gps provider
	 * @see com.google.android.maps.MapActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);	
		
		//set title
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        TextView leftTitle = (TextView)findViewById(R.id.left_text);
        TextView rightTitle = (TextView)findViewById(R.id.right_text);
        leftTitle.setText("GeoCatch");
        rightTitle.setText("Map");
		
		//init map
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);
		mapController = mapView.getController();
		mapController.setZoom(18);

		//get Player
		player = Player.getPlayer();
		
		//keep screen on
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
			//lm.getLastKnownLocation(provider);
			lm.requestLocationUpdates(provider, 10000, 5, geoUpdater);
			
			//init powerUps
			powerUpMessage[0] = "Du kannst jetzt für drei Minuten deinen Jäger in gelb sehen!";
			powerUpMessage[1] = "Du kannst dein Ziel nicht mehr sehen!";
			
			//reset player location
			player.setLatitude(0.0);
			player.setLongitude(0.0);
			
			gameLoop();
			
		} else {
			Toast.makeText(getApplicationContext(), "Übertragung der Spieldaten schlug fehl!", Toast.LENGTH_LONG).show();
			Map.this.finish();
		}
	}	
	
	
    /**
     *	background thread updating player/target position 
     */
    protected void gameLoop() {

    	startTimeMillis = System.currentTimeMillis();
    	powerUpSetTime = startTimeMillis;
    	if(!gameFinished) welcomeAlert();
    	pointList = new GeoPointList();
    	
        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread t = new Thread() {
            public void run() {
            	int cnt = 0;
            	long time;            	
            	
            	while(true){
            		
            		if(gameFinished) break;
            		
	            	synchronized(player){
	            		if(cnt == 0){
	            			
	            			//sync player data with backend service
			            	if(player != null) {
			            		if(player.getLatitude() != 0.0 && player.getLongitude() != 0.0){
				            		if(Integrator.playerUpdateState(player)){
				            			//update targetpoint
					            		if(player.getTargetLat() != 0.0 || player.getTargetLong() != 0.0){
					            			targetPoint = new GeoPoint((int) ((player.getTargetLat()) * 1E6),(int) ((player.getTargetLong()) * 1E6));		            		
					            		}
					            		//update hunterpoint
					            		if(player.getHunterLat() != 0.0 || player.getHunterLong() != 0.0){
					            			hunterPoint = new GeoPoint((int) ((player.getHunterLat()) * 1E6), (int) ((player.getHunterLong()) * 1E6));
					            		}
				            		}
					            		
					            	//player got powerUp
				            		if(gotPowerUp()){
				            			powerUpPoint = null;
				            			if(player != null && player.getMyGame() != null && player.getMyGame().getMode() == 0){ //race
				            				selectedPowerUp = 1;
				            			} else { //catch
				            				Random randomGenerator = new Random();
				            				selectedPowerUp = randomGenerator.nextInt(NUMOFPOWERUPS);
				            			}
				            			powerUpEnabled[selectedPowerUp] = true;	
				            			powerUpTime[selectedPowerUp] = System.currentTimeMillis();
				            			if(selectedPowerUp == 0){ //show hunter
				            				if(!Integrator.activatePowerup(1)) Log.d(LOGTAG, "PowerUp Aktivierung fehlgeschlagen");
				            			}
				            			mHandler.post(mPowerUpEnabledNotification);
				            		}
				            		
				            		//disable PowerUps after 3mins
				            		time = System.currentTimeMillis() - 180000;
				            		for(int i = 0; i < NUMOFPOWERUPS; i++){
				            			if(powerUpEnabled[i]){
				            				Log.d(LOGTAG,"PowerUp will be disabled in " + (powerUpTime[i] - time) / 1000 + "s");
				            				if(powerUpTime[i] <= time) {
					            				powerUpEnabled[i] = false;
					            				mHandler.post(mPowerUpDisabledNotification);
				            				}
				            			}
				            		}
					            		
				            		//reset powerUp after 5mins
				            		time = System.currentTimeMillis() - 300000;
				            		Log.d(LOGTAG,"PowerUp will be reset in " + (powerUpSetTime - time) / 1000 + "s");
				            		if(powerUpSetTime <= time) {
				            			powerUpPoint = null;
				            			mHandler.post(mPowerUpResetNotification);
				            		}	            		
				            		
			            		}
			            	}	            		
		            		
		                }
	                            		
		            	
	            		//check if game has finished
		            	if(player != null && player.getMyGame() != null && player.getMyGame().getState() == 3){
	            			mHandler.post(mWinLoose);
			                lm.removeUpdates(geoUpdater);
			                gameFinished = true;
	            			break;
	            		}
	            		
		            	//check if game has stopped
	            		if(player != null && player.getMyGame() != null && player.getMyGame().getState() == 2){
	            			mHandler.post(mClose);
			                lm.removeUpdates(geoUpdater);
			                gameFinished = true;
	            			break;
	            		}
	            	
	            	}
	                
	                Log.d(LOGTAG, "gameLoop() -> updateMarkers()");
	                mHandler.post(mUpdateMarkers);
	                
	                //cnt = (cnt+1) % 3;
	                
            		SystemClock.sleep(10000);
            		
            	}    	
            	
            }
        };
        t.start();
        
    }
			
	/**
	 * redraw marker overlays on map
	 */
	private void updateMarkers(){
		Log.d(LOGTAG, "updateMarkers()");
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		
		synchronized(player){
			
			if(player.getLatitude() != 0.0 || player.getLongitude()!= 0.0){
				
				if(powerUpPoint == null) setNewPowerUpMarker();
				
				//geopoint for current location
				GeoPoint point = new GeoPoint((int) (player.getLatitude() * 1E6),(int) (player.getLongitude() * 1E6));
				
				if(pointList.size() == 0){
					pointList.add(point);
				} else {
					if(pointList.get(pointList.size()-1).getLatitudeE6() != point.getLatitudeE6() || pointList.get(pointList.size()-1).getLongitudeE6() != point.getLongitudeE6()){
						pointList.add(point);
					}
				}
				
				//draw path and special markers for first and current position
				if(prePoint == null || point.getLatitudeE6() != prePoint.getLatitudeE6() || point.getLongitudeE6() != prePoint.getLongitudeE6()){
					if(myStartPositionOverlay == null || prePoint == null){
						myStartPositionOverlay = new MyOverlay(point,null,R.drawable.point_blue);
						mapOverlays.add(myStartPositionOverlay);
						mapView.getController().animateTo(point);
						
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
					
					Log.d(LOGTAG, "new Marker created at " + point.getLatitudeE6() + ", " + point.getLongitudeE6() );
					
					//mapView.getController().animateTo(point);
					
					//add target overlay
					if(myTargetPositionOverlay != null) mapOverlays.remove(myTargetPositionOverlay);
					if(!powerUpEnabled[1] && targetPoint != null){
						myTargetPositionOverlay = new MyOverlay(targetPoint,null,R.drawable.point_red);
						mapOverlays.add(myTargetPositionOverlay);
					}
					
					//add powerUp overlay
					if(myPowerUpPositionOverlay != null) mapOverlays.remove(myPowerUpPositionOverlay);
					if(powerUpPoint != null){
						myPowerUpPositionOverlay = new MyOverlay(powerUpPoint,null,R.drawable.point_green);
						mapOverlays.add(myPowerUpPositionOverlay);
					}						
					
					//add hunter overlay if powerUp is enabled
					if(myHunterPositionOverlay != null) mapOverlays.remove(myHunterPositionOverlay);
					if(hunterPoint != null && powerUpEnabled[0]){
						myHunterPositionOverlay = new MyOverlay(hunterPoint,null,R.drawable.point_yellow);
						mapOverlays.add(myHunterPositionOverlay);
					}
					
					//redraw map
					mapView.invalidate();
					
				}
			}
		}
	}
	
	/**
	 * calculates game duration
	 * @return game duration in minutes and seconds as a String  
	 */
	private String getFormattetTotalGameTime(){
		int time = (int) (System.currentTimeMillis() - startTimeMillis) / 1000;
		int mins = time / 60;
		int secs = time % 60;
		
		return mins + " Min, " + secs + " Sek";
	}
	
	/**********************************************************************/
	/**                               alerts                             **/
	/**********************************************************************/
	
	/**
	 * alert to say hello to the player and describe what to do
	 */
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
	
	/**
	 * alert shown if game has been finished
	 */
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
		             }
		         });
		  final AlertDialog winLooseAlert = builder.create();
		  winLooseAlert.show();
	}
	
	/**
	 * alert shown if game has been stopped
	 */
	private void gameClosedAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  builder.setMessage("Das Spiel wurde beendet!")
		         .setCancelable(false)
		         .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int id) {
		                  dialog.cancel();
		             }
		         });
		  final AlertDialog gameClosedAlert = builder.create();
		  gameClosedAlert.show();
	}

	/**
	 * alert to ensure that the user really wants to close the map
	 */
	private void mapCloseAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  builder.setMessage("Möchtest du das Spiel wirklich verlassen?")
		         .setCancelable(true)
		         .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int id) {
		                  dialog.cancel();
		             }
		         })
		         .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int id) {
		                  dialog.cancel();
			      		  Integrator.leaveGame(player);
			    		  Toast.makeText(getApplicationContext(), "Du hast das Spiel verlassen", Toast.LENGTH_LONG).show();
			    		  closeMapView();
		             }
		         });
		  final AlertDialog mapCloseAlert = builder.create();
		  mapCloseAlert.show();
	}
	
	/**
	 * removes geo location listener, terminates main thread and finally finishes the map activity
	 */
	private void closeMapView(){
		if(!gameFinished) {
			lm.removeUpdates(geoUpdater);
		}
		gameFinished = true;
        Map.this.finish();	
	}

	/**
	 * alert shown when player got a powerUp
	 */
	private void powerUpAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  builder.setMessage("PowerUp erhalten: " + powerUpMessage[selectedPowerUp])
		         .setCancelable(false)
		         .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int id) {
		                  dialog.cancel();
		             }
		         });
		  final AlertDialog powerUpAlert = builder.create();
		  powerUpAlert.show();
	}
	
	/**********************************************************************/
	/**                           in game menu                           **/
	/**********************************************************************/
	
	/** 
	 * Creates the menu items. 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.in_game_options_menu, menu);
	    return true;
	}

	/** 
	 * Handles menu item selections 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.in_game_options_menu_leave:
			if(!gameFinished){
				mapCloseAlert();
			} else {
				Map.this.finish();				
			}
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
	
	/**
	 * checks if distance between user and powerUp is lower than 20 meters
	 * @return true if player got a powerUp, else false
	 */
	private boolean gotPowerUp(){
		if(powerUpPoint != null && (player.getLatitude() != 0.0 || player.getLongitude() != 0.0)){
			float[] results = new float[1];
			Location.distanceBetween(powerUpPoint.getLatitudeE6() / 1E6, powerUpPoint.getLongitudeE6() / 1E6, player.getLatitude(), player.getLongitude(), results);
			Log.d(LOGTAG, "Distance to powerUp: " + results[0]);
			if( results[0] < 21f) {
				return true;
			}
			/*if( (powerUpPoint.getLatitudeE6()   + 80) >= (int) ( player.getLatitude()   * 1E6 ) &&
				(powerUpPoint.getLatitudeE6()   - 80) <= (int) ( player.getLatitude()   * 1E6 ) &&	
				(powerUpPoint.getLongitudeE6()  + 80) <= (int) ( player.getLongitude()  * 1E6 ) &&	
				(powerUpPoint.getLongitudeE6()  - 80) <= (int) ( player.getLongitude()  * 1E6 ) ){
				return true;
			}*/
		}
		return false;
	}
	
	/**
	 * generates new powerUp position by random and snaps it to street
	 */
	private void setNewPowerUpMarker(){
		Random randomGenerator = new Random();
		double[] res = Integrator.snapToStreet( player.getLatitude()  + (randomGenerator.nextDouble()/4 - 0.125) / 100,
												player.getLongitude() + (randomGenerator.nextDouble()/4 - 0.125) / 100);
		if(res != null){
			powerUpPoint = new GeoPoint((int) (res[0] * 1E6),(int) (res[1] * 1E6));
			powerUpSetTime = System.currentTimeMillis();
			Log.d(LOGTAG, "setNewPowerUpMarker(): " + res[0] + " " + res[1]);
		}
	
		
	}
	
	/**********************************************************************/
	/**                         additional classes                       **/
	/**********************************************************************/
	
	/**
	 * GeoLocation Update Handler
	 */
	public class GeoUpdateHandler implements LocationListener {

		/** 
		 * listens to location updates and writes changes into the player class
		 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
		 */
		public void onLocationChanged(Location location) {
			if(location != null){
				double lat = location.getLatitude();
				double lng = location.getLongitude();		
				synchronized (player){
					player.setLatitude(lat);
					player.setLongitude(lng);
					
            		//Log.d(LOGTAG, "onLocationChanged() -> updateMarkers()");
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
	
	/**
	 * custom overlay item class
	 */
	private class MyOverlay extends Overlay{
		int markerId;
		GeoPoint point1;
		GeoPoint point2;
		
		/**
		 * @param gP1 first geopoint
		 * @param gP2 second geopoint (to draw line)
		 * @param mId marker id
		 */
		public MyOverlay(GeoPoint gP1, GeoPoint gP2, int mId){
			markerId = mId;
			point1 = gP1;
			point2 = gP2;
		}
		
	    /**
	     * draws marker and line 
	     * @see com.google.android.maps.Overlay#draw(android.graphics.Canvas, com.google.android.maps.MapView, boolean)
	     */
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
		        
	        	//draw line
		        Point p2=new Point();
		        mapView.getProjection().toPixels(point2, p2);
		        int x2=p2.x;
		        int y2=p2.y;
        
		        canvas.drawLine(x1, y1, x2, y2, paint);
		        
	        }
	        
	        //draw marker at point1
	        Bitmap bmp = BitmapFactory.decodeResource(getResources(), markerId);            
	        canvas.drawBitmap(bmp, x1-5, y1-5, null);
	    }		
	}
	
	/**
	 * GeoPointList contains all geolocations visited by player
	 * also calculates distance between first and last location in the List
	 */
	private class GeoPointList extends ArrayList<GeoPoint>{

		private static final long serialVersionUID = 1L;
		private float distance = 0f;
		
		/** 
		 * adds new geo point to list and directly calculates distance
		 * @see java.util.ArrayList#add(java.lang.Object)
		 */
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
		
		/**
		 * @return total distance between all geo points in list
		 */
		public float getDistance(){
			return distance;
		}
		
	}
	
}