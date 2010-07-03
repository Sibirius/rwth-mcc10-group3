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
			    	game.setVersion(Float.parseFloat(element.getAttribute("version")));
			    	String [] creatorLocation = element.getAttribute("creatorLocation").split(",");
			    	game.setCreatorLatitude(Float.parseFloat(creatorLocation[0]));
			    	game.setCreatorLongitude(Float.parseFloat(creatorLocation[1]));
			    	game.setMode(Integer.parseInt(element.getAttribute("mode")));
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
			    	player.setMember(true);
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
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        qparams.add(new BasicNameValuePair("g", game.getKey()));
        
        doGet("/join", qparams);
	}
	
	public static void stopGame(Player player, Game game){
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        qparams.add(new BasicNameValuePair("g", game.getKey()));
        
        doGet("/stop", qparams);
	}
	
	public static void startGame(Player player, Game game){
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        qparams.add(new BasicNameValuePair("g", game.getKey()));
        
        doGet("/start", qparams);
	}
	
	public static void leaveGame(Player player, Game game){
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        qparams.add(new BasicNameValuePair("g", game.getKey()));
        
        doGet("/leave", qparams);
	}
	
	public static void playerUpdateState(Player player, Game game){
		//TODO everything xD
	}
	
	public static String registerPlayer(String mac, String name){
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("m", mac));
        qparams.add(new BasicNameValuePair("n", name));
        
        HttpResponse res = doGet("/register", qparams);
        try {
			return res.getEntity().getContent().toString();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String createGame(Player player, Game game){
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        qparams.add(new BasicNameValuePair("g", game.getKey()));
        
        HttpResponse res = doGet("/create", qparams);
        try {
			return res.getEntity().getContent().toString();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
        
	}
	
	
	// DEBUG FUNCTIONS
	
	public static void fillWithTestData(){
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        doGet("/fillWithTestData", qparams);
	}
	
	public static void clearData(){
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
	
	private static Document parseXml(InputStream xmlStream){
		Log.d(LOGTAG, "parseXml()");
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			return db.parse(xmlStream);
			

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

