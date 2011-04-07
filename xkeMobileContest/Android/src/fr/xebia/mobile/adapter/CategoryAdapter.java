package fr.xebia.mobile.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.xebia.mobile.R;
import fr.xebia.mobile.domain.Category;

public class CategoryAdapter extends ArrayAdapter<Category> {
	
	private ArrayList<Category> values;

	public CategoryAdapter(Context context, int textViewResourceId,
			List<Category> objects) {
		super(context, textViewResourceId, objects);
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.list_categories_item, null);
		}
		
		Category category = values.get(position);
		if(category!=null){
			TextView titleTV = (TextView) convertView.findViewById(R.id.CategoryTitle);
			TextView articleCountTV = (TextView) convertView.findViewById(R.id.CategoryArticlesCount);
			
			// TODO
			titleTV.setText(category.getClass().toString());
			articleCountTV.setText(category.getClass().toString());
		}
		
		return super.getView(position, convertView, parent);
	}
	
	

}
