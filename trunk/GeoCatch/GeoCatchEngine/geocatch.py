import cgi
import logging

from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

import os
from google.appengine.ext.webapp import template

from google.appengine.ext import db

DEBUG = True
TEMPLATE_FOLDER = os.path.join(os.path.dirname(__file__), 'templates')

##########################################################
#data models
##########################################################
class Player(db.Model): #dummy class for reference in game
	pass

class Game(db.Model):
	""" game data """
	name = db.StringProperty(multiline=False)
	# 0 = not yet started
	# 1 = running
	# 2 = finished
	status = db.IntegerProperty()
    
	version = db.IntegerProperty() # used when there are multiple versions
	mode = db.IntegerProperty() # used when there are multiple gamemodes
    
	creator = db.ReferenceProperty(Player)
	creatorLocation = db.GeoPtProperty()
	date = db.DateTimeProperty(auto_now_add=True)
	
	players = db.ListProperty(db.Key) #TODO: any way to create a list with keys only from players?
	#how to handle: http://groups.google.com/group/google-appengine/msg/f3139e97ee01ce65
	#http://www.gomuse.com/google-app-engine-using-the-list-property-dbl

	playerCount = db.IntegerProperty()
	maxPlayerCount = db.IntegerProperty()    

class Player(db.Model):	
	""" player data """
	lastLocation = db.GeoPtProperty()
	currentGame = db.ReferenceProperty(Game)
    
	name = db.StringProperty(multiline=False)
	mac = db.StringProperty(multiline=False)
    
	powerUp = db.IntegerProperty()
	powerUpLocation = db.GeoPtProperty()

class Path(db.Model):
	""" path points of the way a player has traveled during a game  """
	player = db.ReferenceProperty(Player)
	game = db.ReferenceProperty(Game)
	date = db.DateTimeProperty(auto_now_add=True)
	location = db.GeoPtProperty()
	
