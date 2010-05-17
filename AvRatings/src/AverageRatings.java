import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.apache.hadoop.mapreduce.lib.map.InverseMapper;
import org.apache.hadoop.mapreduce.Reducer;




public class AverageRatings {
	
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
		Job jobSort = new Job(confSort, "Sort Average Ratings");
		jobSort.setJarByClass(AverageRatings.class);
		jobSort.setMapperClass(InverseMapper.class);
		jobSort.setReducerClass(Reducer.class);
		jobSort.setOutputKeyClass(DoubleWritable.class);
		jobSort.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(jobSort, new Path("tmp/tmp1"));
		FileOutputFormat.setOutputPath(jobSort, new Path(args[1]+"/average"));
		jobSort.waitForCompletion(true);
								
		Sort.main(args);
		Count.main(args);
	}

}
