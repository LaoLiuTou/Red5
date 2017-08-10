import java.util.List;
import java.util.Map;

import lt.red5.database.ConnectPoolC3P0;


public class LT {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ConnectPoolC3P0 conn = ConnectPoolC3P0.getInstance();
		 
        List<Map<String,String>> result =conn.queryForMap("select * from kung_vedio", null);
        System.out.println("￥￥￥￥￥￥￥￥￥："+result.size());
	}

}
