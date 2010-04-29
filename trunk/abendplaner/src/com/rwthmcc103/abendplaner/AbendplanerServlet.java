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

  @Override
  public void onWaveletSelfAdded(WaveletSelfAddedEvent event) {
	//event.getWavelet().getParticipants()
	
	String talk = "\nGreetings + " + " names of people already here" + //TODO people already here
			      "! They call me \"" + this.getRobotName() + "\", at your service. \n" +
				  "\n" +
				  "bla I do this bla bla you have to do that bla \n" + // TODO
				  "\n" +
				  "Tell me as soon as you are complete, a simple 'Abendplaner, we are complete' or 'Abendplaner go' will suffice.";

    Blip blip = event.getWavelet().reply(talk);
  }

  @Override
  public void onWaveletParticipantsChanged(WaveletParticipantsChangedEvent event) {
    String rede = "";
		
	for (String newParticipant: event.getParticipantsAdded()) {
	  if (!newParticipant.contentEquals("rwth-mcc10-group3@appspot.com")) {		
		  rede += newParticipant + ", ";
	  }
	}
	
	rede += " welcome. You are ";
	
	if (voteStarted) {
		rede += " too late, the voting has already started.";
	} else {
		rede += " just in time, stay and vote if you like.";
	}	
  }
}
