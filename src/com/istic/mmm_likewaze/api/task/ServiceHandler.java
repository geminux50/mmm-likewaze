package com.istic.mmm_likewaze.api.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.istic.mmm_likewaze.model.User;


public class ServiceHandler {
 
    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;
 
    public ServiceHandler() {
 
    }
 
    /**
     * Make an HTTP service Call Post
     * */
    public String makeServiceCallPost(String url, Object postMessage){
    	
    	 HttpClient client = new DefaultHttpClient();
         HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
         HttpResponse httpresponse;
         
         HttpEntity httpEntity = null;

         try {
             HttpPost post = new HttpPost(url);
             JSONObject json = prepareJsonObjectToPost(postMessage);
             if(json ==null){
            	 return null ;
             }
             StringEntity se = new StringEntity( json.toString());  
             se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
             post.setEntity(se);
             httpresponse = client.execute(post);
             
             httpEntity = httpresponse.getEntity();
             response = EntityUtils.toString(httpEntity);

         } catch(Exception e) {
             e.printStackTrace();
            // createDialog("Error", "Cannot Estabilish Connection");
         }
         
         return response;
    	
    }
 
    /**
     * Make an HTTP service call GET 
     * */
    public String makeServiceCallGET(String url, List<NameValuePair> params){
    	
    	try{
    		 // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
             
    		 if (params != null) {
                 String paramString = URLEncodedUtils
                         .format(params, "utf-8");
                 url += "?" + paramString;
             }
                HttpGet httpGet = new HttpGet(url);
 
                httpResponse = httpClient.execute(httpGet);
 
            
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
    		
    	}catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
         
    	 return response;
    }
  

    private JSONObject prepareJsonObjectToPost(Object postMessage){
    	
    	JSONObject json = new JSONObject();
    	if(postMessage instanceof User){
       	    try {
				 json.put("email", ((User)postMessage).getEmail() );
				 json.put("passwd",((User)postMessage).getPasswd());
	             json.put("pseudo", ((User)postMessage).getPseudo());
	             Log.i("TYPE OBJECT ServCall","USER TYPE ");
	            return json;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
    	}else{
    		 Log.i("TYPE OBJECT ServCall","SORRY can't handle this kind of object");
    		return null;
    	}
    	return null;
    }
    
}
