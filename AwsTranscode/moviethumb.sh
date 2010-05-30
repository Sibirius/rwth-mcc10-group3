#!/bin/bash
#
numofthumbs=10	#define num of frames for animated gif

tmpdir=$(mktemp -d)
duration=$(ffmpeg -i $1 2>&1 | grep "Duration" | cut -d ' ' -f4 | cut -d '.' -f1)
secs=$(echo $duration | cut -d ':' -f3)
mins=$(echo $duration | cut -d ':' -f2)
hours=$(echo $duration | cut -d ':' -f1)
interval=$(echo "($secs + $mins*60 + $hours*3600)/($numofthumbs+1)" | bc)
for i in $(seq 1 $numofthumbs); do 
pos=$(echo $i*$interval | bc);
ffmpeg -ss $pos -i $1 -vframes 1 -s 200x150 "$tmpdir/frame_$i.jpg";
done
convert -delay 100 -loop 0 "$tmpdir/frame_*.jpg" "${1%.*}_thumba.gif"
convert $tmpdir/frame_1.jpg "${1%.*}_thumb.gif"
rm $tmpdir/frame*.jpg
rmdir $tmpdir
