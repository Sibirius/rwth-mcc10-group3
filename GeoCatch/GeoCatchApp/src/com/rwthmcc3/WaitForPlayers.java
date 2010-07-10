package com.rwthmcc3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class WaitForPlayers extends Activity {
	
	private static ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter mSchedule;
	private Game chosenGame = MainMenu.chosenGame;
	private  Thread background = null;
	private boolean isAlive = true;
	private Player p = Player.getPlayer();
	
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
        	
        	View layoutMainMenuView = (View)findViewById(R.id.layout3_waitforplayers);
        	View listView = (View)findViewById(R.id.listview_waitforplayers);
        	layoutMainMenuView.setVisibility(View.GONE);
        	listView.setVisibility(View.VISIBLE);
        	
        	//compare keys
			boolean sameKey = true;
			String chosenGameKey = chosenGame.getKey();
			if(p.getMyGame()== null){
				sameKey = false;
			}else{
				String myGameKey = p.getMyGame().getKey();
				sameKey = chosenGameKey.equals(myGameKey);
			}
			
			//show start game button
        	if((sameKey && p.isCreator())&&(p.getMyGame().getPlayerCount()==p.getMyGame().getMaxPlayersCount())){
        		View startButtonView = (View)findViewById(R.id.button_start_game_waitforplayers);
        		startButtonView.setVisibility(View.VISIBLE);
            }	
        	
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
		
		//reset view 
		View layoutMainMenuView = (View)findViewById(R.id.layout3_waitforplayers);
    	View listView = (View)findViewById(R.id.listview_waitforplayers);
    	layoutMainMenuView.setVisibility(View.VISIBLE);
    	listView.setVisibility(View.GONE);
    	View startButtonView = (View)findViewById(R.id.button_start_game_waitforplayers);
		startButtonView.setVisibility(View.VISIBLE);
		
		//reset handler
		isAlive=true;
		
		// create a thread for updating the player_list
	    background = new Thread (new Runnable() {
	         public void run() {
	             try {
	            	 	while(isAlive){
	            	 	// wait 
		                     Thread.sleep(1000);
		                     // active the update handler
		                     progressHandler.sendMessage(progressHandler.obtainMessage());
		                     
		                     // wait 
		                     Thread.sleep(14000);
	            	 	}
	             		
	             } catch (java.lang.InterruptedException e) {
	                 // if something fails do something smart
	             }
	         }
	         
	      });
	      
	     background.start();
	     
	     //not show waiting, when maxplayercount is reached
	     View layoutView = (View)findViewById(R.id.layout2_waitforplayers);
	     View lineView = (View)findViewById(R.id.line_waitforplayers);
	     if(chosenGame.getPlayerCount()==chosenGame.getMaxPlayersCount()){
	    	 layoutView.setVisibility(View.GONE);
	    	 lineView.setVisibility(View.GONE);
	     }else{
	    	 layoutView.setVisibility(View.VISIBLE);
	    	 lineView.setVisibility(View.VISIBLE);
	     }
	}
	
	
}
