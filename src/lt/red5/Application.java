package lt.red5;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lt.red5.convert.ConvertUtils;
import lt.red5.database.ConnectPoolC3P0;

import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IPlayItem;
import org.red5.server.api.stream.ISubscriberStream;
import org.red5.server.stream.ClientBroadcastStream;


 
public class Application  extends ApplicationAdapter {

	public Application iapp; 
    public IConnection iconn; 
    public IScope iscope; 
    public ClientBroadcastStream istream;//用来接受flash上传的stream的类 

	@Override
	public void streamPublishStart(IBroadcastStream stream) {
		String streamName=stream.getPublishedName();
		iapp = new Application(); 
		iconn = Red5.getConnectionLocal();//得到当前的连接 
		iscope = iconn.getScope();//一组连入服务器的客户 
		iapp.connect(iconn,iscope,null); 
		System.out.println("开始直播视频流："+streamName); 
		//注意stream name，在flash端也需要匹配 
		istream = (ClientBroadcastStream)iapp.getBroadcastStream(iscope,streamName); 
		try {
			istream.saveAs(streamName, false);
		} catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		}
		/*ConnectPoolC3P0 conn = ConnectPoolC3P0.getInstance();
		 
        List<Map<String,String>> result =conn.queryForMap("select * from kung_vedio", null);
        System.out.println("￥￥￥￥￥￥￥￥￥："+result.size());*/
		 
		
		super.streamPublishStart(stream);
		
		try {
			 
			String[] names= streamName.split("_");
			if(names.length==2){
				ConnectPoolC3P0 conn = ConnectPoolC3P0.getInstance();
				 List<Object> param = new ArrayList<Object>();
				 param.add(streamName);
				 param.add(names[1]);
			    int result= conn.execute("UPDATE KUNG_EDU_CATALOG SET STATUS='1' , TYPE='直播' , ZHIBO_URL=?,URL_STATE=1  WHERE ID=?", param);
			    System.out.println("修改记录数："+result);
			}
			else{
				System.out.println("直播流名称不正确："+streamName); 
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("直播开始异常："+e); 
			e.printStackTrace();
		}
		
	}
 
	@Override
	public void streamRecordStop(IBroadcastStream stream) {
		// TODO Auto-generated method stub
		super.streamRecordStop(stream);
		try {
			String streamName=stream.getPublishedName();
			System.out.println("停止直播视频流："+streamName); 
			String[] names= streamName.split("_");
			if(names.length==2){
				ConnectPoolC3P0 conn = ConnectPoolC3P0.getInstance();
				 List<Object> param = new ArrayList<Object>();
				 param.add(names[1]);
				int result= conn.execute("UPDATE KUNG_EDU_CATALOG SET STATUS='0'   WHERE ID=?", param);
				System.out.println("修改记录数："+result);
				
				Thread.sleep(10000);
				System.out.println("开始转码");
				ConvertUtils cu = new ConvertUtils();
				cu.Contvert(streamName, names[1]);
			}
			else{
				System.out.println("直播流名称不正确："+streamName); 
				
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("直播结束异常："+e); 
			e.printStackTrace();
		}
	}
	 
	 
	 
	/*public synchronized boolean connect(IConnection conn, IScope scope,
			Object[] params)
	{
		System.out.println("好的"+conn.isConnected()+"........."+params.length);
		return super.connect(conn, scope, params);
	}
	

	public boolean appConnect(IConnection conn,Object[] args)
	{
		System.out.println("xxxxxxxxxxxxxxxxx");
		return true;
	}
	
	 
	public synchronized boolean join(IClient client, IScope scope)
	{
		return super.join(client, scope);
	}


	public synchronized void leave(IClient client, IScope scope)
	{
		super.leave(client, scope);
	}


	public synchronized boolean start(IScope scope)
	{
		return super.start(scope);
	}


	public synchronized void stop(IScope scope)
	{
		super.stop(scope);
	}


	public void disconnect(IConnection conn, IScope scope) {
		System.out.println("断开连接"+conn.isConnected());
	}*/

}
