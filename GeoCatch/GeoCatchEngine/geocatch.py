# -*- coding: utf-8 -*-

import cgi
import logging

from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

import os

from google.appengine.ext.webapp import template
from google.appengine.ext import db

from inputcheck import *

import datetime

DEBUG = True
TEMPLATE_FOLDER = os.path.join(os.path.dirname(__file__), 'templates')

def respond(caller, value):
	""" template to respond with a standard response.xml and a caller provided value as result ("error" or "done" most of the time) """
	template_values = {'value': value,}
	template_path = os.path.join(TEMPLATE_FOLDER, 'response.xml') #TODO: more details on what actually happened? error into own xml tag?

	caller.response.out.write(template.render(template_path, template_values))			

# geo stuff:
# http://code.google.com/appengine/articles/geosearch.html
# http://code.google.com/apis/maps/articles/geospatial.html (maps, nvm)

##########################################################
#data models
##########################################################
class Game(db.Model): #dummy class to be referenced by the player class, ignore it
	pass

class Player(db.Model):	
	""" player data model """
	lastLocation = db.GeoPtProperty()
	currentGame = db.ReferenceProperty(Game)
	playerNumber = db.IntegerProperty()              #starts with 1

	name = db.StringProperty(multiline=False)
	mac = db.StringProperty(multiline=False)

	hunter = db.SelfReferenceProperty(collection_name="hunter_set")
	prey = db.SelfReferenceProperty(collection_name="prey_set")
	aboutToBeCaught = db.BooleanProperty()

	nearlyCaught = db.BooleanProperty()

	# 0 is none
	# 1 is "show hunter" 		#ignore from here
	# 2 is ???		
	# 3 is PROFIT
	powerUpActive = db.IntegerProperty()
	powerUpActivated = db.DateTimeProperty()
	powerUpExpires = db.DateTimeProperty()

class Game(db.Model):
	""" game data model """
	name = db.StringProperty(multiline=False)
	# 0 = not yet started
	# 1 = running
	# 2 = stopped
	# 3 = finished
	status = db.IntegerProperty()

	version = db.IntegerProperty() # used when there are multiple versions

	# 0 = race to the point
	# 1 = chain-catch, who catches first wins
	mode = db.IntegerProperty() # used when there are multiple gamemodes

	creator = db.ReferenceProperty(Player)
	creatorLocation = db.GeoPtProperty()
	date = db.DateTimeProperty(auto_now_add=True)

	timer = db.IntegerProperty()

	players = db.ListProperty(db.Key) #TODO: any way to create a list with keys only from players?
	#how to handle: http://groups.google.com/group/google-appengine/msg/f3139e97ee01ce65
	#http://www.gomuse.com/google-app-engine-using-the-list-property-dbl

	playerCount = db.IntegerProperty()
	maxPlayerCount = db.IntegerProperty()

	goal = db.GeoPtProperty()
	winner = db.ReferenceProperty(Player, collection_name="winner_set")

	started = db.DateTimeProperty() #when the creator started it
	starting = db.DateTimeProperty() #when it will really start eg the timer reach zero

class Path(db.Model):
	""" path point data model (used to describe the way a player has traveled during a game) """
	player = db.ReferenceProperty(Player)
	game = db.ReferenceProperty(Game)
	date = db.DateTimeProperty(auto_now_add=True)
	location = db.GeoPtProperty()

##################### functions

def closeTo(pointOne, pointTwo):
	""" checks if two geoPoints are close to each other - less than 0.0002 difference in latitude and longitude """
	if abs(pointOne.lat-pointTwo.lat) < 0.0002:
		if abs(pointOne.lon-pointTwo.lon) < 0.0002:
			return True
	return False


##########################################################
#request handlers
##########################################################
class MainPage(webapp.RequestHandler):
	""" a humble tribute to the coding gods """
	def get(self):
		self.response.headers['Content-Type'] = 'text/plain'
		self.response.out.write('Hello, World! I\'m geocatch')

class GetGameList(webapp.RequestHandler):
	""" returns a xml file with all games yet to be started (e.g. joinable)
	
		request parameters:
		none
	"""
	def get(self):
		#TODO: get player key and player location, update player location and give back games close to player!

		#TODO: limit and sort by distance to requesting person
		games = Game.all().filter("status = ",0).order('-name')

		template_values = {'games': games,}

		template_path = os.path.join(TEMPLATE_FOLDER, 'games.xml')
		self.response.out.write(template.render(template_path, template_values))

