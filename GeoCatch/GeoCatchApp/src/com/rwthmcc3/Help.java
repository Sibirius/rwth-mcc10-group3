package com.rwthmcc3;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Help extends Activity{
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.help);
       
        //set title
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        TextView leftTitle = (TextView)findViewById(R.id.left_text);
        TextView rightTitle = (TextView)findViewById(R.id.right_text);
        leftTitle.setText("GeoCatch");
        rightTitle.setText("Hilfe");
        
        //spinner timer
        Spinner spinner = (Spinner) findViewById(R.id.spinner_help);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.array_helps, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
       
		
        
	}
	
	public class MyOnItemSelectedListener implements OnItemSelectedListener {
		
		private String startseite = "Wenn du die Startseite aufrufst, wirst du automatisch neu registriert! " +
				"\n\nAber ACHTUNG: " +
				"\n\nDu verlässt dein betretenes Spiel! Erstellte Spiele werden beendet!";
		private String neuesSpiel = "Du gelangst zur Übersicht \"neues Spiel\" von der Startseite " +
				"oder von dem Hauptmenü aus. " +
				"\n\nAls erstes solltest du einen sinnvollen Spielernamen wählen. " +
				"\n\nDie Spieleranzahl kann zwischen 1 und 15 Spielern gewählt werden. (Mehr Informationen zur" +
				" Spielanzahl und dem damit verbundenen Spielmodus erfährst du unter \"Spielanleitung\".)" +
				"\n\nDer Timer gibt dir Zeit sich nach Spielstart zu verteilen. In der Regel sollten 6 Minuten reichen. " +
				"\n\nWenn du auf \"Weiter\" klickst, wird ein neues Spiel erstellt. " +
				"\n\nAber ACHTUNG:	\nDu verlässt dein betretenes Spiel! Erstellte Spiele werden beendet! " +
				"\n\nNachdem du ein Spiel erstellt hast, gelangst du automatisch ins Hauptmenü. Wenn du dort dein erstelltes Spiel länger gedrückt hälst, kannst" +
				" du das Spiel öffnen. \n\nIn der Spielübersicht kannst du dein Spiel beenden oder wenn die maximale Spieleranzahl erreicht ist, " +
				"das Spiel starten. \n\nNachdem du das Spiel gestartet hast, fängt der Timer an runter zu zählen. Wenn der Timer bei 0 angelangt ist, " +
				"wird die Map geöffnet und es kann losgehen!";
		private String spielBeitreten = "Wenn du dich im Hauptmenu befindest, kannst du, wenn du länger auf ein Spiel drückst, das Spiel öffnen. " +
				"\n\nIn der Spielübersicht, kannst du dem Spiel über den Button \"Beitreten!\" beitreten. Das Beitreten ist nur möglich, wenn das Spiel" +
				" noch nicht gestartet wurde und die maximale Spieleranzahl noch nicht erreicht ist. \n\nWenn du ein Spiel betreten hast, musst" +
				" du warten bis die maximale Spieleranzahl erreicht ist und der Spielersteller das Spiel gestartet hat. " +
				"\n\nNachdem der Spielersteller das Spiel gestartet hat, fängt der Timer an runter zu zählen. Wenn dieser bei 0 angelangt ist, startet die Map und es" +
				" kann losgehen!";
		private String spielAnleitung = "Es gibt drei verschiedene Spielmodi. \n\nDer erste Spielmodus ist der Singleplayermodus. " +
				"\nHier ist das Ziel den auf der Karte markierten roten Punkt in möglichst kurzer Zeit zu erreichen. Der blaue Punkt auf der Karte ist deine" +
				" aktuelle Position. Dies gilt für alle Spielmodi. \n\nIm Zweispielermodus ist das Ziel den auf der Karte markierten roten Punkt als Erster zu erreichen. " +
				"\n\nIm Mehrspielermodus ist das Ziel eine Person zu fangen. \nDein Ziel ist als roter Punkt auf der Karte dargestellt. \n\nAber ACHTUNG:" +
				" \nAuch du wirst gejagt! \n\nGewonnen hat der Mitspieler, welcher zuerst seine zu jagende Person gefangen hat. Zum Fangen reicht in der Regel" +
				" eine Distanz von ca. 15m.";
		private String spielRegeln = "Es ist nicht erlaubt das GPS-Signal zu unterdrücken oder Bluetooth auszuschalten!";
		private String spielVerlassen ="Du kannst jeder Zeit dein Spiel verlassen! \n\nWenn du dich im Hauptmenu befindest, " +
				"kannst du, wenn du länger auf ein Spiel drückst, das Spiel öffnen. " +
				"In der Spielübersicht, kannst du dein Spiel über den Button \"Verlassen!\" verlassen.";
		//TODO color
		private String powerUps = "Auf der Karte werden von Zeit zu Zeit verschiedene Power-Ups angezeigt. Diese können einen positive oder" +
				" auch negative Effekt haben. \nPower-Ups werden in der Farbe \"?\" dargestellt. Um ein Power-Up zu aktivieren, musst du zu diesem hinlaufen.";
		
		
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// TODO 
			//Startseite, neues Spiel, Spiel beitreten, Spiel verlassen, Spielanleitung, Spielregeln, Power-Ups
			TextView myTextView =(TextView)findViewById(R.id.textview_help);
			
			switch (position){
			case 0:
				myTextView.setText(startseite);
				break;
			case 1:
				myTextView.setText(neuesSpiel);
				break;
			case 2:
				myTextView.setText(spielBeitreten);
				break;
			case 3:
				myTextView.setText(spielVerlassen);
				break;
			case 4:
				myTextView.setText(spielAnleitung);
				break;
			case 5:
				myTextView.setText(spielRegeln);
				break;
			case 6:
				myTextView.setText(powerUps);
				break;
			default:
				myTextView.setText(startseite);
				break;
				
			}
			
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			TextView myTextView =(TextView)findViewById(R.id.textview_help);
			myTextView.setText(startseite);
		}
		
		
		
		
	    
	}
	
}
