package com.rwthmcc3;

public class Game {
	private String name;
	private String key;
	private int playerCount;
	private int maxPlayersCount;
	private float version;
	private float creatorLatitude;
	private float creatorLongitude;
	private int mode;
	
	
	// getter and setter
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getKey() {
		return key;
	}
	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}
	public int getPlayerCount() {
		return playerCount;
	}
	public void setMaxPlayersCount(int maxPlayersCount) {
		this.maxPlayersCount = maxPlayersCount;
	}
	public int getMaxPlayersCount() {
		return maxPlayersCount;
	}
	public void setVersion(float version) {
		this.version = version;
	}
	public float getVersion() {
		return version;
	}
	public void setCreatorLatitude(float creatorLatitude) {
		this.creatorLatitude = creatorLatitude;
	}
	public float getCreatorLatitude() {
		return creatorLatitude;
	}
	public void setCreatorLongitude(float creatorLongitude) {
		this.creatorLongitude = creatorLongitude;
	}
	public float getCreatorLongitude() {
		return creatorLongitude;
	} 
	public void setMode(int mode) {
		this.mode = mode;
	}
	public int getMode() {
		return mode;
	}
}
