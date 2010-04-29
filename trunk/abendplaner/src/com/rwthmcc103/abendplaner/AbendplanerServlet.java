package com.rwthmcc103.abendplaner;

import com.google.wave.api.*;
import com.google.wave.api.event.*;

import java.util.List;

public class AbendplanerServlet extends AbstractRobot {
  private boolean voteStarted = false; 
	
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
  private Participants getImportantPeople(Wavelet wavelet) {
	  Participants p = wavelet.getParticipants();
	  p.remove(wavelet.getRobotAddress()); //TODO return participants, without robot
	  
	  return p;
  }  
  
  /** introduces user and describes procedure */
  @Override
  public void onWaveletSelfAdded(WaveletSelfAddedEvent event) {
	String peopleAlreadyHere = "";		
	
	for (String newParticipant: getImportantPeople(event.getWavelet())) {
		peopleAlreadyHere += newParticipant + ", ";
	}
		
	  
	String talk = "\nGreetings, " + peopleAlreadyHere +
			      "! They call me \"" + this.getRobotName() + "\", at your service. \n" +
				  "\n" +
				  "bla I do this bla bla you have to do that bla \n" + // TODO
				  "\n" +
				  "Tell me as soon as you are complete, a simple \"Abendplaner, we are complete\" or \"Abendplaner go\" will suffice.";

    Blip blip = event.getWavelet().reply(talk);
  }

  /** greets new participants */
  @Override
  public void onWaveletParticipantsChanged(WaveletParticipantsChangedEvent event) {
    String talk = "\n";
    int peopleCount = 0;
		
	for (String newParticipant: event.getParticipantsAdded()) {
	  if (!newParticipant.contentEquals(event.getWavelet().getRobotAddress())) { // to stop it from greeting itself	
	    talk += newParticipant + ", ";
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
	  Blip blip = event.getWavelet().reply(talk);
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
  
  /** reacts to new submitted messages, interprets commands */
  @Override
  public void onBlipSubmitted(BlipSubmittedEvent event) {
	// command string arrays
	String[] start = {"Abendplaner, we are complete", "Abendplaner go"}; 
	
	// start voting
	if (containsOne(event.getBlip().getContent(), start)) {
		if (!voteStarted) {
			Blip blip = event.getWavelet().reply("\nLET'S DO IT YEAHHH!");
			voteStarted = true;
		} else {
			Blip blip = event.getWavelet().reply("\nAlready at it, pay attention please.");
		}
	}
  }
}
