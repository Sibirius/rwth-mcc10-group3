package com.rwthmcc3;




import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



public class NewGame extends Activity implements SeekBar.OnSeekBarChangeListener{
	
	SeekBar seekPlayerCount;
	private Player p = Player.getPlayer();
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_game);
       
        //SeekBar
        seekPlayerCount = (SeekBar)findViewById(R.id.seekbar_player_count); 
        seekPlayerCount.setOnSeekBarChangeListener(this);
        
        //spinner timer
        Spinner spinner = (Spinner) findViewById(R.id.spinner_new_game);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.array_times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        
        
        //button next
    	Button buttoNext = (Button)findViewById(R.id.button_next);
		buttoNext.setOnClickListener(doNextOnClick);
		
        
	}

	
	OnClickListener doNextOnClick = new OnClickListener() {		
		public void onClick(View view) {
			
			EditText gameNameField = (EditText)findViewById(R.id.edit_game_name);
			String gameName = gameNameField.getText().toString();
			SeekBar maxPlayerSeekbar = (SeekBar)findViewById(R.id.seekbar_player_count);
	        int maxPlayersCount = maxPlayerSeekbar.getProgress()+1;
	        Spinner timerSpinner = (Spinner)findViewById(R.id.spinner_new_game);
	        int timerPosition = timerSpinner.getSelectedItemPosition();
	        int timer = (timerPosition)*3*60; 
	        if(timerPosition==0)timer = 60;
	        
			if((!gameName.startsWith(" ")) && (!(gameName.length()< 3)) && (gameName != null)){
				//message to user
				boolean leave = true;
    			if(p.getMyGame()!=null){
    				
    				leave = Integrator.leaveGame(Player.getPlayer());
    				Log.d("LeaveInNewGame", String.valueOf(leave));
    			}
    	    			
			    boolean created = true;
			    //inconsistency
        		Integrator.leaveGame(Player.getPlayer());
        		
				created = Integrator.createGame(p, gameName , maxPlayersCount, 1, timer);
				Log.d("CreatedInNewGame", String.valueOf(created));
				
	        	if(created && leave){
	        		//new game created
	        		startActivityForResult(new Intent(NewGame.this, com.rwthmcc3.MainMenu.class),0);
	        	}else{
	        		
	        		Toast.makeText(NewGame.this, "Fehler! Bitte versuchen Sie es erneut!", Toast.LENGTH_SHORT).show();
	        	}
				
			}else{
								 
				//message to user
				Toast.makeText(NewGame.this, "Ungültiger Spielname", Toast.LENGTH_SHORT).show();
				
			}
		}
	};
        
               
    
    
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
    	TextView text_player_count = (TextView)findViewById(R.id.player_count_new_game);
    	progress+=1;  //set min to 1
        if(seekBar.getId()== R.id.seekbar_player_count) text_player_count.setText(progress+" Spieler");
    }

    
    
	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.newgame_options_menu, menu);
	    
       
	    return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_game_options_menu_main:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.MainMenu.class),0);			
			return true;			
		case R.id.new_game_options_menu_prefs:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.Preferences.class),0);
			return true;
		case R.id.new_game_options_menu_help:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.Help.class),0);
			return true;	
		}
		return false;
	}
        
    
    
    
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
	}
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
	}

	

	
}
