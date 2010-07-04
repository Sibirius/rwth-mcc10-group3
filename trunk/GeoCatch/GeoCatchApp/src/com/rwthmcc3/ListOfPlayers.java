package com.rwthmcc3;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListOfPlayers extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);

	  setListAdapter(new ArrayAdapter<String>(this, R.layout.listofplayers_item, MainMenu.arrayOfPlayers));

	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);

	  
	}
}
