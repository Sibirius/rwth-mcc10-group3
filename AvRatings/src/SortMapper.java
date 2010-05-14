import java.io.IOException;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;



public class SortMapper extends Mapper<Object, Text, Text, IntWritable> {
	private Text rating = new Text();
	
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString();
		rating.set(getrating(line));
		context.write(rating, new IntWritable(getMovieId(line)));
	}

	private int getMovieId(String line) {
		
		String[] strings = line.split("\\s+");
		return Integer.parseInt(strings[1]);
	}

	private String getrating(String line) {
		String[] strings = line.split("\\s+");
		return strings[0];
	}
}
