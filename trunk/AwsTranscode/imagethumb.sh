#!/bin/bash

convert -define jpeg:size=400x300  $1 -thumbnail 200x150  -unsharp 0x.5 ${1%.*}_thumb.gif



