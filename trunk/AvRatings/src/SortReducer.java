import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class SortReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	   	 
	public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException {
		   	int id= 0;
			
			
			for (IntWritable value : values) {
				id = value.get();
			}
			try {
				context.write(key, new IntWritable(id));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
	   
	}
}
