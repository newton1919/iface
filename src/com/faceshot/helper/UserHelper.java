package com.faceshot.helper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.widget.Toast;

import com.faceshot.main.R;

public class UserHelper {
	private String host;
	private String port;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	private static UserHelper userHelper = null;
	public static UserHelper getInstance(String host, String port) {
		if (userHelper == null) {
			userHelper = new UserHelper();
		}
		userHelper.setHost(host);
		userHelper.setPort(port);
		return userHelper;
	}
	private UserHelper() {
		
	}
	//用户登录
	public HashMap loginUser(String userName, String password) {
		HttpClient httpClient = new DefaultHttpClient();
		// 先将参数放入List，再对参数进行URL编码
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("userName", userName));
		params.add(new BasicNameValuePair("password", password));
		
		try {
			String host = this.getHost();
			String port = this.getPort();
			String baseUrl = host + ":" + port + "/faceshot/rest/user/login";
			
			HttpPost postMethod = new HttpPost(baseUrl);
			postMethod.setEntity(new UrlEncodedFormEntity(params, "utf-8")); // 将参数填入POST
																				// Entity中

			HttpResponse response = httpClient.execute(postMethod); // 执行POST方法
			Integer respCode = response.getStatusLine().getStatusCode(); // 获取响应码
			String resp = EntityUtils.toString(response.getEntity(), "utf-8"); // 获取响应内容
			HashMap<Integer, String> respMap = new HashMap<Integer, String>();
			respMap.put(respCode, resp);
			return respMap;
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
