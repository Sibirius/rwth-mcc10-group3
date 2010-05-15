Movies = LOAD 'movies2.dat' USING PigStorage(':') AS (id:int, title:chararray, genre:chararray);
Ratings = LOAD 'ratings2.dat' USING PigStorage(':') AS (id:int, mid:int, rating:float, time:int);
Grouped = GROUP Ratings By mid;
Counted = FOREACH Grouped GENERATE COUNT(Ratings);
Limited = LIMIT Counted 10;
DUMP Limited;
