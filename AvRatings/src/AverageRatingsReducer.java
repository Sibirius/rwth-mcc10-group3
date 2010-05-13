import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class AverageRatingsReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	   	 
	public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException {
		   int sum = 0;
			int count = 0;
			int average;
			for (IntWritable val : values) {{
				sum += val.get();
				count++;
			}
			average = sum/count;
			
			try {
				context.write(key, new IntWritable(average));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   }
	 }
}
