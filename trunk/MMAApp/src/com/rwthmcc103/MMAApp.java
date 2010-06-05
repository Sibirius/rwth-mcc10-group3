package com.rwthmcc103;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.AdapterView.AdapterContextMenuInfo;



public class MMAApp extends Activity { 
	
    private Button thumbnailsButton;
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
        
        //TODO: call menu right away
        
        /*
        this.thumbnailsButton = (Button)this.findViewById(R.id.thumbnails);
        this.thumbnailsButton.setOnClickListener(new Button.OnClickListener() {         	
        	public void onClick (View view){ 
        		Intent myIntent = new Intent(view.getContext(), Thumbnails.class);
                startActivityForResult(myIntent, 0); 
        	}
        }); 
        */               
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }    

    public boolean onContextItemSelected(MenuItem item) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      switch (item.getItemId()) {
      case R.id.thumbnails:
    	  // TODO: switch to view
    	  return true;
      default:
        return super.onContextItemSelected(item);
      }
    }
}