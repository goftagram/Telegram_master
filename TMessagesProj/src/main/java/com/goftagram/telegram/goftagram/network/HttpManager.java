package com.goftagram.telegram.goftagram.network;

import com.goftagram.telegram.goftagram.util.LogUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class HttpManager {

	public static final String LOG_TAG   = LogUtils.makeLogTag(HttpManager.class.getSimpleName());

	public static final int      SOCKET_TIME_OUT       =-1;
	public static final int      IO_EXCEPTION          =-2;
	public static final int      OK                    =-3;

	/**
	 * The result of each http connection
	 */
	public static class NetTransResult{
		/**
		 * The status code which is one of the above const
		 */
		Integer mStatusCode;
		/**
		 * The server response
		 */
		String mResult;

		public NetTransResult(){

		}
		public NetTransResult(Integer statusCode, String result){
			mStatusCode = statusCode;
			mResult = result;
		}
		public Integer getStatusCode() {
			return mStatusCode;
		}

		public void setStatusCode(Integer statusCode) {
			mStatusCode = statusCode;
		}

		public String getResult() {
			return mResult;
		}

		public void setResult(String result) {
			mResult = result;
		}

	}

	/**
	 *
	 * @param urlStr	the url to post data
	 * @param message	the message to send
	 * @return			the transaction result
	 */
	public static NetTransResult postData(String urlStr,String message){


		OkHttpClient client = new OkHttpClient();
		OkUrlFactory okUrlFactory = new OkUrlFactory(client);
		NetTransResult result = new NetTransResult();
		String response = "";
		HttpURLConnection httpURLConnection = null;
		BufferedWriter writer = null;
		BufferedReader reader = null;
		try {


			URL url = new URL(urlStr);
			LogUtils.LOGI(LOG_TAG, "url.toString(): " + url.toString());
			httpURLConnection = okUrlFactory.open(url);
//			httpURLConnection = (HttpURLConnection)url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setReadTimeout(10000);
			httpURLConnection.setConnectTimeout(20000);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			String postDeviceData = message;
			writer = new BufferedWriter(
					new OutputStreamWriter(httpURLConnection.getOutputStream(),"UTF-8"));
			writer.write(postDeviceData);
			writer.flush();
			reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));

				String line;
				StringBuilder sb = new StringBuilder();			    	
				while((line = reader.readLine()) != null ){			    		
					sb.append(line + "\n");
				}

				response = sb.toString();
				writer.close();
				reader.close();  
				httpURLConnection.disconnect();
				result.setStatusCode(OK);
				result.setResult(response);
				return result;

		}
		catch (MalformedURLException e) {
			result.setStatusCode(IO_EXCEPTION);
			result.setResult("");
			return result;

		}catch (SocketTimeoutException e) {
			result.setStatusCode(SOCKET_TIME_OUT);
			result.setResult("");
			return result;

		} catch (IOException e) {
			try {
				InputStream is = httpURLConnection.getErrorStream();
				if(is == null){
					throw new IOException();
				}
				reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));

				String line;
				StringBuilder sb = new StringBuilder();

				while((line = reader.readLine()) != null ){
					sb.append(line + "\n");
				}
				response = sb.toString();
				result.setStatusCode(httpURLConnection.getResponseCode());
				result.setResult(response);
				return result;

			}catch (UnsupportedEncodingException e1) {
				result.setStatusCode(IO_EXCEPTION);
				result.setResult("");
				return result;
			}
			catch (SocketTimeoutException e2) {
				result.setStatusCode(IO_EXCEPTION);
				result.setResult("");
				return result;
			}
			catch (IOException e3) {
				result.setStatusCode(IO_EXCEPTION);
				result.setResult("");
				return result;
			}

		}finally{

			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
				}
			}

			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
				}
			}


			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}

	}


	/**
	 *
	 * @param urlStr 	The url to get data
	 * @return			the transaction result
	 */
	public static NetTransResult getData(String urlStr){

		OkHttpClient client = new OkHttpClient();
		OkUrlFactory okUrlFactory = new OkUrlFactory(client);
		NetTransResult result = new NetTransResult();
		HttpURLConnection httpURLConnection = null;
		String response = "";
		BufferedReader reader = null;
		try {
			URL url = new URL(urlStr);
			LogUtils.LOGI(LOG_TAG, "url.toString(): " + url.toString());
			httpURLConnection = okUrlFactory.open(url);
//			httpURLConnection = (HttpURLConnection)url.openConnection();
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setReadTimeout(10000);
			httpURLConnection.setConnectTimeout(20000);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(false);
			reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));

			String line;
			StringBuilder sb = new StringBuilder();			    	
			while((line = reader.readLine()) != null ){			    		
				sb.append(line + "\n");
			}
			response = sb.toString();
			result.setStatusCode(OK);
			result.setResult(response);
			return result;

		}catch (MalformedURLException e) {
				result.setStatusCode(IO_EXCEPTION);
				result.setResult("");
				return result;

			}catch (SocketTimeoutException e) {
				result.setStatusCode(SOCKET_TIME_OUT);
				result.setResult("");
				return result;

			} catch (IOException e) {
			try {
				InputStream is = httpURLConnection.getErrorStream();
				if(is == null){
					throw new IOException();
				}
				reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));

				String line;
				StringBuilder sb = new StringBuilder();

				while((line = reader.readLine()) != null ){
					sb.append(line + "\n");
				}
				response = sb.toString();
				result.setStatusCode(httpURLConnection.getResponseCode());
				result.setResult(response);
				return result;

			}catch (UnsupportedEncodingException e1) {
				result.setStatusCode(IO_EXCEPTION);
				result.setResult("");
				return result;
			}
			catch (SocketTimeoutException e2) {
				result.setStatusCode(IO_EXCEPTION);
				result.setResult("");
				return result;
			}
			catch (IOException e3) {
				result.setStatusCode(IO_EXCEPTION);
				result.setResult("");
				return result;
			}
			}finally{
				if(reader != null){
					try {
						reader.close();
					} catch (IOException e) {
					}
				}
				if (httpURLConnection != null) {
					httpURLConnection.disconnect();
				}
			}

		}

	}
