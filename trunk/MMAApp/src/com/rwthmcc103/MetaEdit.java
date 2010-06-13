package com.rwthmcc103;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class MetaEdit extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.metaedit);

	    Gallery g = (Gallery) findViewById(R.id.gallery);
	    g.setAdapter(new ImageAdapter(this));

	    showItemData(0);
	    
	    g.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	//@Override
	        public void onItemSelected(AdapterView parent, View v, int position, long id) {
	        	showItemData(position);
	        }

			//@Override
			public void onNothingSelected(AdapterView parent) {
				showItemData(-1);
			}
	    });	    
	}
	
	//TODO implement
	// -1 position means empty
	private void showItemData(int position) {
        //TODO: find filename by position
    	readDB("sample_0.jpg");
	}
	
	// TODO
	public void doSetCurrentLocation(View v) {
		
	}

	// TODO open map view to select location
	public void doSelectLocation(View v) {
    	Intent intent = new Intent(this.getApplicationContext(), com.rwthmcc103.MMMapView.class);
    	
    	LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);    		
    	Location loc = lm.getLastKnownLocation("gps");
    	double lon = loc.getLongitude();
    	double lat = loc.getLatitude();
    	
    	intent.putExtra("edit", true);
    	intent.putExtra("lon", lon);
    	intent.putExtra("lat", lat);

    	this.startActivityForResult(intent, 1);
	}

	// TODO: saves the data for the currently displayed item
	// TODO: test if anything there
	public void doWriteItemData(View v) {
    	//TODO: find filename by position
    	writeDB("sample_0.jpg");
	}
	
	public void doUpload(View v) {
		
	}

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
    	// TODO: get maps lat lon & save
    	
    	super.onActivityResult(requestCode, resultCode, data);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.metaedit_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
    		case R.id.gps:
    			setGPS("sample_0.jpg");
    			return true;
    		case R.id.upload:
    			break;
    	}
        return false;
    }
	
    private void setGPS(String name) {
		//TODO:
    	LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    	Location loc = lm.getLastKnownLocation("gps");
    	double altitude = loc.getAltitude();
    	double latitude = loc.getLatitude();
    	
    	SQLiteDatabase db = null;
     	try{
     	     db = this.openOrCreateDatabase(MMAApp.MY_DB_NAME, MODE_PRIVATE, null);
     	     db.execSQL("UPDATE "+MMAApp.TABLE_NAME+" " +
     	     		"SET " +
     	     		"gps='"+String.valueOf(altitude)+" , "+String.valueOf(latitude) +
     	     		"WHERE name='"+name+"';");
     	     
     	} finally {
    	    if (db != null)
    	     	db.close();
    	}    
		
	}

	//gets an fileId and put entries of the db in the textedit boxes
    public void readDB(String name){
    	
    	SQLiteDatabase db = null;
    	try{
    	     db = this.openOrCreateDatabase(MMAApp.MY_DB_NAME, MODE_PRIVATE, null);
    	     
    	   	 Cursor c = db.rawQuery("SELECT *" +    			  
	                  " FROM " + MMAApp.TABLE_NAME 
	                  + " WHERE name = '" + name + "';",
	                  null);
	    	 
	    	 if(c != null){
	    		 
		    	 int titelColumn = c.getColumnIndex("titel");
		    	 int descriptionColumn = c.getColumnIndex("description");
		         int tagsColumn = c.getColumnIndex("tags");
		         
		         startManagingCursor(c);
		         c.moveToFirst();
		         
		         String titel = c.getString(titelColumn);
		         String description = c.getString(descriptionColumn);
		         String tags = c.getString(tagsColumn);
		         
		         
		         TextView entryTitel = (TextView) findViewById(R.id.entrytitel);
		         entryTitel.setText(titel);
		         
		         TextView entryDescription = (TextView) findViewById(R.id.entrydescription);
		         entryDescription.setText(description);
		         
		         TextView entryTags = (TextView) findViewById(R.id.entrytags);
		         entryTags.setText(tags);
		         
	    	 }
	    	 
	         
    	}finally {
    	    if (db != null)
    	     	db.close();
    	  }
    }
    
    public void writeDB (String name){
    	
    	EditText titel = (EditText)findViewById(R.id.entrytitel);
    	EditText description = (EditText)findViewById(R.id.entrydescription);
    	EditText tags = (EditText)findViewById(R.id.entrytags);
    	
     	SQLiteDatabase db = null;
     	try{
     	     db = this.openOrCreateDatabase(MMAApp.MY_DB_NAME, MODE_PRIVATE, null);
     	     db.execSQL("UPDATE "+MMAApp.TABLE_NAME+" " +
     	     		"SET " +
     	     		"titel='"+titel.getText().toString()+"', " +
     	     		"description='"+description.getText().toString()+"', " +
     	     		"tags='"+tags.getText().toString()+"'" +
     	     		"WHERE name='"+name+"'");
     	     
     	} finally {
    	    if (db != null)
    	     	db.close();
    	}    
    }
    
    
	
	public class ImageAdapter extends BaseAdapter {
	    int mGalleryItemBackground;
	    private Context mContext;

	    private Integer[] mImageIds = {
	            R.drawable.sample_1,
	            R.drawable.sample_2,
	            R.drawable.sample_3,
	            R.drawable.sample_4,
	            R.drawable.sample_5,
	            R.drawable.sample_6,
	            R.drawable.sample_7
	    };

	    public ImageAdapter(Context c) {
	        mContext = c;
	        TypedArray a = obtainStyledAttributes(R.styleable.Edit);
	        mGalleryItemBackground = a.getResourceId(
	                R.styleable.Edit_android_galleryItemBackground, 0);
	        a.recycle();
	    }

	    public int getCount() {
	        return mImageIds.length;
	    }

	    public Object getItem(int position) {
	        return position;
	    }

	    public long getItemId(int position) {
	        return position;
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView i = new ImageView(mContext);

	        i.setImageResource(mImageIds[position]);
	        i.setLayoutParams(new Gallery.LayoutParams(150, 100));
	        i.setScaleType(ImageView.ScaleType.FIT_XY);
	        i.setBackgroundResource(mGalleryItemBackground);

	        return i; 
	    }
	}	   
}
