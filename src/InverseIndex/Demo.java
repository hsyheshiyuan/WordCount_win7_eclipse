package InverseIndex;

/**
 *xxx:1      
	     ==>	xxx:<1,1> 
 *xxx:1
 *
 *
 *
 *xxx:1
 *xxx:1	==> xxx:<1,1,1>   ===>    xxx:<<1,1>,<1,1,1>,<1,1>>
 *xxx:1
 *
 *
 *
 *xxx:1
 *		==>	xxx:<1,1>
 *xxx:1
 *
 */
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
 














import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

//1.统计单词个数(某某单词：个数)√
//2.统计单词个数(某某单词：....几个就几个点儿)√
//3.数据去重√
//4.成绩排名(字典排序√，大数字小排序)
//5.求平均值√
//6.单表关联(给出child-parent（孩子——父母）表，要求输出grandchild-grandparent（孙子——爷奶）表。)√
//7.多表关联√
//8.倒排索引
public class Demo {
	
 
		//前两个类型是map的参数类型，后两个类型是context.write的参数类型
	  public static class MyMapper  extends Mapper<Object, Text, Text, Text>{
		  		//参数value表示一行?这样虽然是排序但是去重了
			    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			    			StringTokenizer tokenizer = new StringTokenizer(value.toString());
			    			String aaa = tokenizer.nextToken();
			    			
			    			FileSplit split = (FileSplit) context.getInputSplit();
			    			String path = split.getPath().toString();
			    			String fileName = path.substring(1+path.lastIndexOf('/'));
			    			context.write(new Text(aaa), new Text(fileName));
			    }
	  }
	  
	  
	  public static class MyCombiner  extends Reducer<Text,Text,Text,Text> {
	  		@Override
	  		protected void reduce(Text key, Iterable<Text> values,Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
	  			            int count=0;
	  			            String fileName="";
	  			            
	  						for (Text text : values) {
	  							fileName=text.toString();
								count++;
							}
	  						context.write(key, new Text("<"+fileName+","+count+">"));
		  	}
	  }
	  //map ---> combiner --->reduce(注意：Output types of a combiner must == output types of a mapper. )
	  	
	  //前两个类型是reduce的前两个参数类型,后两个类型是context.write的参数类型
	  public static class MyReducer  extends Reducer<Text,Text,Text,Text> {
		  		public static Text emptyText=new Text();
		  		@Override
		  		protected void reduce(Text key, Iterable<Text> values,Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
		  					String result="<";
		  					for (Text text : values) {
		  						result+=text.toString();
		  						result+=",";
							}
		  					result=result.substring(0, result.length()-1);
		  					result+=">";
		  					
		  					context.write(key, new Text(result));
		  					
		  		}
	  }
	 /**
	  * 
	  * select * from a,b wherea. 
	  * @param xx
	  */
	  public static void main(String[] xx)  {
		  		boolean isTest=false;
		  		if(isTest){
		  			//测试代码
		  			StringTokenizer tokenizer = new StringTokenizer("张三 46");
		  			String name=tokenizer.nextToken();
		    		double score=Double.parseDouble(tokenizer.nextToken());
		  			System.out.println("name="+name+",score="+score);
		  			return;
		  		}
		  
		  
			  try{
				    Job job = new Job(new Configuration(), "win7中eclipse中word count_4(测试检测海量文章中每个单词的数量耗时！)");
				    
				    job.setJarByClass(Demo.class);
				    
				    job.setMapperClass(MyMapper.class);
				    job.setCombinerClass(MyCombiner.class);
				    job.setReducerClass(MyReducer.class);
				    
				    job.setMapOutputKeyClass(Text.class);
				    job.setMapOutputValueClass(Text.class);
				    
				    job.setOutputKeyClass(Text.class);
				    job.setOutputValueClass(Text.class);
				    
				    /*注意combiner的输入和输出要和map一致，所以job.setMapOutputValueClass就相当于设置了map和combiner，
				  	 * 而job.setOutputValueClass是设置reduce的输出(如果没有上面的话相当于设置了map,combiner,reduce他们三个)*/
				    FileInputFormat.addInputPath(job, new Path("input"));
				    FileOutputFormat.setOutputPath(job, new Path("output"));
				    
				    System.exit(job.waitForCompletion(true) ? 0 : 1);
			  }catch(Exception e){
				    e.printStackTrace();
			  }
	  }
}