package com.rwthmcc3;

import java.io.*;
import java.net.URI;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;

/**
 * Integrator-Class for communication between Android and AppEngine
 *
 */
public class Integrator {
	
	private static String AppEngineURL = "rwth-mcc10-group3.appspot.com";
	private static String LOGTAG = "Integrator";
	
	public static String mainPage(){
		Log.d(LOGTAG, "mainPage()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        HttpResponse res = doGet("/", qparams);
        try {
			return res.getEntity().getContent().toString();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public static List<Game> getGameList(){
		Log.d(LOGTAG, "getGameList()");
		List<Game> result = new ArrayList<Game>();
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        HttpResponse res = doGet("/games", qparams);
        if(res != null){
	        try {
				Log.d(LOGTAG, "start parsing gamelist");
	        	Document doc = parseXml(res.getEntity().getContent());
				NodeList nodes = doc.getElementsByTagName("game");
			    for (int i = 0; i < nodes.getLength(); i++) {
			    	Log.d(LOGTAG, "parsing element #"+i);
			    	Element element = (Element) nodes.item(i);
			    	
			    	Game game = new Game();
			    	
			    	game.setName(element.getAttribute("name"));
			    	game.setKey(element.getAttribute("key"));
			    	game.setPlayerCount(Integer.parseInt(element.getAttribute("playerCount")));
			    	game.setMaxPlayersCount(Integer.parseInt(element.getAttribute("maxPlayersCount")));
			    	game.setVersion(Integer.parseInt(element.getAttribute("version")));
			    	String [] creatorLocation = element.getAttribute("creatorLocation").split(",");
			    	game.setCreatorLatitude(Float.parseFloat(creatorLocation[0]));
			    	game.setCreatorLongitude(Float.parseFloat(creatorLocation[1]));
			    	game.setMode(Integer.parseInt(element.getAttribute("mode")));
			    	game.setTimer(Integer.parseInt(element.getAttribute("timer")));
			    	
			    	result.add(game);
		
			     }
			    Log.d(LOGTAG,"getGameList success");
			    return result;
	
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }else{
        	Log.w(LOGTAG, "No Response from Server");
        }
		Log.e(LOGTAG, "NullPointer, getGameList");
		return null;
	}
	

	public static List<String> getPlayerList(Game game){
		Log.d(LOGTAG, "getPlayerList()");
		if(game != null){
		List<String> result = new ArrayList<String>();
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("g", game.getKey()));
	        HttpResponse res = doGet("/gamePlayers", qparams);
	        if(res != null){
		        try {
					Log.d(LOGTAG, "start parsing playerlist");
		        	Document doc = parseXml(res.getEntity().getContent());
					NodeList nodes = doc.getElementsByTagName("player");
				    for (int i = 0; i < nodes.getLength(); i++) {
				    	Log.d(LOGTAG, "parsing element #"+i);
				    	Element element = (Element) nodes.item(i);
				    	
				    	result.add(element.getAttribute("name"));
	
				     }
				    Log.d(LOGTAG,"getPlayerList success");
				    game.setPlayerCount(nodes.getLength());
				    return result;
		
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }else{
	        	Log.w(LOGTAG, "No Response from Server");
	        }
		}else{
			Log.w(LOGTAG, "Nullpointer Argument game,getPlayerList");
		}
		Log.e(LOGTAG, "NullPointer, getPlayerList");
		return null;
	}
	
	public static List<String> getGameState(Player player){
		Log.d(LOGTAG, "getGameState()");
		Game game = player.getMyGame();
		if(game != null){
		List<String> result = new ArrayList<String>();
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("g", game.getKey()));
			qparams.add(new BasicNameValuePair("p", player.getKey()));
	        HttpResponse res = doGet("/gameState", qparams);
	        if(res != null){
		        try {
					Log.d(LOGTAG, "start parsing playerlist");
		        	Document doc = parseXml(res.getEntity().getContent());
					
		        	NodeList nodes = doc.getElementsByTagName("game");
					Element element = (Element) nodes.item(0);
					if(element != null){
						game.setName(element.getAttribute("name"));
						game.setMode(Integer.parseInt(element.getAttribute("mode")));
						game.setTimer(Integer.parseInt(element.getAttribute("name")));
						game.setState(Integer.parseInt(element.getAttribute("name")));
						game.setMaxPlayersCount(Integer.parseInt(element.getAttribute("name")));
					}
		        	
					
		        	nodes = element.getElementsByTagName("player");
				    for (int i = 0; i < nodes.getLength(); i++) {
				    	Log.d(LOGTAG, "parsing element #"+i);
				    	element = (Element) nodes.item(i);
				    	
				    	result.add(element.getAttribute("name"));
				     }
				    
				    nodes = doc.getElementsByTagName("additional");
				    element = (Element) nodes.item(0);
				    if(element != null){
				    	player.setNumber(Integer.parseInt(element.getAttribute("playerNumber")));
				    	
				    }
				    
				    Log.d(LOGTAG,"getGameState success");
				    game.setPlayerCount(nodes.getLength());
				    return result;
		
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }else{
	        	Log.w(LOGTAG, "No Response from Server");
	        }
		}else{
			Log.w(LOGTAG, "Nullpointer Argument game,getGameState");
		}
		Log.e(LOGTAG, "NullPointer, getPlayerList");
		return null;
	}
	
	public static boolean joinGame(Player player, Game game){
		Log.d(LOGTAG, "joinGame()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        qparams.add(new BasicNameValuePair("g", game.getKey()));
        qparams.add(new BasicNameValuePair("lon", String.valueOf(player.getLongitude())));
        qparams.add(new BasicNameValuePair("lat", String.valueOf(player.getLatitude())));
        
        String result = getResponse(doGet("/join", qparams));
        Log.d(LOGTAG, "joinGame: "+result);
        
        player.setTimerHasCountedDown(false);
        player.setMyGame(game);
        game.setPlayerCount(game.getPlayerCount()+1);
        
        return !result.contains("error");
	}
	
	public static boolean stopGame(Player player){
		Log.d(LOGTAG, "stopGame()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
                
        String result = getResponse(doGet("/stop", qparams));
        Log.d(LOGTAG, "stopGame: "+result);
        if(result.contains("error"))
        	return false;
        else{
        	resetPlayer(player);
        	return true;
        }
	}
	
	private static void resetPlayer(Player player) {
		player.setTimerHasCountedDown(false);
    	player.setMyGame(null);
    	player.setCreator(false);
    	player.setHasWin(false);
	}


	public static boolean startGame(Player player){
		Log.d(LOGTAG, "startGame()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        
        String result = getResponse(doGet("/start", qparams));
        Log.d(LOGTAG, "startGame: "+result);
        return !result.contains("error");
	}
	
	public static boolean leaveGame(Player player){
		Log.d(LOGTAG, "leaveGame()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        
        resetPlayer(player);
        
        String result = getResponse(doGet("/leave", qparams));
        Log.d(LOGTAG, "leaveGame: "+result);
        return !result.contains("error");
	}
	
	/**
	 * 
	 * @param player
	 * @return true if everthing went fine, false otherwise
	 * 
	 * Updates the players longitude and latitude in the database.
	 * Updates Targetlocation, gamemode and gamestate locally.
	 * If there is a winner, then the game is finished and the winnerName is saved in the game-instance.
	 * If the player has won the game, hasWin is set to true.
	 * 
	 */
	public static boolean playerUpdateState(Player player){
		Log.d(LOGTAG, "playerUpdateState()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        qparams.add(new BasicNameValuePair("lon", String.valueOf(player.getLongitude())));
        qparams.add(new BasicNameValuePair("lat", String.valueOf(player.getLatitude())));
        
        HttpResponse res = doGet("/update", qparams);
        if(res != null){
	        try {
	        	
	        	Log.d(LOGTAG, "start parsing gamestate");
	        	Document doc = parseXml(res.getEntity().getContent());
	        	
	        	NodeList node = doc.getElementsByTagName("state");
	        	Element ele = (Element) node.item(0);
	        	if(ele != null){
		        	player.setTargetLong(Float.parseFloat(ele.getAttribute("lon")));
		        	player.setTargetLat(Float.parseFloat(ele.getAttribute("lat")));
		        	player.getMyGame().setMode(Integer.parseInt(ele.getAttribute("mode")));
		        	player.getMyGame().setState(Integer.parseInt(ele.getAttribute("state")));
	        	}
				NodeList nodes = doc.getElementsByTagName("event");
			    for (int i = 0; i < nodes.getLength(); i++) {
			    	Log.d(LOGTAG, "parsing element #"+i);
			    	Element element = (Element) nodes.item(i);
			    	
			    	String title = element.getAttribute("title");
			    	String info = element.getAttribute("info");
			    	String extra = element.getAttribute("extra");
			    	
			    	if(title=="victory"){
			    		player.getMyGame().setState(3);
			    		player.getMyGame().setWinnerName(info);
			    		if(Integer.parseInt(extra)==player.getNumber())
			    			player.setHasWin(true);
			    	}
			    	
		
			     }
			    Log.d(LOGTAG,"playerUpdateState success");
			    return true;
	
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }else{
        	Log.w(LOGTAG, "No Response from Server");
        }
		Log.e(LOGTAG, "NullPointer, playerUpdateState");
		return false;
        
	}
	
	public static boolean registerPlayer(String mac, String name, double longitude, double latitude){
		Log.d(LOGTAG, "registerPlayer()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("m", mac));
        qparams.add(new BasicNameValuePair("n", name));
        qparams.add(new BasicNameValuePair("lon", String.valueOf(longitude)));
        qparams.add(new BasicNameValuePair("lat", String.valueOf(latitude)));
       
        String result = getResponse(doGet("/register", qparams));
        Log.d(LOGTAG, "registerPlayer: "+result);
        if(result.contains("error")){
        	return false;
        }else{
        	Player.getPlayer().setKey(result);
        	Player.getPlayer().setPlayerName(name);
        	Player.getPlayer().setLongitude(longitude);
        	Player.getPlayer().setLatitude(latitude);
        	return true;
        }

	}
	
	public static boolean createGame(Player player, String name, int maxPlayersCount, int version, int timer){
		Log.d(LOGTAG, "createGame()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        qparams.add(new BasicNameValuePair("n", name));
        qparams.add(new BasicNameValuePair("mpc", String.valueOf(maxPlayersCount)));
        qparams.add(new BasicNameValuePair("v", String.valueOf(version)));
        qparams.add(new BasicNameValuePair("lon", String.valueOf(player.getLongitude())));
        qparams.add(new BasicNameValuePair("lat", String.valueOf(player.getLatitude())));
        qparams.add(new BasicNameValuePair("t", String.valueOf(timer)));
        
        
        String key = getResponse(doGet("/create", qparams));
        
        if(key.contains("error"))
        	return false;
        else{
	        Game game = new Game();
	        game.setName(name);
	        game.setCreatorLongitude(player.getLongitude());
	        game.setCreatorLatitude(player.getLatitude());
	        game.setVersion(version);
	        game.setKey(key);
	        game.setMaxPlayersCount(maxPlayersCount);
	        game.setPlayerCount(1);
	        game.setTimer(timer);
	        
	        player.setTimerHasCountedDown(false);
	        player.setCreator(true);
	        player.setMyGame(game);
	        
	        Log.d(LOGTAG, "createGame success");
	        return true;
        }
        
	}
	
	
	// DEBUG FUNCTIONS
	
	public static void fillWithTestData(){
		Log.d(LOGTAG, "fillWithTestData()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        doGet("/fillWithTestData", qparams);
	}
	
	public static void clearData(){
		Log.d(LOGTAG, "clearData");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        doGet("/clearData", qparams);
	}
	
	
	private static HttpResponse doGet(String path, List<NameValuePair> qparams){
		Log.d(LOGTAG, "doGet()");
		try{
			HttpClient client = new DefaultHttpClient();  	        
	        URI uri = URIUtils.createURI("http", AppEngineURL, -1, path, 
	        		URLEncodedUtils.format(qparams, "UTF-8"), null);
	        Log.d(LOGTAG, "doGet uri: "+uri.toString());
	        HttpGet httpget = new HttpGet(uri);
	        Log.d(LOGTAG, "doGet nearly done");
	        HttpResponse response = client.execute(httpget);
	        Log.d(LOGTAG, "doGet success");
	        return response;
		} catch (Exception e) {
            e.printStackTrace();
        }
		Log.e(LOGTAG, "NullPointer, doGet");
		return null;
		
	}
	
	private static String getResponse(HttpResponse res){
		Log.d(LOGTAG, "getResponse()");
		if(res != null){
	        try {
				Log.d(LOGTAG, "parsing response");
	        	Document doc = parseXml(res.getEntity().getContent());
	        	NodeList nodes = doc.getElementsByTagName("response");
	        	Element element = (Element) nodes.item(0);
	        	String result;
	        	if(element!=null)
	        		result = element.getAttribute("value");
	        	else
	        		result = "error";
				Log.d(LOGTAG,"getResponse success");
				
		    	return result;
		    	
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }else{
        	Log.w(LOGTAG, "No Response from Server");
        }
		Log.e(LOGTAG, "NullPointer, getResponse");
		return "error";
	}
	
	private static Document parseXml(InputStream xmlStream){
		Log.d(LOGTAG, "parseXml()");
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			Document doc = db.parse(xmlStream);
			Log.d(LOGTAG,"parseXml success");
			return doc;
			

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		Log.e(LOGTAG, "NullPointer, parseXml");
		return null;
	}

}
