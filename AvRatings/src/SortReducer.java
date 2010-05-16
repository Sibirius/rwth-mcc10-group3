import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class SortReducer extends Reducer<Text, DoubleWritable, DoubleWritable, Text> {
	   	 
	public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException {
		   	int count = 0;
		   	double rating_sum = 0;
			
			for (DoubleWritable value : values) {
				count++;
				rating_sum += value.get();
			}
			
			double average = rating_sum / count;
			
			try {
				context.write(new DoubleWritable(average), key);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
	   
	}
}
