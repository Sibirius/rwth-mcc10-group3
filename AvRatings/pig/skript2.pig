Movies = LOAD '/user/DrWho/input/input/movies2.dat' USING PigStorage(':') AS (id:int, title:chararray, genre:chararray);
Ratings = LOAD '/user/DrWho/input/input/ratings2.dat' USING PigStorage(':') AS (id:int, mid:int, rating:float, time:int);
Grouped = COGROUP Movies BY id, Ratings BY mid;
Averaged = FOREACH Grouped GENERATE FLATTEN(Movies.title), AVG(Ratings.rating);
Ordered = ORDER Averaged BY $1 DESC;
Limited = LIMIT Ordered 10;
DUMP Limited;
