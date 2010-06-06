package com.rwthmcc103;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MMAApp extends Activity { 	 
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
        
        //TODO: call menu right away
    }    
    
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
    		case R.id.camera:
    			break;
    		case R.id.process:
    			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc103.MetaEdit.class),0);
    			return true;
    		case R.id.search:
    			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc103.SearchMM.class),0); 
    			return true;
        	case R.id.thumbnails:
        		startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc103.Thumbnails.class),0);
            	return true;
    		case R.id.gallery:
    			break;
    			
        }
        return false;
    }
}