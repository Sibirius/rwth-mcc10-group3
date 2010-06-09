package com.rwthmcc103;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SearchMM extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.search);        
    }
    
    public void doSearch(View v) {
    	//TODO: actually search
    	startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc103.Thumbnails.class),0);
    }
}