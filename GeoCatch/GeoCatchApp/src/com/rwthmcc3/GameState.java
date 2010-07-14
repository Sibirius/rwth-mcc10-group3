package com.rwthmcc3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class GameState extends Activity {
	
	private static ArrayList<HashMap<String, String>> statesList = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter simpleAdatperStates;
	private static ArrayList<HashMap<String, String>> namesList = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter simpleAdatperNames;
	private Game updatedGame = null;
	private Player player = Player.getPlayer();
	private boolean runBackgroundThread = true;
	private Thread backgroundThread = null;
	
	
	
	
	//**********************************************************************
	//		Activity
	//**********************************************************************
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(false);
		setContentView(R.layout.game_state);
		
		
	
		// create listview for states
		ListView listViewStates = (ListView) findViewById(R.id.listview_gamestates);
		listViewStates.setTextFilterEnabled(true);
		
		simpleAdatperStates = new SimpleAdapter(this, statesList,	R.layout.game_state_stateslist_item, 
				new String[] { "description","value" }, 
				new int[] {R.id.textview_listitem_description, R.id.textview_listitem_value });
		
		listViewStates.setAdapter(simpleAdatperStates);
		
		// create listview for names
		ListView listViewNames = (ListView) findViewById(R.id.listview_gamestate_names);
		listViewNames.setTextFilterEnabled(true);
		
		simpleAdatperNames = new SimpleAdapter(this, namesList,	R.layout.game_state_nameslist_item, 
				new String[] { "name" }, 
				new int[] {R.id.textview_gamestate_nameslist_item});
		
		listViewNames.setAdapter(simpleAdatperNames);
		
		
				
		
		// set buttons
		Button startGameButton = (Button) findViewById(R.id.button_start_game_gamestate);
		Button stopGameButton = (Button) findViewById(R.id.button_stop_game_gamestate);
		Button leaveGameButton = (Button) findViewById(R.id.button_leave_game_gamestate);
		Button joinGameButton = (Button) findViewById(R.id.button_join_game_gamestate);
		startGameButton.setOnClickListener(doStartButtonOnClick);
		stopGameButton.setOnClickListener(doStopButtonOnClick);
		leaveGameButton.setOnClickListener(doLeaveButtonOnClick);
		joinGameButton.setOnClickListener(doJoinButtonOnClick);
		
	}	
	
	@Override
	public void onPause(){
		super.onPause();
		//destroy thread
		
		mHandler.removeCallbacks(mUpdateGameState);
		runBackgroundThread = false;
		
	}
	@Override
	public void onStop(){
		super.onStop();
		//destroy thread
		
		mHandler.removeCallbacks(mUpdateGameState);
		runBackgroundThread = false;
		
	}

	
	public void onResume(){
		super.onResume();
		
		//reset views to start
		resetViews();
		
		//start thread
		
		runBackgroundThread = true;
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
	//		Runnable
	//**********************************************************************
	
	// Need handler for callbacks to the UI thread
    final Handler mHandler = new Handler();

    // Create runnable for posting
    final Runnable mUpdateGameState = new Runnable() {
        public void run() {
        	updateViews();
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
            		
            		
            		try{
            			if(runBackgroundThread){//to kill fast
            				sleep(1000);
                        }
            			mHandler.post(mUpdateGameState);
                		if(runBackgroundThread){//to kill fast
                			sleep(15000);
                		}	
                    }catch(InterruptedException e) {
        				Log.d("threadInGameState", e.toString());
        			}
                Log.d("threadInGameState", "is running");    
        		}
            }
        };
        backgroundThread.start();
    }
    
	//**********************************************************************
	//		Views
	//**********************************************************************
	
    /**
     * Calls updateNames() and updateStates().
     * Checks their results and updates views 
     * or makes a message to user.
     */
    private void updateViews() {
    	
        // Back in the UI thread -- update our UI elements
    	setProgressBarIndeterminateVisibility(true);
    	boolean namesOk = updateNames();
    	boolean statesOk = updateStates();
    	
    	if(namesOk && statesOk){
    		makeUpdatedViewsVisible();
    	}else{
    		Toast.makeText(getApplicationContext(),"Verbindung zum Server fehlgeschlagen!", Toast.LENGTH_SHORT);
    	}
    }
    
    
    /**
     * Makes updated views visible by case.
     * (like show timer or buttons) 
     */
    private void makeUpdatedViewsVisible() {
    	View layoutLoadView = (View) findViewById(R.id.layout_load_gamestate);
    	View layoutStatesView = (View) findViewById(R.id.layout_states_gamestate);
    	View buttonStartView = (View) findViewById(R.id.button_start_game_gamestate);
    	View buttonStopView = (View) findViewById(R.id.button_stop_game_gamestate);
    	View buttonLeaveView = (View) findViewById(R.id.button_leave_game_gamestate);
    	View buttonJoinView = (View) findViewById(R.id.button_join_game_gamestate);
    	TextView textTimerView = (TextView) findViewById(R.id.textview_timer_gamestate);
    	
    	//reset buttons
    	buttonStartView.setVisibility(View.GONE);
    	buttonStopView.setVisibility(View.GONE);
    	buttonLeaveView.setVisibility(View.GONE);
    	buttonJoinView.setVisibility(View.GONE);
    	
    	//make loading invisible
    	layoutLoadView.setVisibility(View.GONE);
    	//show states
    	layoutStatesView.setVisibility(View.VISIBLE);
    	
    	//chosenGame is actual !!!
    	Game chosenGame = MainMenu.chosenGame;
    	Game myGame = player.getMyGame();
    	
    	//i'm in a game
    	if((myGame !=null) && (chosenGame!=null)){
    		Log.d("makeUpdatedViewsVisible", "is running");
    		String chosenGameKey = chosenGame.getKey();
    		String myGameKey = myGame.getKey();
    		//is game stopped or finished?
    		if((chosenGame.getState()==2) || (chosenGame.getState()==3) ){
    			Toast.makeText(GameState.this,"Spiel existiert nicht mehr!",Toast.LENGTH_SHORT).show();
    			startActivityForResult(new Intent(GameState.this,com.rwthmcc3.MainMenu.class), 0);
    			overridePendingTransition(R.anim.fade, R.anim.hold);
    		}
    		//is chosenGame my Game ? 
    		if(myGameKey.equals(chosenGameKey)){
    			//i'm creator ?
    			if(chosenGameKey.equals(player.getKeyOfMyCreatedGame())){
    				//enough players and not started?
    				if((chosenGame.getPlayerCount()==chosenGame.getMaxPlayersCount())&&chosenGame.getState()==0){
    					buttonStartView.setVisibility(View.VISIBLE);
    					buttonStopView.setVisibility(View.VISIBLE);
    				}else{//not enough players
    					buttonStopView.setVisibility(View.VISIBLE);
    				}
    			}else{//i'm not creator, but it is my game
    				buttonLeaveView.setVisibility(View.VISIBLE);
       			}
    			//show timer?
    			if((chosenGame.getState()==1) && (player.isTimerHasCountedDown()== false)){
    				long countDown = chosenGame.getCountDown();
    				int seconds = (int) (countDown);
    				int minutes = seconds / 60;
    				seconds = seconds % 60;
    				
    				if (countDown > 0) {
    					if (seconds < 10) {
    						Log.d("updateTimer","is running2");
    						textTimerView.setText("ungefähre Zeit bis zum Spielstart:" + "  "
    								+ minutes + ":0" + seconds);
    					} else {
    						textTimerView.setText("ungefähre Zeit bis zum Spielstart:" + "  "
    								+ minutes + ":" + seconds);
    					}
    				}
    				
    				textTimerView.setVisibility(View.VISIBLE);
    			}
    			if((chosenGame.getState()==1) && (player.isTimerHasCountedDown()== true)){
    					textTimerView.setVisibility(View.GONE);
    					//TODO start map
    					startActivityForResult(new Intent(GameState.this,com.rwthmcc3.Map.class), 0);
    					overridePendingTransition(R.anim.fade, R.anim.hold);
        		}
    			
       		}else{//not my Game 
       			//not enough players and not started?
       			if(!(chosenGame.getPlayerCount()==chosenGame.getMaxPlayersCount()) || !(chosenGame.getState()==0)){
       				buttonJoinView.setVisibility(View.VISIBLE);
       			}
       		}
    	}else{
	    	//i'm not in a game
	    	if(chosenGame != null){
	    		buttonJoinView.setVisibility(View.VISIBLE);
	    		//is game stopped or finished?
	    		if((chosenGame.getState()==2) || (chosenGame.getState()==3) ){
	    			Toast.makeText(GameState.this,"Spiel existiert nicht mehr!",Toast.LENGTH_SHORT).show();
	    			startActivityForResult(new Intent(GameState.this,com.rwthmcc3.MainMenu.class), 0);
	    			overridePendingTransition(R.anim.fade, R.anim.hold);
	    		}
	    	}
    	}
    	
    	setProgressBarIndeterminateVisibility(false);
	}

    
    
    
	/**
     * Resets all views in GameState.
     */
    private void resetViews(){
    	View layoutLoadView = (View) findViewById(R.id.layout_load_gamestate);
    	View layoutStatesView = (View) findViewById(R.id.layout_states_gamestate);
    	View buttonStartView = (View) findViewById(R.id.button_start_game_gamestate);
    	View buttonStopView = (View) findViewById(R.id.button_stop_game_gamestate);
    	View buttonLeaveView = (View) findViewById(R.id.button_leave_game_gamestate);
    	View buttonJoinView = (View) findViewById(R.id.button_join_game_gamestate);
    	View textTimerView = (View) findViewById(R.id.textview_timer_gamestate);
    	layoutLoadView.setVisibility(View.VISIBLE);
    	layoutStatesView.setVisibility(View.GONE);
    	buttonStartView.setVisibility(View.GONE);
    	buttonStopView.setVisibility(View.GONE);
    	buttonLeaveView.setVisibility(View.GONE);
    	buttonJoinView.setVisibility(View.GONE);
    	textTimerView.setVisibility(View.GONE);
    }
    
    
    
    
    /**
     * Makes a request and updates all names in namesList.
     * Notifies UI.
     * @return boolean: no errors
     */
    private boolean updateNames(){
    	boolean ok = true;
    	namesList.clear();

		List<String> playerNames = Integrator.getPlayerList(MainMenu.chosenGame);
		if(playerNames!=null){
			
			HashMap<String, String> map = null;
	
			for (String i : playerNames) {
				map = new HashMap<String, String>();
				map.put("name", i);
				namesList.add(map);
			}
		}else{
			ok= false;
		}
		simpleAdatperNames.notifyDataSetChanged();
		
		return ok;
	}
    /**
     * Makes a request and updates all states in statesList.
     * Notifies UI.
     * @return boolean: no errors
     */
    private boolean updateStates(){
    	boolean ok = true;
    	statesList.clear();

    	
    	updatedGame= Integrator.getGameState(MainMenu.chosenGame);
		
		
		if(updatedGame!=null){
			addItemToStatesList("Spielname:", updatedGame.getName());
			addItemToStatesList("Spieleranzahl:", updatedGame.getPlayerCount()+"/"+updatedGame.getMaxPlayersCount());
			addItemToStatesList("Timer:", String.valueOf(updatedGame.getTimer()/60)+" min");
			
			switch(updatedGame.getState()){
			case 0:
				addItemToStatesList("Spielstatus:", "nicht gestartet");
				break;
			case 1:
				addItemToStatesList("Spielstatus:", "gestartet");
				break;
			case 2:
				addItemToStatesList("Spielstatus:", "gestoppt");
				break;
			case 3:
				addItemToStatesList("Spielstatus:", "beendet");
				break;
			default:
				addItemToStatesList("Spielstatus:", "unbekannt");
				break;
			}
			
			//if updatedGame is my Game, then update my Game
			if(player.getMyGame()!=null){
				if(updatedGame.getKey().equals(player.getMyGame().getKey())){
					player.setMyGame(updatedGame);
				}
			}
				
		}else{
			ok=false;
		}

		simpleAdatperStates.notifyDataSetChanged();
		
		return ok;
    	
    }
    
    /**
	 * Adds a single item to the states-list.
	 * 
	 * @param description
	 * @param value
	 * 
	 */
	private static void addItemToStatesList(String description, String value) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("description", description);
		map.put("value", value);
		statesList.add(map);
	}
	
	//**********************************************************************
	//		onKlickListners
	//**********************************************************************
	
	OnClickListener doStartButtonOnClick = new OnClickListener() {
		public void onClick(View view) {
			boolean start = Integrator.startGame(Player.getPlayer());
			// check
			if (start) {
				Toast.makeText(GameState.this,"Spiel wurde gestartet!",Toast.LENGTH_SHORT).show();

				// make button invisible
				View startButtonView = (View) findViewById(R.id.button_start_game_gamestate);
				startButtonView.setVisibility(View.GONE);

				//update view works
				mHandler.post(mUpdateGameState);
				
			} else {
				Toast.makeText(GameState.this,"Fehler! Bitte versuchen Sie es erneut!",Toast.LENGTH_SHORT).show();
			}

		}
	};
	
	OnClickListener doStopButtonOnClick = new OnClickListener() {
		public void onClick(View view) {
			boolean leave = Integrator.leaveGame(Player.getPlayer());

			// check
			if (leave) {
				Toast.makeText(GameState.this,"Spiel wurde beendet!",Toast.LENGTH_SHORT).show();
				startActivityForResult(new Intent(GameState.this,com.rwthmcc3.MainMenu.class), 0);
				overridePendingTransition(R.anim.fade, R.anim.hold);
			} else {
				Toast.makeText(GameState.this,"Fehler! Bitte versuchen Sie es erneut!",Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	OnClickListener doLeaveButtonOnClick = new OnClickListener() {
		public void onClick(View view) {
			boolean leave = Integrator.leaveGame(Player.getPlayer());

			// check
			if (leave) {
				mHandler.post(mUpdateGameState);
				Toast.makeText(GameState.this,"Spiel wurde verlassen!",Toast.LENGTH_SHORT).show();
				
			} else {
				Toast.makeText(GameState.this,"Fehler! Bitte versuchen Sie es erneut!",Toast.LENGTH_SHORT).show();
			}
			
		}
	};
	
	OnClickListener doJoinButtonOnClick = new OnClickListener() {
		public void onClick(View view) {
			
			//leave before join
			boolean leave = true;
			if (player.getMyGame() != null) {
				leave = Integrator.leaveGame(Player.getPlayer());
			}

			boolean join = Integrator.joinGame(Player.getPlayer(), MainMenu.chosenGame);

			// check
			if (leave && join) {
				mHandler.post(mUpdateGameState);
				
				Toast.makeText(GameState.this,"Dem Spiel wurde beigetreten!",Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(GameState.this,"Fehler! Bitte versuchen Sie es erneut!",Toast.LENGTH_SHORT).show();
			}
		}
	};
}
