package com.rwthmcc3;

public class Game {
	private String name;
	private String key;
	private int playerCount;
	private int maxPlayersCount;
	private int version;
	private double creatorLatitude;
	private double creatorLongitude;
	private int mode;
	private int state;
	private int timer;
	private String winnerName;
	
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
	public void setVersion(int version) {
		this.version = version;
	}
	public int getVersion() {
		return version;
	}
	public void setCreatorLatitude(double e) {
		this.creatorLatitude = e;
	}
	public double getCreatorLatitude() {
		return creatorLatitude;
	}
	public void setCreatorLongitude(double d) {
		this.creatorLongitude = d;
	}
	public double getCreatorLongitude() {
		return creatorLongitude;
	} 
	public void setMode(int mode) {
		this.mode = mode;
	}
	public int getMode() {
		return mode;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getState() {
		return state;
	}
	public void setTimer(int timer) {
		this.timer = timer;
	}
	public int getTimer() {
		return timer;
	}
	public void setWinnerName(String winnerName) {
		this.winnerName = winnerName;
	}
	public String getWinnerName() {
		return winnerName;
	}
}
