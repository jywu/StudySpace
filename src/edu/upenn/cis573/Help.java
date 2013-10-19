package edu.upenn.cis573;

import edu.upenn.cis573.R;
import android.app.Activity;
import android.os.Bundle;

/**
 * The Help class is started by the SearchActivity class when 
 * a user clicks on the "Help" button on the Search screen. 
 * The Help class simply displays a view with a written 
 * walkthrough of the application.
 */
public class Help extends Activity{
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);  
    }

}
