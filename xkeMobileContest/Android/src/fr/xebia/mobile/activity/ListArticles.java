package fr.xebia.mobile.activity;

import fr.xebia.mobile.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class ListArticles extends ListActivity {
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_articles);
        
        // Here call JSon to list articles
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
//		super.onListItemClick(l, v, position, id);
//		Intent myIntent = new Intent(ListCategories.this, ListArticles.class);
//		ListCategories.this.startActivity(myIntent);
	}

}