class GetGamePlayerList(webapp.RequestHandler):
	""" returns an xml file with all players in a game, meant to be called from the lobby
	
		request parameters:
		g = game key
	"""
	def get(self):
		try:
			game_key = checkKey(self.request.get('g'))
		except:
			logging.error('InputError') #todo: more precise catching and more verbose... debugging will be a nightmare otherwise
			respond(self, "input error")
			return

		game = Game.get(game_key)
		players = []

		if game == None:
			logging.error('Game not existing %s'%game_key)
			respond(self, "error")
			return #game not found, should not happen

		for i,j in enumerate(game.players):
			player = {}
			player["number"] = i+1

			currentPlayer = Player.get(j)

			player["name"] = currentPlayer.name
			player["creator"] = 1 if (game.creator.key() == j) else 0
			players.append(player)

		template_values = {'players': players,}

		template_path = os.path.join(TEMPLATE_FOLDER, 'players.xml')
		self.response.out.write(template.render(template_path, template_values))

class PlayerActivatePowerup(webapp.RequestHandler):
	""" activates a powerup for a given player in his current game
	
		request parameters:
		p = player key
		pow = powerup id
	"""
	def get(self):
		try:
			player_key = checkKey(self.request.get('p'))
			powerupId = checkInt(self.request.get('pow'))
		except:
			logging.error('InputError') #todo: more precise catching and more verbose... debugging will be a nightmare otherwise
			respond(self, "input error")
			return

		player = Player.get(player_key)

		if player != None:
			if player.currentGame != None:
				game_key = player.currentGame.key()
				game = Game.get(game_key)
			else:
				logging.error('Player %s is in no game'%(player_key))
				respond(self,"error")
				return
		else:
			logging.error('There is no spoon, eh... player %s'%(player_key))
			respond(self,"error")
			return		

		if game != None:
			if powerupId == 1: #see hunter
				now = datetime.datetime.now()
				
				player.powerUpActive = powerupId
				player.powerUpActivated = now
				player.powerUpExpires = now+datetime.timedelta(minutes=3) #todo: variable time for powerups?
				player.put()

			logging.info('Player %s activated powerup %s'%(player_key, powerupId))
			respond(self,"done")
			return		
		else:
			logging.error('There is no game %s'%(game_key))
			respond(self,"error")
			return

class GetGameState(webapp.RequestHandler):
	""" returns an xml file with the player list and game state
		additionally the player number for a given player in the game, if a valid player key is provided, ignored otherwise
		
		this method is used to get lobby updates and be notified if the game is starting/has been started
		if the game was started the current time and the time when the game will start are provided

		request parameters:
		g = game key
		p = player key (optional)
		"""
	def get(self):
		try:
			game_key = checkKey(self.request.get('g'))			

			try:
				player_key = checkKey(self.request.get('p'))
				#playerLocation = checkLocation(self.request.get('lat')+","+self.request.get('lon')).split(",") #TODO: update location here?
			except:
				player_key = None
		except:
			logging.error('InputError') #todo: more precise catching and more verbose... debugging will be a nightmare otherwise
			respond(self, "input error")
			return

		game = Game.get(game_key)
		players = []

		if game == None:
			logging.error('Game not existing %s'%game_key)
			respond(self, "error")
			return #game not found, should not happen

		for i,j in enumerate(game.players):
			player = {}
			p = Player.get(j)
			if game.status != 1: #if player numbers not fixed
				player["number"] = i+1
			else:
				player["number"] = p.playerNumber
			player["name"] = p.name
			player["creator"] = 1 if (game.creator.key() == j) else 0
			players.append(player)

		gameInfo = {}
		gameInfo["name"] = game.name
		gameInfo["mode"] = game.mode
		gameInfo["timer"] = game.timer
		gameInfo["status"] = game.status
		gameInfo["mpc"] = game.maxPlayerCount

		additional = {}

		if player_key != None and game.status == 1:
			player = Player.get(player_key)

			if player != None:
				additional["playerNumber"] = player.playerNumber
				additional["started"] = game.started
				additional["timeNow"] = datetime.datetime.now()
				additional["starting"] = game.starting

				#player.lastLocation = db.GeoPt(playerLocation[0], playerLocation[1])
				#player.put()

			else:
				additional = None #todo: error? player key was provided but wrong
		else:
			additional = None

		template_values = {'game': gameInfo, 'players': players, 'additional': additional}

		template_path = os.path.join(TEMPLATE_FOLDER, 'game.xml')
		self.response.out.write(template.render(template_path, template_values))


