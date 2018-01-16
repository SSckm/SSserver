package org.sms.project.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.sms.SysConstants;

/**
 * http请求工具类
 * 
 * @author Sunny
 */
public final class HttpUtil {

	public static String doPost(String url, String postData, String method) {
		if (method == null) {
			method = "POST";
		}
		BufferedReader read = null;
		String responseResult = null;
		try {
			URL realurl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) realurl
					.openConnection();
			connection.setRequestProperty("Content-type", "application/json");
			connection.setConnectTimeout(20 * 1000);
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.connect();
			if (postData != null) {
				DataOutputStream out = new DataOutputStream(connection.getOutputStream());
				out.write(postData.getBytes());
				out.flush();
				out.close();
			}
			read = new BufferedReader(new InputStreamReader(connection.getInputStream(), SysConstants.CHARSET));
			String line;
			StringBuffer result = new StringBuffer();
			while ((line = read.readLine()) != null) {
				result.append(line);
			}
			responseResult = result.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (read != null) {
				try {
					read.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return responseResult;
	}

	public static String readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return new String(data);
	}

	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
			if (ip.indexOf(",") != -1) {
				ip = ip.split(",")[0];
			}
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip == null ? "" : ip;
	}

	public static void addCookie(HttpServletResponse response, String key,
			String value) throws UnsupportedEncodingException {
		Cookie cookie = new Cookie(key, value);
		cookie.setDomain(SysConstants.DOMAIN);
		cookie.setPath("/");
		cookie.setMaxAge(24 * 60 * 60 * 30);
		response.addCookie(cookie);
	}

	public static String httpPost(String url, String postData) {
		CloseableHttpClient client = HttpClients
				.custom()
				.setDefaultRequestConfig(
						RequestConfig.custom()
								.setConnectionRequestTimeout(2000)
								.setConnectTimeout(2000).setSocketTimeout(2000)
								.build()).build();
		HttpPost post = new HttpPost(url);
		StringEntity stringEntity = new StringEntity(postData, "UTF-8");
		stringEntity.setContentType("application/json");
		post.setEntity(stringEntity);
		try {
			CloseableHttpResponse response = client.execute(post);
			int status_code = response.getStatusLine().getStatusCode();
			System.out.println("status_code = " + status_code);
			String respStr = null;
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				respStr = EntityUtils.toString(entity, "UTF-8");
			}
			System.out.println("respStr = " + respStr);
			
			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				System.out.println(header.getName());
				System.out.println(header.getValue());
			}
			EntityUtils.consume(entity);
			return respStr;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
    public static String httpDelete(String url,Map<String,String> headers,String encode){    
        if(encode == null){    
            encode = "utf-8";    
        }    
        String content = null;    
        //since 4.3 不再使用 DefaultHttpClient    
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();     
        HttpDelete httpdelete = new HttpDelete(url);    
        //设置header  
        if (headers != null && headers.size() > 0) {  
            for (Map.Entry<String, String> entry : headers.entrySet()) {  
                httpdelete.setHeader(entry.getKey(),entry.getValue());  
            }  
        }  
        CloseableHttpResponse httpResponse = null;    
        try {    
            httpResponse = closeableHttpClient.execute(httpdelete);    
            HttpEntity entity = httpResponse.getEntity();    
            content = EntityUtils.toString(entity, encode);    
            System.out.println(content);
            return content;
        } catch (Exception e) {    
            e.printStackTrace();    
            return null;    
        }finally{    
            try {    
                httpResponse.close();    
            } catch (IOException e) {    
                e.printStackTrace();    
            }    
        }      
    }    
}