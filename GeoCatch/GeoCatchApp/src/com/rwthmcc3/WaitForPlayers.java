package com.rwthmcc3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class WaitForPlayers extends Activity {
	
	private static ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter mSchedule;
	private Game chosenGame = MainMenu.chosenGame;
	private  Thread background = null;
	private boolean isAlive = true;
	private Player p = Player.getPlayer();
	private TextView timerView = (TextView)findViewById(R.id.text_timer_waitforplayers);
	private Handler timeHandler = new Handler();
	
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
	private Runnable mUpdateTimeTask = new Runnable() {
 	   public void run() {
 	       final long start = SystemClock.uptimeMillis(); //set starttime to milliseconds
 	       long millis = SystemClock.uptimeMillis() - start;
 	       long countDown = p.getMyGame().getTimer()*60*1000 - millis;
 	       int seconds = (int) (countDown / 1000);
 	       int minutes = seconds / 60;
 	       seconds     = seconds % 60;
 	       
 	       if( countDown > 0){
	    	       if (seconds < 10) {
	    	    	   timerView.setText("" + minutes + ":0" + seconds);
	    	       } else {
	    	    	   timerView.setText("" + minutes + ":" + seconds);            
	    	       }
	    	     
	    	       //active for next update
	    	       timeHandler.postAtTime(this,
	    	               start + millis+1000);
 	       }else{
 	    	   //counted to 0
 	    	   p.setTimerHasCountedDown(true);
 	    	   timerView.setVisibility(View.GONE);
 	    	   startActivityForResult(new Intent(WaitForPlayers.this, com.rwthmcc3.Map.class),0);
 	       }
 	   }
 	};
	
	OnClickListener doStartGameButtonOnClick = new OnClickListener() {		
		public void onClick(View view) {
			boolean start = Integrator.startGame(Player.getPlayer());
			//check
    		if(start){
    			Toast.makeText(WaitForPlayers.this,"Spiel wurde gestartet!", Toast.LENGTH_SHORT).show();
    			
    			//make button invisible
    			View startButtonView = (View)findViewById(R.id.button_start_game_waitforplayers);
        		startButtonView.setVisibility(View.VISIBLE);
        		
        		//TODO show timer
    		}else{
    			Toast.makeText(WaitForPlayers.this,"Fehler! Bitte versuchen Sie es erneut!", Toast.LENGTH_SHORT).show();
    		}
			
		}
	};
	
	// handler for the background updating
    Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
        	updatePlayerNames();
        	updateViews();
        	
        	
        }
    };
    
    public void updateViews(){
    	View layoutMainMenuView = (View)findViewById(R.id.layout3_waitforplayers);
    	View listView = (View)findViewById(R.id.listview_waitforplayers);
    	layoutMainMenuView.setVisibility(View.GONE);
    	listView.setVisibility(View.VISIBLE);
    	
    	//update state
    	Integrator.playerUpdateState(p);
    	int myGameState = p.getMyGame().getState();
    	
    	//compare keys
		boolean sameKey = true;
		String chosenGameKey = chosenGame.getKey();
		if(p.getMyGame()== null){
			sameKey = false;
		}else{
			String myGameKey = p.getMyGame().getKey();
			sameKey = chosenGameKey.equals(myGameKey);
		}
		
		
    	//show start game button when player is creator,enough players and game not started
    	if((sameKey && p.isCreator())&&(p.getMyGame().getPlayerCount()==p.getMyGame().getMaxPlayersCount())&&(myGameState==0)){
    		View startButtonView = (View)findViewById(R.id.button_start_game_waitforplayers);
    		startButtonView.setVisibility(View.VISIBLE);
    	}
    	
    	//shows timer when game is started and timer hasn't counted down
    	if((myGameState == 1) && (!p.isCreator()) && p.isTimerHasCountedDown()){
    		
    		timerView.setVisibility(View.VISIBLE);
    		timeHandler.removeCallbacks(mUpdateTimeTask);
            timeHandler.postDelayed(mUpdateTimeTask, 100);
    	}
    }

	
	 /** Clears the playerList and updates playerList from server.
	  *  Updates counts of players.
	  */
	public void updatePlayerNames(){
		
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
