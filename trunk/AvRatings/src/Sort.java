import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class Sort {
	
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		Configuration confsort = new Configuration();
		Job jobsort = new Job(confsort, "Sorted Average Ratings");
		jobsort.setJarByClass(AverageRatings.class);
		jobsort.setMapperClass(SortMapper.class);
		jobsort.setReducerClass(SortReducer.class);
		jobsort.setOutputKeyClass(Text.class);
		jobsort.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPaths(jobsort, args[1]);
		FileOutputFormat.setOutputPath(jobsort, new Path("SortedOutput"));
		jobsort.waitForCompletion(true);
	}
}
