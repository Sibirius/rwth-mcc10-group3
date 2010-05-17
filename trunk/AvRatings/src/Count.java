
import java.io.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.apache.hadoop.mapreduce.lib.map.InverseMapper;
import org.apache.hadoop.mapreduce.Reducer;

public class Count {
	public static class CountInverseMapper extends Mapper<Object, Text, IntWritable, Text> {
		private Text id = new Text();
		private IntWritable count = new IntWritable();		
		
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			String[] strings = line.split("\\s+");
			
			id.set(strings[0]);
			count.set(Integer.parseInt(strings[1]));
			
			context.write(count, id);
		}
	}
	
	/** Die Top 10 Filme nach der Anzahl der Ratings finden. */
	public static void main(String[] args) throws IOException,
			InterruptedException, ClassNotFoundException {
		
		//read config-file
		Configuration confcount = new Configuration();
		
		//create a job
		Job jobcount = new Job(confcount, "Counted Ratings");
		
		//set classes
		jobcount.setJarByClass(AverageRatings.class);
		jobcount.setMapperClass(CountMapper.class);
		jobcount.setReducerClass(CountReducer.class);
		jobcount.setOutputKeyClass(Text.class);
		jobcount.setOutputValueClass(IntWritable.class);
		
		//set paths
		FileInputFormat.addInputPaths(jobcount, args[0]);
		FileOutputFormat.setOutputPath(jobcount, new Path("tmp/tmp2"));		
		//jobcount.waitForCompletion(true);

		Configuration confSort = new Configuration();
		Job jobSort = new Job(confSort, "Sort Counted Ratings");
		jobSort.setJarByClass(AverageRatings.class);
		jobSort.setMapperClass(CountInverseMapper.class);
		jobSort.setReducerClass(Reducer.class);
		jobSort.setOutputKeyClass(IntWritable.class);
		jobSort.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(jobSort, new Path("tmp/tmp2"));
		FileOutputFormat.setOutputPath(jobSort, new Path(args[1]+"/count"));
		jobSort.waitForCompletion(true);
		
		//write top10
		Top10(new Path(args[1]+"/count"), confcount);
	}

	public static void Top10(Path path, Configuration conf) throws IOException {
		//copy output to the local file system
		FileSystem fs = path.getFileSystem(conf);
		fs.copyToLocalFile(path, path);

		
		String line = "";
		String completeString = "";
		
		try {
			//set reader
			BufferedReader reader= new BufferedReader(new FileReader(path.toString()
					+ "/part-r-00000"));
			//set writer
			BufferedWriter writer = new BufferedWriter(new FileWriter(path.toString()
					+ "/Top10.txt"));
			//read lines and make a big string (\n to separate lines)					
			while (line != null) {
				completeString += line + "\n";
				line = reader.readLine();
			}
			//put the big string in an array - one cell per line
			String[] lines = completeString.split("\n");
			
			//write the last 10 lines to a file
			for (int i = 0; i < 10; i++) {
				writer.write(lines[lines.length - 1 - i]+"\n");
			}
			writer.close();
			
		} catch (IOException e) {
			System.err.println("Error2 :" + e);
		}
	}
}
