# Services #

Here you see a complete list of the offered services with their parameters and their response XML-file.



&lt;hr&gt;



## /games ##
Returns a list with all available games.
#### Parameters ####
None
#### XML-file ####
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<games>
	{% for game in games %}
	<game 
		name = "{{ game.name }}"
		key = "{{ game.key }}"
		playerCount = "{{ game.playerCount }}"
		maxPlayersCount = "{{ game.maxPlayerCount }}"
		version = "{{ game.version }}"
		creatorLocation = "{{ game.creatorLocation }}"
		mode = "{{ game.mode }}"
	/>		
	{% endfor %}
</games>
```



&lt;hr&gt;



## /gamePlayers ##
Returns a list with all players in the chosen game.
#### Parameters ####
  * g : The key of the game for which you want to get the lsit of players.
#### XML-file ####
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<players>
	{% for player in players %}
	<player 
		name = "{{ player.name }}"
		number = "{{ player.number }}"
		creator = "{{ player.creator }}"
	/>		
	{% endfor %}
</players>
```



&lt;hr&gt;



## /gameState ##
Returns all information about a game.
#### Parameters ####
  * g : The key of the game for which you want to get the information
  * p : The key of a player in this game to get the additional information
#### XML-file ####
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<game

name = "{{game.name}}"
mode = "{{game.mode}}"
timer = "{{game.timer}}"
status = "{{game.status}}"
mpc = "{{game.mpc}}"

>
  <players>
  	{% for player in players %}
  	<player
  		name = "{{ player.name }}"
  		number = "{{ player.number }}"
  		creator = "{{ player.creator }}"
  	/>
  	{% endfor %}
	</players>
  
	{% if additional %}
	<additional
		playerNumber = "{{ additional.playerNumber}}"
		started = "{{ additional.started }}"
		timeNow = "{{ additional.timeNow }}"
		starting = "{{ additional.starting }}"
	/>
	{% endif %}
</game>
```



&lt;hr&gt;



## /register ##
Registers a new player or (if already registered) kicks the player from any game and updates his name.
#### Parameters ####
  * m : The mac address of the player you want to register
  * n : The name of the player
  * lat : Latitude of the player
  * lon : Longitude of the player
#### XML-file ####
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<response
	value="{{value}}"
/>
```
Annotation: `value` is the key of the registered player or 'input error' if a parameter is not well formatted



&lt;hr&gt;



## /name ##
Changes the name of registered player without kicking him from any game.
#### Parameters ####
  * p : The key of the player
  * n : The new name
#### XML-file ####
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<response
	value="{{value}}"
/>
```
Annotation: `value` is 'done' if everything went fine, 'input error' if a parameter was not well formatted or 'error' if
  * the player does not exist



&lt;hr&gt;



## /activate ##
Activates a server-side powerup.
#### Parameters ####
  * p : The key of the player who wants to activate a powerup
  * pow : The id of the powerup that you want to activate
#### XML-file ####
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<response
	value="{{value}}"
/>
```
Annotation: `value` is 'done' if everything went fine, 'input error' if a parameter was not well formatted or 'error' if
  * the player does not exist
  * the player is in no game



&lt;hr&gt;



## /create ##
Creates a new game.
#### Parameters ####
  * n : The name of the game
  * v : The version of the game
  * lat : The latitude of the creator
  * lon : The longitude of the creator
  * p : The player-key of the creator
  * t : The timer (in seconds)
  * mpc : The maximum player count
#### XML-file ####
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<response
	value="{{value}}"
/>
```
Annotation: `value` is the key of the created game, 'input error' if a parameter was not well formatted or 'error' if
  * the player does not exist
  * the player already is in a game



&lt;hr&gt;



## /join ##
The given player joins the given game.
#### Parameters ####
  * g : The game-key
  * p : The player-key
  * lat : The player-latitude
  * lon : The player-longitude
#### XML-file ####
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<response
	value="{{value}}"
/>
```
Annotation: `value` is 'done' if everything went fine, 'input error' if a parameter was not well formatted or 'error' if
  * the player or the game does not exist
  * the game is full
  * the player already is in another game



&lt;hr&gt;



## /start ##
Starts a game
#### Parameters ####
  * p : The player-key of the creator
#### XML-file ####
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<response
	value="{{value}}"
/>
```
Annotation: `value` is 'done' if everything went fine, 'input error' if a parameter was not well formatted or 'error' if
  * the player does not exist
  * the player is in no game
  * the player is not the creator
  * the game is not full
  * the game-status is not 0



&lt;hr&gt;



## /stop ##
Stops a game
#### Parameters ####
  * p : The player-key of the creator
#### XML-file ####
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<response
	value="{{value}}"
/>
```
Annotation: `value` is 'done' if everything went fine, 'input error' if a parameter was not well formatted or 'error' if
  * the player does not exist
  * the player is in no game
  * the player is not the creator



&lt;hr&gt;



## /leave ##
The player leaves his game. If he was the creator, the game is stopped.
#### Parameters ####
  * p : The key of the player
#### XML-file ####
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<response
	value="{{value}}"
/>
```
Annotation: `value` is 'done' if everything went fine, 'input error' if a parameter was not well formatted or 'error' if
  * the player does not exist
  * the player is in no game



&lt;hr&gt;



## /update ##
#### Parameters ####
  * p : The key of the player you want to update
  * lat : The new latitude of the player
  * lon : The new longitude of the player
#### XML-file ####
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<wayne>
<state 
	mode ="{{mode}}"
	state="{{state}}"

	lat="{{modespecific.lat}}"
	lon="{{modespecific.lon}}"	
/>

<events>
	{% for event in events %}
	<event
		title="{{event.title}}"
		info="{{event.info}}"
		extra="{{event.extra}}"
	/>		
	{% endfor %}
</events>
</wayne>
```
Annotation: `lat` and `lon` is the target location. Actually there are 2 events:
```
title = "victory"
info = name of the winner
extra = player-number of the winner
```
```
title = "hunter"
info = latitude of the hunter
extra = longitude of the hunter
```