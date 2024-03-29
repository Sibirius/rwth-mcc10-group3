package com.rwthmcc103;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	
	private ImageAdapter ada;
		
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery); 
        
        //AwsIntegrator aws = new AwsIntegrator();
        
	    Gallery g = (Gallery) findViewById(R.id.gallery_gallery);
	    ada = new ImageAdapter(this,AwsIntegrator.getSampleImages());	    
	    //ada = new ImageAdapter(this,aws.getAll("pics"));
	    g.setAdapter(ada);
	    
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
    
    // show information of item
    // -1 stands for empty
    private void showItemInformation(int position) {
    	currentMediaItem = getMediaItem(position);
    	
    	TextView entryTitel = (TextView) findViewById(R.id.gallery_title);
    	TextView entryDescription = (TextView) findViewById(R.id.gallery_description);
    	TextView entryTags = (TextView) findViewById(R.id.gallery_tags);
    	
        entryTitel.setText(currentMediaItem.getTitle());
        entryDescription.setText(currentMediaItem.getDescription());
        entryTags.setText(currentMediaItem.getTags());           	
    }
    
    private MediaItem getMediaItem(int position) {
    	if (position == -1) {
    		return new MediaItem("", "this should not be empty", "ever"); // proper empty item
    	} else {
    		//return new MediaItem("", "this should not be empty", "ever"); // proper empty item
    		return ada.pics.get(position);	
    	}    	    	
    }

    private boolean correctLatLon(String s) {
    	try {
    		Double.valueOf(s);    	
    	} catch(NumberFormatException e) {
    		return false;    
    	}
    	
    	return true;
    }
    
    // open a map screen and show the item location
    public void doViewLocation(View v) {
    	Intent intent = new Intent(this.getApplicationContext(), com.rwthmcc103.MMMapView.class);
    	
    	if (!correctLatLon(currentMediaItem.getLat()) || !correctLatLon(currentMediaItem.getLon())) { // dummy data if none in the item
    		
    		// works in emulator after 
    		// telnet localhost 5554
    		/// geo fix xx.xxx yy.yyy
    		
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

        List<MediaItem> pics;
        
        public ImageAdapter(Context c, List<MediaItem> p) {
            mContext = c;
            
            pics = p;
            
            TypedArray a = obtainStyledAttributes(R.styleable.Edit);
            mGalleryItemBackground = a.getResourceId(
                    R.styleable.Edit_android_galleryItemBackground, 0);
            a.recycle();
        }

        public int getCount() {
            return pics.size();
        }

        private Bitmap getRemoteImage(final URL aURL) {
            try {
                    final URLConnection conn = aURL.openConnection();
                    conn.connect();
                    final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                    final Bitmap bm = BitmapFactory.decodeStream(bis);
                    bis.close();
                    return bm;
            } catch (IOException e) {
                    //Log.d("DEBUGTAG", "Oh noooz an error...");
            }
            return null;
    }        
        
        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);

            Bitmap bmp;
			try {
				bmp = getRemoteImage(new URL(pics.get(position).getThumbnailURI()));
	            i.setImageBitmap(bmp);
			} catch (MalformedURLException e) {
	            i.setImageResource(R.drawable.logo);
			}
            
            i.setLayoutParams(new Gallery.LayoutParams(150, 100));
            i.setScaleType(ImageView.ScaleType.FIT_XY);
            i.setBackgroundResource(mGalleryItemBackground);

            return i;
        }
    }    
}