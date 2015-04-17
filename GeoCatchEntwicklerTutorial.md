# Beispiel: Spieler Daten updaten #

Am Beispiel der `Integrator.playerUpdateState()` Methode aus der Integrator-Klasse soll innerhalb dieses Tutorials der Zugriff aus der GeoCatch App auf die Backend Services dargestellt werden. Innerhalb dieser Methode wird die aktuelle Position des Spielers ans Backend übermittelt. Außerdem werden die Spiel-Daten sowie die Position des Ziel-Objekts aktualisiert. Dazu werden via HTTP-GET-Request die Geodaten ans Backend gesendet. Als Antwort enthält man eine XML-Datei mit den entsprechenden Spiel-Daten, die für die weitere Benutzung noch geparst werden muss!

# Details #

Im ersten Schritt wird eine Liste von Anfrage-Parametern erstellt. Diese enthält in diesem Beispiel die Position des Spielers sowie zur Authentifizierung dessen eindeutigen Schlüssel.

```
List<NameValuePair> qparams = new ArrayList<NameValuePair>();
qparams.add(new BasicNameValuePair("p", player.getKey()));
qparams.add(new BasicNameValuePair("lon", String.valueOf(player.getLongitude())));
qparams.add(new BasicNameValuePair("lat", String.valueOf(player.getLatitude())));
```

Mittels der `Integrator.doGet()` Methode, der der zu verwendende Service sowie die soeben erstellten Parameter übergeben werden, wird nun der Request ans Backend abgesendet und die Response in der Variablen `res` gespeichert (generierte URL: http://rwth-mcc10-group3.appspot.com/update?p=[playerKey]&lon=[playerLon]&lat=[playerLat])

```
HttpResponse res = doGet("/update", qparams);
```

Response:

```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<wayne>
<state mode="0" state="1" lat="50.777707" lon="6.078014" />
<events></events>
</wayne>
```

Mittels der Hilfs-Methode `Integrator.parseXML()` können nun die XML-Response geparst und die entsprechenden Daten ausgelesen werden. In diesem Fall werden nun Spiel-Modus und -Status, sowie die Geo Location des Ziel-Objekts ausgelesen.

```
Document doc = parseXml(res.getEntity().getContent());      	
NodeList node = doc.getElementsByTagName("state");
Element ele = (Element) node.item(0);
if(ele != null){
    String mode = ele.getAttribute("mode");
    String state = ele.getAttribute("state");
    String lon = ele.getAttribute("lon");
    String lat = ele.getAttribute("lat");
}
```

Diese Daten können nun weiterverwendet werden, z.B. um die Marker auf der Map zu aktualisieren...

Weitere Dokumentation:
[Integrator-Klasse](http://rwth-mcc10-group3.googlecode.com/svn/trunk/GeoCatch/GeoCatchApp/doc/com/rwthmcc3/Integrator.html) - [komplettes JavaDoc (Frontend)](http://rwth-mcc10-group3.googlecode.com/svn/trunk/GeoCatch/GeoCatchApp/doc/index.html) - [komplettes PyDoc (Backend)](http://rwth-mcc10-group3.googlecode.com/svn/trunk/GeoCatch/engine_doc/frames.html)