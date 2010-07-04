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
			    	//TODO game.setPlayerList(getPlayerList(game));
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
	
	public static List<Player> getPlayerList(Game game){
		Log.d(LOGTAG, "getPlayerList()");
		List<Player> result = new ArrayList<Player>();
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
			    	
			    	Player player = new Player();
			    	
			    	player.setPlayerName(element.getAttribute("name"));
			    	player.setCreator(Boolean.parseBoolean(element.getAttribute("creator")));
			    	player.setIsMember(true);
			    	//TODO lon, lat, key
			    	result.add(player);
		
			     }
			    Log.d(LOGTAG,"getPlayerList success");
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
	
	public static void joinGame(Player player, Game game){
		Log.d(LOGTAG, "joinGame()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        qparams.add(new BasicNameValuePair("g", game.getKey()));
        
        Log.d(LOGTAG, "joinGame: "+getResponse(doGet("/join", qparams)));
        
        player.setIsMember(true);
        game.addToPlayerList(player);
        game.setPlayerCount(game.getPlayerCount()+1);
	}
	
	//TODO
	public static void stopGame(Player player){
		Log.d(LOGTAG, "stopGame()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        
        Log.d(LOGTAG, "stopGame: "+getResponse(doGet("/stop", qparams)));
	}
	
	//TODO
	public static void startGame(Player player){
		Log.d(LOGTAG, "startGame()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        
        Log.d(LOGTAG, "startGame: "+getResponse(doGet("/start", qparams)));
	}
	
	public static void leaveGame(Player player){
		Log.d(LOGTAG, "leaveGame()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        
        Log.d(LOGTAG, "leaveGame: "+getResponse(doGet("/leave", qparams)));
        
        player.setIsMember(false);
        //game.removeFromPlayerList(player);
        
        Log.d(LOGTAG, "leaveGame success");
	}
	
	public static void playerUpdateState(Player player){
		Log.d(LOGTAG, "playerUpdateState()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        qparams.add(new BasicNameValuePair("lon", String.valueOf(player.getLongitude())));
        qparams.add(new BasicNameValuePair("lat", String.valueOf(player.getLatitude())));
        
        //TODO get updated state
        doGet("/update", qparams);
	}
	
	public static Player registerPlayer(String mac, String name){
		Log.d(LOGTAG, "registerPlayer()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("m", mac));
        qparams.add(new BasicNameValuePair("n", name));
       
        Player player = new Player();
        player.setKey(getResponse(doGet("/register", qparams)));
        player.setPlayerName(name);
        Log.d(LOGTAG, "registerPlayer success");
        return player;
	}
	
	public static Game createGame(Player player, String name, int maxPlayersCount, int version, float creatorLongitude, float creatorLatitude){
		Log.d(LOGTAG, "createGame()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        qparams.add(new BasicNameValuePair("n", name));
        qparams.add(new BasicNameValuePair("mpc", String.valueOf(maxPlayersCount)));
        qparams.add(new BasicNameValuePair("v", String.valueOf(version)));
        qparams.add(new BasicNameValuePair("lon", String.valueOf(creatorLongitude)));
        qparams.add(new BasicNameValuePair("lat", String.valueOf(creatorLatitude)));
        
        String key = getResponse(doGet("/create", qparams));
        
        Game game = new Game();
        game.setName(name);
        game.setCreatorLongitude(creatorLongitude);
        game.setCreatorLatitude(creatorLatitude);
        game.setVersion(version);
        game.setKey(key);
        game.addToPlayerList(player);
        game.setMaxPlayersCount(maxPlayersCount);
        game.setPlayerCount(1);
        
        player.setCreator(true);
        player.setIsMember(true);
        
        Log.d(LOGTAG, "createGame success");
        return game;
        
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
	        	String result = element.getAttribute("value");
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
		return "";
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

