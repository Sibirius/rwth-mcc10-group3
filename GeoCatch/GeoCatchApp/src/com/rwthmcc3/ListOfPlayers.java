package com.rwthmcc3;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListOfPlayers extends ListActivity {
	
	
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  List<String> playerList = Integrator.getPlayerList(MainMenu.chosenGame);
	  String[] arrayOfPlayers = (String[])playerList.toArray(new String[playerList.size()]);
	  

	  setListAdapter(new ArrayAdapter<String>(this, R.layout.listofplayers_item, arrayOfPlayers));

	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);

	  
	}
}
