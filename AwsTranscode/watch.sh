#!/bin/bash

export PATH=$PATH:/var/lib/gems/1.8/bin
export AWS_ACCESS_KEY_ID=AKIAJ2UWGFAIASZOMPSA
export AWS_SECRET_ACCESS_KEY=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

qresp='mcc10group3response'
qreq='mcc10group3request'

while true; do

message=$(clisqs pop $qresp 2> /dev/null)

if [ "$message" != "" ]; then

echo $message >> server.log

else 

instancecount_all=$(ec2-describe-instances --region eu-west-1 2> /dev/null | grep running | wc -l)
instancecount=$(echo "$instancecount_all-1" | bc)
messagecount=$(clisqs size $qreq 2> /dev/null)

# Instances maxFiles
# 1	    2
# 2         5
# 3         10
# n         n*n+1

testvalue=$(echo "$instancecount*$instancecount+1-$messagecount" | bc )

if [[ 0 -gt $testvalue ]]; then

echo starting
#ec2-run-instances "ami-b93c16cd" -g "transcodingServerGroup" -k "transcodingServerKeys" --region eu-west-1 --instance-type m1.large

fi

echo "$(date) $(uname -n): $messagecount waiting, $instancecount working" >> server.log

sleep 120

fi

done
