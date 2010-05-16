Movies = LOAD '/user/DrWho/input/movies.dat' USING PigStorage('::') AS (id:int, title:chararray, genre:chararray);
Ratings = LOAD '/user/DrWho/input/ratings.dat' USING PigStorage('::') AS (id:int, mid:int, rating:float, time:int);
Joined = JOIN Movies BY id, Ratings BY mid;
Grouped = GROUP Joined BY Movies::id;
Averaged = FOREACH Grouped GENERATE AVG(Joined.rating), Joined.title;
Ordered = ORDER Averaged BY $0 DESC;
DUMP Ordered;
