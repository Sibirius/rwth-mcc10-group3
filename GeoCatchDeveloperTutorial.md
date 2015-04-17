# Example: Updating the player data #

Using the example of the `Integrator.playerUpdateState()` method from the Integrator-class this tutorial will show you how to access the backend services of GeoCatch. In this method the current position of the player is transfered to the backend. Also the game-data plus the target position are updated. Therefore a HTTP-GET-request including the geodata is sent to the backend. In response you will get an XML-file which contains the appropriate game-date and needs to parsed for further use. The used programming-language is Java.

# Details #

In the first step we create a list of GET-parameters. In this example we include the player-position as well as the unique player-key.

```
List<NameValuePair> qparams = new ArrayList<NameValuePair>();
qparams.add(new BasicNameValuePair("p", player.getKey()));
qparams.add(new BasicNameValuePair("lon", String.valueOf(player.getLongitude())));
qparams.add(new BasicNameValuePair("lat", String.valueOf(player.getLatitude())));
```

The `Integrator.doGet()` method sends the request with the parameters to the given service. The response is stored in the `res` variable. (generated URL: http://rwth-mcc10-group3.appspot.com/update?p=[playerKey]&lon=[playerLon]&lat=[playerLat])

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

To build a DOM-document as a representation of the returned XML we use the`Integrator.parseXML()` method. Afterwards we can get the individual attributes. In this case the game-mode and -status plus the target-location are read.

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

Those data can now be used, e.g. to update the markers on the map.

Further documentation:
[Integrator-class](http://rwth-mcc10-group3.googlecode.com/svn/trunk/GeoCatch/GeoCatchApp/doc/com/rwthmcc3/Integrator.html) - [entire JavaDoc (Frontend)](http://rwth-mcc10-group3.googlecode.com/svn/trunk/GeoCatch/GeoCatchApp/doc/index.html) - [entire PyDoc (Backend)](http://rwth-mcc10-group3.googlecode.com/svn/trunk/GeoCatch/engine_doc/frames.html)