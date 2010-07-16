package com.rwthmcc3;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Help extends Activity{
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
       
        
        
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
		private String neuesSpiel = "Hier sollten Sie einen sinnvollen Spielernamen wählen. " +
				"Die Spieleranzahl kann zwischen 1 und 15 Spielern gewählt werden. " +
				"Der Timer gibt Ihnen Zeit sich nach Spielstart zu verteilen. In der Regel sollten 6 Minuten reichen. " +
				"Wenn Sie auf >>Weiter<< klicken, wird ein neues Spiel erstellt. " +
				"Aber ACHTUNG:	Sie verlassen Ihr betretenes Spiel! Zuvor erstellte Spiele werden beendet! " +
				"Nachdem Sie ein Spiel erstellt haben, gelangen Sie automatisch ins Hauptmenü. Wenn Sie dort Ihr erstelltes Spiel länger gedrückt halten, können" +
				" Sie das Spiel öffnen. In der Spielübersicht können Sie Ihr Spiel beenden oder wenn die maximale Spieleranzahl erreicht ist, " +
				"das Spiel starten. Nachdem Sie das Spiel gestartet haben, fängt der Timer an runter zu zählen. Wenn der Timer bei 0 angelangt ist, " +
				"wird die Map geöffnet und es kann losgehen!";
		//TODO
		private String spielBeitreten = "Wenn Sie sich im Hauptmenu befinden, können Sie, wenn Sie länger auf ein Spiel drücken, das Spiel öffnen. " +
				"Wenn Sie ein Spiel geöffnet haben, können Sie diesem über den Button >>Beitreten!<< beitreten. Das Beitreten ist nur möglich, wenn das Spiel" +
				" noch nicht gestartet wurde und die maximale Spieleranzahl noch nicht erreicht ist. Wenn Sie ein Spiel betreten haben, müssen" +
				" Sie warten bis die maximale Spieleranzahl erreicht ist und der Spielersteller das Spiel gestartet hat. " +
				"Nachdem der Spielersteller das Spiel gestartet hat, fängt der Timer an runter zu zählen. Wenn dieser bei 0 angelangt ist, startet die Map und es" +
				" kann losgehen!";
		private String spielAnleitung = "";
		private String spielRegeln = "";
		private String spielVerlassen ="Sie können jeder Zeit Ihr Spiel verlassen! Wenn Sie in der ";
		
		
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