#############################################		

class JoinGame(webapp.RequestHandler):
	""" calling player joins the given game if it exists and he is not already in another one

		request parameters:
		g = game key
		p = player key
		lat = latitude of the player
		lon = longitude of the player
	"""
	def get(self):
		try:
			game_key = checkKey(self.request.get('g'))
			player_key = checkKey(self.request.get('p'))
			playerLocation = checkLocation(self.request.get('lat')+","+self.request.get('lon')).split(",")
		except:
			logging.error('InputError') #todo: more precise catching and more verbose... debugging will be a nightmare otherwise
			respond(self, "input error")
			return

		game = Game.get(game_key)
		player = Player.get(player_key)

		value = "error"

		if game != None and player != None:
			player.lastLocation = db.GeoPt(playerLocation[0], playerLocation[1])
			player.put()

			if game.playerCount < game.maxPlayerCount:
				if player.currentGame == None:				
					if not (player.key() in game.players):
						game.players.append(player.key())
						game.playerCount += 1
						game.put()

						player.currentGame = game.key()						
						player.put()

						logging.info('Player %s joined game %s'%((player_key,game_key))) 

						value = "done"
					else:
						logging.error('Player %s tried to join game %s where he already is'%(player_key,game_key))
				else:
					logging.error('Player %s tried to join game %s but he already is in another game'%(player_key,game_key))
			else:
				logging.error('Player %s tried to join game %s but there was no room'%(player_key,game_key))
				#TODO: tell the poor guy
		else:
			logging.error('Either player %s or game %s is not existing, join failed'%(player_key,game_key))

		#TODO: give feedback to the player? or an error message
		#TODO: how about establishing a connection so the client will receive changes in the lobby and other events instead of constantly polling for them?
		respond(self, value)

class StopGame(webapp.RequestHandler):
	"""stops the game if player is the creator of a game

		request parameters:
		p = player key
	"""
	def get(self):
		try:
			player_key = checkKey(self.request.get('p'))
		except:
			logging.error('InputError') #todo: more precise catching and more verbose... debugging will be a nightmare otherwise
			respond(self, "input error")
			return

		player = Player.get(player_key)

		if player != None:
			if player.currentGame != None:
				game_key = player.currentGame.key()
				game = Game.get(game_key)
			else:
				logging.error('Player %s is in no game'%(player_key))
				respond(self,"error")
				return
		else:
			logging.error('There is no player %s'%(player_key))
			respond(self,"error")
			return

		#check if game can be started and user has the right to
		if game != None and game.creator.key() == player.key(): #and game.status == 1:
			game.status = 2
			game.put()
			logging.info('Game %s stopped by player %s'%(game_key,player_key))
			respond(self, "done")
		else:
			logging.error('Attempt to stop game %s by player %s failed'%(game_key,player_key))
			respond(self, "error")

class StartGame(webapp.RequestHandler):
	""" start an already created game, only usable if player is a creator
	
		request parameters:
		p = player key
	"""
	def get(self):		
		try:
			player_key = checkKey(self.request.get('p'))
		except:
			logging.error('InputError') #todo: more precise catching and more verbose... debugging will be a nightmare otherwise
			respond(self, "input error")
			return

		player = Player.get(player_key)

		if player != None:
			if player.currentGame != None:
				game_key = player.currentGame.key()
				game = Game.get(game_key)
			else:
				logging.error('Player %s is in no game'%(player_key))
				respond(self,"error")
				return
		else:
			logging.error('There is no player %s'%(player_key))
			respond(self,"error")
			return

		#check if game can be started and user has the right to
		if game != None and game.creator.key() == player.key() and game.status == 0:
			if game.playerCount == game.maxPlayerCount:

				for i,j in enumerate(game.players): #set player numbers for each player
					p = Player.get(j)
					p.playerNumber = i+1
					
					p.aboutToBeCaught = False	#set flags to false
					p.nearlyCaught = False
					
					p.put()

				#gamemode specific settings			
				if game.playerCount < 3: #"race to the point" game
					#TODO: any way to make sure the marker is on a walkable place after this?
					if game.playerCount == 1:
						lat = player.lastLocation.lat+0.0005
						lon = player.lastLocation.lon+0.0005 #TODO random in a certain radius range, calculating meter to lat/lon dependig on the position will sure be a lot fun
					else:
						#TODO: calculate something fair between the 2 players
						#right now it's just the average, at least it should be but i doubt it
						lat = 0.0
						lon = 0.0

						for i in game.players:
							p = Player.get(i)
							lat += p.lastLocation.lat
							lon += p.lastLocation.lon

						lat /= len(game.players)
						lon /= len(game.players)

					#TODO: check & modulo lat and lon? are they bounded? (for the unexpected case where people are playing between min and max 

					game.goal = db.GeoPt(lat,lon)
					game.mode = 0 #TODO: don't, you should get the mode and check it, not set it
				else:
					#TODO: random and watch out for it to be one big cycle
					for i,j in enumerate(game.players):
						p = Player.get(j)
						
						next = (i+1)%len(game.players)
						prev = (i-1)%len(game.players)
						
						p.hunter = game.players[next]
						p.prey = game.players[prev]
						p.put()
					game.mode = 1 #TODO: don't, you should get the mode and check it, not set it

				# set timer for start and started
				started = datetime.datetime.now()

				game.started = started
				game.starting = started+datetime.timedelta(seconds=game.timer)

				game.status = 1
				game.put()
				logging.info('Game %s started by player %s'%(game_key,player_key))
				respond(self, "done")
			else:
				logging.info('Game %s starting by player %s failed due to too small playercount'%(game_key,player_key))
				respond(self, "error")
				#TODO: give feedback to creator why it failed?
		else:
			logging.error('Attempt to start game %s by player %s failed'%(game_key,player_key))
			respond(self, "error")

