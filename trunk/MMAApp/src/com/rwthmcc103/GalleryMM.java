package com.rwthmcc103;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
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
    
    // TODO set information of item
    // -1 stands for empty
    private void showItemInformation(int position) {
    	MediaItem current = getMediaItem(position);
    	
    	TextView entryTitel = (TextView) findViewById(R.id.gallery_title);
    	TextView entryDescription = (TextView) findViewById(R.id.gallery_description);
    	TextView entryTags = (TextView) findViewById(R.id.gallery_tags);
    	
        entryTitel.setText(current.getTitle());
        entryDescription.setText(current.getDescription());
        entryTags.setText(current.getTags());        
    	
    	//Toast.makeText(GalleryMM.this, "" + position, Toast.LENGTH_SHORT).show();
    }
    
    private MediaItem getMediaItem(int position) {
    	// TODO get information
    	return new MediaItem("something", Integer.toString(position), "tag");
    }
    
    // TODO: open a map screen and show the item location
    public void doViewLocation(View v) {
    	Intent intent = new Intent(this.getApplicationContext(), com.rwthmcc103.MMMapView.class);
    	//TODO: feed desired location into intent
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