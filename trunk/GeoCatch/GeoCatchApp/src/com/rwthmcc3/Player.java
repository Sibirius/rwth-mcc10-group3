package com.rwthmcc3;




public class Player {
	
	private static  Player player = null;
	private  boolean isCreator = false;
	private  String name = null;
	private  double longitude = 0;
	private  double latitude = 0;
	private  String key = null;
	private  String hunterKey = null;
	private  String targetKey = null;
	private  double hunterLong = 0;
	private  double targetLong = 0;
	private  double hunterLat = 0;
	private  double targetLat = 0;
	private  Game myGame = null;
	private  int listSize;
	private  String mac;
	private boolean hasWin;
	
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
	
	
	
	
	public double distFromToPlayer(double lat1, double lng1) {
		double lat2 = getLatitude();
		double lng2 = getLongitude();
	    double earthRadius = 3958.75;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    int meterConversion = 1609;

	    return (dist * meterConversion);
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

	public void setLongitude(double d) {
		this.longitude = d;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLatitude(double d) {
		this.latitude = d;
	}

	public double getLatitude() {
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

	public double getHunterLong() {
		return hunterLong;
	}

	public void setTargetLong(float targetLong) {
		this.targetLong = targetLong;
	}

	public double getTargetLong() {
		return targetLong;
	}

	public void setHunterLat(float hunterLat) {
		this.hunterLat = hunterLat;
	}

	public double getHunterLat() {
		return hunterLat;
	}

	public void setTargetLat(float targetLat) {
		this.targetLat = targetLat;
	}

	public double getTargetLat() {
		return targetLat;
	}

	public void setListSize(int listSize) {
		this.listSize = listSize;
	}

	public int getListSize() {
		return listSize;
	}

	
	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getMac() {
		return mac;
	}

	public void setHasWin(boolean hasWin) {
		this.hasWin = hasWin;
	}

	public boolean isHasWin() {
		return hasWin;
	}

	
	

	


}
