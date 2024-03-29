package com.rwthmcc103;



import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
		}
    };
    
	// open map view to select location
	public void doSelectLocation(View v) {
    	Intent intent = new Intent(this.getApplicationContext(), com.rwthmcc103.MMMapView.class);
    	
    	LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);    		
    	Location loc = lm.getLastKnownLocation("gps");
    	double lon = loc.getLongitude();
    	double lat = loc.getLatitude();
    	
    	intent.putExtra("edit", true);
    	intent.putExtra("lon", Double.toString(lon));
    	intent.putExtra("lat", Double.toString(lat));

    	this.startActivityForResult(intent, 1);
	}
	
	private double lon;
	private double lat;
	
/*    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 

    	Bundle result = data.getExtras();    	
    	lon = Double.valueOf(result.getString("lon"));
    	lat = Double.valueOf(result.getString("lat"));
    	
    	super.onActivityResult(requestCode, resultCode, data);
	}	
*/    
    OnClickListener doSearchBtnOnClick = new OnClickListener() {		
		public void onClick(View view) {
				//getResults();
				//AwsIntegrator aws = new AwsIntegrator();
				//list = aws.getFilesByTag("Picture", "eins zwei drei");
				list = AwsIntegrator.getSampleImages();
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
        
        AwsIntegrator aws = new AwsIntegrator();
        
        /*
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);            
        Location loc = lm.getLastKnownLocation("gps");

        double lon = loc.getLongitude();
        double lat = loc.getLatitude();
        */ 
        double lon = 50.7777210897;
        double lat = 6.0778105258;
        
		if(selectedItem == 0){ //Tags
			if(((vidsChecked == true) && (picsChecked == true)) || (vidsChecked == false) && (picsChecked == false)){
				list = aws.getFilesByTag("all", searchString);
			}
			else if((vidsChecked == true) && (picsChecked == false)){
				list = aws.getFilesByTag("video", searchString);
			}else{
				list = aws.getFilesByTag("picture", searchString);
			}
		}else{ //Localization
			   // multiply lon/lat with 10E10 to be comparable with values in database
			if(((vidsChecked == true) && (picsChecked == true)) || (vidsChecked == false) && (picsChecked == false)){
				list = aws.getFilesByLocation("all", new Double(lon*10E10).longValue(), new Double(lat * 10E10).longValue(), new Double(progress_long*10E4).longValue(), new Double(progress_lat*10E4).longValue());
			}
			else if((vidsChecked == true) && (picsChecked == false)){
				list = aws.getFilesByLocation("video", new Double(lon*10E10).longValue(), new Double(lat * 10E10).longValue(), new Double(progress_long*10E4).longValue(), new Double(progress_lat*10E4).longValue());
			}else{
				list = aws.getFilesByLocation("picture", new Double(lon*10E10).longValue(), new Double(lat * 10E10).longValue(), new Double(progress_long*10E4).longValue(), new Double(progress_lat*10E4).longValue());
			}
		}
		
	
		
	}

	public void onStartTrackingTouch(SeekBar seekBar) {		
	}

	public void onStopTrackingTouch(SeekBar seekBar) {		
	}
}