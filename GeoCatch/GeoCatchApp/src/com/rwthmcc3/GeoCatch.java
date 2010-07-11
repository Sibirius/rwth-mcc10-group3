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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class GeoCatch extends Activity {
	private String mac = null;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private  Player p = Player.getPlayer();
	private LocationManager lmGeoCatch;
	private String providerGeoCatch = "";
	public static boolean debugMode = false;
	public static String debugMac = "16:66:FF:66:66:65";
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.on_start);
		Button buttoNewGame = (Button)findViewById(R.id.button_new_game);
		Button buttonJoinGame = (Button)findViewById(R.id.button_join_game);
		buttoNewGame.setOnClickListener(doNewGameOnClick);
		buttonJoinGame.setOnClickListener(doJoinGameOnClick);
		
		//restore player_name and list-size
		SharedPreferences settings = getSharedPreferences(Preferences.PREFS_NAME, 0);
		Player p = Player.getPlayer();
	    p.setName(settings.getString("player_name", "Player 1"));
	    p.setListSize(settings.getInt("list_size", 10));
	    
	    //create LocationManager for GPS
	    lmGeoCatch = (LocationManager) getSystemService(LOCATION_SERVICE);

		if(lmGeoCatch.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			providerGeoCatch = LocationManager.GPS_PROVIDER;
		} else {
			providerGeoCatch = LocationManager.NETWORK_PROVIDER;			
		}
		
		lmGeoCatch.getLastKnownLocation(providerGeoCatch);
		lmGeoCatch.requestLocationUpdates(providerGeoCatch, 0, 0, locationListenerGeoCatch);
	     
		
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
		    //register player
		    if(mBluetoothAdapter.isEnabled()){
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
		         }
		    }else{//for debugging
		    	Integrator.registerPlayer(debugMac, p.getName(), p.getLongitude(), p.getLatitude());
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
			}
	};
	
	OnClickListener doJoinGameOnClick = new OnClickListener() {		
		public void onClick(View view) {
			startActivityForResult(new Intent(GeoCatch.this, com.rwthmcc3.MainMenu.class),0);
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
		case R.id.geocatch_options_menu_new_game:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.NewGame.class),0);	
			return true;
		case R.id.geocatch_options_menu_join_game:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.MainMenu.class),0);	
			return true;
		case R.id.geocatch_options_menu_view_map:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.Map.class),0);			
			return true;			
		case R.id.geocatch_options_menu_prefs:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.Preferences.class),0);
			return true;
		case R.id.geocatch_options_menu_help:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.Help.class),0);
			return true;	
		}
		return false;
	}
	
	

}