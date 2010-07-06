package com.rwthmcc3;


public class Player {
	
	private static  Player player = null;
	private  boolean isCreator = false;
	private  String name = null;
	private  float longitude = 0;
	private  float latitude = 0;
	private  String key = null;
	private  String hunterKey = null;
	private  String targetKey = null;
	private  float hunterLong = 0;
	private  float targetLong = 0;
	private  float hunterLat = 0;
	private  float targetLat = 0;
	private  Game myGame = null;
	private  int listSize;
	
	private Player(){
		
	}
	
	//TODO get actual position
	
	
	public  static Player getPlayer() {
		 	
	        if (player == null) {
	        	player = new Player();
	        }
	        return player;
	}
	
	public  void setPlayerName(String newName){
		setName(newName);
	}
	
	
	
	
	public  float distFrom(float lat1, float lng1, float lat2, float lng2) {
	    double earthRadius = 3958.75;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    int meterConversion = 1609;

	    return new Float(dist * meterConversion).floatValue();
	}



	


	public  void setMyGame(Game game) {
		myGame = game;
	}

	public  Game getMyGame() {
		return myGame;
	}

	public void setCreator(boolean isCreator) {
		this.isCreator = isCreator;
	}

	public boolean isCreator() {
		return isCreator;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setHunterKey(String hunterKey) {
		this.hunterKey = hunterKey;
	}

	public String getHunterKey() {
		return hunterKey;
	}

	public void setTargetKey(String targetKey) {
		this.targetKey = targetKey;
	}

	public String getTargetKey() {
		return targetKey;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setHunterLong(float hunterLong) {
		this.hunterLong = hunterLong;
	}

	public float getHunterLong() {
		return hunterLong;
	}

	public void setTargetLong(float targetLong) {
		this.targetLong = targetLong;
	}

	public float getTargetLong() {
		return targetLong;
	}

	public void setHunterLat(float hunterLat) {
		this.hunterLat = hunterLat;
	}

	public float getHunterLat() {
		return hunterLat;
	}

	public void setTargetLat(float targetLat) {
		this.targetLat = targetLat;
	}

	public float getTargetLat() {
		return targetLat;
	}

	public void setListSize(int listSize) {
		this.listSize = listSize;
	}

	public int getListSize() {
		return listSize;
	}

	


}
