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

/**
 * Integrator-Class for communication between Android and AppEngine
 *
 */
public class Integrator {
	
	private static String AppEngineURL = "";
	
	public static String mainPage(){        
        HttpResponse res = doGet("/", null);
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
		List<Game> result = new ArrayList<Game>();      
        HttpResponse res = doGet("/games", null);
        try {
			Document doc = parseXml(res.getEntity().getContent());
			NodeList nodes = doc.getElementsByTagName("topic");
		    for (int i = 0; i < nodes.getLength(); i++) {
		    	Element element = (Element) nodes.item(i);
		    	
		    	Game game = new Game();
		    	
		    	game.setName(element.getAttribute("name"));
		    	game.setKey(element.getAttribute("key"));
		    	game.setPlayerCount(Integer.parseInt(element.getAttribute("playerCount")));
		    	game.setMaxPlayersCount(Integer.parseInt(element.getAttribute("maxPlayersCount")));
		    	game.setVersion(Float.parseFloat(element.getAttribute("version")));
		    	String [] creatorLocation = element.getAttribute("creatorLocation").split(" ");
		    	game.setCreatorLatitude(Float.parseFloat(creatorLocation[0]));
		    	game.setCreatorLongitude(Float.parseFloat(creatorLocation[1]));
		    	game.setMode(Integer.parseInt(element.getAttribute("mode")));
		    	
		    	result.add(game);
	
		     }
		    return result;

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void joinGame(String player, String game){
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player));
        qparams.add(new BasicNameValuePair("g", game));
        
        doGet("/join", qparams);
	}
	
	public static void stopGame(String player, String game){
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player));
        qparams.add(new BasicNameValuePair("g", game));
        
        doGet("/stop", qparams);
	}
	
	public static void startGame(String player, String game){
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player));
        qparams.add(new BasicNameValuePair("g", game));
        
        doGet("/start", qparams);
	}
	
	public static void leaveGame(String player, String game){
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player));
        qparams.add(new BasicNameValuePair("g", game));
        
        doGet("/leave", qparams);
	}
	
	public static void playerUpdateState(String player, String game){
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
	
	public static String createGame(String player, String game){
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player));
        qparams.add(new BasicNameValuePair("g", game));
        
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
        doGet("/fillWithTestData", null);
	}
	
	public static void clearData(){        
        doGet("/clearData", null);
	}
	
	
	private static HttpResponse doGet(String path, List<NameValuePair> qparams){
		try{
			HttpClient client = new DefaultHttpClient();  	        
	        URI uri = URIUtils.createURI("http", AppEngineURL, -1, path, 
	        		URLEncodedUtils.format(qparams, "UTF-8"), null);
	        HttpGet httpget = new HttpGet(uri);
	        HttpResponse response = client.execute(httpget);
	        return response;
		} catch (Exception e) {
            e.printStackTrace();
        }
		return null;
		
	}
	
	private static Document parseXml(InputStream xmlStream){
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
		return null;
	}

}

