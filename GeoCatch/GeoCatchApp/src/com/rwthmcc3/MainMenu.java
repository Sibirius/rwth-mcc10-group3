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
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class MainMenu extends Activity {

	private static ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter mSchedule;
	private static String LOGTAG = "MainMenu";
	private AlertDialog alert;
	private List<Game> games = null;
	public static Game chosenGame = null;
	public static String[] arrayOfPlayers = null;
	private LocationManager lmMainMenu;
	private String providerMainMenu = "";
	private Player p = Player.getPlayer();
	private Handler myTimerHandler = new Handler();
	private Handler myUpdateHandler = new Handler();
	private long mStartTime = 0;
	

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
		mSchedule = new SimpleAdapter(this, mylist,
				R.layout.main_menu_list_item, new String[] { "game_name",
						"player_count", "distance" }, new int[] {
						R.id.game_name, R.id.player_count_list, R.id.distance });
		lv.setAdapter(mSchedule);

		// set button
		Button startGameButton = (Button) findViewById(R.id.button_start_game_mainmenu);
		startGameButton.setOnClickListener(doStartGameButtonOnClick);

		// create LocationManager for GPS
		lmMainMenu = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (lmMainMenu.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			providerMainMenu = LocationManager.GPS_PROVIDER;
		} else {
			providerMainMenu = LocationManager.NETWORK_PROVIDER;
		}

		lmMainMenu.getLastKnownLocation(providerMainMenu);
		lmMainMenu.requestLocationUpdates(providerMainMenu, 0, 0,
				locationListenerMainMenu);

	};
	
	
	
	@Override
	public void onPause(){
		super.onPause();
		
		myUpdateHandler.removeCallbacks(mUpdateViewTask);
		myTimerHandler.removeCallbacks(mUpdateTimeTask);
	}

	@Override
	public void onResume() {
		super.onResume();

		
		// reset view
		View layoutMainMenuView = (View) findViewById(R.id.layout2_mainmenu);
		View listView = (View) findViewById(R.id.listview_mainmenu);
		layoutMainMenuView.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
		View startButtonView = (View) findViewById(R.id.button_start_game_mainmenu);
		startButtonView.setVisibility(View.GONE);
		TextView timerView = (TextView) findViewById(R.id.text_timer_mainmenu);
		timerView.setVisibility(View.GONE);

		
		myUpdateHandler.removeCallbacks(mUpdateViewTask);
		myTimerHandler.removeCallbacks(mUpdateTimeTask);
	
		myUpdateHandler.postDelayed(mUpdateViewTask, 100);
		mStartTime = SystemClock.uptimeMillis();

	}


	// *******************************************************************************************************
	// 									Runnables
	// *******************************************************************************************************

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			
			try{
				TextView timerView = (TextView) findViewById(R.id.text_timer_mainmenu);
				final long start = mStartTime; 
																// milliseconds
				long millis = SystemClock.uptimeMillis() - start;
				if (p.getMyGame() != null) {
					long countDown = p.getMyGame().getTimer() * 1000 - millis;
					int seconds = (int) (countDown / 1000);
					int minutes = seconds / 60;
					seconds = seconds % 60;
	
					if (countDown > 0) {
						if (seconds < 10) {
							timerView.setText("Zeit bis zum Spielstart:"+"  " + minutes + ":0" + seconds);
						} else {
							timerView.setText("Zeit bis zum Spielstart:"+"  " + minutes + ":" + seconds);
						}
	
						// active for next update
						myTimerHandler.postAtTime(this, start + millis + 1000);
					} else {
						// counted to 0
						p.setTimerHasCountedDown(true);
						timerView.setVisibility(View.GONE);
						startActivityForResult(new Intent(MainMenu.this,
								com.rwthmcc3.Map.class), 0);
					}
				}
	        }catch(Exception e){
	               
	        }
		}
	};

	private Runnable mUpdateViewTask = new Runnable() {
		public void run() {
			try{
				setListofGames();
				updateViews();
				// active for next update
				myUpdateHandler.postDelayed(this , 15000);
	        }catch(Exception e){
	               
	        }
			
		}
	};

	// *******************************************************************************************************
	// 								Location Listener
	// *******************************************************************************************************
	private final LocationListener locationListenerMainMenu = new LocationListener() {
		public void onLocationChanged(Location location) {
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

	

	// *******************************************************************************************************
	// 										set List / update views
	// *******************************************************************************************************

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
	 * 
	 */
	public void setListofGames() {
		// delete list before set new list
		mylist.clear();

		// nicer look - gps
		DecimalFormat format = new DecimalFormat("#0.00");

		// for every item: addItemToList
		// Player player = Integrator.registerPlayer("F1:12:23:34:45:56",
		// "playertest");
		// Integrator.createGame(player, "testgame", 5, 1, 13.37f, 13.337f);
		games = Integrator.getGameList();

		if (games != null) {
			if (games.isEmpty())
				Toast.makeText(MainMenu.this, "Keine Spiele vorhanden!",
						Toast.LENGTH_LONG).show();
			for (Game i : games) {
				Log.d(LOGTAG, "game: " + i.getName());
				addItemToList(i.getName(), i.getPlayerCount() + "/"
						+ i.getMaxPlayersCount() + " Spieler",
						"Entfernung zum Spielersteller: "
								+ format.format(p.distFromToPlayer(i
										.getCreatorLatitude(), i
										.getCreatorLongitude())) + " m");
			}
		}

		// addItemToList("Unreal Tournament","1/8 Player","Distance to Creator: 0.8 km");
		// addItemToList("Super Mario","4/5 Player","Distance to Creator: 1.5 km");
		// addItemToList("Tekken","3/4 Player","Distance to Creator: 2 km");
		// addItemToList("Halo","1/5 Player","Distance to Creator: 3.5 km");

		mSchedule.notifyDataSetChanged();

	}
	
	public void updateViews() {
		View layoutMainMenuView = (View) findViewById(R.id.layout2_mainmenu);
		View listView = (View) findViewById(R.id.listview_mainmenu);
		layoutMainMenuView.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		View startButtonView = (View) findViewById(R.id.button_start_game_mainmenu);
		
		
		Integrator.playerUpdateState(p);

		if (p.getMyGame() != null) {
			int myGameState = p.getMyGame().getState();
			//leaves game if it is stopped
			if(myGameState == 2)
				Integrator.leaveGame(p);
			
			// show start game button when player is creator,enough players and
			// game not started
			if ((p.isCreator())
					&& (p.getMyGame().getPlayerCount() == p.getMyGame()
							.getMaxPlayersCount()) && (myGameState == 0)) {
				
				startButtonView.setVisibility(View.VISIBLE);
			}
			// shows timer when game is started and timer hasn't counted down
			if ((myGameState == 1) && (!p.isCreator())
					&& p.isTimerHasCountedDown()) {
				TextView timerView = (TextView) findViewById(R.id.text_timer_mainmenu);
				timerView.setVisibility(View.VISIBLE);
				myUpdateHandler.removeCallbacks(mUpdateTimeTask);
				myUpdateHandler.postDelayed(mUpdateTimeTask, 100);
			}
		}else{
			startButtonView.setVisibility(View.GONE);
		}

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
			setListofGames();
			updateViews();
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
	
	OnClickListener doStartGameButtonOnClick = new OnClickListener() {
		public void onClick(View view) {
			boolean start = Integrator.startGame(Player.getPlayer());
			// check
			if (start) {
				Toast.makeText(MainMenu.this, "Spiel wurde gestartet!",
						Toast.LENGTH_SHORT).show();

				// make button invisible
				View startButtonView = (View) findViewById(R.id.button_start_game_mainmenu);
				startButtonView.setVisibility(View.GONE);

				// show timer and activate
				TextView timerView = (TextView) findViewById(R.id.text_timer_mainmenu);
				timerView.setVisibility(View.VISIBLE);
				myTimerHandler.removeCallbacks(mUpdateTimeTask);
				myTimerHandler.postDelayed(mUpdateTimeTask, 100);
				mStartTime = SystemClock.uptimeMillis();

			} else {
				Toast.makeText(MainMenu.this,
						"Fehler! Bitte versuchen Sie es erneut!",
						Toast.LENGTH_SHORT).show();
			}

		}
	};
	
	OnItemLongClickListener doListItemOnLongClick = new OnItemLongClickListener() {
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			chosenGame = games.get(arg2);

			// create dialog
			CharSequence[] chosenItems = null;

			// compare keys
			boolean sameKey = true;
			String chosenGameKey = chosenGame.getKey();
			if (p.getMyGame() == null) {
				sameKey = false;
			} else {
				String myGameKey = p.getMyGame().getKey();
				sameKey = chosenGameKey.equals(myGameKey);
			}

			if (sameKey && p.isCreator()) {// for creator
				if (p.getMyGame().getPlayerCount() == p.getMyGame()
						.getMaxPlayersCount()) {// enough player
					CharSequence[] items = { "Spiel starten", "Spiel beenden",
							"Spielerliste anzeigen" };
					chosenItems = items;
				} else {// not enough player
					CharSequence[] items = { "Spiel beenden",
							"Spielerliste anzeigen" };
					chosenItems = items;
				}

			} else {
				if (sameKey) {// jointed
					CharSequence[] items = { "Spiel verlassen",
							"Spielerliste anzeigen" };
					chosenItems = items;
				} else {// not jointed
					CharSequence[] items = { "Spiel beitreten",
							"Spielerliste anzeigen" };
					chosenItems = items;
				}

			}

			AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
			builder.setTitle("Bitte auswählen:");
			builder.setItems(chosenItems,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {

							// compare keys
							boolean sameKey = true;
							String chosenGameKey = chosenGame.getKey();
							if (p.getMyGame() == null) {
								sameKey = false;
							} else {
								String myGameKey = p.getMyGame().getKey();
								sameKey = chosenGameKey.equals(myGameKey);
							}

							if (sameKey && p.isCreator()) {// for creator
								if (p.getMyGame().getPlayerCount() == p
										.getMyGame().getMaxPlayersCount()) {// enough
																			// player
									switch (item) {
									case 0:
										boolean start = Integrator
												.startGame(Player.getPlayer());
										// check
										if (start) {
											Toast.makeText(MainMenu.this,
													"Spiel wurde gestartet!",
													Toast.LENGTH_SHORT).show();

											startActivityForResult(
													new Intent(
															MainMenu.this,
															com.rwthmcc3.MainMenu.class),
													0);
										} else {
											Toast
													.makeText(
															MainMenu.this,
															"Fehler! Bitte versuchen Sie es erneut!",
															Toast.LENGTH_SHORT)
													.show();
										}
										break;
									case 1:
										boolean stop = Integrator
												.leaveGame(Player.getPlayer());
										// check
										if (stop) {
											Toast.makeText(MainMenu.this,
													"Spiel wurde beendet!",
													Toast.LENGTH_SHORT).show();
										} else {
											Toast
													.makeText(
															MainMenu.this,
															"Fehler! Bitte versuchen Sie es erneut!",
															Toast.LENGTH_SHORT)
													.show();
										}
										break;
									case 2:
										startActivityForResult(
												new Intent(
														MainMenu.this,
														com.rwthmcc3.WaitForPlayers.class),
												0);
										break;
									default:
										break;
									}

								} else {// not enough player
									switch (item) {
									case 0:
										boolean stop = Integrator
												.leaveGame(Player.getPlayer());
										// check
										if (stop) {
											Toast.makeText(MainMenu.this,
													"Spiel wurde beendet!",
													Toast.LENGTH_SHORT).show();
										} else {
											Toast
													.makeText(
															MainMenu.this,
															"Fehler! Bitte versuchen Sie es erneut!",
															Toast.LENGTH_SHORT)
													.show();
										}
										break;
									case 1:
										startActivityForResult(
												new Intent(
														MainMenu.this,
														com.rwthmcc3.WaitForPlayers.class),
												0);
										break;
									default:
										break;
									}
								}

							} else {
								if (sameKey) {// jointed
									switch (item) {
									case 0:
										boolean leave = Integrator
												.leaveGame(Player.getPlayer());

										// check
										if (leave) {
											Toast.makeText(MainMenu.this,
													"Spiel wurde verlassen!",
													Toast.LENGTH_SHORT).show();
										} else {
											Toast
													.makeText(
															MainMenu.this,
															"Fehler! Bitte versuchen Sie es erneut!",
															Toast.LENGTH_SHORT)
													.show();
										}
										break;
									case 1:
										startActivityForResult(
												new Intent(
														MainMenu.this,
														com.rwthmcc3.WaitForPlayers.class),
												0);
										break;
									default:
										break;
									}
								} else {// not jointed
									switch (item) {
									case 0:
										boolean leave = true;
										if (p.getMyGame() != null) {
											leave = Integrator.leaveGame(Player
													.getPlayer());
										}

										boolean join = Integrator.joinGame(
												Player.getPlayer(), chosenGame);

										// check
										if (leave && join) {
											startActivityForResult(
													new Intent(
															MainMenu.this,
															com.rwthmcc3.WaitForPlayers.class),
													0);
										} else {
											Toast
													.makeText(
															MainMenu.this,
															"Fehler! Bitte versuchen Sie es erneut!",
															Toast.LENGTH_SHORT)
													.show();
										}
										break;
									case 1:
										startActivityForResult(
												new Intent(
														MainMenu.this,
														com.rwthmcc3.WaitForPlayers.class),
												0);
										break;
									default:
										break;
									}
								}

							}

						}
					});
			alert = builder.create();
			alert.show();
			return false;

		}
	};
}
