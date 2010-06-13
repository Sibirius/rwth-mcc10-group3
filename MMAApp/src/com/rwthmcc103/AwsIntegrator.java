package com.rwthmcc103;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
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
	private String bucketName = "7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media";
	private String myDomain = "mcc10group3media";

	public AwsIntegrator(){
		AWSCredentials credentials = new BasicAWSCredentials("*", "*");
		s3 = new AmazonS3Client(credentials);
		sdb = new AmazonSimpleDBClient(credentials);
	}	
	
	public AmazonSimpleDB getSDB(){
		return sdb;
	}
	
	public void uploadFile(MediaItem mItem, File mediaFile, File thumbnailFile){
		s3.putObject(new PutObjectRequest(bucketName, mItem.getFilename(), mediaFile));
        s3.setObjectAcl(bucketName, mItem.getFilename(), CannedAccessControlList.PublicRead);
		s3.putObject(new PutObjectRequest(bucketName, mItem.getThumbnailname(), thumbnailFile));
        s3.setObjectAcl(bucketName, mItem.getThumbnailname(), CannedAccessControlList.PublicRead);
        
        List<ReplaceableAttribute> data = new ArrayList<ReplaceableAttribute>();
        data.add(new ReplaceableAttribute().withName("Type").withValue((mItem.getIsVideo()) ? "Video" : "Picture"));
        data.add(new ReplaceableAttribute().withName("FileName").withValue(mItem.getFilename()));
        data.add(new ReplaceableAttribute().withName("ThumbnailName").withValue(mItem.getThumbnailname()));   
        data.add(new ReplaceableAttribute().withName("FileURI").withValue(mItem.getFileURI()));
        data.add(new ReplaceableAttribute().withName("ThumbnailURI").withValue(mItem.getThumbnailURI()));        
        data.add(new ReplaceableAttribute().withName("Title").withValue(mItem.getTitle()));
        data.add(new ReplaceableAttribute().withName("Description").withValue(mItem.getDescription()));
        
        // "Tags" is a multi-value attribute
        String[] tagNames = mItem.getTags().split(" ");
		for(String tag : tagNames){		
			data.add(new ReplaceableAttribute().withName("Tags").withValue(tag));
		}
		
		data.add(new ReplaceableAttribute().withName("Latitude").withValue("" + Double.parseDouble(mItem.getLat()) * 10E10));
		data.add(new ReplaceableAttribute().withName("Longitude").withValue("" + Double.parseDouble(mItem.getLon()) * 10E10)); 
		
		sdb.putAttributes(new PutAttributesRequest(myDomain, mItem.getId(), data));			
	}

	public List<MediaItem> getFilesByTag(String type, String tags){
		
		List<MediaItem> mItemList = new ArrayList<MediaItem>();
		String[] tagNames = tags.split(" ");
		
		String typeQuery = "Type = 'Picture' or Type = 'Video'";
		if (!type.equals("all")) typeQuery="Type = '" + type + "'"; 
		
		String tagQuery = "";
		for(int i = 0; i<tagNames.length; i++){
			if(i != 0) tagQuery += " or ";
			tagQuery += "Tags = '" + tagNames[i] + "'";
		}
		
		MediaItem mItem;
		String itemTags;
		String selectExpression = "select * from `" + myDomain + "` where (" + typeQuery + ") and ("+tagQuery+")";
		SelectRequest selectRequest = new SelectRequest(selectExpression);
		for (Item item : sdb.select(selectRequest).getItems()) {
		    
			mItem = new MediaItem();
			mItem.setId(item.getName());
			
			if (type == "video") mItem.setIsVideo(true);
			else mItem.setIsVideo(false);
			
			itemTags = "";
		    for (Attribute a : item.getAttributes()) {
		    	if(a.getName().equals("Title")) mItem.setTitle(a.getValue());
		    	else if(a.getName().equals("Description")) mItem.setDescription(a.getValue());
		    	else if(a.getName().equals("Tags")) itemTags += a.getValue() + " ";
		    	else if(a.getName().equals("FileName")) mItem.setFilename(a.getValue());
		    	else if(a.getName().equals("ThumbnailName")) mItem.setThumbnailname(a.getValue());	
		    	else if(a.getName().equals("FileURI")) mItem.setFileURI(a.getValue());	
		    	else if(a.getName().equals("ThumbnailURI")) mItem.setThumbnailURI(a.getValue());			    	
		    	else if(a.getName().equals("Latitude")) mItem.setLat(a.getValue());
		    	else if(a.getName().equals("Longitude")) mItem.setLon(a.getValue());
		    }
		    mItem.setTags(itemTags.trim());
		    		    
		    mItemList.add(mItem);

		}	
		
		return mItemList;
		
	}	
	
	public static List<MediaItem> getSampleImages(){
		MediaItem m;
		List<MediaItem> ml= new ArrayList<MediaItem>();
		
		m = new MediaItem();
		m.setId("sample0");
		m.setIsVideo(false);
		m.setTitle("Sample 0");
		m.setDescription("Image Sample Nr 0");
		m.setTags("eins zwei drei");
		m.setFileURI("http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/sample_0.jpg");
		m.setThumbnailURI("http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/sample_0_thumb.jpg");
		m.setLat("50.77772108971944");
		m.setLon("6.077810525894165");
		ml.add(m);

		m = new MediaItem();
		m.setId("sample1");
		m.setIsVideo(false);
		m.setTitle("Sample 1");
		m.setDescription("Image Sample Nr 1");
		m.setTags("eins zwei drei");
		m.setFileURI("http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/sample_1.jpg");
		m.setThumbnailURI("http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/sample_1_thumb.jpg");
		m.setLat("50.77772108971944");
		m.setLon("6.077810525894165");
		ml.add(m);
		
		m = new MediaItem();
		m.setId("sample2");
		m.setIsVideo(false);
		m.setTitle("Sample 2");
		m.setDescription("Image Sample Nr 2");
		m.setTags("eins zwei drei");
		m.setFileURI("http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/sample_2.jpg");
		m.setThumbnailURI("http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/sample_2_thumb.jpg");
		m.setLat("50.77772108971944");
		m.setLon("6.077810525894165");
		ml.add(m);
		
		m = new MediaItem();
		m.setId("sample3");
		m.setIsVideo(false);
		m.setTitle("Sample 3");
		m.setDescription("Image Sample Nr 3");
		m.setTags("eins zwei drei");
		m.setFileURI("http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/sample_3.jpg");
		m.setThumbnailURI("http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/sample_3_thumb.jpg");
		m.setLat("50.77772108971944");
		m.setLon("6.077810525894165");
		ml.add(m);
		
		m = new MediaItem();
		m.setId("sample4");
		m.setIsVideo(false);
		m.setTitle("Sample 4");
		m.setDescription("Image Sample Nr 4");
		m.setTags("eins zwei drei");
		m.setFileURI("http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/sample_4.jpg");
		m.setThumbnailURI("http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/sample_4_thumb.jpg");
		m.setLat("50.77772108971944");
		m.setLon("6.077810525894165");
		ml.add(m);
		
		m = new MediaItem();
		m.setId("sample5");
		m.setIsVideo(false);
		m.setTitle("Sample 5");
		m.setDescription("Image Sample Nr 5");
		m.setTags("eins zwei drei");
		m.setFileURI("http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/sample_5.jpg");
		m.setThumbnailURI("http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/sample_5_thumb.jpg");
		m.setLat("50.77772108971944");
		m.setLon("6.077810525894165");
		ml.add(m);
		
		m = new MediaItem();
		m.setId("sample6");
		m.setIsVideo(false);
		m.setTitle("Sample 6");
		m.setDescription("Image Sample Nr 6");
		m.setTags("eins zwei drei");
		m.setFileURI("http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/sample_6.jpg");
		m.setThumbnailURI("http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/sample_6_thumb.jpg");
		m.setLat("50.77772108971944");
		m.setLon("6.077810525894165");
		ml.add(m);	
		
		m = new MediaItem();
		m.setId("sample7");
		m.setIsVideo(false);
		m.setTitle("Sample 7");
		m.setDescription("Image Sample Nr 7");
		m.setTags("eins zwei drei");
		m.setFileURI("http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/sample_7.jpg");
		m.setThumbnailURI("http://7ecee678-7d24-4cae-8edc-a7bba5e391e7-mcc10group3media.s3.amazonaws.com/sample_7_thumb.jpg");
		m.setLat("50.77772108971944");
		m.setLon("6.077810525894165");
		ml.add(m);		
		
		return ml;

	}
	
	public List<MediaItem> getFilesByLocation(String type, long lon, long lat, long lonrg, long latrg){

		List<MediaItem> mItemList = new ArrayList<MediaItem>();
		
		String typeQuery = "Type = 'Picture' or Type = 'Video'";
		if (!type.equals("all")) typeQuery="Type = '" + type + "'"; 
		
		long latA = lat - latrg/2;
		long latB = lat + latrg/2;
		long lonA = lon - lonrg/2;
		long lonB = lon + lonrg/2;
		
		MediaItem mItem;
		String itemTags;
		String selectExpression = "select * from `" + myDomain + "` where (" + typeQuery + ") " +
									"and (Longitude between '"+ lonA + "' and '"+ lonB + "')" +
									"and (Latitude between '"+ latA + "' and '"+ latB +"') ";

		SelectRequest selectRequest = new SelectRequest(selectExpression);
		for (Item item : sdb.select(selectRequest).getItems()) {
		    
			mItem = new MediaItem();
			mItem.setId(item.getName());
			
			if (type == "video") mItem.setIsVideo(true);
			else mItem.setIsVideo(false);
			
			itemTags = "";
		    for (Attribute a : item.getAttributes()) {
		    	if(a.getName().equals("Title")) mItem.setTitle(a.getValue());
		    	else if(a.getName().equals("Description")) mItem.setDescription(a.getValue());
		    	else if(a.getName().equals("Tags")) itemTags += a.getValue() + " ";
		    	else if(a.getName().equals("FileName")) mItem.setFilename(a.getValue());
		    	else if(a.getName().equals("ThumbnailName")) mItem.setThumbnailname(a.getValue());	
		    	else if(a.getName().equals("FileURI")) mItem.setFileURI(a.getValue());	
		    	else if(a.getName().equals("ThumbnailURI")) mItem.setThumbnailURI(a.getValue());			    	
		    	else if(a.getName().equals("Latitude")) mItem.setLat(a.getValue());
		    	else if(a.getName().equals("Longitude")) mItem.setLon(a.getValue());
		    }
		    mItem.setTags(itemTags.trim());
		    		    
		    mItemList.add(mItem);

		}	
		
		return mItemList;
		
	}
	
}
