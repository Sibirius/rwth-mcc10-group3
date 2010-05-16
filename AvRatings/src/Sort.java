import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class Sort {
	/** Die Top 10 Filme nach den durchschnittlichen Ratings finden. */
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		
		//read config-file
		Configuration confsort = new Configuration();
		
		//create a job
		Job jobsort = new Job(confsort, "Sorted Average Ratings");
		
		//set classes
		jobsort.setJarByClass(AverageRatings.class);
		jobsort.setMapperClass(SortMapper.class);
		jobsort.setReducerClass(SortReducer.class);
		jobsort.setOutputKeyClass(DoubleWritable.class);
		jobsort.setOutputValueClass(Text.class);
		
		//set paths
		FileInputFormat.addInputPaths(jobsort, args[1]);
		Path SortedOutput = new Path("SortedOutput");
		FileOutputFormat.setOutputPath(jobsort, SortedOutput);
		
		//wait until job is complete
		jobsort.waitForCompletion(true);
		
		//write top10
		Top10(SortedOutput, confsort);
	}
	
	
	public static void Top10(Path path, Configuration conf) throws IOException {
		//copy output to the local file system
		FileSystem fs = path.getFileSystem(conf);
		fs.copyToLocalFile(path, path);

		
		String line = "";
		String completeString = "";
		
		try {
			//set reader
			BufferedReader reader= new BufferedReader(new FileReader(path.toString()
					+ "/part-r-00000"));
			//set writer
			BufferedWriter writer = new BufferedWriter(new FileWriter(path.toString()
					+ "/Top10.txt"));
			//read lines and make a big string (\n to separate lines)					
			while (line != null) {
				completeString += line + "\n";
				line = reader.readLine();
			}
			//put the big string in an array - one cell per line
			String[] lines = completeString.split("\n");
			
			//write the last 10 lines to a file
			for (int i = 0; i < 10; i++) {
				writer.write(lines[lines.length - 1 - i]+"\n");
			}
			writer.close();
			
		} catch (IOException e) {
			System.err.println("Error2 :" + e);
		}
	}
}

