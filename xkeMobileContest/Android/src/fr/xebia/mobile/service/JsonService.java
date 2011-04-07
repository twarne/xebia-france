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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.xebia.mobile.domain.Article;
import fr.xebia.mobile.domain.Category;

public class JsonService {
	
	final static HttpClient httpClient;
	final static HttpHost targetHost;
	
	static {
		 httpClient = new DefaultHttpClient();
	     targetHost = new HttpHost("blog.xebia.fr", 80, "http");
	}

	public List<Category> getCategories(){
	        HttpGet httpget = new HttpGet("/wp-json-api/get_category_index/");
			try {
				HttpResponse response = httpClient.execute(targetHost, httpget, new BasicHttpContext());
				BasicResponseHandler responseHandler = new BasicResponseHandler();
				String json = responseHandler.handleResponse(response);
				JSONObject categoryJson = new JSONObject(json);
				JSONArray categoriesJson = categoryJson.getJSONArray("categories");
				List<Category> categories = new ArrayList<Category>();
				for (int i = 0; i < categoriesJson.length(); i++) {
					Category cat = new Category();
					cat.setTitle(categoriesJson.getJSONObject(i).getString("title"));
					cat.setPostCount(categoriesJson.getJSONObject(i).getInt("post_count"));
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
	
	public List<Article> getArticles(String categoryId){
		String getUrl = "/wp-json-api/get_category_posts/?category_id=" + categoryId +
		 	"&page=1&count=10&exclude=content,comments,attachments";
        HttpGet httpget = new HttpGet(getUrl);
		try {
			HttpResponse response = httpClient.execute(targetHost, httpget, new BasicHttpContext());
			BasicResponseHandler responseHandler = new BasicResponseHandler();
			String json = responseHandler.handleResponse(response);
			JSONObject articleJson = new JSONObject(json);
			JSONArray articlesJson = articleJson.getJSONArray("posts");
			List<Article> articles = new ArrayList<Article>();
			for (int i = 0; i < articlesJson.length(); i++) {
				Article article = new Article();
				article.setTitle(articlesJson.getJSONObject(i).getString("title"));
				article.setDescription(articlesJson.getJSONObject(i).getString("excerpt"));
				article.setAuthorName(articlesJson.getJSONObject(i).getJSONObject("author").getString("name"));
				articles.add(article);
			}
			return articles;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new ArrayList<Article>();
}
}
