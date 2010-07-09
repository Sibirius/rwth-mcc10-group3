package com.rwthmcc3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class WaitForPlayers extends Activity {
	
	private static ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter mSchedule;
	private Game chosenGame = MainMenu.chosenGame;
	private  Thread background = null;
	private boolean isAlive = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.wait_for_players);
	 
	  ListView lv = (ListView)findViewById(R.id.listview_waitforplayers);
	  lv.setTextFilterEnabled(true);
	  
	  mSchedule = new SimpleAdapter(this, mylist, R.layout.listofplayers_item,
              new String[] {"player_name"}, new int[] {R.id.text_listofplayers});
	  lv.setAdapter(mSchedule);
	 
	  
	  TextView info = (TextView)findViewById(R.id.text_waitforplayers);
	  info.setText("Warten auf Mitspieler:         "+ chosenGame.getPlayerCount()+"/"+chosenGame.getMaxPlayersCount());
	  	 	  
	
     
	 }
	
	// handler for the background updating
    Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
        	updateScreen();
        	
        }
    };

	
	 /** Clears the playerList and updates playerList from server.
	  *  Updates counts of players.
	  */
	public void updateScreen(){
		
		mylist.clear();
		
		List<String> playerNames = Integrator.getPlayerList(chosenGame);
		
		HashMap<String, String> map = null;
	    
	    for(String i: playerNames){
	    	map = new HashMap<String, String>();
			map.put("player_name", i);
			mylist.add(map);
			
		}
	    
		mSchedule.notifyDataSetChanged();
		
		TextView info = (TextView)findViewById(R.id.text_waitforplayers);
		info.setText("Warten auf Mitspieler:         "+ chosenGame.getPlayerCount()+"/"+chosenGame.getMaxPlayersCount());
	}
	
	@Override
	public void onStop(){
		super.onStop();
		isAlive=false;;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		ProgressDialog dialog = ProgressDialog.show(this, "", 
                "Laden. Bitte warten...", true);
		updateScreen();
		dialog.dismiss();
		isAlive=true;
		// create a thread for updating the player_list
	    background = new Thread (new Runnable() {
	         public void run() {
	             try {
	            	 	while(isAlive){
		                      // wait 
		                     Thread.sleep(15000);
	
		                     // active the update handler
		                     progressHandler.sendMessage(progressHandler.obtainMessage());
	            	 	}
	             		
	             } catch (java.lang.InterruptedException e) {
	                 // if something fails do something smart
	             }
	         }
	         
	      });
	      
	     background.start();
	}
	
	
}
