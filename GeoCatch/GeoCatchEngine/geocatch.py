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
class Player(db.Model):
    status = db.UserProperty()
    content = db.StringProperty(multiline=True)
    date = db.DateTimeProperty(auto_now_add=True)

class Game(db.Model):
    name = db.StringProperty(multiline=False)
    # 0 = not yet started
    # 1 = running
    # 2 = finished
    status = db.IntegerProperty()
    
    version = db.IntegerProperty() # used when there are multiple versions
    
    creator = db.ReferenceProperty(Player)
    creatorLocation = db.GeoPtProperty()
    date = db.DateTimeProperty(auto_now_add=True)

##########################################################
#request handlers
##########################################################
class MainPage(webapp.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.out.write('Hello, webapp World!')

class GetGameList(webapp.RequestHandler):
    def get(self):
		games = Game.all().filter("status = ",0).order('-date').
		
		template_values = {'games': games,}
		
		template_path = os.path.join(TEMPLATE_FOLDER, 'games.xml')
		self.response.out.write(template.render(template_path, template_values))
		
class JoinGame(webapp.RequestHandler):
    def get(self):
		game_key = cgi.escape(self.request.get('g'))
		
		#check if there is place
		
		#add to "people who want to play" list
		
				
class CreateGame(webapp.RequestHandler):
	""" creates game and returns it's key, 0 otherwise """
    def get(self):
		#TODO: test if game can be created, abort otherwise
		if (True):					
			name = cgi.escape(self.request.get('n'))
			version = cgi.escape(self.request.get('v'))
			creatorLocation = (cgi.escape(self.request.get('lat')),
								cgi.escape(self.request.get('lon')))
			creator = cgi.escape(self.request.get('c'))
								
			#TODO: sanity checks of the data?
			
			game = Game()
			
			game.name = name
			game.status = 0
			game.version = version
			game.creatorLocation = creatorLocation
			
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
                                      ('/create', CreateGame),
                                      ('/join', JoinGame),
                                      
                                     ],
                                     debug=DEBUG)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
