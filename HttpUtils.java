package com.demo.cilent;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class HttpUtils {
	private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(15000)
            .setConnectTimeout(15000)
            .setConnectionRequestTimeout(15000)
            .build();
	
	private static HttpUtils instance = null;
	private HttpUtils(){
//		RequestConfig defaultRequestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
//		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
//		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).build();
//		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
//		httpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(defaultRequestConfig).build();
	}
	public static HttpUtils getInstance(){
		if (instance == null) {
			instance = new HttpUtils();
		}
		return instance;
	}
	
	/**
	 * 发送 post请求
	 * @param httpUrl 地址
	 * @throws UnknownHostException 
	 */
	public String sendHttpPost(String httpUrl) throws UnknownHostException {
		HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost  
		return sendHttpPost(httpPost);
	}
	
	/**
	 * 发送 post请求
	 * @param httpUrl 地址
	 * @param params 参数(格式:key1=value1&key2=value2)
	 * @throws UnknownHostException 
	 */
	public String sendHttpPost(String httpUrl, String params) throws UnknownHostException {
		HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost  
		try {
			//设置参数
			StringEntity stringEntity = new StringEntity(params, "UTF-8");
			stringEntity.setContentType("application/x-www-form-urlencoded");
			httpPost.setEntity(stringEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sendHttpPost(httpPost);
	}
	
	/**
	 * 发送 post请求
	 * @param httpUrl 地址
	 * @param maps 参数
	 * @throws UnknownHostException 
	 */
	public String sendHttpPost(String httpUrl, Map<String, String> maps){
		HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost  
		// 创建参数队列  
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for (String key : maps.keySet()) {
			nameValuePairs.add(new BasicNameValuePair(key, maps.get(key)));
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sendHttpPost(httpPost);
	}
	
	/**
	 * 发送 post请求
	 * @param httpUrl 地址
	 * @param maps 参数
	 * @throws UnknownHostException 
	 */
	public String sendHttpPost(String httpUrl, Map<String, String> headers,Map<String, String> params) throws UnknownHostException {
		HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost  
		for (String key : headers.keySet()) {
			httpPost.addHeader(key,headers.get(key));
		}
		// 创建参数队列  
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sendHttpPost(httpPost);
	}
	/**
	 * 发送 post请求（带文件）
	 * @param httpUrl 地址
	 * @param maps 参数
	 * @param fileLists 附件
	 * @throws UnknownHostException 
	 */
	public String sendHttpPost(String httpUrl, Map<String, String> maps, List<File> fileLists) throws UnknownHostException {
		HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost  
		MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();
		for (String key : maps.keySet()) {
			meBuilder.addPart(key, new StringBody(maps.get(key), ContentType.TEXT_PLAIN));
		}
		for(File file : fileLists) {
			FileBody fileBody = new FileBody(file);
			meBuilder.addPart("files", fileBody);
		}
		HttpEntity reqEntity = meBuilder.build();
		httpPost.setEntity(reqEntity);
		return sendHttpPost(httpPost);
	}
	
	/**
	 * 发送Post请求
	 * @param httpPost
	 * @return
	 * @throws UnknownHostException 
	 */
	private String sendHttpPost(HttpPost httpPost){
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		String responseContent = null;
		try {
			// 创建默认的httpClient实例.
			httpClient = HttpClients.createDefault();
			httpPost.setConfig(requestConfig);
			// 执行请求
			response = httpClient.execute(httpPost);
			entity = response.getEntity();
			responseContent = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭连接,释放资源
				if (response != null) {
					response.close();
				}
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseContent;
	}

	/**
	 * 发送 get请求
	 * @param httpUrl
	 */
	public String sendHttpGet(String httpUrl) {
		HttpGet httpGet = new HttpGet(httpUrl);// 创建get请求
		return sendHttpGet(httpGet);
	}
	/**
	 * 发送 get请求
	 * @param httpUrl
	 */
	public String sendHttpGet(String httpUrl,Map<String,String> headers) {
		HttpGet httpGet = new HttpGet(httpUrl);// 创建get请求
		for (String key : headers.keySet()) {
			httpGet.addHeader(key,headers.get(key));
		}
		return sendHttpGet(httpGet);
	}
	/**
	 * 发送 get请求Https
	 * @param httpUrl
	 */
	public String sendHttpsGet(String httpUrl) {
		HttpGet httpGet = new HttpGet(httpUrl);// 创建get请求
		return sendHttpsGet(httpGet);
	}
	
	/**
	 * 发送Get请求
	 * @param httpPost
	 * @return
	 */
	private String sendHttpGet(HttpGet httpGet) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		String responseContent = null;
		try {
			// 创建默认的httpClient实例.
			httpClient = HttpClients.createDefault();
			httpGet.setConfig(requestConfig);
			// 执行请求
			response = httpClient.execute(httpGet);
			entity = response.getEntity();
			responseContent = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭连接,释放资源
				if (response != null) {
					response.close();
				}
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseContent;
	}
	
	/**
	 * 发送Get请求Https
	 * @param httpPost
	 * @return
	 */
	private String sendHttpsGet(HttpGet httpGet) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		String responseContent = null;
		try {
			// 创建默认的httpClient实例.
			PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.load(new URL(httpGet.getURI().toString()));
			DefaultHostnameVerifier hostnameVerifier = new DefaultHostnameVerifier(publicSuffixMatcher);
			httpClient = HttpClients.custom().setSSLHostnameVerifier(hostnameVerifier).build();
			httpGet.setConfig(requestConfig);
			// 执行请求
			response = httpClient.execute(httpGet);
			entity = response.getEntity();
			responseContent = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭连接,释放资源
				if (response != null) {
					response.close();
				}
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseContent;
	}

	public String searchInfo(Map params){
//		String queryType=params.get("queryType").toString();
//		StringBuffer stringBuffer=new StringBuffer();
//		Map queryParams =new HashMap<String, String>();
//		queryParams.put("queryNo", params.get("queryNo"));
//		queryParams.put("queryName", params.get("queryName"));
//		queryParams.put("userId", params.get("KEY_USER_USER_CODE"));
//		queryParams.put("apiKey", params.get("apiKey"));
//		queryParams.put("fromQuery", "web");
//		//不同的查询方式传递不同的url地址
//		if(queryType.equals("undertaker")){
//			stringBuffer.append("http://127.0.0.1:1319/APISERVICES/mvc/undertaker");
//		}else if(queryType.equals("disruptinfo")){
//			stringBuffer.append("http://127.0.0.1:1319/APISERVICES/mvc/disruptinfo");
//		}else if(queryType.equals("entbaseinfo")){
//			queryParams.put("keyType", params.get("keyType"));
//			stringBuffer.append("http://127.0.0.1:1319/APISERVICES/mvc/entbaseinfo");
//		}
//		String result=sendHttpPost(stringBuffer.toString(),queryParams);
		return null;
	}
	public static void main(String[] args) throws Exception {
//		HttpPost httpPost = new HttpPost("https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsg?pass_ticket=HwZSbvpU6VJcjgbKFCcednbCc%252FL5Qwkm1ronAiPazfI%253D");// 创建httpPost  
//		JSONObject jsonParams = new JSONObject("{'BaseRequest':{'Uin':17975375,'Sid':'MD5V2Nq9r4FDL0xH','Skey':'@crypt_569912e8_05927eb49f16b803b1b7432b67109d8b','DeviceID':'e277116673067212'},'Msg':{'Type':1,'Content':'睡得真好','FromUserName':'@625c995e03255a4698201df0aeda2127','ToUserName':'@d67ceb36974b3cfc1b079d2ffdfd20ec','LocalID':'14845570063970532','ClientMsgId':'14845570063970532'},'Scene':0}");
//		// 创建参数队列  
//		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//		for (String key : maps.keySet()) {
//			nameValuePairs.add(new BasicNameValuePair(key, maps.get(key)));
//		}
//		try {
//			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println(HttpUtils.getInstance().sendHttpPost(httpPost));
		
		String result=HttpUtils.getInstance().sendHttpGet("http://zhixing.court.gov.cn/search/captcha.do");
		System.out.println(result);
	}
}
