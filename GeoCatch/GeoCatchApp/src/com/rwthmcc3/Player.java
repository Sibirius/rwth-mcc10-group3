package com.rwthmcc3;


public class Player {
	
	private static Player player = null;
	private static boolean isCreator = false;
	private static String name = null;
	private static float longitude = 0;
	private static float latitude = 0;
	private static String key = null;
	private static String hunterKey = null;
	private static String targetKey = null;
	private static float hunterLong = 0;
	private static float targetLong = 0;
	private static float hunterLat = 0;
	private static float targetLat = 0;
	private static Game myGame = null;
	private static int listSize;
	
	private Player(){
		
	}
	
	//TODO get actual position
	
	
	public static Player getPlayer() {
		 	
	        if (player == null) {
	        	player = new Player();
	        }
	        return player;
	}
	
	public static void setPlayerName(String newName){
		setName(newName);
	}
	
	
	
	
	public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
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



	


	public static void setMyGame(Game game) {
		myGame = game;
	}

	public static Game getMyGame() {
		return myGame;
	}

	public static void setHunterKey(String hunterKey) {
		Player.hunterKey = hunterKey;
	}

	public static String getHunterKey() {
		return hunterKey;
	}

	public static void setTargetKey(String targetKey) {
		Player.targetKey = targetKey;
	}

	public static String getTargetKey() {
		return targetKey;
	}

	public static void setCreator(boolean isCreator) {
		Player.isCreator = isCreator;
	}

	public static boolean isCreator() {
		return isCreator;
	}

	public static void setLongitude(float longitude) {
		Player.longitude = longitude;
	}

	public static float getLongitude() {
		return longitude;
	}

	public static void setLatitude(float latitude) {
		Player.latitude = latitude;
	}

	public static float getLatitude() {
		return latitude;
	}

	public static void setName(String name) {
		Player.name = name;
	}

	public static String getName() {
		return name;
	}

	public static void setKey(String key) {
		Player.key = key;
	}

	public static String getKey() {
		return key;
	}

	public static void setListSize(int listSize) {
		Player.listSize = listSize;
	}

	public static int getListSize() {
		return listSize;
	}

	public static void setHunterLong(float hunterLong) {
		Player.hunterLong = hunterLong;
	}

	public static float getHunterLong() {
		return hunterLong;
	}

	public static void setTargetLong(float targetLong) {
		Player.targetLong = targetLong;
	}

	public static float getTargetLong() {
		return targetLong;
	}

	public static void setHunterLat(float hunterLat) {
		Player.hunterLat = hunterLat;
	}

	public static float getHunterLat() {
		return hunterLat;
	}

	public static void setTargetLat(float targetLat) {
		Player.targetLat = targetLat;
	}

	public static float getTargetLat() {
		return targetLat;
	}


}
