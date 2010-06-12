package com.rwthmcc103;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;

public class AwsIntegrator {
	
	private AmazonS3 s3;
	private AmazonSimpleDB sdb;
	private PropertiesCredentials pC;
	private String bucketName = "7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media";
	private String myDomain = "mcc10group3media";

	public AwsIntegrator(PropertiesCredentials pC){
		this.pC = pC; 
		s3 = new AmazonS3Client(pC);
		sdb = new AmazonSimpleDBClient(pC);
	}	
	
	public void uploadFile(MediaItem mItem, File mediaFile, File thumbnailFile){
		s3.putObject(new PutObjectRequest(bucketName, mItem.getFileURI(), mediaFile));
        s3.setObjectAcl(bucketName, mItem.getFileURI(), CannedAccessControlList.PublicRead);
		s3.putObject(new PutObjectRequest(bucketName, mItem.getThumbnailURI(), thumbnailFile));
        s3.setObjectAcl(bucketName, mItem.getThumbnailURI(), CannedAccessControlList.PublicRead);
        
        List<ReplaceableAttribute> data = new ArrayList<ReplaceableAttribute>();
        data.add(new ReplaceableAttribute().withName("Type").withValue((mItem.getIsVideo()) ? "Video" : "Picture"));
        data.add(new ReplaceableAttribute().withName("FileName").withValue(mItem.getFileURI()));
        data.add(new ReplaceableAttribute().withName("ThumbnailName").withValue(mItem.getThumbnailURI()));        
        data.add(new ReplaceableAttribute().withName("Title").withValue(mItem.getTitle()));
        data.add(new ReplaceableAttribute().withName("Description").withValue(mItem.getDescription()));
		data.add(new ReplaceableAttribute().withName("Tags").withValue(mItem.getTags()));
		data.add(new ReplaceableAttribute().withName("Latitude").withValue(mItem.getLat()));
		data.add(new ReplaceableAttribute().withName("Longitude").withValue(mItem.getLon())); 
		
		sdb.putAttributes(new PutAttributesRequest(myDomain, mItem.getId(), data));			
	}

	public List<MediaItem> getFilesByTag(String type, String tags){
		
		List<MediaItem> mItemList = new ArrayList<MediaItem>();
		String[] tagNames = tags.split(" ");
		
		String typeQuery = "Type = 'picture' or Type = 'video'";
		if (!type.equals("all")) typeQuery="Type = '" + type + "'"; 
		
		String tagQuery = "";
		for(int i = 0; i<tagNames.length; i++){
			if(i != 0) tagQuery += " or ";
			tagQuery += "Tag = '" + tagNames[i] + "'";
		}
		
		MediaItem mItem;
		String selectExpression = "select * from `" + myDomain + "` where (" + typeQuery + ") and ("+tagQuery+")";
		SelectRequest selectRequest = new SelectRequest(selectExpression);
		for (Item item : sdb.select(selectRequest).getItems()) {
		    
			mItem = new MediaItem();
			mItem.setId(item.getName());
			
			if (type == "video") mItem.setIsVideo(true);
			else mItem.setIsVideo(false);
			
		    for (Attribute a : item.getAttributes()) {
		    	if(a.getName().equals("FileName")) mItem.setFileURI(a.getValue());
		    	else if(a.getName().equals("Title")) mItem.setTitle(a.getValue());
		    	else if(a.getName().equals("Description")) mItem.setDescription(a.getValue());
		    	else if(a.getName().equals("Tags")) mItem.setTags(a.getValue());
		    	else if(a.getName().equals("FileName")) mItem.setFileURI(a.getValue());	    	
		    	else if(a.getName().equals("Latitude")) mItem.setLat(a.getValue());
		    	else if(a.getName().equals("Longitude")) mItem.setLon(a.getValue());
		    }
		    
		    mItemList.add(mItem);

		}	
		
		return mItemList;
	}	
	
	public List<MediaItem> getFilesByLocation(String type, String lon, String lat, String lonrg, String latrg){

		return new ArrayList<MediaItem>();
		
	}
	
}
