# -*- coding: utf-8 -*-

# methods check input and throw errors if something goes wrong
# http://docs.python.org/library/exceptions.html

import re

def checkKey(key):
	""" check if key is a google store type key """
	#TODO: wrong
	if re.match("^[a-zA-Z0-9-_]{8,100}$", key) != None:
		return key
	else:
		raise ValueError()

def checkMac(mac):
	""" check mac for form xx:xx:xx:xx where x is alphanumeric(?) """
	if re.match("^[a-fA-F0-9]{2}(:[a-fA-F0-9]{2}){5}$", mac) != None:
		return mac
	else:
		raise ValueError()

def checkName(name):
	""" check if name is valid (only alphanumeric without spaces(?) """
	#check if string long enough
	if re.match("^[a-zA-Z0-9-_ .]{3,20}$", name) != None:
		return name
	else:
		raise ValueError()

def checkInt(n):
	""" check if string carries a valid integer (captain state the obvious: "it should be positive") """
	if n.isdigit():
		return int(n)
	else:
		raise TypeError() #todo: more precise?

def checkLocation(loc):
	""" check if "lat,lon"  lat|lon = xx.xxxx """
	#TODO: what length can the lat lon coords be? xx.xxxxx?
	if re.match("^\d{1,2}.\d{1,20},\d{1,2}.\d{1,20}$", loc) != None:
		return loc
	else:
		raise ValueError()
		
if __name__ == "__main__":
	print checkMac("a4:5f:3b:5B:3b:34")
	print checkLocation("13.23,34.324")
