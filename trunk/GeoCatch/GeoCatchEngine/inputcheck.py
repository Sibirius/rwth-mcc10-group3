# methods check input and throw errors if something goes wrong
# http://docs.python.org/library/exceptions.html

import re

def checkKey(key):
	""" check if key is a google store type key """
	#TODO: wrong
	if len(key) == 30 and key.isalnum():
		return key
	else:
		raise ValueError()

def checkMac(mac):
	""" check mac for form xx:xx:xx:xx where x is alphanumeric(?) """
	if re.match("^[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}$", mac) != None:
		return mac
	else:
		raise ValueError()

def checkName(name):
	""" check if name is valid (only alphanumeric without spaces(?) """
	#check if string long enough
	if name.isalnum() and len(name) > 2:
		return name
	else:
		raise ValueError()

def checkInt(n):
	""" check if string carries a valid integer (captain state the obvious: "it should be positive") """
	if n.isdigit():
		return n
	else:
		raise TypeError() #todo: more precise?

def checkLocation(loc):
	""" check if "lat,lon"  lat|lon = xx.xxxx """
	#TODO: what length can the lat lon coords be? xx.xxxxx?
	if re.match("^\d{1,2}.\d{1,10},\d{1,2}.\d{1,10}$", mac) != None:
		return loc
	else:
		raise ValueError()
		
if __name__ == "__main__":
	print checkMac("o4:5r:3r")
