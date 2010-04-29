package com.rwthmcc103.abendplaner;

import com.google.wave.api.*;
import com.google.wave.api.event.*;

public class AbendplanerServlet extends AbstractRobot {

  @Override
  protected String getRobotName() {
    return "Abendplaner";
  }

  @Override
  protected String getRobotAvatarUrl() {
	// TODO: rather take one from imageshack or something :P.
    return "http://rlv.zcache.com/awesome_smiley_photo_sculpture_photosculpture-p153359710604909267qdjh_400.jpg";
  }

  @Override
  protected String getRobotProfilePageUrl() {
    return "http://code.google.com/apis/wave/extensions/robots/java-tutorial.html";
  }

  @Override
  public void onWaveletSelfAdded(WaveletSelfAddedEvent event) {
    Blip blip = event.getWavelet().reply("\nHi everybody!");
  }

  @Override
  public void onWaveletParticipantsChanged(WaveletParticipantsChangedEvent event) {
    for (String newParticipant: event.getParticipantsAdded()) {
      Blip blip = event.getWavelet().reply("\nHi : " + newParticipant);
    }
  }
}
