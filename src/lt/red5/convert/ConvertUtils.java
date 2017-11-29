package lt.red5.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties; 

import lt.red5.Application;
import lt.red5.database.ConnectPoolC3P0;


public class ConvertUtils {
 
	
	
	
	
	public void Contvert(String fileName,String id){
		
		Properties properties = new Properties();
		String base = ConvertUtils.class.getResource("/")
				.getPath();
		try {
			properties.load(new FileInputStream(base
					+ "path/filepath.properties"));
			//数据库中存储的路径名
			//String fileName = kung_edu_catalog.getUrl().trim(); 
		
			//完整的本地路径
    		String inputFile_home = properties.getProperty("inputFile_home").trim()+
    				fileName+".flv";  
    		/*String outputFile_home = properties.getProperty("outputFile_home").trim()+
    				fileName.substring(0,fileName.lastIndexOf("."))+".mp4";  
    		String tempFile_home = properties.getProperty("tempFile_home").trim()+
    				fileName.substring(0,fileName.lastIndexOf("."))+".avi"; */ 
    		String outputFile_home =inputFile_home.substring(0,inputFile_home.lastIndexOf("."))+"_output.mp4";  
    		String tempFile_home =inputFile_home.substring(0,inputFile_home.lastIndexOf("."))+"_temp.avi"; 
    		String os=properties.getProperty("OS").trim() ;
    		String ffmpeg_home = base+"tools/ffmpeg/ffmpeg.exe";//ffmpeg.exe所放的路径  
    		String mencoder_home = base+"tools/mencoder/mencoder.exe";//mencoder.exe所放的路径  
    		//Linux 环境下更改目录
    		if(os.toLowerCase().equals("Linux".toLowerCase())){
    			ffmpeg_home=properties.getProperty("ffmpeg_home").trim() ;
    			mencoder_home=properties.getProperty("mencoder_home").trim() ;
    		}
    		
    		File file = new File(inputFile_home);    
            if (!file.isFile()) {   
            	System.out.println("未读取到源视频文件");
            	System.out.println("文件路径："+inputFile_home);
            	
            	
            } 
            else{
            	
            	new ConvertThread(id,inputFile_home,outputFile_home,tempFile_home,ffmpeg_home,mencoder_home).start();
            	System.out.println("已提交到转码服务");
            	
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("未读取到路径配置文件");
			e.printStackTrace();
		} 
		
	}
	class ConvertThread extends Thread{
		private String cuttentId;
		private String inputFile_home;
		private String outputFile_home;
		private String tempFile_home;
		private String ffmpeg_home;
		private String mencoder_home;
	    public ConvertThread(String cuttentId,String inputFile_home,String outputFile_home,
	    		String tempFile_home,String ffmpeg_home,String mencoder_home){ 
	       this.cuttentId=cuttentId;
	       this.inputFile_home=inputFile_home;
	       this.outputFile_home=outputFile_home;
	       this.tempFile_home=tempFile_home;
	       this.ffmpeg_home=ffmpeg_home;
	       this.mencoder_home=mencoder_home;
	    }
        @Override
        public void run() {
        	process(cuttentId,inputFile_home,outputFile_home,tempFile_home,ffmpeg_home,mencoder_home);
        }
	}
	
	 /**  
     * 转换过程 ：先检查文件类型，在决定调用 processFlv还是processAVI  
     * @param inputFile  
     * @param outputFile  
     * @return  
     */    
    private  boolean process(String cuttentId,String inputFile,String outputFile,String tempFile_home,String ffmpeg_home,String mencoder_home) {    
        int type = checkContentType( inputFile);    
        boolean status = false;    
        if (type == 0) {    
            status = processFlvorMp4(cuttentId,inputFile,outputFile,tempFile_home,ffmpeg_home);// 直接将文件转为flv文件    
        } else if (type == 1) {    
            String avifilepath = processAVI(type,inputFile,tempFile_home,mencoder_home);    
            if (avifilepath == null)    
                return false;// avi文件没有得到    
            status = processFlvorMp4(cuttentId,avifilepath,outputFile,tempFile_home,ffmpeg_home);// 将avi转为flv    
        }    
        return status;    
    } 
    /**  
     * 检查视频类型  
     * @param inputFile  
     * @return ffmpeg 能解析返回0，不能解析返回1  
     */    
    private  int checkContentType(String inputFile) {    
        String type = inputFile.substring(inputFile.lastIndexOf(".") + 1,inputFile.length()).toLowerCase();    
        // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）    
        if (type.equals("avi")) {    
            return 0;    
        } else if (type.equals("mpg")) {    
            return 0;    
        } else if (type.equals("wmv")) {    
            return 0;    
        } else if (type.equals("3gp")) {    
            return 0;    
        } else if (type.equals("mov")) {    
            return 0;    
        } else if (type.equals("mp4")) {    
            return 0;    
        } else if (type.equals("asf")) {    
            return 0;    
        } else if (type.equals("asx")) {    
            return 0;    
        } else if (type.equals("flv")) {    
            return 0;    
        }    
        // 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),    
        // 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.    
        else if (type.equals("wmv9")) {    
            return 1;    
        } else if (type.equals("rm")) {    
            return 1;    
        } else if (type.equals("rmvb")) {    
            return 1;    
        }    
        return 9;    
    }    
	
    /**  
     *  ffmepg: 能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）  
     * @param inputFile  
     * @param outputFile  
     * @return  
     */    
    private  boolean processFlvorMp4(String cuttentId,String inputFile,String outputFile,
    		String tempFile_home,String ffmpeg_home) {    
           
        File file = new File(outputFile);  
        if(file.exists()){  
            System.out.println("输出文件已经存在！无需转换");
            return true;  
        } else {  
            //System.out.println("正在转换文件……");  
            System.out.println("正在转换文件……");
              
            List<String> commend = new java.util.ArrayList<String>();  
            
            //低精度    
          /* commend.add(ffmpeg_home);  
            commend.add("-i");    
            commend.add(inputFile);    
            
            commend.add("-ab");  //flv  
            commend.add("128");  //flv   
            commend.add("-acodec");    
            commend.add("libmp3lame");    
            commend.add("-ac");    
            commend.add("1");    
            commend.add("-ar");    
            commend.add("22050");    
            commend.add("-r");    
            commend.add("29.97");   
            // 清晰度 -qscale 4 为最好但文件大, -qscale 6就可以了  
            commend.add("-qscale");    
            commend.add("4");    
            commend.add("-y");   */  
            
            
            
            //h264
            Properties properties = new Properties();
    		String base = ConvertUtils.class.getResource("/")
    				.getPath();
    		try {
    			properties.load(new FileInputStream(base
    					+ "path/filepath.properties"));
        		String os=properties.getProperty("OS").trim() ;
        		//Linux  
        		if(os.toLowerCase().equals("Linux".toLowerCase())){
        			/*commend.add(ffmpeg_home);  
                    commend.add("-i");    
                    commend.add(inputFile);    
                    //commend.add("-c:v");  //windows  
                    commend.add("-vcodec");  //Linux  
                    commend.add("libx264");      
                    //commend.add("-acodec");    
                    //commend.add("libfaac");
                    //commend.add("-b");    
                    //commend.add("1.5M");    
                    commend.add("-vcodec");    
                    commend.add("copy"); 
                    commend.add("-vpre");      
                    commend.add("fast");   
                    commend.add("-strict");    
                    commend.add("-2"); */ 
        			commend.add(ffmpeg_home);  
                    commend.add("-i");    
                    commend.add(inputFile);    
                    commend.add("-c:v");  //windows  
                    commend.add("libx264");      
                    commend.add("-strict");    
                    commend.add("-2"); 
        		}
        		else{
        			commend.add(ffmpeg_home);  
                    commend.add("-i");    
                    commend.add(inputFile);    
                    commend.add("-c:v");  //windows  
                    commend.add("libx264");      
                    commend.add("-strict");    
                    commend.add("-2");  
        		}
             
		        commend.add(outputFile);    
		        StringBuffer test=new StringBuffer();    
		        for(int i=0;i<commend.size();i++)    
		            test.append(commend.get(i)+" ");    
		        //System.out.println(test);   
		        System.out.println("转换指令："+test);
               
                ProcessBuilder builder = new ProcessBuilder();    
                builder.command(commend);    
                //builder.start();    
                Process p=builder.start();    
                /**  
                 * 清空Mencoder进程 的输出流和错误流  
                 * 因为有些本机平台仅针对标准输入和输出流提供有限的缓冲区大小，  
                 * 如果读写子进程的输出流或输入流迅速出现失败，则可能导致子进程阻塞，甚至产生死锁。   
                 */    
                final InputStream is1 = p.getInputStream();    
                final InputStream is2 = p.getErrorStream();    
                new Thread() {    
                    public void run() {    
                        BufferedReader br = new BufferedReader(new InputStreamReader(is1));    
                        try {    
                            String lineB = null;    
                            while ((lineB = br.readLine()) != null ){    
                                /*if(lineB != null)
                                	System.out.println(lineB);    */
                            }    
                        } catch (IOException e) {    
                            e.printStackTrace();    
                        }    
                    }    
                }.start();     
                new Thread() {    
                    public void run() {    
                        BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));    
                        try {    
                            String lineC = null;    
                            while ( (lineC = br2.readLine()) != null){    
                                /*if(lineC != null)
                                	System.out.println(lineC);*/    
                            }    
                        } catch (IOException e) {    
                            e.printStackTrace();    
                        }    
                    }    
                }.start();  
                p.waitFor();
               // System.out.println("ffmpeg.exe运行完毕！");  
                System.out.println("转换完成："+outputFile);
                
               
	    		//更新数据库记录	  
	    		String inputFile_home = properties.getProperty("inputFile_home").trim();
	    		String outfileName=outputFile.replace(inputFile_home, "");
	    		/*kung_edu_catalog=iKung_edu_catalogService.selectkung_edu_catalogById(id);
	    		kung_edu_catalog.setC_url(outfileName);
	    		kung_edu_catalog.setUrl_state(Long.parseLong("2"));
	    		kung_edu_catalog.setId(Long.parseLong(cuttentId));
                iKung_edu_catalogService.updatekung_edu_catalog(kung_edu_catalog);*/
	    		ConnectPoolC3P0 conn = ConnectPoolC3P0.getInstance();
				 List<Object> param = new ArrayList<Object>();
				 param.add(outfileName);
				 param.add(cuttentId);
				int result= conn.execute("UPDATE KUNG_EDU_CATALOG SET URL_STATE='2',c_url= ?   WHERE ID=?", param);
				System.out.println("转换完成");
				
				
               //是否删除临时文件和源文件
                String deleteTemp_flag =properties.getProperty("deleteTemp_flag").trim();
                String deleteInput_flag =properties.getProperty("deleteInput_flag").trim();
                if(deleteTemp_flag.equals("true")){
            	    if(deleteFile(tempFile_home)){
	    				System.out.println("删除临时文件完成："+tempFile_home);
	    			}
	    			else{
	    				System.out.println("删除临时文件失败："+tempFile_home);
	    			}
                }
                if(deleteInput_flag.equals("true")){
            	    if(deleteFile(inputFile)){
	    				System.out.println("删除源文件完成："+inputFile);
	    			}
	    			else{
	    				System.out.println("删除源文件失败："+inputFile);
	    			}
	            }
	                
	    	
                
                
                
                return true;    
            } catch (Exception e) {  
            	System.out.println("MP4FLV转换异常："+e);
                e.printStackTrace();    
                return false;    
            }    
             
        }  
         
    }
    /** 
     * 删除单个文件 
     * @param   sPath    被删除文件的文件名 
     * @return 单个文件删除成功返回true，否则返回false 
     */  
    public boolean deleteFile(String sPath) {  
        boolean flag = false;  
        File file = new File(sPath);  
        // 路径为文件且不为空则进行删除  
        if (file.isFile() && file.exists()) {  
            file.delete();  
            flag = true;  
        }  
        return flag;  
    }  
    /**  
     * Mencoder:  
     * 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.  
     * @param type  
     * @param inputFile  
     * @return  
     */    
    private  String processAVI(int type,String inputFile,String tempFile_home,String mencoder_home) {    
        File file =new File(tempFile_home);    
        if(file.exists()){  
            //System.out.println("avi文件已经存在！无需转换");  
            System.out.println("avi文件已经存在！无需转换");
            return tempFile_home;  
        }    
        List<String> commend = new java.util.ArrayList<String>();    
        commend.add(mencoder_home);    
        commend.add(inputFile);    
        commend.add("-oac");    
        commend.add("mp3lame");    
        commend.add("-lameopts");    
        commend.add("preset=64");    
        commend.add("-ovc");    
        commend.add("xvid");    
        commend.add("-xvidencopts");    
        commend.add("bitrate=600");    
        commend.add("-of");    
        commend.add("avi");    
        commend.add("-o");    
        commend.add(tempFile_home);    
        StringBuffer test=new StringBuffer();    
        for(int i=0;i<commend.size();i++)    
            test.append(commend.get(i)+" ");    
        System.out.println("AVI转换指令："+test);
        try     
        {    
            ProcessBuilder builder = new ProcessBuilder();    
            builder.command(commend);    
            Process p=builder.start();    
            /**  
             * 清空Mencoder进程 的输出流和错误流  
             * 因为有些本机平台仅针对标准输入和输出流提供有限的缓冲区大小，  
             * 如果读写子进程的输出流或输入流迅速出现失败，则可能导致子进程阻塞，甚至产生死锁。   
             */    
            final InputStream is1 = p.getInputStream();    
            final InputStream is2 = p.getErrorStream();    
            new Thread() {    
                public void run() {    
                    BufferedReader br = new BufferedReader(new InputStreamReader(is1));    
                    try {    
                        String lineB = null;    
                        while ((lineB = br.readLine()) != null ){    
                            //if(lineB != null)System.out.println(lineB);    
                        }    
                    } catch (IOException e) {    
                        e.printStackTrace();    
                    }    
                }    
            }.start();     
            new Thread() {    
                public void run() {    
                    BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));    
                    try {    
                        String lineC = null;    
                        while ( (lineC = br2.readLine()) != null){    
                            //if(lineC != null)System.out.println(lineC);    
                        }    
                    } catch (IOException e) {    
                        e.printStackTrace();    
                    }    
                }    
            }.start();     
                
            //等Mencoder进程转换结束，再调用ffmpeg进程    
            p.waitFor();    
            return tempFile_home;    
        }catch (Exception e){     
            System.out.println("AVI转换异常："+e);
            return null;    
        }     
    }    
	
    
    public void sendHls(String name){
    	
    	/*try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
    	
    	 Properties properties = new Properties();
			String base = Application.class.getResource("/")
					.getPath();
			 
				try {
					
				
					
					List<String> commend = new java.util.ArrayList<String>();  
					properties.load(new FileInputStream(base
							+ "path/filepath.properties"));
					//String ffmpeg_home = base+"tools/ffmpeg/ffmpeg.exe";//ffmpeg.exe所放的路径  
					//String nginx_url = properties.getProperty("nginx_url").trim(); 
					String rtmp_url = properties.getProperty("rtmp_url").trim(); 
					String inputFile_home = properties.getProperty("inputFile_home").trim(); 

					String os=properties.getProperty("OS").trim() ;
		    		String ffmpeg_home = base+"tools/ffmpeg/ffmpeg.exe";//ffmpeg.exe所放的路径  
		    		//String mencoder_home = base+"tools/mencoder/mencoder.exe";//mencoder.exe所放的路径  
		    		//Linux 环境下更改目录
		    		if(os.toLowerCase().equals("Linux".toLowerCase())){
		    			ffmpeg_home=properties.getProperty("ffmpeg_home").trim() ;
		    			//mencoder_home=properties.getProperty("mencoder_home").trim() ;
		    		}
					
					File file = new File(inputFile_home+name);
					if (!file.exists())
					{
						file.mkdir();
					}
					
					
					
					commend.add(ffmpeg_home);  
					  
				    /*commend.add("-i");    
				    commend.add(inputFile_home+"wukong.flv");    
				    commend.add("-vcodec");   
	                commend.add("copy");    
	                commend.add("-f");    
	                commend.add("flv");   
				    commend.add(rtmp_url+"stream_555"); */
				    
				    
					 /*commend.add("-re");    
				    commend.add("-i");    
				    commend.add(rtmp_url+name+" live=1");   
				    commend.add("-strict");   
	                commend.add("-2");   
	                commend.add("-vcodec");   
	                commend.add("copy");  
	                 
	                commend.add("-f");
	                commend.add("hls");
	                commend.add(inputFile_home+name+"/"+name+".m3u8");  */
				   
	                commend.add("-v");    
	                commend.add("verbose");    
	                 commend.add("-i");    
	                commend.add(rtmp_url+name);   
	                commend.add("-strict");   
	                commend.add("-2"); 
	                if(os.toLowerCase().equals("Linux".toLowerCase())){
	                	commend.add("-vcodec");    
		                commend.add("copy");
		                commend.add("-acodec");    
		                commend.add("copy");
	                }
	                else{
	                	commend.add("-c:v");    
		                commend.add("libx264");
		                commend.add("-c:a");
		                commend.add("aac");
		                
		                //commend.add("-crf");
		                //commend.add("20");
		                //commend.add("-profile:v");
		                //commend.add("main");
	                }
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
	                commend.add("-segment_wrap");
	                commend.add("5");
	                
	                commend.add("-segment_time");
	                commend.add("10");
	                commend.add(inputFile_home+name+"/"+name+"%03d.ts"); 
	                
				  
					StringBuffer test=new StringBuffer();    
					for(int i=0;i<commend.size();i++)    
					    test.append(commend.get(i)+" ");    
					//System.out.println(test);   
					System.out.println("转换指令："+test);
					/*ProcessBuilder builder = new ProcessBuilder();    
					builder.command(commend); 
					
				  
		            builder.redirectErrorStream(true);  
		            builder.start();  */
				     
		            
		            Runtime rt = Runtime.getRuntime();  
		            Process proc = rt.exec(test.toString());  
		            InputStream stderr = proc.getErrorStream();  
		            InputStreamReader isr = new InputStreamReader(stderr);  
		            BufferedReader br = new BufferedReader(isr);  
		            String line = null;  
		            System.out.println("<ERROR>");  
		            while ((line = br.readLine()) != null){  
		                //System.out.println(line);  
		            }  
		            System.out.println("</ERROR>");  
		            int exitVal = proc.waitFor();  
		            System.out.println("Process exitValue: " + exitVal);
		            
					//builder.start();    
					//Process process = Runtime.getRuntime().exec(test.toString());//执行命令
					//Process p=builder.start();
				  
		           /*InputStreamReader ir = new InputStreamReader(p.getInputStream());
		           LineNumberReader input = new LineNumberReader(ir);
		           String line;
		           while ((line = input.readLine()) != null) {//输出结果
		        	   System.out.println(line);
		           }*/
		       	  
					//p.waitFor();  
					//System.out.println("XXXXXXXXXXXXXXXXXXXXX");
					
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    
	}
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ConvertUtils cu = new ConvertUtils();
		//cu.Contvert("stream_4", "4");
		cu.sendHls("stream_555");
	}

}
