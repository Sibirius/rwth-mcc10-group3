import cgi

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
class Player(db.Model): #dummy class
	pass

class Game(db.Model):
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
	lastLocation = db.GeoPtProperty()
	currentGame = db.ReferenceProperty(Game)
    
	name = db.StringProperty(multiline=False)
	mac = db.StringProperty(multiline=False)
    
	powerUp = db.IntegerProperty()
	powerUpLocation = db.GeoPtProperty()

class Path(db.Model):
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
    def get(self):
		games = Game.all().filter("status = ",0).order('-name')
		
		template_values = {'games': games,}
		
		template_path = os.path.join(TEMPLATE_FOLDER, 'games.xml')
		self.response.out.write(template.render(template_path, template_values))
		
class JoinGame(webapp.RequestHandler):
    def get(self):
		game_key = cgi.escape(self.request.get('g'))
		player_key = cgi.escape(self.request.get('p'))
		
		#TODO: maybe check mac address?
		#TODO: sanity checks of the data?
		
		game = Game.all().filter("key =", game_key).get()
		player = Player.all().filter("key =", game_key).get()
		
		if game != None && player != None:
			if not player_key in game.players:
				game.players.append(player_key)
				game.playerCount += 1
				game.put()
				
				player.currentGame = game_key
				player.put()
			else:
				pass # WTF? already in there and trying to join
				
		#TODO: give feedback to the player? or an error message

class StopGame(webapp.RequestHandler):
	"""stops the game if the """
	def get(self):	
		player_key = cgi.escape(self.request.get('p'))
		game_key = cgi.escape(self.request.get('g'))
		
		#TODO: maybe check mac address?
		#TODO: sanity checks of the data?
		
		game = Game.all().filter("key =", game_key).get()
		
		#check if game can be started and user has the right to
		if game.creator == player_key && game.status == 1:
			game.status = 2
			game.put()

class StartGame(webapp.RequestHandler):
	def get(self):		
		player_key = cgi.escape(self.request.get('p'))
		game_key = cgi.escape(self.request.get('g'))
		
		#TODO: maybe check mac address?
		#TODO: sanity checks of the data?
		
		game = Game.all().filter("key =", game_key).get()
		
		#check if game can be started and user has the right to
		if game.creator == player_key && game.status == 0:
			if game.playerCount == game.maxPlayerCount:
				game.status = 1
				game.put()
			else:
				pass #TODO: error message?

class LeaveGame(webapp.RequestHandler):
	def get(self):
		game_key = cgi.escape(self.request.get('g'))
		player_key = cgi.escape(self.request.get('p'))
		
		#TODO: maybe check mac address?
		#TODO: sanity checks of the data?
		
		game = Game.all().filter("key =", game_key).get()
		player = Player.all().filter("key =", game_key).get()
		
		#check if player is in game and game exists, if the player is the creator close the game
		if game != None && player != None:
			if game.creator == player_key:
				#TODO: close game
				
				game.creator.currentGame = None
				game.status = 2
				
				game.creator.put()
				game.put()
				
			elif player_key in game.players:
				player.currentGame = None
				player.put()
				
				game.players.remove(player_key)
				game.playerCount -= 1
				game.put()
				
				#TODO: deal with the horrible aftermath
				#maybe if only 2 left start showdown, give 2 minutes then set marker in between them
				
		#TODO: give feedback to the player? or an error message

class PlayerUpdateState(webapp.RequestHandler):
	"""updates the player state and provides him with stuff he should know"""
	def get(self):
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
		else:
			if player.currentGame != None: 
				pass #TODO: do something if someone tries to register whiile there is an ongoing game?
			
			player.name = name

		player_key = player.put()
		self.response.out.write(player_key)
		
		#todo: say if everything went fine
		
class CreateGame(webapp.RequestHandler):
	""" creates game and returns it's key, 0 otherwise """
	def get(self):
		self.response.headers['Content-Type'] = 'text/plain'
		
		#TODO: test if game can be created, abort otherwise
		if (True):					
			name = cgi.escape(self.request.get('n'))
			version = int(cgi.escape(self.request.get('v')))
			creatorLocation = cgi.escape(self.request.get('lat'))+","+cgi.escape(self.request.get('lon'))
			creator = cgi.escape(self.request.get('c'))
								
			#TODO: sanity checks of the data?
			
			game = Game()
			
			game.name = name
			game.status = 0
			game.version = version
			game.creatorLocation = creatorLocation
			game.playerCount = 1
			game.maxPlayerCount = 3
			
			game.creator = creator
			
			game_key = game.put() #TODO: use the database key seems best, as they are unique already - no conflicts
			
			self.response.out.write(game_key)
			return
		
		self.response.out.write("0") #abort
		return

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
                                     ],
                                     debug=DEBUG)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
