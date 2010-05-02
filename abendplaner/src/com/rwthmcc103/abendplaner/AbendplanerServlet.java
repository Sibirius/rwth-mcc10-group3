package com.rwthmcc103.abendplaner;

import com.google.wave.api.*;
import com.google.wave.api.event.*;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import java.io.File; 
import javax.xml.parsers.*; 

import org.w3c.dom.*; 
import org.w3c.dom.Element;

public class AbendplanerServlet extends AbstractRobot {		//requires getRobotName () function
  private boolean voteStarted = false;
  private boolean[] preferedActivities = new boolean[4];
  private List<String> voters = new LinkedList<String>(); 
  private int[][] voteResults;
  private String[] activities = new String[]{"eat","dance","drink","cinema"};
  
  @Override
  protected String getRobotName() {
    return "Abendplaner";
  }

  @Override
  protected String getRobotAvatarUrl() {
    return "http://i39.tinypic.com/351eq00.jpg";
  }

  @Override
  protected String getRobotProfilePageUrl() {
    return "http://code.google.com/apis/wave/extensions/robots/java-tutorial.html";
  }

  /* get list of people, not including robots */
  private List<String> getImportantPeople(Wavelet wavelet) {
	  List<String> output = new LinkedList<String>();
	  
	  for (String newParticipant: wavelet.getParticipants()) {
	    if (!newParticipant.contentEquals(wavelet.getRobotAddress())) {
	    	output.add(newParticipant);
	    }
	  }	  
	  
	  return output;
  }  
  
  /** introduces user and describes procedure */
  @Override
  public void onWaveletSelfAdded(WaveletSelfAddedEvent event) {
	String peopleAlreadyHere = "";		
	voteStarted = false;
	
	for (String newParticipant: getImportantPeople(event.getWavelet())) {
		peopleAlreadyHere += newParticipant + ", ";
		if(!voteStarted && !voters.contains(newParticipant)) voters.add(newParticipant);
	}
	  
	String talk = "\nGreetings, " + peopleAlreadyHere + " welcome!\n" +
			      "They call me \"" + this.getRobotName() + "\", at your service. \n" +
				  "\n" +				  
				  "It's my task to help you to decide what to do tonight.\n" +
				  "Tell me your preferences! Then I'll suggest some real cool locations related " +
				  "to your preferences! You can have look at them on a map and vote for " +
				  "your favorite(s), too. If all of you have voted, I tell you where to go tonight!\n" +
				  "\n" +
				  "Come on, let's start! I'm waiting for your preferences!\n" +
				  "Tell me your preferences with the following command:\n" +
				  "prefer (essen|trinken|tanzen|kino)+\n";

    event.getWavelet().reply(talk);
  }

  /** greets new participants */
  @Override
  public void onWaveletParticipantsChanged(WaveletParticipantsChangedEvent event) {
    String talk = "\n";
    int peopleCount = 0;
		
	for (String newParticipant: event.getParticipantsAdded()) {
	  if (!newParticipant.contentEquals(event.getWavelet().getRobotAddress())) { // to stop it from greeting itself	
	    talk += newParticipant + ", ";
	    if(!voteStarted && !voters.contains(newParticipant)) voters.add(newParticipant);
	    peopleCount++;
	  }
	}
	
	boolean removed = false;
	for (String oldParticipant: event.getParticipantsRemoved()) {
	  if (!oldParticipant.contentEquals(event.getWavelet().getRobotAddress())) { // to stop it from greeting itself	
	    if(voters.contains(oldParticipant)){
	    	voters.remove(oldParticipant);
	    	removed = true;
	    	event.getWavelet().reply("\nGood Bye, " + oldParticipant);
	    }
	  }
	}
	
	if(removed && voters.isEmpty() && !getImportantPeople(event.getWavelet()).isEmpty()){
		if(!voteStarted){
			startVoting(event);
		} else {
			evalVoting(event);
		}
	}
	
	talk += "welcome. You are ";
	
	if (voteStarted) {
	  talk += "too late, the voting has already started.";
	} else {
	  talk += "just in time, stay and vote if you like.";
	}	
	
	if (peopleCount > 0) {
	  event.getWavelet().reply(talk);
	}
  }
  
	//selected activities represented within one integer value
	//like unix-file-permissions
	//eat    <-> 2^3
	//dance  <-> 2^2
	//drink  <-> 2^1
	//cinema <-> 2^0
  private int calcActivityValue(boolean[] prefAct){
	  int result = 0;
	  for(int i = 0; i<4; i++){
		  if(prefAct[i]) result += Math.pow(2,3-i);
	  }
	  return result;
  }
  
  public String getLocationName(int activityIndex, int locationIndex) {
	    try {
		  	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		    DocumentBuilder builder = factory.newDocumentBuilder(); 
		    Document document = builder.parse( new File("WEB-INF/locationdb.xml") ); 
		    org.w3c.dom.Element e = (org.w3c.dom.Element) (document.getElementsByTagName(activities[activityIndex]).item(0));
		    org.w3c.dom.Element f = (org.w3c.dom.Element) (e.getElementsByTagName("location").item(locationIndex));
	    	return f.getElementsByTagName("name").item(0).getTextContent();
	    }
	    catch(Exception e){
	    	e.printStackTrace();	    	
	    }
    	return "";	  	  
  }
  
