package com.rwthmcc3;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;


public class GeoCatch extends Activity {
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.on_start);
		LinearLayout layout = (LinearLayout)findViewById(R.id.on_start_layout);
        layout.setOnClickListener(doLayoutOnClick);
        
        //load list
        MainMenu.setListofGames();

	}

	
	OnClickListener doLayoutOnClick = new OnClickListener() {		
		public void onClick(View view) {
			startActivityForResult(new Intent(GeoCatch.this, com.rwthmcc3.MainMenu.class),0);
			}
	};

}