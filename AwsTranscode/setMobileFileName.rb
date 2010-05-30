#!/usr/bin/ruby1.8

require 'rubygems'
require 'aws_sdb'

sdb = AwsSdb::Service.new
sdb.put_attributes('mcc10group3media', ARGV[0], { 'mobileFileName' => ARGV[1] })
