package com.demo.cilent;


/**
 * 通过https的GET方式查询数据
 */
public class SearchCilentGet {
	public static void main(String[] args) throws Exception {
		/**
		 * 
		 * https://test.biinfo.cn/biinfoservice/icbc/finst 一审数据api调用地址
		 * 
		 * 比如：https://test.biinfo.cn/biinfoservice/icbc/finst?apiKey=分配用户的apikey
		 */
		String httpUrl="https://wx.biinfo.cn/biinfoservice/icbc/finst?apiKey=zupWLKvjSlcesrdgkNXmOAMhVeOPHYYSnefieLwgUAFcYKiH&page=1";
		String result=SearchTools.sendHttpsGet(httpUrl);
		//System.out.println(result);
	}
}
