package com.rwthmcc3;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class MainMenu extends Activity {

	private static ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter mSchedule;
	private static String LOGTAG = "MainMenu";
	private AlertDialog alertMain;
	private AlertDialog alertDialog;
	private List<Game> games = null;
	public static Game chosenGame = null;
	private LocationManager lmMainMenu;
	private String providerMainMenu = "";
	private Player player = Player.getPlayer();
	private Thread backgroundThread = null;
	private boolean runBackgroundThread = true;
	

	// *******************************************************************************************************
	// 								Activity Actions
	// *******************************************************************************************************
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);

		// create listview
		ListView lv = (ListView) findViewById(R.id.listview_mainmenu);
		lv.setTextFilterEnabled(true);
		lv.setOnItemLongClickListener(doListItemOnLongClick);
		mSchedule = new SimpleAdapter(this, mylist,	R.layout.main_menu_list_item, new String[] { "game_name",
						"player_count", "distance" }, new int[] {R.id.game_name, R.id.player_count_list, R.id.distance });
		lv.setAdapter(mSchedule);

		

		// create LocationManager for GPS
		lmMainMenu = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (lmMainMenu.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			providerMainMenu = LocationManager.GPS_PROVIDER;
		} else {
			providerMainMenu = LocationManager.NETWORK_PROVIDER;
		}

		lmMainMenu.getLastKnownLocation(providerMainMenu);
		lmMainMenu.requestLocationUpdates(providerMainMenu, 0, 0,locationListenerMainMenu);

	};
	
	
	
	@Override
	public void onPause(){
		super.onPause();
		//destroy thread
		runBackgroundThread = false;
		
	}
	@Override
	public void onStop(){
		super.onStop();
		//destroy thread
		runBackgroundThread = false;
		
	}

	@Override
	public void onResume() {
		super.onResume();
		
		
		// reset view
		resetViews();
		
		//TODO check if is running
		if(backgroundThread==null){
			runBackgroundThread = true;
			startLongRunningOperation();
		}else{
			if(!backgroundThread.isAlive()){
				runBackgroundThread = true;
				startLongRunningOperation();
			}
		}
		

	}


	//**********************************************************************
	//		Runnables
	//**********************************************************************
	
	// Need handler for callbacks to the UI thread
    final Handler mHandler = new Handler();
    
	private Runnable mUpdateViewTask = new Runnable() {
		public void run() {
			// Back in the UI thread -- update our UI elements
		    boolean gamesOk = updateListofGames();
		    if(gamesOk){
		    	makeUpdatedViewsVisible();
		    }else{
		    	Toast.makeText(getApplicationContext(),"Verbindung zum Server fehlgeschlagen!", Toast.LENGTH_SHORT);
		    }
		}
	};

	

    //**********************************************************************
	//		Thread
	//**********************************************************************
    
	/**
     * Starts a thread for updating the UI.
     */
    protected void startLongRunningOperation() {

        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
    	backgroundThread = new Thread() {
            public void run() {
            	
            	while(runBackgroundThread){//restart
            		//update all 15 seconds
                	mHandler.post(mUpdateViewTask);
            		try{	
                        if(runBackgroundThread)sleep(15000);
                    }catch(InterruptedException e){
        					Log.d("threadInMainMenu", e.toString());
        			}
                    
        		}
            	
            }
        };
        backgroundThread.start();
    }
    
	// *******************************************************************************************************
	// 								Location Listener
	// *******************************************************************************************************
	private final LocationListener locationListenerMainMenu = new LocationListener() {
		public void onLocationChanged(Location location) {
			player.setLatitude(location.getLatitude());
			player.setLongitude(location.getLongitude());
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
	
	// *******************************************************************************************************
	// 										Views
	// *******************************************************************************************************
	
	/**
     * Resets all views in GameState.
     */
	private void resetViews(){
		View layoutMainMenuView = (View) findViewById(R.id.layout2_mainmenu);
		View listView = (View) findViewById(R.id.listview_mainmenu);
		layoutMainMenuView.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
	}
	
	/**
	 * Adds a single item to the game-list.
	 * 
	 * @param game_name
	 * @param player_count
	 * @param distance
	 */
	public static void addItemToList(String game_name, String player_count,
			String distance) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("game_name", game_name);
		map.put("player_count", player_count);
		map.put("distance", distance);
		mylist.add(map);

	}

	/**
	 * Clears game-list and adds new games to the list. Calls integrator
	 * getGameList(). Notifies user.
	 * @return boolean: no errors
	 */
	public boolean updateListofGames() {
		// delete list before set new list
		mylist.clear();

		// better look - gps
		DecimalFormat format = new DecimalFormat("#0.00");

		games = Integrator.getGameList();

		if (games != null) {
			
			if (games.isEmpty()){
				Toast.makeText(MainMenu.this, "Keine Spiele vorhanden!",Toast.LENGTH_LONG).show();
			}else{
				for (Game i : games) {
					Log.d(LOGTAG, "game: " + i.getName());
					addItemToList(i.getName(), i.getPlayerCount() + "/"	+ i.getMaxPlayersCount() + " Spieler",
							"Entfernung zum Spielersteller: "+ format.format(player.distFromToPlayer(i.getCreatorLatitude(),
									i.getCreatorLongitude())) + " m");
				}
			}
			mSchedule.notifyDataSetChanged();
			return true;
		}else{
			return false;
		}

		
		
	}
	
	/**
     * Makes updated views visible. 
     * Changes screen from Loading to List.
     *  
     */
	public void makeUpdatedViewsVisible() {
		View layoutMainMenuView = (View) findViewById(R.id.layout2_mainmenu);
		View listView = (View) findViewById(R.id.listview_mainmenu);
		layoutMainMenuView.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);


	
	}

	// *******************************************************************************************************
	// Menu
	// *******************************************************************************************************

	/**
	 * Creates the menu items.
	 * 
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_options_menu, menu);
		return true;
	}

	/**
	 * Handles item selections
	 * 
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_options_menu_update:
			mHandler.post(mUpdateViewTask);
			return true;
		case R.id.main_options_menu_new_game:
			startActivityForResult(new Intent(this.getApplicationContext(),
					com.rwthmcc3.NewGame.class), 0);
			return true;
		case R.id.main_options_menu_view_map:
			startActivityForResult(new Intent(this.getApplicationContext(),
					com.rwthmcc3.Map.class), 0);
			return true;
		case R.id.main_options_menu_prefs:
			startActivityForResult(new Intent(this.getApplicationContext(),
					com.rwthmcc3.Preferences.class), 0);
			return true;
		}
		return false;
	}
	
	// *******************************************************************************************************
	// 								on click Listener
	// *******************************************************************************************************
	
	OnItemLongClickListener doListItemOnLongClick = new OnItemLongClickListener() {
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
			
			chosenGame = games.get(arg2);

			// create dialog
			CharSequence[] chosenItems = {"Spiel �ffnen", "Spielerliste anzeigen"};

			

			AlertDialog.Builder builderMain = new AlertDialog.Builder(MainMenu.this);
			builderMain.setTitle("Bitte ausw�hlen:");
			builderMain.setItems(chosenItems,new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if(item == 0){
								startActivityForResult(new Intent(MainMenu.this, com.rwthmcc3.GameState.class), 0);
							}else{
								
								//create input
								List<String> playerNamesList = Integrator.getPlayerList(chosenGame);
								String[] playerNamesArray = (String[])playerNamesList.toArray(new String[playerNamesList.size()]);
								
								AlertDialog.Builder builderDialog = new AlertDialog.Builder(MainMenu.this);
								builderDialog.setTitle("Spielerliste:");
								builderDialog.setItems(playerNamesArray,new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int item) {
										
									}

								});	
								alertDialog = builderDialog.create();
								dialog.dismiss();
								alertDialog.show();

							}
							

						}
					});
			alertMain = builderMain.create();
			alertMain.show();
			return false;

		}
	};
}
