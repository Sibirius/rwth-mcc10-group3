package com.rwthmcc3;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
	private static boolean[] powerupActive = new boolean[1];
	
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

	/**
	 * @return List of active games as list of instances
	 */
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
			    	game.setCreatorLatitude(Double.parseDouble(creatorLocation[0]));
			    	game.setCreatorLongitude(Double.parseDouble(creatorLocation[1]));
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
	
	
	/**
	 * @param game The game instance you want to get the playerlist for
	 * @return List of strings with names of the players in the given game 
	 * 			or null if there was an error
	 * 
	 */
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
	
	/**
	 * Activates a server-side powerup
	 * @param powerup-number: 1 = show hunter
	 * @return true if everthing went fine, false otherwise
	 */
	public static boolean activatePowerup(int powerup){
		Log.d(LOGTAG, "activatePowerup()");
		
		if(powerup > powerupActive.length || powerup < 1)
			return false;
		
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();		
		qparams.add(new BasicNameValuePair("p", Player.getPlayer().getKey()));
        qparams.add(new BasicNameValuePair("pow", String.valueOf(powerup)));
        
        String result = getResponse(doGet("/activate", qparams));
        Log.d(LOGTAG, "activatePowerup: "+result);
        powerupActive[powerup-1] = true;
        return !result.contains("error");
	}
	
	/**
	 * 
	 * @param game The game instance you want the updated gameState for
	 * @return The updated game instance or null if there was an error
	 */
	public static Game getGameState(Game game){
		Log.d(LOGTAG, "getGameState()");
		Player player = Player.getPlayer();
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
						game.setState(Integer.parseInt(element.getAttribute("status")));
						game.setMaxPlayersCount(Integer.parseInt(element.getAttribute("mpc")));
						game.setTimer(Integer.parseInt(element.getAttribute("timer")));
						//game started
						if(game.getState() == 1){
							NodeList nodes2 = doc.getElementsByTagName("additional");
							Element element2 = (Element) nodes2.item(0);
							if(element2 != null){
								SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String starting = element2.getAttribute("starting").split("\\.")[0];
								Date date = dateFormat.parse(starting);
								Log.d(LOGTAG, "date: "+dateFormat.format(date)+", real: "+starting);
								String timeNow = element2.getAttribute("timeNow").split("\\.")[0];
								Date date2 = dateFormat.parse(timeNow);
								Log.d(LOGTAG, "date2: "+dateFormat.format(date2)+", real: "+timeNow);
								long timer = (date.getTime()-date2.getTime())/1000;
								Log.d(LOGTAG, "Timer: "+timer);
								if(timer < 0){
									player.setTimerHasCountedDown(true);
									timer = 0;
								}
								game.setCountDown(((int)timer));
								player.setNumber(Integer.parseInt(element2.getAttribute("playerNumber")));
							}
						}

					}
		        	
					
		        	nodes = element.getElementsByTagName("player");
				    for (int i = 0; i < nodes.getLength(); i++) {
				    	Log.d(LOGTAG, "parsing element #"+i);
				    	element = (Element) nodes.item(i);
				    	
				    	result.add(element.getAttribute("name"));
				     }
				    
				    game.setPlayerCount(nodes.getLength());
				    
				    Log.d(LOGTAG,"getGameState success");
				    game.setPlayerCount(nodes.getLength());
				    return game;
		
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParseException e) {
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
	
	
	/**
	 * The given player joins the given game
	 * @param player
	 * @param game
	 * @return true if everthing went fine, false otherwise
	 */
	public static boolean joinGame(Player player, Game game){
		Log.d(LOGTAG, "joinGame()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        qparams.add(new BasicNameValuePair("g", game.getKey()));
        qparams.add(new BasicNameValuePair("lon", String.valueOf(player.getLongitude())));
        qparams.add(new BasicNameValuePair("lat", String.valueOf(player.getLatitude())));
        
        String result = getResponse(doGet("/join", qparams));
        Log.d(LOGTAG, "joinGame: "+result);
        
        if(!result.contains("error")){
	        player.setTimerHasCountedDown(false);
	        player.setMyGame(game);
	        game.setPlayerCount(game.getPlayerCount()+1);
        }
        return !result.contains("error");
	}
	
	/**
	 * The given player stops his created game
	 * @param player The player who wants to stop his game
	 * @return true if everthing went fine, false otherwise
	 */
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
	
	/**
	 * Resets the player to a defined initial state
	 * @param player
	 */
	private static void resetPlayer(Player player) {
		player.setTimerHasCountedDown(false);
    	player.setMyGame(null);
    	player.setCreator(false);
    	player.setHasWin(false);
    	player.setKeyOfMyCreatedGame(null);
	}

	
	/**
	 * The player starts his created game
	 * @param player
	 * @return true if everthing went fine, false otherwise
	 */
	public static boolean startGame(Player player){
		Log.d(LOGTAG, "startGame()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        
        String result = getResponse(doGet("/start", qparams));
        Log.d(LOGTAG, "startGame: "+result);
        return !result.contains("error");
	}
	
	/**
	 * The player leaves his current game. If he was the creator, the game is stopped.
	 * @param player
	 * @return true if everthing went fine, false otherwise
	 */
	public static boolean leaveGame(Player player){
		Log.d(LOGTAG, "leaveGame()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        
        String result = getResponse(doGet("/leave", qparams));
        Log.d(LOGTAG, "leaveGame: "+result);
        if(!result.contains("error")){
        	resetPlayer(player);
        }
        return !result.contains("error");
	}
	
	/**
	 * Updates the players longitude and latitude in the database.
	 * Updates Targetlocation, gamemode and gamestate locally.
	 * If there is a winner, then the game is finished and the winnerName is saved in the game-instance.
	 * If the player has won the game, hasWin is set to true.
	 * Gets powerup information and sets powerupActive:
	 * 1 - Hunter Powerup => Hunter Location is set
	 * 
	 * @param player instance
	 * @return true if everthing went fine, false otherwise
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
	        	
	        	Log.d(LOGTAG, "start parsing playerstate");
	        	Document doc = parseXml(res.getEntity().getContent());
	        	
	        	NodeList node = doc.getElementsByTagName("state");
	        	Element ele = (Element) node.item(0);
	        	if(ele != null){
	        		if(player.getMyGame()!=null){
			        	player.getMyGame().setMode(Integer.parseInt(ele.getAttribute("mode")));
			        	player.getMyGame().setState(Integer.parseInt(ele.getAttribute("state")));
		        	}
	        		String lon = ele.getAttribute("lon");
	        		String lat = ele.getAttribute("lat");
		        	if(lon != "" && player.getMyGame().getState()==1) player.setTargetLong(Double.parseDouble(lon));
		        	if(lat != "" && player.getMyGame().getState()==1) player.setTargetLat(Double.parseDouble(lat));
		        	Log.d(LOGTAG, "update TargetLong: "+player.getTargetLong()+" / "+lon);
		        	Log.d(LOGTAG, "update TargetLat: "+player.getTargetLat()+" / "+lat);
	        	}
	        	
	        	//reset powerups
	        	powerupActive[0] = false; //hunter
	        	
				NodeList nodes = doc.getElementsByTagName("event");
				Log.d(LOGTAG, "parsing events");
			    for (int i = 0; i < nodes.getLength(); i++) {
			    	Log.d(LOGTAG, "parsing element #"+i+1+"/"+nodes.getLength());
			    	Element element = (Element) nodes.item(i);
			    	
			    	String title = element.getAttribute("title");
			    	Log.d(LOGTAG, "title: "+title);
			    	String info = element.getAttribute("info");
			    	Log.d(LOGTAG, "info: "+info);
			    	String extra = element.getAttribute("extra");
			    	Log.d(LOGTAG, "extra: "+extra);
			    	
			    	// game over
			    	if(title.equals("victory")){
			    		if(player.getMyGame()!=null){
				    		player.getMyGame().setState(3);
				    		player.getMyGame().setWinnerName(info);
			    		}
			    		if(Integer.parseInt(extra)==player.getNumber())
			    			player.setHasWin(true);
			    		else
			    			player.setHasWin(false);
			    		
			    	}
			    	
			    	// hunter powerup
			    	if(title.equals("hunter")){
			    		powerupActive[0] = true;
			    		player.setHunterLat(Double.parseDouble(info));
			    		player.setHunterLong(Double.parseDouble(extra));
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
	
	/**
	 * A new player is registered. If he is already registered, 
	 * the db is updated and the player leaves his current game
	 * @param mac Mac address to distinguish between the players
	 * @param name
	 * @param longitude
	 * @param latitude
	 * @return
	 */
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
        	resetPlayer(Player.getPlayer());
        	Player.getPlayer().setKey(result);
        	Player.getPlayer().setPlayerName(name);
        	Player.getPlayer().setLongitude(longitude);
        	Player.getPlayer().setLatitude(latitude);
        	return true;
        }
        
	}
	
	/**
	 * Creates a new Game in the db
	 * @param player
	 * @param name
	 * @param maxPlayersCount
	 * @param version
	 * @param timer
	 * @return true if everthing went fine, false otherwise
	 */
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
	        
	        player.setKeyOfMyCreatedGame(key);
	        player.setTimerHasCountedDown(false);
	        player.setCreator(true);
	        player.setMyGame(game);
	        
	        Log.d(LOGTAG, "createGame success");
	        return true;
        }
        
	}
	
	/**
	 * Changes the name of the given player to the given name
	 * @param player
	 * @param name
	 * @return true if everthing went fine, false otherwise
	 */
	public static boolean changePlayerName(Player player, String name){
		Log.d(LOGTAG, "changePlayerName()");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("p", player.getKey()));
        qparams.add(new BasicNameValuePair("n", name));
       
        String result = getResponse(doGet("/name", qparams));
        if(result.contains("error")){
        	return false;
        }else{
        	player.setPlayerName(name);
        	return true;
        }

	}
	
	/**
	 * 
	 * @param powerup The id of the powerup. 1 = show hunter
	 * @return Returns whether the serverside powerup is active or not
	 */
	public static boolean powerupIsActive(int powerup){
		if(powerup < 1 || powerup > powerupActive.length )
			return false;
		return powerupActive[powerup-1];
	}
	
	/**
	 * Performs a HTTP-Get Request
	 * @param path The path to which you want to send your request 
	 * @param qparams The Get parameters
	 * @return Returns a HTTP Response or null if there was an error (no response from server)
	 */
	private static HttpResponse doGet(String path, List<NameValuePair> qparams){
		Log.d(LOGTAG, "doGet()");
		try{
			HttpClient client = new DefaultHttpClient();  	        
	        URI uri = URIUtils.createURI("http", AppEngineURL, -1, path, 
	        		URLEncodedUtils.format(qparams, "UTF-8"), null);
	        Log.d(LOGTAG, "doGet uri: "+uri.toString());
	        HttpGet httpget = new HttpGet(uri);
	        HttpResponse response = client.execute(httpget);
	        Log.d(LOGTAG, "doGet success");
	        return response;
		} catch (Exception e) {
            e.printStackTrace();
        }
		Log.e(LOGTAG, "NullPointer, doGet");
		return null;
		
	}
	
	/**
	 * Parses the response.xml
	 * @param res The HTTP Response you want to parse
	 * @return The value of the "value" element in the xml or null if there was an error
	 */
	private static String getResponse(HttpResponse res){
		Log.d(LOGTAG, "getResponse()");
		if(res != null){
	        try {
	        	Document doc = parseXml(res.getEntity().getContent());
	        	NodeList nodes = doc.getElementsByTagName("response");
	        	Element element = (Element) nodes.item(0);
	        	String result;
	        	if(element!=null)
	        		result = element.getAttribute("value");
	        	else
	        		result = "error";
				Log.d(LOGTAG,"getResponse success: "+result);
				
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
	
	/**
	 * Parses an XML Document
	 * @param xmlStream The stream you want to parse
	 * @return The DOM Document or null if there was an error
	 */
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
	
	/**
	 * Snaps the given position to the next street using google maps api
	 * @param lat
	 * @param lng
	 * @return The new position on a street or null if there was an error. [0]=Latitude [1]=Longitude 
	 */
	public static double[] snapToStreet(double lat, double lng){
		try{
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("sensor", "false"));
			qparams.add(new BasicNameValuePair("origin", lat + "," + lng ));
			qparams.add(new BasicNameValuePair("destination", (lat + 0.001) + "," + (lng + 0.001) ));
			//http://maps.google.com/maps/api/directions/xml?origin=42.3585300,-71.0610700&destination=42.3585300,-71.0610900&sensor=false
			HttpClient client = new DefaultHttpClient();  	        
	        URI uri = URIUtils.createURI("http", "maps.google.com", -1, "maps/api/directions/xml", 
	        		URLEncodedUtils.format(qparams, "UTF-8"), null);
	        Log.d(LOGTAG, "snapToStreet uri: "+uri.toString());
	        
        	Document doc = parseXml(client.execute(new HttpGet(uri)).getEntity().getContent());
        	
        	Element ele = (Element) doc.getElementsByTagName("start_location").item(0);
        	if(ele != null){
        		String newLat = ((Node) ele.getElementsByTagName("lat").item(0).getChildNodes().item(0)).getNodeValue();
        		String newLng = ((Node) ele.getElementsByTagName("lng").item(0).getChildNodes().item(0)).getNodeValue();
        		if(newLat != "" && newLng != ""){
        			double[] result = new double[2];
        			result[0] = Double.parseDouble(newLat);
        			result[1] = Double.parseDouble(newLng);
        			Log.d(LOGTAG, "snapToStreet res: "+result[0]+","+result[1]);
        			return result;
        		}
        	}
		} catch (Exception e) {
            e.printStackTrace();
        }		
		return null;
	}

}

