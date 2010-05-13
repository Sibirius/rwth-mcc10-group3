import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class AverageRatingsReducer extends Reducer<Text, DoubleWritable, DoubleWritable, Text> {
	   	 
	public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException {
		   int sum = 0;
			int count = 0;
			double average;
			for (IntWritable val : values) {{
				sum += val.get();
				count++;
			}
			average = sum/count;
			try {
				context.write(new DoubleWritable(average), key);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   }
	 }
}