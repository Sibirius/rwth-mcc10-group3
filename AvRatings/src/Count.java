import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Count {

	public static void main(String[] args) throws IOException,
			InterruptedException, ClassNotFoundException {
		Configuration confcount = new Configuration();
		Job jobcount = new Job(confcount, "Counted Ratings");
		jobcount.setJarByClass(AverageRatings.class);
		jobcount.setMapperClass(CountMapper.class);
		jobcount.setReducerClass(CountReducer.class);
		jobcount.setOutputKeyClass(IntWritable.class);
		jobcount.setOutputValueClass(Text.class);
		FileInputFormat.addInputPaths(jobcount, args[1]);
		FileOutputFormat.setOutputPath(jobcount, new Path("CountOutput"));
		jobcount.waitForCompletion(true);
	}

}
