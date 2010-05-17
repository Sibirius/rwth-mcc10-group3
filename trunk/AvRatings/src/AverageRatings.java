import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;



public class AverageRatings {
	/** Die Filme nach den durchschnittlichen Ratings sortieren. */
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "Average Ratings");
		job.setJarByClass(AverageRatings.class);
		job.setMapperClass(AverageRatingsMapper.class);
		job.setReducerClass(AverageRatingsReducer.class);
		job.setOutputKeyClass(DoubleWritable.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.waitForCompletion(true);
								
		Sort.main(args);
		
		Count.main(args);
	}

}
