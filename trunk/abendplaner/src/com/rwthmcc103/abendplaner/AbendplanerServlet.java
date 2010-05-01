package com.rwthmcc103.abendplaner;

import com.google.wave.api.*;
import com.google.wave.api.event.*;

import java.util.List;
import java.util.LinkedList;

public class AbendplanerServlet extends AbstractRobot {		//requires getRobotName () function
  private boolean voteStarted = false;
  //activityorder: "eat","dance","drink","cinema"
  private boolean[] preferedActivities = new boolean[4];
  private List<String> voters = new LinkedList<String>();
  
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
	
	for (String newParticipant: getImportantPeople(event.getWavelet())) {
		peopleAlreadyHere += newParticipant + ", ";
		voters.add(newParticipant);
	}
	  
	String talk = "\nGreetings, " + peopleAlreadyHere + " welcome!\n" +
			      "They call me \"" + this.getRobotName() + "\", at your service. \n" +
				  "\n" +				  
				  "bla I do this bla bla you have to do that bla \n" + // TODO
				  "\n" +
				  "Tell me your preferences with the following command:\n" +
				  "prefer [essen|trinken|tanzen|kino]+\n" +
				  "Remember, commands are only processed from top level Blips\n";

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
	    voters.add(newParticipant);
	    peopleCount++;
	  }
	}
	
	talk += " welcome. You are ";
	
	if (voteStarted) {
	  talk += " too late, the voting has already started.";
	} else {
	  talk += " just in time, stay and vote if you like.";
	}	
	
	if (peopleCount > 0) {
	  event.getWavelet().reply(talk);
	}
  }
  
  /** tests if string contains at least one fragment from the array */
  private boolean containsOne(String wo, String[] array) {
	  for (int i = 0; i < array.length; i++) {
		if (wo.contains(array[i])) {			
			return true;
		}
	  }	  
	  return false;
  }
  
	//selected activities respresented within one integer value
	//like unix-file-permissons
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
				if (content.matches("^\\nprefer [\\w ]*$"))
					talk += "Didn't understand your preference, " + event.getBlip().getCreator() + "!\n\n";
			}
			if(voters.isEmpty()){
				voteStarted = true;
				Blip blip = event.getWavelet().reply("\nLET'S DO IT YEAHHH!");
				voteStarted = true;
				Gadget gadget = new Gadget("http://rwth-mcc10-group3.googlecode.com/svn/trunk/abendplaner/gadgets/map.xml");
				gadget.setProperty("value", ""+calcActivityValue(preferedActivities));
				blip.append(gadget);				
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