class LeaveGame(webapp.RequestHandler):
	""" a player can leave a game he joined/created, game is closed if the player was the creator

		request parameters:
		p = player key
	"""
	def get(self):
		try:
			player_key = checkKey(self.request.get('p'))
		except:
			logging.error('InputError') #todo: more precise catching and more verbose... debugging will be a nightmare otherwise
			respond(self, "input error")
			return

		player = Player.get(player_key)

		if player != None:
			if player.currentGame != None:
				game_key = player.currentGame.key()
				game = Game.get(game_key)
			else:
				logging.error('Player %s is in no game'%(player_key))
				respond(self,"error")
				return
		else:
			logging.error('There is no player %s'%(player_key))
			respond(self,"error")
			return

		value = leaveGame(game,player)

		#TODO: give feedback to the player? or an error message
		respond(self, value)

def leaveGame(game, player): # is also called in register player if THE UNPROBABLE happens (e.g. there was a crash and bobby can't come in again)
	#check if player is in game and game exists, if the player is the creator close the game
	""" helper function which kicks a player out of a game, has to get validated input """
	game_key = game.key()
	player_key = player.key()

	if game != None and player != None:			
		if game.creator.key() == player.key():
			#TODO: close game

			player.currentGame = None
			player.put()

			game.status = 2
			game.players.remove(player.key())
			game.playerCount -= 1
			game.put()

			logging.info('Creator %s left game %s, game stopped'%(player_key,game_key))
			value = "done"
		elif player.key() in game.players:
			player.currentGame = None
			player.put()

			game.players.remove(player.key())
			game.playerCount -= 1
			game.put()

			logging.info('Player %s left game %s, game has now %s players left'%(player_key,game_key,game.playerCount))

			#TODO: deal with the horrible aftermath
			#maybe if only 2 left start showdown, give 2 minutes then set marker in between them
			value = "done"
		else:
			logging.error('Attempt to leave game %s by player %s failed, not in list apparently and not creator'%(game_key,player_key))			
			value = "error"		
	else:
		logging.error('Attempt to leave game %s by player %s failed, no game or player'%(game_key,player_key))			
		value = "error"

	return value

class Event: #todo: save events in the database, this is just a testing thing
	""" internal datamodel of a meta-event, used solely in templates

		currently used for 2 event types, with following var meanings:	
		what: victory
		who: playername
		extra: playernumber

		what: hunter
		who: lat
		extra: lon
	"""
	def __init__(self, what, who, extra):
		self.title = what
		self.info = who
		self.extra = extra #TODO: wtf are these var names?

