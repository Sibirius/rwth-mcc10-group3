package com.rwthmcc3;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {

		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		
		//set title
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        TextView leftTitle = (TextView)findViewById(R.id.left_text);
        TextView rightTitle = (TextView)findViewById(R.id.right_text);
        leftTitle.setText("GeoCatch");
        rightTitle.setText("Einstellungen");
		
		addPreferencesFromResource(R.xml.preferences);
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Player p = Player.getPlayer();
        if(key.equals("player_name")){
        	String playerName = sharedPreferences.getString("player_name", "Player 1");
        	if(playerName.startsWith(" ") || (playerName.length() < 4)){
        		Toast.makeText(getApplicationContext(), "Ung�ltiger Name! Name wurde zur�ckgesetzt!" , Toast.LENGTH_LONG).show();
        		SharedPreferences.Editor editor = sharedPreferences.edit();
        	    editor.putString("player_name", "Player 1");
        	    editor.commit();
        	    playerName = "Player 1";
        	}
        	boolean changed = Integrator.changePlayerName(p, playerName);
        	if(changed){
        		Toast.makeText(getApplicationContext(), "Name wurde gespeichert" , Toast.LENGTH_LONG).show();
        		
        	}else{
        		Toast.makeText(getApplicationContext(), "Fehler! Bitte versuchen Sie es erneut!" , Toast.LENGTH_LONG).show();
//        		SharedPreferences.Editor editor = sharedPreferences.edit();
//        	    editor.putString("player_name", "Player 1");
//        	    editor.commit();
        	}
        	
        }
        
        
    }
	
	
}
