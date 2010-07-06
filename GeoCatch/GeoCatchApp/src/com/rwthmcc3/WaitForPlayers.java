package com.rwthmcc3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class WaitForPlayers extends ListActivity {
	
	private static ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter mSchedule;
	private Player p = Player.getPlayer();
	private Game chosenGame = p.getMyGame();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);

	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);
	  
	  mSchedule = new SimpleAdapter(this, mylist, R.layout.listofplayers_item,
              new String[] {"player_name"}, new int[] {R.id.text_listofplayers});
	  lv.setAdapter(mSchedule);
	  updateList();
	  
	  
	  
	  AlertDialog.Builder builder = new AlertDialog.Builder(this);
	  builder.setMessage("Warten auf Mitspieler...")
	         .setCancelable(false)
	         .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int id) {
	                  dialog.cancel();
	                  Integrator.leaveGame(Player.getPlayer());
	                  WaitForPlayers.this.finish();
	             }
	         });
	  final AlertDialog waitForPlayers = builder.create();
	  
	  
	  // create a thread for updating the player_list
      Thread background = new Thread (new Runnable() {
         public void run() {
             try {
                 while (chosenGame.getMaxPlayersCount()!= chosenGame.getPlayerCount()) {
                     // wait 
                     Thread.sleep(30000);

                     // active the update handler
                     progressHandler.sendMessage(progressHandler.obtainMessage());
                 }
                 waitForPlayers.cancel();
                 Thread.sleep(100);
                 
                 //TODO start map or wait on started game	
             } catch (java.lang.InterruptedException e) {
                 // if something fails do something smart
             }
         }
         
      });
      
      background.start();
      waitForPlayers.show();
	 }
	
	// handler for the background updating
    Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
        	updateList();
        	
        }
    };

	
	 /** Clears the playerList and updates playerList from server.
	  * 
	  */
	public void updateList(){
		
		mylist.clear();
		
		List<String> playerNames = Integrator.getPlayerList(p.getMyGame());
		
		HashMap<String, String> map = null;
	    
	    for(String i: playerNames){
	    	map = new HashMap<String, String>();
			map.put("player_name", i);
			mylist.add(map);
			
		}
	    
		mSchedule.notifyDataSetChanged();
	}
	
	
}
