package com.rwthmcc3;




import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;



public class NewGame extends Activity implements SeekBar.OnSeekBarChangeListener{
	
	SeekBar seekPlayerCount;
	
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
                this, R.array.times_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        
        //button next
    	Button buttoNext = (Button)findViewById(R.id.button_next);
		buttoNext.setOnClickListener(doNextOnClick);
		
        
	}

	
	OnClickListener doNextOnClick = new OnClickListener() {		
		public void onClick(View view) {
			
			EditText gameNameField = (EditText)findViewById(R.id.edit_game_name);
			String gameName = gameNameField.toString();
			SeekBar maxPlayerSeekbar = (SeekBar)findViewById(R.id.seekbar_player_count);
	        int maxPlayersCount = maxPlayerSeekbar.getProgress() + 3;
			
			ProgressDialog dialog = ProgressDialog.show(NewGame.this, "", 
	                "Bitte warten...", true);
			if((!gameName.startsWith(" ")) || (gameName.length()< 3)){
				//message to user
			    Integrator.createGame(Player.getPlayer(),gameName , maxPlayersCount, 2, Player.getPlayer().getLongitude(), Player.getPlayer().getLatitude());
			    dialog.dismiss();
				startActivityForResult(new Intent(NewGame.this, com.rwthmcc3.WaitForPlayers.class),0);
			}else{
				 dialog.dismiss();
				//message to user
				Context context = getApplicationContext();
				CharSequence text = "Ungültiger Spielername";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
		}
	};
        
               
    
    
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
    	TextView text_player_count = (TextView)findViewById(R.id.player_count_new_game);
    	progress+=3;  //set min to 3
        if(seekBar.getId()== R.id.seekbar_player_count) text_player_count.setText(progress+" Spieler");
    }

    public class MyOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
          
        }

        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        }
    }
    
	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.new_game_options_menu, menu);
	    
       
	    return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_game_options_menu_view_map:
			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc3.Map.class),0);			
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
