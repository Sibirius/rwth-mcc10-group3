package com.rwthmcc3;


//singleton
public class Player {
	
	private static Player player = null;
	
	private boolean isMemberOfGame = false;
	private String name = "Player 1";
	
	private Player (){
		
	}
	
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
}
