package com.rwthmcc3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class WaitForPlayers extends Activity {

	private static ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter mSchedule;
	private Game chosenGame = MainMenu.chosenGame;
	private Player p = Player.getPlayer();
	private Handler myTimerHandler = new Handler();
	private Handler myUpdateHandler = new Handler();
	private long mStartTime = 0;

	// *******************************************************************************************************
	// Activity Actions
	// *******************************************************************************************************

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wait_for_players);

		ListView lv = (ListView) findViewById(R.id.listview_waitforplayers);
		lv.setTextFilterEnabled(true);

		mSchedule = new SimpleAdapter(this, mylist,
				R.layout.listofplayers_item, new String[] { "player_name" },
				new int[] { R.id.text_listofplayers });
		lv.setAdapter(mSchedule);

		TextView info = (TextView) findViewById(R.id.text_waitforplayers);
		info.setText("Warten auf Mitspieler:         "
				+ chosenGame.getPlayerCount() + "/"
				+ chosenGame.getMaxPlayersCount());
		
		// set button
		Button startGameButton = (Button) findViewById(R.id.button_start_game_waitforplayers);
		startGameButton.setOnClickListener(doStartGameButtonOnClick);

	}

	@Override
	public void onPause() {
		super.onPause();

		myUpdateHandler.removeCallbacks(mUpdateViewTask);
		myTimerHandler.removeCallbacks(mUpdateTimeTask);
	}

	@Override
	public void onResume() {
		super.onResume();

		// reset view
		View layoutWaitForPlayersView = (View) findViewById(R.id.layout3_waitforplayers);
		View listView = (View) findViewById(R.id.listview_waitforplayers);
		layoutWaitForPlayersView.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
		View startButtonView = (View) findViewById(R.id.button_start_game_waitforplayers);
		startButtonView.setVisibility(View.GONE);

		// not show waiting, when maxplayercount is reached
		View layoutView = (View) findViewById(R.id.layout2_waitforplayers);
		View lineView = (View) findViewById(R.id.line_waitforplayers);
		if (chosenGame.getPlayerCount() == chosenGame.getMaxPlayersCount()) {
			layoutView.setVisibility(View.GONE);
			lineView.setVisibility(View.GONE);
		} else {
			layoutView.setVisibility(View.VISIBLE);
			lineView.setVisibility(View.VISIBLE);
		}

		myUpdateHandler.removeCallbacks(mUpdateViewTask);
		myTimerHandler.removeCallbacks(mUpdateTimeTask);

		myUpdateHandler.postDelayed(mUpdateViewTask, 100);
		mStartTime = SystemClock.uptimeMillis();

	}

	// *******************************************************************************************************
	// Runnables
	// *******************************************************************************************************

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {

			try {
				TextView timerView = (TextView) findViewById(R.id.text_timer_waitforplayers);
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
							timerView.setText("Zeit bis zum Spielstart:" + "  "
									+ minutes + ":0" + seconds);
						} else {
							timerView.setText("Zeit bis zum Spielstart:" + "  "
									+ minutes + ":" + seconds);
						}

						// active for next update
						myTimerHandler.postAtTime(this, start + millis + 1000);
					} else {
						// counted to 0
						p.setTimerHasCountedDown(true);
						timerView.setVisibility(View.GONE);
						startActivityForResult(new Intent(WaitForPlayers.this,
								com.rwthmcc3.Map.class), 0);
					}
				}
			} catch (Exception e) {

			}
		}
	};

	private Runnable mUpdateViewTask = new Runnable() {
		public void run() {
			try {
				updatePlayerNames();
				updateViews();
				// active for next update
				myUpdateHandler.postDelayed(this, 15000);
			} catch (Exception e) {

			}

		}
	};

// *******************************************************************************************************
// 										set List / update views
// *******************************************************************************************************
	
	public void updateViews() {
		View layoutWaitForPlayersView = (View) findViewById(R.id.layout3_waitforplayers);
		View listView = (View) findViewById(R.id.listview_waitforplayers);
		layoutWaitForPlayersView.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		View startButtonView = (View) findViewById(R.id.button_start_game_waitforplayers);
		
		Integrator.playerUpdateState(p);

		if (p.getMyGame() != null) {
			int myGameState = p.getMyGame().getState();
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
				TextView timerView = (TextView) findViewById(R.id.text_timer_waitforplayers);
				timerView.setVisibility(View.VISIBLE);
				myUpdateHandler.removeCallbacks(mUpdateTimeTask);
				myUpdateHandler.postDelayed(mUpdateTimeTask, 100);
			}
		}else{
			startButtonView.setVisibility(View.GONE);
		}

	}

	/**
	 * Clears the playerList and updates playerList from server. Updates counts
	 * of players.
	 */
	public void updatePlayerNames() {

		mylist.clear();

		List<String> playerNames = Integrator.getPlayerList(chosenGame);

		HashMap<String, String> map = null;

		for (String i : playerNames) {
			map = new HashMap<String, String>();
			map.put("player_name", i);
			mylist.add(map);

		}

		mSchedule.notifyDataSetChanged();

		TextView info = (TextView) findViewById(R.id.text_waitforplayers);
		info.setText("Warten auf Mitspieler:         "
				+ chosenGame.getPlayerCount() + "/"
				+ chosenGame.getMaxPlayersCount());
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
			updatePlayerNames();
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
				Toast.makeText(WaitForPlayers.this, "Spiel wurde gestartet!",
						Toast.LENGTH_SHORT).show();

				// make button invisible
				View startButtonView = (View) findViewById(R.id.button_start_game_waitforplayers);
				startButtonView.setVisibility(View.GONE);

				// show timer and activate
				TextView timerView = (TextView) findViewById(R.id.text_timer_waitforplayers);
				timerView.setVisibility(View.VISIBLE);
				myTimerHandler.removeCallbacks(mUpdateTimeTask);
				myTimerHandler.postDelayed(mUpdateTimeTask, 100);
				mStartTime = SystemClock.uptimeMillis();

			} else {
				Toast.makeText(WaitForPlayers.this,
						"Fehler! Bitte versuchen Sie es erneut!",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
}
