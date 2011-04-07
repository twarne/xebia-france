package fr.xebia.mobile;

import android.app.ListActivity;
import android.os.Bundle;

public class ListCategories extends ListActivity {
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_categories);
        
        // Here call JSon to list Categories
    }

}
