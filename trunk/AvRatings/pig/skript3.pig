Movies = LOAD '/user/DrWho/input/movies.dat' USING PigStorage('::') AS (id:int, title:chararray, genre:chararray);
Ratings = LOAD '/user/DrWho/input/ratings.dat' USING PigStorage('::') AS (id:int, mid:int, rating:float, time:int);
Grouped = GROUP Ratings By mid;
Counted = FOREACH Grouped GENERATE COUNT(Ratings);
Limited = LIMIT Counted 10;
DUMP Limited;