class PlayerUpdateState(webapp.RequestHandler):
	"""updates the player state and provides him with the current game state (e.g. stuff he should know)

		request parameters:
		p = player key
		lat = latitude of the player
		lon = longitude of the player
	"""
	def get(self):
		try:
			player_key = checkKey(self.request.get('p'))

			newLocation = checkLocation(self.request.get('lat')+","+self.request.get('lon')).split(",")
			#TODO: additional event requests, like "aktivate a powerup"			
		except:
			logging.error('InputError') #todo: more precise catching and more verbose... debugging will be a nightmare otherwise
			respond(self, "input error")
			return

		player = Player.get(player_key)

		if player != None:
			if player.currentGame != None:
				game_key = player.currentGame.key()
				game = Game.get(game_key)
			else:
				logging.error('Player %s is in no game'%(player_key))
				respond(self,"error")
				return
		else:
			logging.error('There is no player %s'%(player_key))
			respond(self,"error")
			return

		logging.debug('Player %s sent update for game %s'%(player_key,game_key))			

		if game != None and player != None:
			if not player.key() in game.players:
				logging.error('Player %s sent update for game %s, but he is not in the game at all'%(player_key,game_key))
				return #TODO: proper error message
			modespecific = {}
			modespecific["lat"] = 0 #dummy values
			modespecific["lon"] = 0 #dummy values
			
			if game.status == 1: # if game running you can change stuff, otherwise no
				loc = db.GeoPt(newLocation[0], newLocation[1])
				
				#TODO: deal with update information
				path = Path()
				path.player = player.key()
				path.game = game.key()
				path.location = loc
				path.put()

				player.lastLocation = loc
				player.put()

				if game.mode == 1: #catch
					
					modespecific["lat"] = player.prey.lastLocation.lat #goal lat and lon
					modespecific["lon"] = player.prey.lastLocation.lon


				#check if someone caught someone else
				#check should take 2 refresh states to make sure there are no "lagcatches"
				#the first proximity puts up a flag, the second makes it final 

					#check if near to prey
					if closeTo(player.lastLocation,player.prey.lastLocation):
						if player.prey.aboutToBeCaught: #already has been close:
							#the player wins the game
							game.winner = player.key()
							game.status = 3
							game.put()
						else:
							player.prey.aboutToBeCaught = True
							player.prey.put()
					else:
						player.prey.aboutToBeCaught = False
						player.prey.put()


					#check if near to hunter
					if closeTo(player.lastLocation,player.hunter.lastLocation):
						if player.aboutToBeCaught: #already has been close:
							#the hunter wins the game
							game.winner = player.hunter.key()
							game.status = 3
							game.put()
						else:
							player.aboutToBeCaught = True
							player.put()
					else:
						player.aboutToBeCaught = False
						player.put()

				elif game.mode == 0: #race, im moment auf 5 nachkommastellen genau prüfen ob am ziel angekommen
					
					modespecific["lat"] = game.goal.lat #goal lat and lon
					modespecific["lon"] = game.goal.lon

					#test if player close enough to goal point
					#if round(loc.lat, 5) ==  round(game.goal.lat, 5) and round(loc.lon, 5) ==  round(game.goal.lon, 5):
					if closeTo(loc,game.goal):
						# \o/ victory
						#finish game, save who won
						game.winner = player.key()
						game.status = 3
						game.put()
						
			events = []

			if game.status == 3:
				events.append(Event("victory", game.winner.name, game.winner.playerNumber))

			if player.powerUpActive == 1:
				if player.powerUpExpires < datetime.datetime.now(): #if powerup expired
					player.powerUpActive = 0
					player.put()
					logging.info('Hunter powerup of player %s expired'%(player_key))
				else:
					events.append(Event("hunter", player.hunter.lastLocation.lat, player.hunter.lastLocation.lon))			

			template_values = {'state': game.status, 'mode': game.mode, 'modespecific':modespecific, 'events': events}

			template_path = os.path.join(TEMPLATE_FOLDER, 'state.xml')
			self.response.out.write(template.render(template_path, template_values))				
		else:
			logging.error('Player %s sent update for game %s, game or player not found'%(player_key,game_key))
			return

class NameChangePlayer(webapp.RequestHandler):
	"""checks if mac address already in database, registers a new player otherwise
	
		request parameters:
		p = player key
		n = new name
	"""
	def get(self):
		try:
			player_key = checkKey(self.request.get('p'))
			name = checkName(self.request.get('n'))
		except:
			logging.error('InputError') #todo: more precise catching and more verbose... debugging will be a nightmare otherwise
			respond(self, "input error")
			return

		player = Player.get(player_key)

		if player == None:
			logging.info('Changing playername for player %s'%(player_key))
			respond(self, "error")
			return
		else:
			player.name = name
			player_key = player.put()
			logging.info('Changed player name for %s to %s'%(player.key(),player.name))
			respond(self, "done")		

