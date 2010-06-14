package com.rwthmcc103;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.james.mime4j.message.TextBody;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsSpinner;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class MetaEdit extends Activity implements OnItemClickListener{

	private Cursor mCursor;
	private AbsSpinner mGallery;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.metaedit);
	    mGallery = (Gallery)findViewById(R.id.gallery);
	    
	    displayGallery();
	}
	
	private void displayGallery() {
		Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI; // Where images are store
		String[] projection = {
				MediaStore.Images.ImageColumns._ID,  // The columns we want
				MediaStore.Images.Thumbnails.IMAGE_ID,  
				MediaStore.Images.Thumbnails.KIND };
		String selection = MediaStore.Images.Thumbnails.KIND + "="  + // Select only mini's
		MediaStore.Images.Thumbnails.MINI_KIND;
		mCursor = this.managedQuery(uri, projection, selection, null, null);	
		if (mCursor != null) { 
			mCursor.moveToFirst();
			ImageAdapter adapter = new ImageAdapter(mCursor, this);
			mGallery.setAdapter(adapter);
			mGallery.setOnItemClickListener(this);
		}
	}
	
	//TODO implement
	// -1 position means empty
	private void showItemData(int position) {
        //TODO: find filename by position
    	readDB("sample_0.jpg");
	}
		
	public void doSetCurrentLocation(View v) {
    	LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);    		
    	Location loc = lm.getLastKnownLocation("gps");
    	double lon = loc.getLongitude();
    	double lat = loc.getLatitude();
    	
    	setLocation(lon,lat);
	}
	
	public void setLocation(double lon, double lat) {
		// TODO
	}

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

	// TODO: saves the data for the currently displayed item
	// TODO: test if anything there
	public void doWriteItemData(View v) {
    	//TODO: find filename by position
    	writeDB("sample_0.jpg");
	}
	
	public void doUpload(View v) {
		MediaItem m = new MediaItem();
		
        TextView entryTitel = (TextView) findViewById(R.id.entrytitel);        
        TextView entryDescription = (TextView) findViewById(R.id.entrydescription);                
        TextView entryTags = (TextView) findViewById(R.id.entrytags);
        
        m.setTitle(entryTitel.getText().toString());
        m.setDescription(entryDescription.getText().toString());
        m.setTags(entryTags.getText().toString());
        
        
        File file = new File("path/to/your/file.txt"); //TODO set this to the right file path
        
        try {
             HttpClient client = new DefaultHttpClient();  
             String postURL = "http://ec2-79-125-28-225.eu-west-1.compute.amazonaws.com/AwsTranscode/UploadServlet"; //TODO set right url to upload
             HttpPost post = new HttpPost(postURL);
                 
             FileBody bin = new FileBody(file);             
             
             MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
             
             reqEntity.addPart("title", new StringBody(m.getTitle()));
             reqEntity.addPart("description", new StringBody(m.getDescription()));
             reqEntity.addPart("tags", new StringBody(m.getTags()));
             reqEntity.addPart("file", bin);                          
             
             post.setEntity(reqEntity);  
             HttpResponse response = client.execute(post);  
             HttpEntity resEntity = response.getEntity();  
             if (resEntity != null) {    
                       //Log.i("RESPONSE",EntityUtils.toString(resEntity));
                 }
        } catch (Exception e) {
            e.printStackTrace();
        }        
	}

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
    	// get maps lat lon & save
    	Bundle result = data.getExtras();    	
    	setLocation(Double.valueOf(result.getString("lon")),Double.valueOf(result.getString("lat")));
    	
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
	
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long rowId) {
		mCursor.moveToPosition(position);
		long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.IMAGE_ID));
		//create the Uri for the Image 
		Uri uri = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, id+"");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(uri);
		startActivity(intent);
	} 
    
	public class ImageAdapter extends BaseAdapter {
	
		int mGalleryItemBackground;
		private Context mContext;
		private Cursor mCursor;
		private static final String TAG = "ImageAdapter";


	    public ImageAdapter(Cursor cursor, Context c) {
	        mContext = c;
	        mCursor = cursor;
	        // See res/values/attrs.xml for the  defined values here for styling
	        TypedArray a = mContext.obtainStyledAttributes(R.styleable.Edit);
	        mGalleryItemBackground = a.getResourceId(
	                R.styleable.Edit_android_galleryItemBackground, 0);
	        a.recycle();

	    }

	    public int getCount() {
	      return mCursor.getCount();
	    }

	    public Object getItem(int position) {
	        return position;
	    }

	    public long getItemId(int position) {
	        return position;
	    }

	    /**
	     * Called repeatedly to render the View of each item in the gallery.
	     */
	    public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
	    	mCursor.requery();
	    	  	
	    	 if (convertView == null) {
	    		mCursor.moveToPosition(position);
	    		int id = mCursor.getInt(mCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID));
	    		Uri uri = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, ""+id);
	 //   		Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ""+id);
    			i.setImageURI(uri);
    			i.setScaleType(ImageView.ScaleType.FIT_XY);
    			i.setLayoutParams(new Gallery.LayoutParams(136, 136));
    			i.setBackgroundResource(mGalleryItemBackground);
	    	}
	    	return i;
	    }
	  }
	
	}
