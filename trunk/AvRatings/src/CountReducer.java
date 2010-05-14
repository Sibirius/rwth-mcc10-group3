import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class CountReducer extends Reducer<IntWritable, Text , IntWritable, Text > {
	   	 
	public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException {
				
			for (Text value : values) {
				try {
					context.write(key, value);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			

		
	   
	}
}
