import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import lt.red5.Application;
import lt.red5.database.ConnectPoolC3P0;


public class LT {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*ConnectPoolC3P0 conn = ConnectPoolC3P0.getInstance();
		 
        List<Map<String,String>> result =conn.queryForMap("select * from kung_vedio", null);
        System.out.println("￥￥￥￥￥￥￥￥￥："+result.size());*/
	 
			  //h264
	        Properties properties = new Properties();
			String base = Application.class.getResource("/")
					.getPath();
			 
				try {
					
					// -i rtmp://192.168.1.11:1935/live/teststream -acodec  -vcodec -f flv rtmp://10.2.11.111/live/newstream
					List<String> commend = new java.util.ArrayList<String>();  
					properties.load(new FileInputStream(base
							+ "path/filepath.properties"));
					String ffmpeg_home = base+"tools/ffmpeg/ffmpeg.exe";//ffmpeg.exe所放的路径  
					//String nginx_url = properties.getProperty("nginx_url").trim(); 
					String rtmp_url = properties.getProperty("rtmp_url").trim(); 
					String inputFile_home = properties.getProperty("inputFile_home").trim(); 
					 
					//ffmpeg -i rtmp://server/live/originalStream -c:a copy -c:v libx264 -vpre slow -f flv rtmp://server/live/h264Stream  
			 
	                
					commend.add(ffmpeg_home);  
					  
				    /*commend.add("-i");    
				    commend.add(inputFile_home+"wukong.flv");    
				    commend.add("-vcodec");   
	                commend.add("copy");    
	                commend.add("-f");    
	                commend.add("flv");   
				    commend.add(rtmp_url+"stream_555"); */
				        
 				   /*commend.add("-i");    
				   // commend.add(inputFile_home+"stream_20001"); 
 				   commend.add(rtmp_url+"stream_20001 live=1");   
				    
				    commend.add("-strict");   
	                commend.add("-2");   
	                commend.add("-vcodec");   
	                commend.add("copy"); 
	                commend.add("-f");
	                commend.add("hls");
	                commend.add(inputFile_home+"test/"+"stream_2000"+".m3u8");*/
					
					String name="stream_2000";
					commend.add("-re");    
	                commend.add("-i");    
	                commend.add(rtmp_url+name+" live=1");   
	                commend.add("-vcodec");    
	                commend.add("copy");
	                commend.add("-map");
	                commend.add("0");
	                commend.add("-f");
	                commend.add("hls");
	                commend.add("-hls_list_size");
	                commend.add("6");
	                commend.add("-hls_wrap");
	                commend.add("10");
	                commend.add("-hls_time");
	                commend.add("10");
	                commend.add(inputFile_home+name+"/"+name+".m3u8");
					
					 
					
					
					/*commend.add("-v");    
	                commend.add("verbose");    
	                 commend.add("-i");    
	                commend.add(rtmp_url+name+" live=1");   
	                commend.add("-strict");   
	                commend.add("-2"); 
	                	commend.add("-vcodec");    
		                commend.add("copy");
	                commend.add("-ac");   
	                commend.add("1");   
	                commend.add("-maxrate");
	                commend.add("800k");
	                commend.add("-bufsize");
	                commend.add("1835k");
	                commend.add("-pix_fmt");
	                commend.add("yuv420p");
	                commend.add("-flags");
	                commend.add("-global_header");
	                commend.add("-hls_time");
	                commend.add("10"); 
	                commend.add("-start_number");
	                commend.add("1");
	                commend.add("-f");
	                commend.add("segment");
	                commend.add("-segment_list");
	                commend.add(inputFile_home+name+"/"+name+".m3u8");
	                commend.add("-segment_list_flags");
	                commend.add("+live");
	                commend.add("-segment_time");
	                commend.add("10");
	                commend.add(inputFile_home+name+"/"+name+"%03d.ts"); */
 				   
	                
	                
				  
					StringBuffer test=new StringBuffer();    
					for(int i=0;i<commend.size();i++)    
					    test.append(commend.get(i)+" ");    
					//System.out.println(test);   
					System.out.println("转换指令："+test);
					ProcessBuilder builder = new ProcessBuilder();    
					builder.command(commend);    
					//builder.start();    
					Process p=builder.start();
					
				   //Process process = Runtime.getRuntime().exec(test.toString());//执行命令
					
		          /* InputStreamReader ir = new InputStreamReader(p.getInputStream());
		           LineNumberReader input = new LineNumberReader(ir);
		           String line;
		           while ((line = input.readLine()) != null) {//输出结果
		        	   System.out.println(line);
		           }
		       	   p.waitFor(); */
					
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    
	}
	public static void getThumb() throws IOException, 
	    InterruptedException { 
	
	//ffmpeg -re -i /Users/samson/Desktop/apple-iphone4-design_video-us-20100607_848x480.mov -vcodec copy -f flv rtmp://localhost:1935/live1/room2 
	ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", 
	        "-re","-i", "/Users/samson/Desktop/apple-iphone4-design_video-us-20100607_848x480.mov", 
	        "-vcodec", "copy" , 
	        "-f", "flv","rtmp://localhost:1935/live1/room2"); 
	
	Process process = processBuilder.start(); 
	
	InputStream stderr = process.getErrorStream(); 
	InputStreamReader isr = new InputStreamReader(stderr); 
	BufferedReader br = new BufferedReader(isr); 
	String line; 
	while ((line = br.readLine()) != null) ; 
	process.waitFor(); 
	
	if (br != null) 
	    br.close(); 
	if (isr != null) 
	    isr.close(); 
	if (stderr != null) 
	    stderr.close(); 
	} 

}





