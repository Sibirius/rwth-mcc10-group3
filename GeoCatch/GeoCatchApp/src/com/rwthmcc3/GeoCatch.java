package com.rwthmcc3;



import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class GeoCatch extends Activity {
	private String mac = null;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private  Player p = Player.getPlayer();
	private LocationManager lmGeoCatch;
	private String providerGeoCatch = "";
	public static boolean debugMode = true;
	public static String debugMac = "AF:AD:AF:64:66:66";
	SharedPreferences preferences;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.on_start);
		
		//set title
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        TextView leftTitle = (TextView)findViewById(R.id.left_text);
        TextView rightTitle = (TextView)findViewById(R.id.right_text);
        leftTitle.setText("GeoCatch");
        rightTitle.setText("Startseite");
		
		
		//buttons
		Button buttoNewGame = (Button)findViewById(R.id.button_new_game);
		Button buttonJoinGame = (Button)findViewById(R.id.button_join_game);
		buttoNewGame.setOnClickListener(doNewGameOnClick);
		buttonJoinGame.setOnClickListener(doJoinGameOnClick);
		
		//restore player_name and list-size
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		Player p = Player.getPlayer();
	    p.setName(preferences.getString("player_name", "Player 1"));
	    
	    //debugging
	    if(debugMode)
	    	Integrator.registerPlayer(debugMac, p.getName(), p.getLongitude(), p.getLatitude());
	    
	    
	     
		
	}
	
	@Override
	public void onPause(){
		super.onPause();
		
		if(lmGeoCatch !=null)lmGeoCatch.removeUpdates(locationListenerGeoCatch);
		
		
	}
	
	@Override
	public void onStop(){
		super.onStop();
		
		if(lmGeoCatch !=null)lmGeoCatch.removeUpdates(locationListenerGeoCatch);
		
		
		
	}
	
	public void onResume(){
		super.onResume();
		
		if(!debugMode){
			//check bluetooth
		    if (mBluetoothAdapter == null) {
		    	
		        // Device does not support Bluetooth
		    	AlertDialog.Builder builderNoBluetooth = new AlertDialog.Builder(this);
		    	builderNoBluetooth.setMessage("Sie benötigen Bluetooth um dieses Spiel zu spielen! GeoCatch beenden?")
		        	       .setCancelable(false)
		        	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        	           public void onClick(DialogInterface dialog, int id) {
		        	                GeoCatch.this.finish();
		        	           }
		        	       });
		        	AlertDialog alertNoBluetooth = builderNoBluetooth.create();;
		        	alertNoBluetooth.show();
		    }
		    
		    //create LocationManager for GPS
		    lmGeoCatch = (LocationManager) getSystemService(LOCATION_SERVICE);
		    
		    
		    if (!mBluetoothAdapter.isEnabled()) {
		    	AlertDialog.Builder builderBluetoothOff = new AlertDialog.Builder(this);
		    	builderBluetoothOff.setMessage("Bluetooth muss aktiviert sein um dieses Spiel zu spielen! Bluetooth aktivieren?")
		        	       .setCancelable(false)
		        	       .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
		        	           public void onClick(DialogInterface dialog, int id) {
		        	        	   	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		        	   	        	startActivityForResult(enableBtIntent, RESULT_OK);
		        	           }
		        	       })
		        	       .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
		        	           public void onClick(DialogInterface dialog, int id) {
		        	        	   GeoCatch.this.finish();
		        	           }
		        	       });
		        	AlertDialog alertBluetoothOff = builderBluetoothOff.create();;
		        	alertBluetoothOff.show();
		        
		    }
		    
		    
		    //at first enable bluetooth then gps
		    if (!lmGeoCatch.isProviderEnabled(LocationManager.GPS_PROVIDER) && mBluetoothAdapter.isEnabled()) {
		    	AlertDialog.Builder builderGpsOff = new AlertDialog.Builder(this);
		    	builderGpsOff.setMessage("GPS muss aktiviert sein um dieses Spiel zu spielen! GPS aktivieren?")
		        	       .setCancelable(false)
		        	       .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
		        	           public void onClick(DialogInterface dialog, int id) {
		        	        	   Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		        	        			   startActivity(gpsOptionsIntent);
		        	           }
		        	       })
		        	       .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
		        	           public void onClick(DialogInterface dialog, int id) {
		        	        	   GeoCatch.this.finish();
		        	           }
		        	       });
		        	AlertDialog alertGpsOff = builderGpsOff.create();;
		        	alertGpsOff.show();
		        
		    }
		    
			if(lmGeoCatch.isProviderEnabled(LocationManager.GPS_PROVIDER)){
				providerGeoCatch = LocationManager.GPS_PROVIDER;
			} else {
				providerGeoCatch = LocationManager.NETWORK_PROVIDER;			
			}
			
			lmGeoCatch.getLastKnownLocation(providerGeoCatch);
			lmGeoCatch.requestLocationUpdates(providerGeoCatch, 0, 0, locationListenerGeoCatch);
		    
		    //register player
		    if(mBluetoothAdapter.isEnabled() && lmGeoCatch.isProviderEnabled(LocationManager.GPS_PROVIDER)){
		    	 //set mac
			     mac = mBluetoothAdapter.getAddress();
			     p.setMac(mac);
			     		     
		    	 boolean hasRegister = Integrator.registerPlayer(p.getMac(), p.getName(), p.getLongitude(), p.getLatitude());
		    	 if(hasRegister == false){
		         	hasRegister = Integrator.registerPlayer(p.getMac(), p.getName(), p.getLongitude(), p.getLatitude());
		         	 //register player failed (second time)
		         	if(hasRegister == false){
		 	        	AlertDialog.Builder builderRegisterFailed = new AlertDialog.Builder(this);
		 	        	builderRegisterFailed.setMessage("Registrierung fehlgeschlagen! Bitte überprüfen Sie Ihre Internetverbindung! Registrierung wiederholen?")
		 	        	       .setCancelable(false)
		 	        	       .setPositiveButton("Wiederholen", new DialogInterface.OnClickListener() {
		 	        	           public void onClick(DialogInterface dialog, int id) {
		 	        	        	  dialog.dismiss();
		 	        	        	  onResume();	//restart
		 	        	           }
		 	        	      });
		 	        	AlertDialog alertRegisterFailed = builderRegisterFailed.create();;
		 	        	alertRegisterFailed.show();
		         	}
		         }else{
		        	 Toast.makeText(getApplicationContext(), "Registrierung erfolgreich!", Toast.LENGTH_SHORT);
		         }
		    }else{//for debugging
		    	p.setMac(debugMac);
		    	boolean register=Integrator.registerPlayer(p.getMac(), p.getName(), p.getLongitude(), p.getLatitude());
		    	if(!register)Toast.makeText(getApplicationContext(), "Registrierung fehlgeschlagen", Toast.LENGTH_SHORT);
		    	
		    	lmGeoCatch = (LocationManager) getSystemService(LOCATION_SERVICE);
		    	
		    	if(lmGeoCatch.isProviderEnabled(LocationManager.GPS_PROVIDER)){
					providerGeoCatch = LocationManager.GPS_PROVIDER;
				} else {
					providerGeoCatch = LocationManager.NETWORK_PROVIDER;			
				}
				
				lmGeoCatch.getLastKnownLocation(providerGeoCatch);
				lmGeoCatch.requestLocationUpdates(providerGeoCatch, 0, 0, locationListenerGeoCatch);
		    }
	    }
	   
		   
	    
	}
	
	private final LocationListener locationListenerGeoCatch = new LocationListener() {
	    	public void onLocationChanged(Location location){
	           p.setLatitude(location.getLatitude());
	           p.setLongitude(location.getLongitude());
	        }

			public void onProviderDisabled(String provider) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
	    };
	
	OnClickListener doNewGameOnClick = new OnClickListener() {		
		public void onClick(View view) {
			startActivityForResult(new Intent(GeoCatch.this, com.rwthmcc3.NewGame.class),0);
			overridePendingTransition(R.anim.fade, R.anim.hold);
			}
	};
	
	OnClickListener doJoinGameOnClick = new OnClickListener() {		
		public void onClick(View view) {
			startActivityForResult(new Intent(GeoCatch.this, com.rwthmcc3.MainMenu.class),0);
			overridePendingTransition(R.anim.fade, R.anim.hold);
			}
	};
	
	
	
	/**
	 *	Creates the menu items 
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.geocatch_options_menu, menu);
	    
       
	    return true;
	}

	/**
	 *  Handles item selections.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.geocatch_options_menu_prefs:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.Preferences.class),0);
			overridePendingTransition(R.anim.fade, R.anim.hold);
			return true;
		case R.id.geocatch_options_menu_help:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.Help.class),0);
			overridePendingTransition(R.anim.fade, R.anim.hold);
			return true;	
		}
		return false;
	}
	
	

}