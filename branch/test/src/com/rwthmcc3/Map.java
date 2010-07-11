package com.rwthmcc3;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	
	private GeoPoint prePoint = null;
	private GeoPoint targetPoint = null;
	
	private Player player;
		
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
		mapController = mapView.getController();
		mapController.setZoom(5);

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
			gameLoop();
			
		} else {
			Map.this.finish();
		}
	}	
	
	//background thread updating player/target position
    protected void gameLoop() {

    	startTimeMillis = System.currentTimeMillis();
    	pointList = new GeoPointList();
    	
        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread t = new Thread() {
            public void run() {
            	while(true){
            		synchronized(player){
		            	if(player != null) {
		            		Integrator.playerUpdateState(player); 
		            		targetPoint = new GeoPoint((int) ((player.getTargetLat()) * 1E6),(int) ((player.getTargetLong()) * 1E6));
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
            		
            		SystemClock.sleep(10000);
            		mHandler.post(mWinLoose);
            		break;
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
			GeoPoint point = new GeoPoint((int) (player.getLatitude() * 1E6),(int) (player.getLongitude() * 1E6));
			
			if(pointList.size() == 0){
				pointList.add(point);
			} else {
				if(pointList.get(pointList.size()-1) != point){
					pointList.add(point);
				}
			}
				
			if(point != prePoint){
				if(myStartPositionOverlay == null){
					myStartPositionOverlay = new MyOverlay(point,null,R.drawable.start);
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
					myPositionOverlay = new MyOverlay(prePoint,null,R.drawable.point_blue);
					mapOverlays.add(myPositionOverlay);	
					
					prePoint = point;						
				}
				
				mapView.getController().animateTo(point);
				
				if(myTargetPositionOverlay != null) mapOverlays.remove(myTargetPositionOverlay);
				myTargetPositionOverlay = new MyOverlay(targetPoint,null,R.drawable.point_yellow);
				mapOverlays.add(myTargetPositionOverlay);	
				
				mapView.postInvalidate();
				
			}
		}
	}
	
	private String getFormattetTotalGameTime(){
		int time = (int) (System.currentTimeMillis() - startTimeMillis) / 1000;
		int mins = time / 60;
		int secs = time % 60;
		
		return mins + " Min, " + secs + " Sek";
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
		                  closeMapView();
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
		                  closeMapView();
		             }
		         });
		  final AlertDialog winLooseAlert = builder.create();
		  winLooseAlert.show();
	}
	
	private void closeMapView(){
        lm.removeUpdates(geoUpdater);
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
		}
		return false;
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
				float lat = (float) (location.getLatitude());
				float lng = (float) (location.getLongitude());		
				synchronized (player){
					player.setLatitude(lat);
					player.setLongitude(lng);
					Toast.makeText(getApplicationContext(), "Lat: "+lat+"\nLng: "+lng, Toast.LENGTH_LONG).show();
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
					Location.distanceBetween(g.getLatitudeE6(), g.getLongitudeE6(), this.get(this.size()-1).getLatitudeE6(), this.get(this.size()-1).getLongitudeE6(), results);
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