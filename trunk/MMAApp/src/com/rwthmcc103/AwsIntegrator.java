package com.rwthmcc103;

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
	
	public void uploadFile(MediaItem mItem){
		s3.putObject(new PutObjectRequest(bucketName, mItem.getFilename(), mItem.getFile()));
        s3.setObjectAcl(bucketName, mItem.getFilename(), CannedAccessControlList.PublicRead);
        
        List<ReplaceableAttribute> data = new ArrayList<ReplaceableAttribute>();
        data.add(new ReplaceableAttribute().withName("Type").withValue((mItem.getIsVideo()) ? "Video" : "Picture"));
        data.add(new ReplaceableAttribute().withName("FileName").withValue(mItem.getFilename()));
        data.add(new ReplaceableAttribute().withName("Title").withValue(mItem.getTitle()));
        data.add(new ReplaceableAttribute().withName("Description").withValue(mItem.getDescription()));
		data.add(new ReplaceableAttribute().withName("Tags").withValue(mItem.getTags()));
		data.add(new ReplaceableAttribute().withName("Latitude").withValue(mItem.getLat()));
		data.add(new ReplaceableAttribute().withName("Longitude").withValue(mItem.getLon())); 
		
		sdb.putAttributes(new PutAttributesRequest(myDomain, mItem.getId(), data));			
	}
	
	//TODO: Limit the number of items
	public List<MediaItem> getAllFiles(String type){
		
		List<MediaItem> mItemList = new ArrayList<MediaItem>();
		
		MediaItem mItem;
		String selectExpression = "select * from `" + myDomain + "` where Type = '"+type+"'";
		SelectRequest selectRequest = new SelectRequest(selectExpression);
		for (Item item : sdb.select(selectRequest).getItems()) {
		    
			mItem = new MediaItem();
			mItem.setId(item.getName());
			
			if (type == "video") mItem.setIsVideo(true);
			else mItem.setIsVideo(false);
			
		    for (Attribute a : item.getAttributes()) {
		    	if(a.getName().equals("FileName")) mItem.setFilename(a.getValue());
		    	else if(a.getName().equals("Title")) mItem.setTitle(a.getValue());
		    	else if(a.getName().equals("Description")) mItem.setDescription(a.getValue());
		    	else if(a.getName().equals("Tags")) mItem.setTags(a.getValue());
		    	else if(a.getName().equals("FileName")) mItem.setFilename(a.getValue());	    	
		    	else if(a.getName().equals("Latitude")) mItem.setLat(a.getValue());
		    	else if(a.getName().equals("Longitude")) mItem.setLon(a.getValue());
		    }
		    
		    mItemList.add(mItem);

		}	
		
		return mItemList;
	}
	
}
