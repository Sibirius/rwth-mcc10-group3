import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.apache.hadoop.mapred.lib.InverseMapper;
import org.apache.hadoop.mapred.lib.IdentityReducer;



public class AverageRatings {
	public static class ARIdentityReducer extends IdentityReducer<Text, DoubleWritable> {}
	public static class ARInverseMapper extends InverseMapper<DoubleWritable, Text> {}
	
	/** Die Filme nach den durchschnittlichen Ratings sortieren. */
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "Average Ratings");
		job.setJarByClass(AverageRatings.class);
		job.setMapperClass(AverageRatingsMapper.class);
		job.setReducerClass(AverageRatingsReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path("tmp/tmp1"));
		job.waitForCompletion(true);

		Configuration confSort = new Configuration();
		Job jobSort = new Job(conf2, "Average Ratings");
		jobSort.setJarByClass(AverageRatings.class);
		jobSort.setMapperClass(ARInverseMapper.class);
		jobSort.setReducerClass(ARIdentityReducer.class);
		jobSort.setOutputKeyClass(DoubleWritable.class);
		jobSort.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(jobSort, new Path("tmp/tmp1"));
		FileOutputFormat.setOutputPath(jobSort, new Path(args[1]));
		jobSort.waitForCompletion(true);
								
		//Sort.main(args);
		//Count.main(args);
	}

}
