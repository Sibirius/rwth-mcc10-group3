package com.rwthmcc103;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MetaEdit extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.metaedit);

	    Gallery g = (Gallery) findViewById(R.id.gallery);
	    g.setAdapter(new ImageAdapter(this));

	    g.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView parent, View v, int position, long id) {
	            //Toast.makeText(MetaEdit.this, "" + position, Toast.LENGTH_SHORT).show();
	        	readDB("sample_0.jpg");
	        	
	        }
	    });
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
    			break;
    		case R.id.upload:
    			break;
    	}
        return false;
    }
	
    //gets an fileId and put entries of the db in the textedit boxes
    public void readDB(String fileId){
    	
    	SQLiteDatabase db = null;
    	try{
    	     db = this.openOrCreateDatabase(MMAApp.MY_DB_NAME, MODE_PRIVATE, null);
    	     
    	   	 Cursor c = db.rawQuery("SELECT *" +    			  
	                  " FROM " + MMAApp.TABLE_NAME 
	                  + " WHERE name = '" + fileId + "';",
	                  null);
	    	 
	    	 if(c !=null){
	    		 
		    	 int titelColumn = c.getColumnIndex("name");
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
}
