package com.rwthmcc103;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class MMAApp extends Activity {
	
	static final int DIALOG_PHOTO_OR_VIDEO_ID = 0;
	
	public static final String MY_DB_NAME = "mmaapp";
	public static final String TABLE_NAME = "metatable";
	public static final String TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "name" + " TEXT, " +
                "titel" + " TEXT, " +
                "description" + " TEXT, " +
                "tags" + " TEXT, " +
                "gps" + " TEXT, " +	
                "isvideo" + " TEXT, " +
                "ispicture" + " TEXT" +");";
	
	//private boolean menuFlag = false;
    
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateDBAndDBTabled();
        setContentView(R.layout.main);
    }
    
	@Override
	public void onPanelClosed(int featureId, Menu menu) {
		super.onPanelClosed(featureId, menu);
		
		/*
		if (menu != null && !menuFlag) {
			terminate();			
		} else {
			menuFlag = false;
		}
		*/
	}

	public void terminate() {
    	super.onDestroy();
    	this.finish();
    }

	@Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch(id) {
        case DIALOG_PHOTO_OR_VIDEO_ID:
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("Photo or Video?")
        	       .setCancelable(true)
        	       .setPositiveButton("Photo", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   //TODO: get captured image and save it
        	    			Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);    			
        	    			startActivityForResult(intent, 1);
        	           }
        	       })
        	       .setNegativeButton("Video", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   //TODO: get captured video and save it        	        	   
        	    			Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);    			
        	    			startActivityForResult(intent, 1);
        	           }
        	       });
        	
        	dialog = builder.create();
        	
        	break;
        default:
            dialog = null;
        }
        return dialog;
    }
    
    //open menu permanently
    @Override 
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) openOptionsMenu();
    } 
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	//menuFlag = true;
    	
        switch (item.getItemId()) {
    		case R.id.camera:
    			showDialog(DIALOG_PHOTO_OR_VIDEO_ID);    			    			
    			return true;
    		case R.id.process:
    			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc103.MetaEdit.class),0);
    			return true;
    		case R.id.search:
    			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc103.SearchMM.class),0); 
    			return true;
        	case R.id.thumbnails:
        		startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc103.Thumbnails.class),0);
            	return true;
    		case R.id.gallery:
    			startActivityForResult(new Intent(this.getApplicationContext(), com.rwthmcc103.GalleryMM.class),0);
    			return true;
        }
        return false;
    }
    
    private void onCreateDBAndDBTabled(){
    	
    	SQLiteDatabase myDB = null;
    	try {
    		myDB = this.openOrCreateDatabase(MY_DB_NAME, MODE_PRIVATE, null);
    		myDB.execSQL(TABLE_CREATE);
    		
    		//fuellen
    		myDB.execSQL("INSERT INTO "+ TABLE_NAME +" (name, titel, description, tags, gps, isvideo, ispicture) "
					+ "VALUES ('sample_0.jpg', 'Sample 0', 'Samples', 'Picture', '32352, 43534' ,'false', 'true');");
    		myDB.execSQL("INSERT INTO "+ TABLE_NAME +" (name, titel, description, tags, gps, isvideo, ispicture) "
					+ "VALUES ('sample_1.jpg', 'Sample 1', 'Samples', 'Picture', '32352, 43534' ,'false', 'true');");
    		myDB.execSQL("INSERT INTO "+ TABLE_NAME +" (name, titel, description, tags, gps, isvideo, ispicture) "
					+ "VALUES ('sample_2.jpg', 'Sample 2', 'Samples', 'Picture', '32352, 43534' ,'false', 'true');");
    		myDB.execSQL("INSERT INTO "+ TABLE_NAME +" (name, titel, description, tags, gps, isvideo, ispicture) "
					+ "VALUES ('sample_3.jpg', 'Sample 3', 'Samples', 'Picture', '32352, 43534' ,'false', 'true');");
    		myDB.execSQL("INSERT INTO "+ TABLE_NAME +" (name, titel, description, tags, gps, isvideo, ispicture) "
					+ "VALUES ('sample_4.jpg', 'Sample 4', 'Samples', 'Picture', '32352, 43534' ,'false', 'true');");
    		myDB.execSQL("INSERT INTO "+ TABLE_NAME +" (name, titel, description, tags, gps, isvideo, ispicture) "
					+ "VALUES ('sample_5.jpg', 'Sample 5', 'Samples', 'Picture', '32352, 43534' ,'false', 'true');");
    		myDB.execSQL("INSERT INTO "+ TABLE_NAME +" (name, titel, description, tags, gps, isvideo, ispicture) "
					+ "VALUES ('sample_6.jpg', 'Sample 6', 'Samples', 'Picture', '32352, 43534' ,'false', 'true');");
        } finally {
             if (myDB != null)
                  myDB.close();
        }
    }
}