package com.rwthmcc103;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MMAApp extends Activity {
	
    private Button thumbnailsButton;
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.thumbnailsButton = (Button)this.findViewById(R.id.thumbnails);
        this.thumbnailsButton.setOnClickListener(new Button.OnClickListener() { 
        	
        	public void onClick (View view){ 
        		Intent myIntent = new Intent(view.getContext(), Thumbnails.class);
                startActivityForResult(myIntent, 0); 
        	}
        });
        
        
    }
}