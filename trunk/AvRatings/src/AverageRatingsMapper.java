import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;



public class AverageRatingsMapper extends Mapper<Object, Text, Text, IntWritable> {
		
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString();
		String movieId = getMovieId(line);
		int rating = getrating(line);
		
		context.write(new Text(movieId), new IntWritable(rating));
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
