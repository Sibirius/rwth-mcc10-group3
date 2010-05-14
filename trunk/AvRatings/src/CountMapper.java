import java.io.IOException;
	import org.apache.hadoop.io.*;
	import org.apache.hadoop.mapreduce.Mapper;



	public class CountMapper extends Mapper<Object, Text, IntWritable, Text> {
		private Text id = new Text();
		
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			id.set(getMovieId(line));
			context.write(new IntWritable(getCount(line)), id);
		}

		private String getMovieId(String line) {
			String[] strings = line.split("\\s+");
			return strings[1];
		}

		private int getCount(String line) {
			String[] strings = line.split("\\s+");
			return Integer.parseInt(strings[2]);
		}
	}


