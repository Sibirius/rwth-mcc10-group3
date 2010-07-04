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
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class WaitForPlayers extends ListActivity {
	
	private static ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter mSchedule;
	private Game chosenGame = MainMenu.chosenGame;
	
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
	  final AlertDialog alert = builder.create();
	  
	  
	  // create a thread for updating the progress bar
      Thread background = new Thread (new Runnable() {
         public void run() {
             try {
                 while (chosenGame.getMaxPlayersCount()!= chosenGame.getPlayerCount()) {
                     // wait 
                     Thread.sleep(30000);

                     // active the update handler
                     progressHandler.sendMessage(progressHandler.obtainMessage());
                 }
                 alert.cancel();
                 Thread.sleep(100);
                 startActivityForResult(new Intent(WaitForPlayers.this, com.rwthmcc3.Map.class),0);	
             } catch (java.lang.InterruptedException e) {
                 // if something fails do something smart
             }
         }
         
      });
      
      background.start();
      alert.show();
	 }
	
	// handler for the background updating
    Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
        	updateList();
        	
        }
    };

	
	  
	public void updateList(){
		mylist.clear();
		List<Player> players = Integrator.getPlayerList(chosenGame);
		
		HashMap<String, String> map = null;
	    int playercount = 0;
	    for(Player i: players){
	    	map = new HashMap<String, String>();
			map.put("player_name", i.getPlayerName());
			mylist.add(map);
			playercount++;
		}
	    chosenGame.setPlayerCount(playercount);
		mSchedule.notifyDataSetChanged();
	}
	
	
}
