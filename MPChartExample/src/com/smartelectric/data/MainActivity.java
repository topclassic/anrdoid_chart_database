package com.smartelectric.data;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xxmassdeveloper.mpchartexample.R;


public class MainActivity extends Activity {
  
    private ListView listView;
    private String names[] = {
    		" GRAPH VIEW",
            " Edit Name Outlet",
            " Set Unit Limit",
            " HELP"
    };

    private String desc[] = {
            " You can look graph watt, bath and unit"+"\n"+" of outlet",
            " Fisrt contact outlet name is 'Unknown'"+"\n"+" You can change name so as 'Toilet' ",
            " If energy electric high you can set"+"\n"+" limit to one outlet and all outlet ",
            " If you has problem, We can help you"
    };


    private Integer imageid[] = {
    		R.drawable.graph,
            R.drawable.tool,
            R.drawable.meter,
            R.drawable.help
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomList customList = new CustomList(this, names, desc, imageid);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(customList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int sequence, long l) {
            	
            	
            	switch(sequence){
            	case 0 : 	Intent intent0 = new Intent(getApplicationContext(),MainView.class);
		 					startActivity(intent0); 	
		 					break;
            	case 1 : 	Intent intent1 = new Intent(getApplicationContext(),MainEdit.class);
		 		 			startActivity(intent1); 	
		 		 			break;
            	case 2 : 	Intent intent2 = new Intent(getApplicationContext(),MainSetlimit1.class);
		 					startActivity(intent2);
		 					break;
            	case 3 : 	Intent intent3 = new Intent(getApplicationContext(),Help.class);
            				startActivity(intent3); 	
				 		 	break;
            	
            	}
            	
            }
        });
        
       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),"You Clicked "+names[i],Toast.LENGTH_SHORT).show();
            }
        });*/
    }
   /* public void barGraphHandler (View view)
    {
    	WatBarchart bar = new WatBarchart();
    	Intent lineIntent = bar.execute(this);
        startActivity(lineIntent);
    }*/
    
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
  //      if (id == R.id.action_settings) {
            return true;
  //      }

  //      return super.onOptionsItemSelected(item);
    }
}