##########################################################
#request handlers
##########################################################
class MainPage(webapp.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.out.write('Hello, World! I\'m geocatch')

class GetGameList(webapp.RequestHandler):
	""" returns an xml file with all games with vacant slots """
	def get(self):
		games = Game.all().filter("status = ",0).order('-name')
		
		template_values = {'games': games,}
		
		template_path = os.path.join(TEMPLATE_FOLDER, 'games.xml')
		self.response.out.write(template.render(template_path, template_values))
		
class JoinGame(webapp.RequestHandler):
	""" calling player joins the game if it exists and he is not already in another one """
	def get(self):
		game_key = cgi.escape(self.request.get('g'))
		player_key = cgi.escape(self.request.get('p'))
		
		#TODO: maybe check mac address?
		#TODO: sanity checks of the data?
		
		game = Game.all().filter("key =", game_key).get()
		player = Player.all().filter("key =", game_key).get()
		
		if game != None and player != None:
			if not player_key in game.players:
				game.players.append(player_key)
				game.playerCount += 1
				game.put()
				
				player.currentGame = game_key
				player.put()
				logging.info('Player %s joined game %s'%((player_key,game_key))) 
			else:
				logging.error('Player %s tried to join game %s where he already is'%(player_key,game_key))
			logging.error('Either player %s or game %s is not existing, join failed'%(player_key,game_key))
				
		#TODO: give feedback to the player? or an error message

class StopGame(webapp.RequestHandler):
	"""stops the game if called by the creator """
	def get(self):	
		player_key = cgi.escape(self.request.get('p'))
		game_key = cgi.escape(self.request.get('g'))
		
		#TODO: maybe check mac address?
		#TODO: sanity checks of the data?
		
		game = Game.all().filter("key =", game_key).get()
		
		#check if game can be started and user has the right to
		if game != None and game.creator == player_key and game.status == 1:
			game.status = 2
			game.put()
			logging.info('Game %s stopped by player %s'%(game_key,player_key))
		else:
			logging.error('Attempt to stop game %s by player %s failed'%(game_key,player_key))

class StartGame(webapp.RequestHandler):
	""" start an already created game, only usable by the creator """
	def get(self):		
		player_key = cgi.escape(self.request.get('p'))
		game_key = cgi.escape(self.request.get('g'))
		
		#TODO: maybe check mac address?
		#TODO: sanity checks of the data?
		
		game = Game.all().filter("key =", game_key).get()
		
		#check if game can be started and user has the right to
		if game != None and game.creator == player_key and game.status == 0:
			if game.playerCount == game.maxPlayerCount:
				game.status = 1
				game.put()
				logging.info('Game %s started by player %s'%(game_key,player_key))
			else:
				logging.info('Game %s starting by player %s failed due to too small playercount'%(game_key,player_key))
				#TODO: give feedback to creator why it failed?
		else:
			logging.error('Attempt to start game %s by player %s failed'%(game_key,player_key))

class LeaveGame(webapp.RequestHandler):
	""" a player can leave a game, game is closed if the player was the creator """
	def get(self):
		game_key = cgi.escape(self.request.get('g'))
		player_key = cgi.escape(self.request.get('p'))
		
		#TODO: maybe check mac address?
		#TODO: sanity checks of the data?
		
		game = Game.all().filter("key =", game_key).get()
		player = Player.all().filter("key =", game_key).get()
		
		#check if player is in game and game exists, if the player is the creator close the game
		if game != None and player != None:
			if game.creator == player_key:
				#TODO: close game
				
				game.creator.currentGame = None
				game.status = 2
				
				game.creator.put()
				game.put()
				
				logging.info('Creator %s left game %s, game stopped'%(player_key,game_key))
				
			elif player_key in game.players:
				player.currentGame = None
				player.put()
				
				game.players.remove(player_key)
				game.playerCount -= 1
				game.put()

				logging.info('Player %s left game %s, game has now %s players left'%(player_key,game_key,game.playerCount))
				
				#TODO: deal with the horrible aftermath
				#maybe if only 2 left start showdown, give 2 minutes then set marker in between them
		else:
			logging.error('Attempt to leave game %s by player %s failed, no game or player'%(game_key,player_key))			
				
		#TODO: give feedback to the player? or an error message

class PlayerUpdateState(webapp.RequestHandler):
	"""updates the player state and provides him with stuff he should know"""
	def get(self):
		logging.info('Player %s sent update for game %s'%(player_key,game_key))
		pass #TODO: deal with update information
		#TODO: return updated game state

class RegisterPlayer(webapp.RequestHandler):
	"""checks if mac address already in database, registers a new player otherwise""" #TODO: really a good idea? captcha?
	def get(self):
		mac = cgi.escape(self.request.get('m'))
		name = cgi.escape(self.request.get('n'))
		
		#TODO: sanity checks of the data?
		
		player = Player.all().filter("mac =", mac).get()
		
		if player == None:
			player = Player()
			
			player.mac = mac
			player.name = name
			
			logging.info('Created new Player %s with name %s and mac %s'%(player_key,player.name,player.mac))
		else:
			if player.currentGame != None: 
				logging.error('Creating new Player %s failed because player is in game %s'%(player_key,player.currentGame))
				return #TODO: do something if someone tries to register whiile there is an ongoing game?

			player.name = name
			logging.info('Changed player name for %s to %s'%(player_key,player.name))

		player_key = player.put()
		self.response.out.write(player_key)
		#TODO: response as xml
		
		#todo: say if everything went fine
		
class CreateGame(webapp.RequestHandler):
	""" creates game and returns it's key, 0 otherwise """
	def get(self):
		self.response.headers['Content-Type'] = 'text/plain'

		name = cgi.escape(self.request.get('n'))
		version = int(cgi.escape(self.request.get('v')))
		creatorLocation = cgi.escape(self.request.get('lat'))+","+cgi.escape(self.request.get('lon'))
		player_key = cgi.escape(self.request.get('p'))
							
		#TODO: sanity checks of the data?
		
		#TODO: test if game can be created, abort otherwise
		if (True):					
			game = Game()
			
			game.name = name
			game.status = 0
			game.version = version
			game.creatorLocation = creatorLocation
			game.playerCount = 1
			game.maxPlayerCount = 3
			game.creator = player_key
			
			game_key = game.put() #TODO: use the database key seems best, as they are unique already - no conflicts
			
			logging.info('New game %s created by player %s'%(game_key,player_key))
			
			self.response.out.write(game_key)
			#TODO: response as xml
			return
		
		logging.error('Attempt to create new game by player %s failed'%(player_key))
		self.response.out.write("0") #abort
		#TODO: response as xml
		return
		

def clearDatabase():
	models = ["Player","Game","Path"]
	base_query = "SELECT __key__ FROM "
	
	for i in models:
		q = db.GqlQuery(base_query+i)
		results = q.fetch(100) #has to be called multiple times maybe
		#TODO: mind that it only deletes 100 items at once 
		for r in results:
			db.delete(r)

class ClearData(webapp.RequestHandler):
	""" clears the database - as good and shiny as new """
	def get(self):
		logging.critical('Database cleared!') #todo: log from where?
		clearDatabase()
		
	
class FillWithTestdata(webapp.RequestHandler):
	""" clears the database and fills it with brand new samples. boring. the implemented classes should be rather used """
	def get(self):
		logging.critical('Database cleared and filled with test data!')
		
		#clear data models
		clearDatabase()
		
		#fill with junk
		player1 = Player()
		player1.name = "Player 1"
		player1.mac = "mac1"
		player1.lastLocation = "0,0"
		player1_key = player1.put()
		
		player2 = Player()
		player2.name = "Player 2"
		player2.mac = "mac2"
		player2.lastLocation = "20,20"
		player2_key = player2.put()
		
		player3 = Player()
		player3.name = "Player 3"
		player3.mac = "mac3"
		player3.lastLocation = "50,30"		
		player3_key = player3.put()
		
		game1 = Game()
		game1.name = "game1"
		game1.status = 0
		game1.mode = 0
		game1.version = 1
		game1.creator = player3_key
		game1.creatorLocation = player3.lastLocation
		game1.players = [player3_key, player1_key]
		game1.playerCount = 2
		game1.maxPlayerCount = 3
		
		game1_key = game1.put()
		player1.currentGame = game1_key
		player3.currentGame = game1_key

		game2 = Game()
		game2.name = "game2"
		game2.status = 0
		game2.mode = 0
		game2.version = 1
		game2.creator = player2_key
		game2.creatorLocation = player2.lastLocation
		game2.players = [player2_key,]
		game2.playerCount = 1
		game2.maxPlayerCount = 3

		player2.currentGame = game2.put()


##########################################################
application = webapp.WSGIApplication(
                                     [('/', MainPage),
                                      ('/games', GetGameList),
                                      
                                      ('/register', RegisterPlayer),
                                      
                                      ('/create', CreateGame),
                                      ('/join', JoinGame),                                      
                                      ('/start', StartGame),
                                      ('/stop', StopGame),
                                      ('/leave', LeaveGame),
                                      
                                      ('/update', PlayerUpdateState),
                                      
                                      #DEBUG FUNCTIONS, TODO: mind them
                                      ('/fillWithTestdata', FillWithTestdata),
                                      ('/clearData', ClearData),
                                     ],
                                     debug=DEBUG)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
