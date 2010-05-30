#!/usr/bin/ruby1.8

require 'rubygems'
require 'aws_sdb'

sdb = AwsSdb::Service.new
print sdb.query('mcc10group3media', "['FileName' = '" + ARGV[0] + "']")
print "\n"

