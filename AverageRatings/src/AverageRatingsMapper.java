import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

@SuppressWarnings("deprecation")
public class AverageRatingsMapper extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, IntWritable> {
	public void map(LongWritable key, Text value,
			OutputCollector<Text, IntWritable> output, Reporter reporter)
			throws IOException {
		String line = value.toString();
		String movieId = getMovieId(line);
		int rating = getrating(line);
		try {
			output.collect(new Text(movieId), new IntWritable(rating));
		} catch (Exception ex) {
			System.out.println("Exception: " + line);
		}
	}

	private String getMovieId(String line) {
		String token = "::";
		String[] strings = line.split(token);
		return strings[1];
	}

	private int getrating(String line) {
		String token = "::";
		String[] strings = line.split(token);
		return Integer.parseInt(strings[2]);
	}
}
