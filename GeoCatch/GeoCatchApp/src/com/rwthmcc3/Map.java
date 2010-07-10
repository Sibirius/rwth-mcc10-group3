package com.rwthmcc3;

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
	
    // Need handler for callbacks to the UI thread
    final Handler mHandler = new Handler();

    // Create runnable for posting
    final Runnable mUpdateMarkers = new Runnable() {
        public void run() {
            updateMarkers();
        }
    };

    protected void gameLoop() {

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
            		SystemClock.sleep(5000);
            	}
            }
        };
        t.start();
        winLooseAlert();
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);	
				
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
			player.setLatitude(6.654321f);
			player.setLongitude(54.654321f);
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
		
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);

		if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			provider = LocationManager.GPS_PROVIDER;
		} else {
			provider = LocationManager.NETWORK_PROVIDER;			
		}
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
		
	private void winLooseAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  builder.setMessage("Du hast " + (player.getName().equals("gewonnen") ? "gewonnen" : "verloren"))
		         .setCancelable(false)
		         .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		             public void onClick(DialogInterface dialog, int id) {
		                  dialog.cancel();
		             }
		         });
		  final AlertDialog winLooseAlert = builder.create();
		  winLooseAlert.show();
	}
	
	class MyOverlay extends Overlay{
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
	
	private void updateMarkers(){
		Log.d(LOGTAG, "updateMarkers()");
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		
		synchronized(player){
			GeoPoint point = new GeoPoint((int) (player.getLatitude() * 1E6),(int) (player.getLongitude() * 1E6));
			
			if(point != prePoint){
				if(myStartPositionOverlay == null){
					Log.d(LOGTAG, "updateMarkers() - 1");
					myStartPositionOverlay = new MyOverlay(point,null,R.drawable.start);
					mapOverlays.add(myStartPositionOverlay);
					
					prePoint = point;
				} else if(myPositionOverlay == null){
					Log.d(LOGTAG, "updateMarkers() - 2");
					mapOverlays.remove(myStartPositionOverlay);
					mapOverlays.add(new MyOverlay(prePoint,point,R.drawable.start));
					myPositionOverlay = new MyOverlay(point,null,R.drawable.point_blue);
					mapOverlays.add(myPositionOverlay);
					
					prePoint = point;
				} else {
					Log.d(LOGTAG, "updateMarkers() - 3");
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
	
}