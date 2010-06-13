package com.rwthmcc103;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

	public class GalleryMM extends Activity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery); 
        
	    Gallery g = (Gallery) findViewById(R.id.gallery_gallery);
	    g.setAdapter(new ImageAdapter(this));
	    
	    //g.setOnItemClickListener(new OnItemClickListener() {
	    g.setOnItemSelectedListener(new OnItemSelectedListener() {
	        public void onItemSelected(AdapterView parent, View v, int position, long id) {
	        	showItemInformation(position);
	        }

			//@Override
			public void onNothingSelected(AdapterView parent) {
				showItemInformation(-1);
			}
	    });
    }
    
    private MediaItem currentMediaItem = getMediaItem(-1);
    
    // TODO set information of item
    // -1 stands for empty
    private void showItemInformation(int position) {
    	currentMediaItem = getMediaItem(position);
    	
    	TextView entryTitel = (TextView) findViewById(R.id.gallery_title);
    	TextView entryDescription = (TextView) findViewById(R.id.gallery_description);
    	TextView entryTags = (TextView) findViewById(R.id.gallery_tags);
    	
        entryTitel.setText(currentMediaItem.getTitle());
        entryDescription.setText(currentMediaItem.getDescription());
        entryTags.setText(currentMediaItem.getTags());       
    	
    	//Toast.makeText(GalleryMM.this, "" + position, Toast.LENGTH_SHORT).show();
    }
    
    private MediaItem getMediaItem(int position) {
    	// TODO get information
    	if (position == -1) {
    		return new MediaItem("", "-1", ""); // TODO: proper empty item
    	} else {
    		return new MediaItem("something", Integer.toString(position), "tag");	
    	}    	    	
    }

    private boolean correctLatLon(String s) {
    	return false; //TODO
    }
    
    // open a map screen and show the item location
    public void doViewLocation(View v) {
    	Intent intent = new Intent(this.getApplicationContext(), com.rwthmcc103.MMMapView.class);
    	
    	if (!correctLatLon(currentMediaItem.getLat()) || !correctLatLon(currentMediaItem.getLon())) { // dummy data if none in the item
    		
    		// DOES NOT WORK FOR SOME REASON, CENTER ON AACHEN INSTEAD BEEEP
    		// set current location
        	LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);    		
        	Location loc = lm.getLastKnownLocation("gps");
        	double lon = loc.getLongitude();
        	double lat = loc.getLatitude();

        	currentMediaItem.setLat(Double.toString(lat));
        	currentMediaItem.setLon(Double.toString(lon));

    		/*
        	currentMediaItem.setLat("50.77772108971944");
        	currentMediaItem.setLon("6.077810525894165");    		    		
        	*/
    	}    	
    	
    	//feed desired location into intent
    	intent.putExtra("edit", false);
    	intent.putExtra("lon", currentMediaItem.getLon());
    	intent.putExtra("lat", currentMediaItem.getLat());
    	
    	this.startActivityForResult(intent, 0);    	
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