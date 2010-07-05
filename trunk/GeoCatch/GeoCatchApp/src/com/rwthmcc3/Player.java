package com.rwthmcc3;

import android.bluetooth.*;

public class Player {
	
	private static Player player = null;
	
	private boolean isCreator = false;
	private boolean isMemberOfGame = false;
	private String name = "Player 1";
	private float longitude = (float) 6.087310;
	private float latitude = (float) 50.768295;
	private String key;
	private Player hunter;
	private Player target;
	private static Game myGame;
	
	
	/*private Player (){
		
	}*/
	//TODO get actual position
	
	
	public static Player getPlayer() {
	        if (player == null) {
	        	String mac = BluetoothAdapter.getDefaultAdapter().getAddress();
	            player = Integrator.registerPlayer(mac, "Player5");
	        }
	        return player;
	}
	
	public void setPlayerName(String newName){
		name = newName;
	}
	
	public String getPlayerName(){
		return name;
	} 
	
	public void setIsMember(boolean is){
		isMemberOfGame = is;
	}
	
	public boolean isMemberOfGame(){
		return isMemberOfGame;
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


	public void setCreator(boolean isCreator) {
		this.isCreator = isCreator;
	}


	public boolean isCreator() {
		return isCreator;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
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

	public void setHunter(Player hunter) {
		this.hunter = hunter;
	}

	public Player getHunter() {
		return hunter;
	}

	public void setTarget(Player target) {
		this.target = target;
	}

	public Player getTarget() {
		return target;
	}

	public static void setMyGame(Game game) {
		myGame = game;
	}

	public static Game getMyGame() {
		return myGame;
	}

}
