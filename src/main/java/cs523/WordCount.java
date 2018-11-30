package cs523;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class WordCount extends Configured implements Tool {

    public static class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            for (String token : value.toString().split("\\s+")) {

                //Problem C : This block of code below outputs the counts of only the words "Hadoop" and "Java" both case insensitive
                /*if (token.equalsIgnoreCase("Hadoop") || token.equalsIgnoreCase("Java")) {
                    word.set(token);
                    context.write(word, one);
                }*/

                word.set(token);
                context.write(word, one);
            }
        }
    }


    public static class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();
        int count = 0;


        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            count++;

            // Problem D : This block of code below outputs the counts of words whose words appear at least 25 times
            /*if (sum >= 25) {
                result.set(sum);
            }*/

            /*result.set(sum);
            context.write(key, result);*/
        }


        // Problem E
        protected void cleanup(Context context) throws IOException, InterruptedException {
            Text t1 = new Text("Total Count");
            context.write(t1, new IntWritable(count));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        int res = ToolRunner.run(conf, new WordCount(), args);

        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {

        Job job = new Job(getConf(), "WordCount");
        job.setJarByClass(WordCount.class);

        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        //job.setNumReduceTasks(2);

        File file = new File("./output");
        deleteIfDirectoryExists(file);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    // Problem A
    private static boolean deleteIfDirectoryExists(File file) {

        if (file.exists() && file.isDirectory()) {

            deleteDirectory(file);

            return true;
        }

        return false;
    }

    // Problem A
    private static boolean deleteDirectory(File dir) {

        if (dir.isDirectory()) {
            File[] children = dir.listFiles();

            for (int i = 0; i < children.length; i++) {

                boolean success = deleteDirectory(children[i]);

                if (!success) {
                    return false;
                }
            }
        } // either file or an empty directory
        System.out.println("removing file or directory : " + dir.getName());
        return dir.delete();

    }

}

