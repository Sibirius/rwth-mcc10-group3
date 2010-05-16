import java.io.IOException;
import java.util.StringTokenizer;

	import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;



	public class CountMapper extends Mapper<Object, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text id = new Text();
		
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			id.set(getMovieId(line));
			context.write(id, one);
		}

		private String getMovieId(String line) {
			String[] strings = line.split("::");
			return strings[1];
		}
	}


