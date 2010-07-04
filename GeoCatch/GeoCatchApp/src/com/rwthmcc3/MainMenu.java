package com.rwthmcc3;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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



public class MainMenu extends ListActivity{
	
	private static ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter mSchedule;
	private static String LOGTAG = "MainMenu";
	private AlertDialog alert;
	private List<Game> games = null;
	private Game chosenGame = null;
	public static String[] arrayOfPlayers = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //create list
        ListView lv = getListView();
	    lv.setTextFilterEnabled(true);
	    lv.setOnItemLongClickListener(doListItemOnClick);
	    mSchedule = new SimpleAdapter(this, mylist, R.layout.main_menu_list_item,
                new String[] {"game_name", "player_count", "distance"}, new int[] {R.id.game_name, R.id.player_count_list, R.id.distance});
	    lv.setAdapter(mSchedule);
	    
	    
	    //load list 
	    setListofGames();
	    
	    
	    //create dialog
	    final CharSequence[] items = {"Spiel beitreten", "Spielerliste anzeigen"};
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Bitte auswählen:");
	    builder.setItems(items, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int item) {
	        	
	        	if(item == 0){ // join game
	        		Integrator.leaveGame(Player.getPlayer());
	        		//hier vielleicht auf antwort warten?
	        		Integrator.joinGame(Player.getPlayer(), chosenGame);
	        		
	        		List<Player> players = Integrator.getPlayerList(chosenGame);
	        		int j =0;
	        		arrayOfPlayers = new String[players.size()];
	        		for(Player i: players){
	        			arrayOfPlayers[j] = i.getPlayerName();
	        			j++;
	        		}
	        		//startActivityForResult(new Intent(MainMenu.this, com.rwthmcc3.WaitForPlayers.class),0);
	        		
	        	}else{
	        		List<Player> players = Integrator.getPlayerList(chosenGame);
	        		int j =0;
	        		arrayOfPlayers = new String[players.size()];
	        		for(Player i: players){
	        			arrayOfPlayers[j] = i.getPlayerName();
	        			j++;
	        		}
	        		startActivityForResult(new Intent(MainMenu.this, com.rwthmcc3.ListOfPlayers.class),0);
	        	}
	            
	            
	        }
	    });
	    alert = builder.create();
	    
    };
	
    
    OnItemLongClickListener doListItemOnClick = new OnItemLongClickListener() {
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			alert.show();
			chosenGame = games.get(arg2);
			return false;
		}		
    };
    
    
    
    /**
     * Adds a single item to the game-list.
     * @param game_name
     * @param player_count
     * @param distance
     */
	public static void addItemToList(String game_name, String player_count, String distance){
		HashMap<String, String> map = new HashMap<String, String>();
	    map.put("game_name", game_name);
	    map.put("player_count", player_count);
	    map.put("distance", distance);
	    mylist.add(map);
	    
	}
	
	
	/**Calls setListofGames() and notifies list adapter.
	 * 
	 */
	public void updateList(){
		setListofGames();
		mSchedule.notifyDataSetChanged();
	}
	
	/**Clears game-list and adds new games to the list.
	 * Calls integrator getGameList(). Notifies user.
	 * 
	 */
	public void setListofGames(){
		
		//message to user and load list 
	    ProgressDialog dialog = ProgressDialog.show(MainMenu.this, "", 
                "Spielliste wird vom Server abgerufen. Bitte warten...", true);
	  
		//delete list before set new list
		mylist.clear();
		
		//TODO set distance
		
		//for every item: addItemToList
		//Player player = Integrator.registerPlayer("F1:12:23:34:45:56", "playertest");
		//Integrator.createGame(player, "testgame", 5, 1, 13.37f, 13.337f);
		games = Integrator.getGameList();
		if (games != null){
			for (Game i : games) {
				Log.d(LOGTAG, "game: "+i.getName());
				addItemToList(i.getName(),i.getPlayerCount()+"/"+i.getMaxPlayersCount()+" Spieler","Entfernung zum Spielersteller: 0.8 km");
			}
		}
		
		/*
		//test data
		addItemToList("Unreal Tournament","1/8 Player","Distance to Creator: 0.8 km");
		addItemToList("Super Mario","4/5 Player","Distance to Creator: 1.5 km");
		addItemToList("Tekken","3/4 Player","Distance to Creator: 2 km");
		addItemToList("Halo","1/5 Player","Distance to Creator: 3.5 km");
		addItemToList("Resident Evil","4/7 Player","Distance to Creator: 4.5 km");
		*/
		
		
		
	    dialog.dismiss();
	}
	
	
	/**
	 *  Creates the menu items. 
	 *  
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_options_menu, menu);
	    return true;
	}

	/** Handles item selections 
	 *
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_options_menu_update:
			updateList();
			return true;
		case R.id.main_options_menu_new_game:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.NewGame.class),0);	
			return true;
		case R.id.main_options_menu_view_map:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.Map.class),0);			
			return true;			
		case R.id.main_options_menu_prefs:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.Preferences.class),0);
			return true;						
		}
		return false;
	}
	
}
