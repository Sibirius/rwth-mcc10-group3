package com.rwthmcc3;


import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;



public class MainMenu extends ListActivity{
	
	private static ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter mSchedule;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	
        ListView lv = getListView();
	    lv.setTextFilterEnabled(true);
	    
	    setListofGames();
	    
	    mSchedule = new SimpleAdapter(this, mylist, R.layout.main_menu_list_item,
	                new String[] {"game_name", "player_count", "distance"}, new int[] {R.id.game_name, R.id.player_count_list, R.id.distance});
	    lv.setAdapter(mSchedule);
	    	    
    };
	
    
    
	public static void addItemToList(String game_name, String player_count, String distance){
		HashMap<String, String> map = new HashMap<String, String>();
	    map.put("game_name", game_name);
	    map.put("player_count", player_count);
	    map.put("distance", distance);
	    mylist.add(map);
	    
	}
	
	
	
	public void updateList(){
		setListofGames();
		
		//test
		mylist.remove(2);
		mylist.remove(2);
		
	
		mSchedule.notifyDataSetChanged();
	}
	
	
	public static void setListofGames(){
		
		//delete list before set new list
		mylist.clear();
		
		//call integrator method
		
		//for every item: addItemToList
		
		//test data
		addItemToList("Unreal Tournament","1/8 Player","Distance to Creator: 0.8 km");
		addItemToList("Super Mario","4/5 Player","Distance to Creator: 1.5 km");
		addItemToList("Tekken","3/4 Player","Distance to Creator: 2 km");
		addItemToList("Halo","1/5 Player","Distance to Creator: 3.5 km");
		addItemToList("Resident Evil","4/7 Player","Distance to Creator: 4.5 km");
		
		
	}
	
	
	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu, menu);
	    
       
	    return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.update:
			updateList();
			return true;
		case R.id.new_game:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.NewGame.class),0);	
			return true;
		case R.id.view_map:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.Map.class),0);			
			return true;			
		case R.id.prefs:
			return true;						
		}
		return false;
	}
	
}
