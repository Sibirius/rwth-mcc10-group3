package com.rwthmcc103;

import android.app.Activity;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;


public class ViewImage extends Activity{
	
	private static final String URL = "url";
	private Bundle extras = getIntent().getExtras();
	String url = extras.getString(URL);
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);		
		setContentView(R.layout.viewimage);
		Drawable image = ImageOperations(url,"image.jpg");		
		ImageView imgView = new ImageView(getBaseContext());			
		imgView = (ImageView)findViewById(R.id.image1);
		imgView.setImageDrawable(image);
		
	}	
	
	private Drawable ImageOperations(String url, String saveFilename) {
		try {			
			InputStream is = (InputStream) this.fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");						
			return d;
		} catch (MalformedURLException e) {			
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}

	public Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}	

}

