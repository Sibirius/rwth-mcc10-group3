package com.rwthmcc3;


//singleton
public class Player {
	
	private static Player player = null;
	
	private boolean isMemberOfGame = false;
	private String name = "Player 1";
	private float longitude = 0;
	private float latitude = 0;
	private Player (){
		
	}
	//TODO get actual position
	
	
	public static Player getPlayer() {
	        if (player == null) {
	            player = new Player();
	        }
	        return player;
	}
	
	public void setPlayerName(String newName){
		name = newName;
	}
	
	public String getPlayerName(){
		return name;
	} 
	
	public void setMember(boolean is){
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
}
