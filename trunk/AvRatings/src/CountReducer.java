import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class CountReducer extends Reducer<Text, IntWritable, IntWritable, Text > {
	   	 
	public void reduce(Text key, IntWritable values, Context context) throws IOException {
			int count = 0;
	        
			for (IntWritable value : values) {
				count += value.get();
	        }
			
			context.write(new IntWritable(count), key);
	}
}