  private void evalVoting(Event event){
	if(voters.isEmpty()){
		//all votes collected
		int maxLocationIndex = 0;
		int maxActivityIndex = 0;
		for(int i = 0; i<4; i++){
			for(int j = 0; j < voteResults[i].length; j++){
				if(voteResults[i][j] > voteResults[maxLocationIndex][maxActivityIndex]){
					maxLocationIndex = i;
					maxActivityIndex = j;
				} else if(voteResults[i][j] == voteResults[maxLocationIndex][maxActivityIndex]){
					if((int) (Math.random() + 0.5) == 1){
						maxLocationIndex = i;
						maxActivityIndex = j;							
					}
				}
			}
		}
		event.getWavelet().reply("\nFinal result: This evening you'll: " + activities[maxLocationIndex] + "@" + getLocationName(maxLocationIndex,maxActivityIndex) + "\n" );
		for(int i=0; i<preferedActivities.length; i++) preferedActivities[i] = false;    		
	}	  
  }
  
  @Override
  public void onGadgetStateChanged(GadgetStateChangedEvent event) {
	if(voteStarted){
	    Blip blip = event.getBlip();      
	    Gadget gadget = Gadget.class.cast(blip.at(event.getIndex()).value());
	    if (!gadget.getUrl().startsWith("http://rwth-mcc10-group3.googlecode.com/svn/trunk/abendplaner/gadgets/voting.xml")) {
	      return;
	    }
	
	    for (Map.Entry<String, String> entry : gadget.getProperties().entrySet()) {
	    	if(voters.contains(entry.getKey())){
	    		String participant = entry.getKey();
	    		String voting = entry.getValue();
	    		String[] vote = entry.getValue().split("#");
				if(voteResults == null){
					voteResults = new int[4][vote[0].length()];
				}	    		
	    		for(int i = 0; i<4; i++){
	    			for(int j = 0; j<vote[i].length(); j++){
	    				voteResults[i][j] += Integer.parseInt(String.valueOf(vote[i].charAt(j)));
	    			}
	    		}
	    		//eat # dance # drink # location
	    		//vote example = 0000#1000#0010#0000
	    		//one digit each location
	    		//e.g.: yes to first dance example, no to 2nd-4th dance location
	    		event.getWavelet().reply("\nThanks for your vote, " + participant );
	    		voters.remove(participant);
	    	}
	    } 
	    evalVoting(event);
	}

  }
  
  private void startVoting(Event event){
	voteStarted = true;
	voters = getImportantPeople(event.getWavelet());
	Blip blip = event.getWavelet().reply("\nLET'S DO IT YEAHHH!");
	Gadget mapsGadget = new Gadget("http://rwth-mcc10-group3.googlecode.com/svn/trunk/abendplaner/gadgets/map.xml");
	mapsGadget.setProperty("value", ""+calcActivityValue(preferedActivities));
	blip.append(mapsGadget);	
	Gadget voteGadget = new Gadget("http://rwth-mcc10-group3.googlecode.com/svn/trunk/abendplaner/gadgets/voting.xml");
	voteGadget.setProperty("value", ""+calcActivityValue(preferedActivities));
	blip.append(voteGadget);	  
  }
  
  /** reacts to new submitted messages, interprets commands */
  @Override
  public void onBlipSubmitted(BlipSubmittedEvent event) {
	//TODO: only proceed if blip is not a reply? So it will only react to stuff said not as a direct comment to something. 
	//activityorder: "eat","dance","drink","cinema"
	  
	if(!voteStarted && !voters.isEmpty()){
		if(voters.contains(event.getBlip().getCreator())){
			String content = event.getBlip().getContent();
			String talk = "\n";
			boolean success = false;
			if( content.matches("^\\nprefer [\\w ]*essen[\\w ]*$") ){
				preferedActivities[0] = true;
				success = true;
			}
			if( content.matches("^\\nprefer [\\w ]*tanzen[\\w ]*$") ){
				preferedActivities[1] = true;
				success = true;
			}
			if( content.matches("^\\nprefer [\\w ]*trinken[\\w ]*$") ){
				preferedActivities[2] = true;
				success = true;
			}
			if( content.matches("^\\nprefer [\\w ]*kino[\\w ]*$") ){
				preferedActivities[3] = true;
				success = true;
			}			
			if(success){
				voters.remove(event.getBlip().getCreator());
			} else {
				if (content.matches("^\\nprefer[\\w ]*$"))
					talk += "Didn't understand your preference, " + event.getBlip().getCreator() + "!\n\n";
			}
			if(voters.isEmpty()){
				startVoting(event);
			} else {
				talk += "Waiting for the Preferences of:\n";
				for(String voter: voters){
					talk += voter + "\n";
				}
				event.getWavelet().reply(talk);				
			}
				
		}
		
	} 
	
  }
}
