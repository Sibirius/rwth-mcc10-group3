package com.rwthmcc3;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class GeoCatch extends Activity {
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.on_start);
		Button buttoNewGame = (Button)findViewById(R.id.button_new_game);
		Button buttonJoinGame = (Button)findViewById(R.id.button_join_game);
		buttoNewGame.setOnClickListener(doNewGameOnClick);
		buttonJoinGame.setOnClickListener(doJoinGameOnClick);
        //load list
        MainMenu.setListofGames();

	}

	
	OnClickListener doNewGameOnClick = new OnClickListener() {		
		public void onClick(View view) {
			startActivityForResult(new Intent(GeoCatch.this, com.rwthmcc3.NewGame.class),0);
			}
	};
	
	OnClickListener doJoinGameOnClick = new OnClickListener() {		
		public void onClick(View view) {
			startActivityForResult(new Intent(GeoCatch.this, com.rwthmcc3.MainMenu.class),0);
			}
	};
	
	
	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.geocatch_options_menu, menu);
	    
       
	    return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.geocatch_options_menu_new_game:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.NewGame.class),0);	
			return true;
		case R.id.geocatch_options_menu_view_map:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.Map.class),0);			
			return true;			
		case R.id.geocatch_options_menu_prefs:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.Preferences.class),0);
			return true;
		case R.id.geocatch_options_menu_help:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.Help.class),0);
			return true;	
		}
		return false;
	}
	
	

}