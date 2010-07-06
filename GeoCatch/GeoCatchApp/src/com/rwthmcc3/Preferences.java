package com.rwthmcc3;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	public static final String PREFS_NAME = "MyPrefsFile";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		addPreferencesFromResource(R.xml.preferences);
		
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("player_name")){
        	String playerName = sharedPreferences.getString("player_name", "Player 1");
        	if(playerName.startsWith(" ") || (playerName.length() < 4)){
        		Toast.makeText(getApplicationContext(), "Ungültiger Name! Name wurde zurückgesetzt!" , Toast.LENGTH_LONG).show();
        		SharedPreferences.Editor editor = sharedPreferences.edit();
        	    editor.putString("player_name", "Player 1");
        	    editor.commit();
        	    playerName = "Player 1";
        	}
        	Player.setName(playerName);
        }
        if(key.equals("list_size")){
        	int listSize = Integer.parseInt(sharedPreferences.getString("list_size", "10"));
        	Player.setListSize(listSize);
        }
        
    }
	
	
}
