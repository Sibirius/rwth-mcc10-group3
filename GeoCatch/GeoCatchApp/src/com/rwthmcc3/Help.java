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

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// TODO 
			//Startseite, neues Spiel, Spiel beitreten, Spielanleitung, Spielregeln
			TextView myTextView =(TextView)findViewById(R.id.textview_help);
			
			switch (position){
			case 0:
				myTextView.setText(startseite);
				break;
			case 1:
				myTextView.setText(neueSpiel);
				break;
			case 2:
				myTextView.setText(spielBeitreten);
				break;
			case 3:
				myTextView.setText(spielAnleitung);
				break;
			case 4:
				myTextView.setText(spielRegeln);
				break;
			default:
				myTextView.setText(startseite);
				break;
				
			}
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			
			
		}
		
		
		private String startseite = "Wenn Sie die Startseite aufrufen, werden Sie automatisch neu registriert! Aber ACHTUNG: Sie verlassen Ihr betretenes Spiel! Erstellte Spiele werden beendet!";
		private String neueSpiel = "Hier sollten Sie einen sinnvollen Spielernamen wählen. Die Spieleranzahl kann zwischen 1 und 15 Spielern gewählt werden." +
				"Der Timer gibt Ihnen Zeit sich nach Spielstart zu verteilen. In der Regel sollten 6 min reichen. Wenn Sie auf >> Weiter << klicken, wird ein neues Spiel erstellt. Aber ACHTUNG: Sie verlassen Ihr betretenes Spiel! Zuvor erstellte Spiele werden beendet!";
		private String spielBeitreten = "";
		private String spielAnleitung = "";
		private String spielRegeln = "";
		
	    
	}
	
}
