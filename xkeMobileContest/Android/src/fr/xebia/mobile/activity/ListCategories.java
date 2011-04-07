package fr.xebia.mobile.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import fr.xebia.mobile.R;
import fr.xebia.mobile.adapter.CategoryAdapter;
import fr.xebia.mobile.domain.Category;
import fr.xebia.mobile.service.JsonService;

public class ListCategories extends ListActivity  {
	
	private JsonService service = new JsonService();
	 /** Called when the activity is first created. */
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_categories);
        
        List<Category> categories = service.getCategories();
        
        ListAdapter adapter =  new CategoryAdapter(ListCategories.this, R.layout.list_categories_item, categories);
        this.setListAdapter(adapter);

    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent myIntent = new Intent(ListCategories.this, ListArticles.class);
		ListCategories.this.startActivity(myIntent);
	}

	
}
