package com.smartelectric.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.xxmassdeveloper.mpchartexample.CombinedChartActivity;
import com.xxmassdeveloper.mpchartexample.DayGraph;
import com.xxmassdeveloper.mpchartexample.DayTest;
import com.xxmassdeveloper.mpchartexample.DayTest2;
import com.xxmassdeveloper.mpchartexample.MonthGraph;
import com.xxmassdeveloper.mpchartexample.R;
import com.xxmassdeveloper.mpchartexample.YearGraph;

public class MainView extends Activity implements OnItemClickListener {
    private static final String[] format = {"Day","Month","Year"};
    private static final String[] day = {"1","2","3","4","5","6","7","8","9","10",
    									 "11","12","13","14","15","16","17","18",
    									 "19","20","21","22","23","24","25","26",
    									 "27","28","29","30","31"};
    private static final String[] month = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    private static final String[] year = {"2015","2016","2017","2018","2019","2020","2021","2022","2023","2024"};
	ListView lvOutlet;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_editdata);
		lvOutlet = (ListView)findViewById(R.id.ListViewOutlet);
		lvOutlet.setOnItemClickListener(this);
		
		ReadData task1 = new ReadData();
		task1.execute(new String[]{"http://192.168.43.130/elec_index.php?format=json"});
	}
	
	ArrayList<Outlet> listOutlet;
	OutletArrayAdapter adapter;
	
	private class ReadData extends AsyncTask<String, Void, Boolean>{

		private ProgressDialog dialog = new ProgressDialog(MainView.this);
		private String error;
		
		InputStream is1;
		String text = "";
		
		@Override
		protected void onPreExecute() {
			dialog.setMessage("Reading Data...");
			dialog.show();
		}
		

		@Override
		protected Boolean doInBackground(String... urls) {
			for(String url: urls){
				try {
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(url); 
					HttpResponse response = client.execute(post);
					is1 = response.getEntity().getContent();
					
				} catch (ClientProtocolException e) {
					error = "ClientProtocolException: " + e.getMessage();
					return false;
				} catch (IOException e) {
					error = "ClientProtocolException: " + e.getMessage();
				}
				
			}
			
			BufferedReader reader;
						
			try {
				reader = new BufferedReader(new InputStreamReader(is1 ,"iso-8859-1"), 8);
				String line = null;
				
				while ((line = reader.readLine()) != null) {
					text += line + "\n";
				}
				
				is1.close();	
				
			} catch (UnsupportedEncodingException e) {
				error = "Unsupport Encoding: " + e.getMessage();
			} catch (IOException e) {
				error = "Error IO: " + e.getMessage();
			}
			
			listOutlet = new ArrayList<Outlet>();
			
			try {
				JSONArray jArray = new JSONArray(text);
				for(int i=0; i<jArray.length(); i++){
					JSONObject json = jArray.getJSONObject(i);
						
					Outlet readoutlet = new Outlet();
					readoutlet.setId(json.getInt("outlet_id"));
				//	readoutlet.setOutletID(json.getString("outlet_id"));
					readoutlet.setOutletname(json.getString("outlet_name"));
					readoutlet.setPower(json.getDouble("elec_power"));
					readoutlet.setLimit(json.getInt("elec_limit"));
				//	readoutlet.setDate_time(json.getString("date_time"));
				//	readoutlet.setLimit(json.getDouble("elec_limit"));
				//	readoutlet.setDay(json.getInt("day"));
				//	readoutlet.setMonth(json.getInt("month"));
				//	readoutlet.setYear(json.getInt("year"));
	
					listOutlet.add(readoutlet);									
				}
			} catch (JSONException e) {
				error = "Error Convert to JSON or Error JSON Format: " + e.getMessage();
			}
			
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(dialog.isShowing()){
				dialog.dismiss();
			}
			
			if(result == false){
				Toast.makeText(MainView.this, error, Toast.LENGTH_LONG).show();
			}
			else{
				adapter = new OutletArrayAdapter(MainView.this, R.layout.list_layout_view, listOutlet);
				lvOutlet.setAdapter(adapter);
			}
		}
		
	}//End of private class ReadData
	
	private class OutletArrayAdapter extends ArrayAdapter<Outlet>{

		Context context;
		int resource;
		List<Outlet> outletList;
		
		public OutletArrayAdapter(Context context, int resource,List<Outlet> outletList) {
			super(context, resource, outletList);
			this.context = context;
			this.resource = resource;
			this.outletList = outletList;	
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View listItem = convertView;
			
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		    listItem = inflater.inflate(resource, parent, false);
		    
		    TextView outletid = (TextView) listItem.findViewById(R.id.OutletID);
		    TextView outletname = (TextView) listItem.findViewById(R.id.OutletName);


		    Outlet showoutlet = listOutlet.get(position);
		    
		    
		    outletid.setText("  Outlet ID : " + showoutlet.getId()+"  ");
		    outletname.setText("  Name : " + showoutlet.getOutletname()+"  ");
		    
		     			
			return listItem;
		}
			
	}//end of private class ProductArrayAdapter

	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View clickedView, final int pos, long id) {
		/*Outlet clickedOutlet = (Outlet) adapter.getItem(pos);
		int pro_id = clickedOutlet.getId();
		
		Intent in = new Intent(this, EditData.class);
		in.putExtra("pro_id", pro_id);
		startActivity(in);
		finish();	*/
		
		 AlertDialog.Builder builder = new AlertDialog.Builder(MainView.this);
         builder.setTitle("Select Day, Month, Year");
         builder.setItems(format, new DialogInterface.OnClickListener() {
            
         	@Override
             public void onClick(DialogInterface dialog, int which) {
               //  String selected = format[which];
              if(which == 0){           	               
            	  AlertDialog.Builder builder = new AlertDialog.Builder(MainView.this);
                  builder.setTitle("Select Day");
                  builder.setItems(day, new DialogInterface.OnClickListener() {
                	  
                	  public void onClick(DialogInterface dialog, int which) {
                		  
                		Outlet clickedOutlet = (Outlet) adapter.getItem(pos);
                  		int main_id = clickedOutlet.getId();
                  		double elec_power = clickedOutlet.getPower();
                  		int day ;
                  		Intent in = new Intent(getApplicationContext(),DayGraph.class);
                  		in.putExtra("main_id", main_id);
                  		in.putExtra("elec_power", elec_power);
                  		
                  		
                		switch(which){
                		
                		case 0 : day = 1; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 1 : day = 2; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 2 : day = 3; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 3 : day = 4; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 4 : day = 5; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 5 : day = 6; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 6 : day = 7; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 7 : day = 8; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 8 : day = 9; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 9 : day = 10; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 10 : day = 11; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 11 : day = 12; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 12 : day = 13; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 13 : day = 14; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 14 : day = 15; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 15 : day = 16; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 16 : day = 17; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 17 : day = 18; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 18 : day = 19; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 19 : day = 20; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 20 : day = 21; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 21 : day = 22; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 22 : day = 23; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 23 : day = 24; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 24 : day = 25; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 25 : day = 26; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 26 : day = 27; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 27 : day = 28; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 28 : day = 29; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 29 : day = 30; in.putExtra("day", day); startActivity(in); finish(); break;
                		case 30 : day = 31; in.putExtra("day", day); startActivity(in); finish(); break;
                		
                		}
                		  
                	  }
                 
                  
              });           	              
                  builder.create();
                  builder.show();
         	}
              if(which == 1){
            	  AlertDialog.Builder builder = new AlertDialog.Builder(MainView.this);
                  builder.setTitle("Select Month");
                  builder.setItems(month, new DialogInterface.OnClickListener() {
                	  
                	  public void onClick(DialogInterface dialog, int which) {
                		  
                		Outlet clickedOutlet = (Outlet) adapter.getItem(pos);
                  		int main_id = clickedOutlet.getId();	
                  		int month;
                  		Intent in = new Intent(getApplicationContext(),MonthGraph.class);
                  		in.putExtra("main_id", main_id);

                  		
                		switch(which){
                		
                		case 0 : month = 1; in.putExtra("month", month); startActivity(in); finish(); break;
                		case 1 : month = 2; in.putExtra("month", month); startActivity(in); finish(); break;
                		case 2 : month = 3; in.putExtra("month", month); startActivity(in); finish(); break;
                		case 3 : month = 4; in.putExtra("month", month); startActivity(in); finish(); break;
                		case 4 : month = 5; in.putExtra("month", month); startActivity(in); finish(); break;
                		case 5 : month = 6; in.putExtra("month", month); startActivity(in); finish(); break;
                		case 6 : month = 7; in.putExtra("month", month); startActivity(in); finish(); break;
                		case 7 : month = 8; in.putExtra("month", month); startActivity(in); finish(); break;
                		case 8 : month = 9; in.putExtra("month", month); startActivity(in); finish(); break;
                		case 9 : month = 10; in.putExtra("month", month); startActivity(in); finish(); break;
                		case 10 : month = 11; in.putExtra("month", month); startActivity(in); finish(); break;
                		case 11 : month = 12; in.putExtra("month", month); startActivity(in); finish(); break;
                		
                		}
                		  
                	  }
                 
                  
              });           	              
                  builder.create();
                  builder.show();
         	}
              if(which == 2){
            	  AlertDialog.Builder builder = new AlertDialog.Builder(MainView.this);
                  builder.setTitle("Select Year");
                  builder.setItems(year, new DialogInterface.OnClickListener() {
                	  
                	  public void onClick(DialogInterface dialog, int which) {
                		  
                		Outlet clickedOutlet = (Outlet) adapter.getItem(pos);
                  		int main_id = clickedOutlet.getId();	
                  		int year;
                  		Intent in = new Intent(getApplicationContext(),YearGraph.class);
                  		in.putExtra("main_id", main_id);

                  		
                		switch(which){
                		
                		case 0 : year = 2015; in.putExtra("year", year); startActivity(in); finish(); break;
                		case 1 : year = 2016; in.putExtra("year", year); startActivity(in); finish(); break;
                		case 2 : year = 2017; in.putExtra("year", year); startActivity(in); finish(); break;
                		case 3 : year = 2018; in.putExtra("year", year); startActivity(in); finish(); break;
                		case 4 : year = 2019; in.putExtra("year", year); startActivity(in); finish(); break;
                		case 5 : year = 2020; in.putExtra("year", year); startActivity(in); finish(); break;
                		case 6 : year = 2021; in.putExtra("year", year); startActivity(in); finish(); break;
                		case 7 : year = 2022; in.putExtra("year", year); startActivity(in); finish(); break;
                		case 8 : year = 2023; in.putExtra("year", year); startActivity(in); finish(); break;
                		case 9 : year = 2024; in.putExtra("year", year); startActivity(in); finish(); break;
                		
                		}
                		  
                	  }              
              });           	              
                  builder.create();
                  builder.show();
         	}
             }
         });           	              
         builder.create();
         builder.show();
	}
}
