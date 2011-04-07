package fr.xebia.mobile.activity;

import fr.xebia.mobile.R;
import fr.xebia.mobile.R.layout;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

public class ListCategories extends ListActivity implements
AdapterView.OnItemClickListener, View.OnClickListener {
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_categories);
        
        // Here call JSon to list Categories
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

}
