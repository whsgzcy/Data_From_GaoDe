package com.iwant.download2geodata;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import net.sf.json.JSONObject;



/**
 * @Description:
 * @author: whsgzcy
 * @date: 2016-12-17 下午1:30:05
 * 
 */
public class HttpRequestUtil {
	
	/**
	* @Description:纯get请求
	* @author: whsgzcy
	* @date: 2016-12-17 下午1:51:14  
	* @param url
	* @return
	* JSONObject
	* @throws
	 */
	public static JSONObject getJsonObject(String url) {
		JSONObject jsonObject = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
			HttpResponse response = httpClient.execute(httpGet);
			StringBuilder builder = new StringBuilder();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent(),
							"utf-8"));
			for (String s = bufferedReader.readLine(); s != null; s = bufferedReader
					.readLine()) {
				builder.append(s);
			}
			jsonObject = JSONObject.fromObject(builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = null;
		}
		return jsonObject;
	}
}
