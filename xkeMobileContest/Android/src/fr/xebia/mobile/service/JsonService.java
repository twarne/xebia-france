package fr.xebia.mobile.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.xebia.mobile.domain.Category;

public class JsonService {

	public List<Category> getCategories(){
		 HttpClient httpClient = new DefaultHttpClient();
	        HttpHost targetHost = new HttpHost("blog.xebia.fr", 80, "http");
	        HttpContext context = new BasicHttpContext();
	        HttpGet httpget = new HttpGet("wp-json-api/get_category_index/");
			try {
				HttpResponse response = httpClient.execute(targetHost, httpget, context);
				BasicResponseHandler responseHandler = new BasicResponseHandler();
				String json = responseHandler.handleResponse(response);
				JSONObject categoryJson = new JSONObject(json);
				JSONArray categoriesJson = categoryJson.getJSONArray("categories");
				List<Category> categories = new ArrayList<Category>();
				for (int i = 0; i < categoriesJson.length(); i++) {
					Category cat = new Category();
					cat.setTitle(categoriesJson.getJSONObject(i).getString("title"));
					categories.add(cat);
				}
				return categories;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new ArrayList<Category>();
	}
}
