package com.rwthmcc103;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


public class SearchMM extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.search);  
        //Button
        Button doSearchButton = (Button)findViewById(R.id.search_button);
        doSearchButton.setOnClickListener(doSearchBtnOnClick);
        //Spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner_search);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.search_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(spinnerListener);
    }
    
    OnItemSelectedListener spinnerListener = new OnItemSelectedListener(){
	    public void onItemSelected(AdapterView<?> parent,View view, int pos, long id) {
	        Toast.makeText(parent.getContext(), "The planet is " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
	    	
	    	//set which item is set (tags or location)
	    	
	        }

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
    };
    
    OnClickListener doSearchBtnOnClick = new OnClickListener() {		
		public void onClick(View view) {
				Intent intent = new Intent("com.rwthmcc103.Thumbnails.class");
		    	intent.putExtra("RESULT", getResults());
		    	startActivityForResult(intent,0);
			}
	};
	
	public MediaItem[] getResults(){
		
		
		
		
		return null;
		
	}
}