import java.io.IOException;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;



public class AverageRatingsMapper extends Mapper<Object, Text, Text, DoubleWritable> {
		
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString();
		String movieId = getMovieId(line);
		double rating = getrating(line);
		
		context.write(new Text(movieId), new DoubleWritable(rating));
	}

	private String getMovieId(String line) {
		String token = "::";
		String[] strings = line.split(token);
		return strings[1];
	}

	private double getrating(String line) {
		String token = "::";
		String[] strings = line.split(token);
		return Double.parseDouble(strings[2]);
	}
}
