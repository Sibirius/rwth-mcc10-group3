package com.rwthmcc3;

import java.util.*;

public class Game {
	private String name;
	private String key;
	private int playerCount;
	private int maxPlayersCount;
	private int version;
	private float creatorLatitude;
	private float creatorLongitude;
	private int mode;
	private List<Player> playerList = new ArrayList<Player>();
	
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
	public void setPlayerList(List<Player> playerList) {
		this.playerList = playerList;
	}
	public List<Player> getPlayerList() {
		return playerList;
	}
	public void addToPlayerList(Player player) {
		this.playerList.add(player);
	}
	public void removeFromPlayerList(Player player) {
		this.playerList.remove(player);
	}
}
