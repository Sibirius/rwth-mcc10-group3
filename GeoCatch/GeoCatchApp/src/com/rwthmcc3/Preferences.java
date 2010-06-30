package com.rwthmcc3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Preferences extends Activity {

	private static String playerName = "Player 1";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		
		//config button
		Button buttonSavePlayerName = (Button)findViewById(R.id.button_save_player_name);
		buttonSavePlayerName.setOnClickListener(doSavePlayerNameOnClick);
		
		//config editbox and show playername
		EditText savePlayerName = (EditText)findViewById(R.id.edit_player_name);
		savePlayerName.setText(Player.getPlayer().getPlayerName());
		
	}
	
	

	OnClickListener doSavePlayerNameOnClick = new OnClickListener() {		
		public void onClick(View view) {
			
			//read newname and set
			EditText savePlayerName = (EditText)findViewById(R.id.edit_player_name);
			String newName = savePlayerName.getText().toString();
			Player.getPlayer().setPlayerName(newName);
			
			//message to user and update name on server
			if(Player.getPlayer().isMemberOfGame()==true){
				    ProgressDialog dialog = ProgressDialog.show(Preferences.this, "", 
			                "Spielername wird mit Server synchronisiert. Bitte warten...", true);
				    //TODO update playername on server
				    dialog.dismiss();
			}
			//TODO catch error
		    
			//message to user
			Context context = getApplicationContext();
			CharSequence text = "Name wurde gespeichert!";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			
			
			}
	};
	
	
}
