package com.rwthmcc103;

import java.io.File;

public class MediaItem {
	
	private File file;
	private String id;
	private String filename;
	private Boolean isVideo;
	private String title;
	private String description;
	private String tags;
	private String lat;
	private String lon;
	
	public MediaItem() {		
	}
	
	public MediaItem(String title, String description, String tags) {
		this.title = title;
		this.description = description;
		this.tags = tags;
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public Boolean getIsVideo() {
		return isVideo;
	}
	
	public void setIsVideo(Boolean isVideo) {
		this.isVideo = isVideo;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getTags() {
		return tags;
	}
	
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public String getLat() {
		return lat;
	}
	
	public void setLat(String lat) {
		this.lat = lat;
	}
	
	public String getLon() {
		return lon;
	}
	
	public void setLon(String lon) {
		this.lon = lon;
	}

	
}
