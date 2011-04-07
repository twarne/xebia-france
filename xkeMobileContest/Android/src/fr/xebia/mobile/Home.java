package fr.xebia.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Home extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void listCategories(View view) {
		Intent myIntent = new Intent(Home.this, ListCategories.class);
		Home.this.startActivity(myIntent);

	}
}