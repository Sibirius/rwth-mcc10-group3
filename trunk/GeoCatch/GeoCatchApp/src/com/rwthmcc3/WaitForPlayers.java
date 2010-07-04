package com.rwthmcc3;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class WaitForPlayers extends ListActivity {
	
	private CountDownTimer counter;
	private static ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter mSchedule;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);

	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);
	  
	  mSchedule = new SimpleAdapter(this, mylist, R.layout.listofplayers_item,
              new String[] {"player_name"}, new int[] {R.id.text_listofplayers});
	  lv.setAdapter(mSchedule);
	  
	  /*counter =  new CountdownTimer(30000, 1000) {

		     public void onTick(long millisUntilFinished) {
		         mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
		     }

		     public void onFinish() {
		         mTextField.setText("done!");
		     }
		  };
	  */
	 }
	
	
}
