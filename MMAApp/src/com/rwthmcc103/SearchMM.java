package com.rwthmcc103;



import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class SearchMM extends Activity implements OnSeekBarChangeListener {
	
	public static List<MediaItem> list = null;
	SeekBar seekLong;
	SeekBar seekLat;
	
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
        
        //Seekbars
        seekLong = (SeekBar)findViewById(R.id.seek_long); 
        seekLat = (SeekBar)findViewById(R.id.seek_lat); 
        seekLong.setOnSeekBarChangeListener(this);
        seekLat.setOnSeekBarChangeListener(this);
        
    
        
    }
    
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
    	TextView text_long_range = (TextView)findViewById(R.id.search_long);
        TextView text_lat_range = (TextView)findViewById(R.id.search_lat);
        if(seekBar.getId()== R.id.seek_long) text_long_range.setText("Longituderegion + "+progress);
        if(seekBar.getId()== R.id.seek_lat) text_lat_range.setText("Longituderegion + "+progress);
    }
    
      
    
    OnItemSelectedListener spinnerListener = new OnItemSelectedListener(){
	    public void onItemSelected(AdapterView<?> parent,View view, int pos, long id) {
	        
	    	//set visibilities
	    	
	        View edit = (View) findViewById(R.id.edit_search);
	        View seekLong = (View)findViewById(R.id.seek_long); 
	        View seekLat = (View)findViewById(R.id.seek_lat); 
	        View text_long = (View)findViewById(R.id.search_long); 
	        View text_lat = (View)findViewById(R.id.search_lat);
	        
	        if(pos == 0){
	        	edit.setVisibility(View.VISIBLE);
	        	seekLong.setVisibility(View.GONE);
	        	seekLat.setVisibility(View.GONE);
	        	text_long.setVisibility(View.GONE);
	        	text_lat.setVisibility(View.GONE);
	        }else{
	        	edit.setVisibility(View.GONE);
	        	seekLong.setVisibility(View.VISIBLE);
	        	seekLat.setVisibility(View.VISIBLE);
	        	text_long.setVisibility(View.VISIBLE);
	        	text_lat.setVisibility(View.VISIBLE);
	        }
	        
	    
	    	
	        }

		//@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
    };
    
    OnClickListener doSearchBtnOnClick = new OnClickListener() {		
		public void onClick(View view) {
				Intent intent = new Intent(SearchMM.this,com.rwthmcc103.Thumbnails.class);
		    	//intent.putExtra("RESULT", getResults());
		    	startActivityForResult(intent,0);
			}
	};
	
	public void getResults(){
		 
		EditText searchField = (EditText)findViewById(R.id.edit_search);
    	String searchString = searchField.getText().toString();
    	
		Spinner spinner = (Spinner)findViewById(R.id.spinner_search);
		//  first item: tags, second item: location
		long selectedItem = spinner.getSelectedItemPosition();
		
		CheckBox vidCheck = (CheckBox)findViewById(R.id.check_vids);
		CheckBox picCheck = (CheckBox)findViewById(R.id.check_pics);
		boolean vidsChecked = vidCheck.isChecked();
		boolean picsChecked = picCheck.isChecked();
		
		
		SeekBar long_range = (SeekBar)findViewById(R.id.seek_long);
        SeekBar lat_range = (SeekBar)findViewById(R.id.seek_lat);
        int progress_long = long_range.getProgress();
        int progress_lat = lat_range.getProgress();
                
        //TODO: At GPS Position
		if(selectedItem == 0){ //Tags
			if(((vidsChecked == true) && (picsChecked == true)) || (vidsChecked == false) && (picsChecked == false)){
				list = AwsIntegrator.getFilesByTag("all", searchString);
			}
			if((vidsChecked == true) && (picsChecked == false)){
				list = AwsIntegrator.getFilesByTag("video", searchString);
			}else{
				list = AwsIntegrator.getFilesByTag("picture", searchString);
			}
		}else{ //Localization
			if(((vidsChecked == true) && (picsChecked == true)) || (vidsChecked == false) && (picsChecked == false)){
				list = AwsIntegrator.getFilesByLocation("all", searchString, searchString, String.valueOf(progress_long), String.valueOf(progress_lat));
			}
			if((vidsChecked == true) && (picsChecked == false)){
				list = AwsIntegrator.getFilesByLocation("video", "long", "lat", String.valueOf(progress_long), String.valueOf(progress_lat));
			}else{
				list = AwsIntegrator.getFilesByLocation("picture", "long", "lat", String.valueOf(progress_long)  , String.valueOf(progress_lat));
			}
		}
		
	
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
}