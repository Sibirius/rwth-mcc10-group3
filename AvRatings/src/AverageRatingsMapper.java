import java.io.IOException;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;



public class AverageRatingsMapper extends Mapper<Object, Text, Text, DoubleWritable> {
	private Text id = new Text();
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString();
		id.set(getMovieId(line));
		double rating = getrating(line);
		
		context.write(id, new DoubleWritable(rating));
	}

	private String getMovieId(String line) {
		String[] strings = line.split("::");
		return strings[1];
	}

	private double getrating(String line) {
		String[] strings = line.split("::");
		return Double.parseDouble(strings[2]);
	}
}