class RegisterPlayer(webapp.RequestHandler): 	#TODO: really a good idea? captcha?
	"""checks if mac address already in database, registers a new player otherwise

		!!! kicks the player out of any game he might be still in, used as "come clean/reset" function
	
		request parameters:		
		m = device mac
		n = name
		lat = latitude of the player
		lon = longitude of the player
	"""
	def get(self):
		try:
			mac = checkMac(self.request.get('m'))
			name = checkName(self.request.get('n'))
			playerLocation = checkLocation(self.request.get('lat')+","+self.request.get('lon')).split(",")
		except:
			logging.error('InputError') #todo: more precise catching and more verbose... debugging will be a nightmare otherwise
			respond(self, "input error")
			return

		player = Player.all().filter("mac =", mac).get()

		if player == None:
			player = Player()

			player.mac = mac
			player.name = name
			player.lastLocation = db.GeoPt(playerLocation[0], playerLocation[1])

			logging.info('Created new Player with name %s and mac %s'%(player.name,player.mac))
		else:
			if player.currentGame != None:
				logging.error('Creating new Player %s, but he is already in game %s, leaving'%(player.key(),player.currentGame.key()))
				# kicking the bugger
				leaveGame(player.currentGame,player) #TODO: errors here are not caught or returned to the client, well, shit

				#TODO: maybe this could be done better?
				player = Player.get(player.key()) #get a new player, otherwise the old one is overwritten and still has a current game

			player.name = name
			player.lastLocation = db.GeoPt(playerLocation[0], playerLocation[1])
			logging.info('Changed player name for %s to %s'%(player.key(),player.name))

		player_key = player.put()

		#say if everything went fine
		respond(self, player_key)

class CreateGame(webapp.RequestHandler):
	""" creates game and returns it's key, 0 otherwise

		request parameters:		
		n = game name
		v = program version (meant to provide backward compatibility once there are other versions)
		lat = latitude of the creator
		lon = longitude of the creator

		p = creator player key
		t = timer value (int seconds)
		mpc = maximal player count ( 1 = singleplayer, 2 = 2 players, 3+ = chaincatch modes)		
	"""
	def get(self):
		try:
			name = checkName(self.request.get('n'))
			version = checkInt(self.request.get('v'))
			creatorLocation = checkLocation(self.request.get('lat')+","+self.request.get('lon')).split(",")
			player_key = checkKey(self.request.get('p'))
			timer = checkInt(self.request.get('t'))

			maxPlayerCount = checkInt(self.request.get('mpc'))
		except:
			logging.error('InputError') #todo: more precise catching and more verbose... debugging will be a nightmare otherwise
			respond(self, "input error")
			return

		player = Player.get(player_key)

		#TODO: test if game can be created, abort otherwise
		if player != None and player.currentGame == None:
			game = Game()

			loc = db.GeoPt(creatorLocation[0], creatorLocation[1])

			game.name = name
			game.status = 0
			game.mode = 2 #TODO: set here, not in the game start thingy
			game.version = version
			game.creatorLocation = loc
			game.playerCount = 1
			game.timer = timer
			game.maxPlayerCount = maxPlayerCount
			game.creator = player.key()
			game.players = [player.key(), ]

			#todo: update player location?

			game_key = game.put() #the use the database keys seems best, as they are unique already - no conflicts

			player.currentGame = game.key()
			player.lastLocation = loc
			player.put()

			logging.info('New game %s created by player %s'%(game_key,player_key))

			value = game_key
		else:
			logging.error('Attempt to create new game by player %s failed'%(player_key))
			value = "error"

		#TODO: more detailed and a reason why it can't be created if there are issues?
		respond(self, value)

##########################################################
application = webapp.WSGIApplication(
	[('/', MainPage),
	 ('/games', GetGameList),
	 ('/gamePlayers', GetGamePlayerList),
	 ('/gameState', GetGameState),

	 ('/register', RegisterPlayer),
	 ('/name', NameChangePlayer),

	 ('/activate', PlayerActivatePowerup),

	 ('/create', CreateGame),
	 ('/join', JoinGame),                                      
	 ('/start', StartGame),
	 ('/stop', StopGame),
	 ('/leave', LeaveGame),

	 ('/update', PlayerUpdateState),

	 ],
	 debug=DEBUG)

def main():
	run_wsgi_app(application)

if __name__ == "__main__":
	main()
