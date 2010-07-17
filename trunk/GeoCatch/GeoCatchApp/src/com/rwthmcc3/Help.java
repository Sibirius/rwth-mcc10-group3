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
		
		private String startseite = "Wenn Sie die Startseite aufrufen, werden Sie automatisch neu registriert! " +
				"Aber ACHTUNG: Sie verlassen Ihr betretenes Spiel! Erstellte Spiele werden beendet!";
		private String neuesSpiel = "Sie gelangen zur �bersicht >>neues Spiel<< von der Startseite " +
				"oder von dem Hauptmen� aus. Als erstes sollten Sie einen sinnvollen Spielernamen w�hlen. " +
				"Die Spieleranzahl kann zwischen 1 und 15 Spielern gew�hlt werden. (Mehr Informationen zur" +
				" Spielanzahl und dem damit verbundenen Spielmodus erfahren Sie unter >>Spielanleitung<<.)" +
				" Der Timer gibt Ihnen Zeit sich nach Spielstart zu verteilen. In der Regel sollten 6 Minuten reichen. " +
				"Wenn Sie auf >>Weiter<< klicken, wird ein neues Spiel erstellt. " +
				"Aber ACHTUNG:	Sie verlassen Ihr betretenes Spiel! Zuvor erstellte Spiele werden beendet! " +
				"Nachdem Sie ein Spiel erstellt haben, gelangen Sie automatisch ins Hauptmen�. Wenn Sie dort Ihr erstelltes Spiel l�nger gedr�ckt halten, k�nnen" +
				" Sie das Spiel �ffnen. In der Spiel�bersicht k�nnen Sie Ihr Spiel beenden oder wenn die maximale Spieleranzahl erreicht ist, " +
				"das Spiel starten. Nachdem Sie das Spiel gestartet haben, f�ngt der Timer an runter zu z�hlen. Wenn der Timer bei 0 angelangt ist, " +
				"wird die Map ge�ffnet und es kann losgehen!";
		
		private String spielBeitreten = "Wenn Sie sich im Hauptmenu befinden, k�nnen Sie, wenn Sie l�nger auf ein Spiel dr�cken, das Spiel �ffnen. " +
				"Wenn Sie ein Spiel ge�ffnet haben, k�nnen Sie diesem �ber den Button >>Beitreten!<< beitreten. Das Beitreten ist nur m�glich, wenn das Spiel" +
				" noch nicht gestartet wurde und die maximale Spieleranzahl noch nicht erreicht ist. Wenn Sie ein Spiel betreten haben, m�ssen" +
				" Sie warten bis die maximale Spieleranzahl erreicht ist und der Spielersteller das Spiel gestartet hat. " +
				"Nachdem der Spielersteller das Spiel gestartet hat, f�ngt der Timer an runter zu z�hlen. Wenn dieser bei 0 angelangt ist, startet die Map und es" +
				" kann losgehen!";
		//TODO check colors in 2-player-mode
		private String spielAnleitung = "Es gibt drei verschiedene Spielmodi. Der erste Spielmodus ist der Singleplayermodus. " +
				"Hier ist das Ziel den auf der Karte markierten roten Punkt in m�glichst kurzer Zeit zu erreichen. Der blaue Punkt auf der Karte ist Ihre" +
				" aktuelle Position. Dies gilt f�r alle Spielmodi. Im Zweispielermodus ist das Ziel den auf der Karte markierten roten Punkt als Erster zu erreichen. " +
				"Im Mehrspieler";
		private String spielRegeln = "Es ist nicht erlaubt das GPS-Signal zu unterdr�cken.";
		private String spielVerlassen ="Sie k�nnen jeder Zeit Ihr Spiel verlassen! Wenn Sie sich im Hauptmenu befinden, " +
				"k�nnen Sie, wenn Sie l�nger auf ein Spiel dr�cken, das Spiel �ffnen. " +
				"Wenn Sie ein Spiel ge�ffnet haben, k�nnen Sie dieses �ber den Button >>Verlassen!<< verlassen.";
		
		
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// TODO 
			//Startseite, neues Spiel, Spiel beitreten, Spiel verlassen, Spielanleitung, Spielregeln
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
