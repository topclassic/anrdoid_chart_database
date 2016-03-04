package com.xxmassdeveloper.mpchartexample;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.smartelectric.data.Outlet;
import com.xxmassdeveloper.mpchartexample.listviewitems.ChartItem;
import com.xxmassdeveloper.mpchartexample.listviewitems.LineChartItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DayGraph extends Activity{
	float watt;
	float unit;
	float bath;
	ChartDataAdapter cda;
	ArrayList<ChartItem> list;
	ListView lv;
	int main_id;
	String main_outletname;
	int limit;
	int test;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	    WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    setContentView(R.layout.activity_listview_chart);
	//	setContentView(R.layout.setlimit);
		
		lv = (ListView) findViewById(R.id.listView1);
		
		

		
		Bundle extras = getIntent().getExtras();
		if(extras == null){
			return;
		}
		
		main_id = extras.getInt("main_id");
		
		ReadData task1 = new ReadData();
		task1.execute(new String[]{"http://192.168.43.130/elec_data.php?format=json&id=" + main_id});
				

	}
	
	Outlet outlet = new Outlet();
	
	private class ReadData extends AsyncTask<String, Void, Boolean>{

		private ProgressDialog dialog = new ProgressDialog(DayGraph.this);
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
						
			try {
				JSONArray jArray = new JSONArray(text);
				for(int i=0; i<jArray.length(); i++){
					JSONObject json = jArray.getJSONObject(i);
										
					outlet.setId(json.getInt("outlet_id"));
				//	outlet.setOutletID(json.getString("outlet_id"));
				//	outlet.setOutletname(json.getString("outlet_name"));
				//	outlet.setPower(json.getDouble("elec_power"));
				//	outlet.setLimit(json.getInt("elec_limit")); 
					outlet.setWatt(json.getDouble("watt"));
					outlet.setUnit(json.getDouble("unit"));
					outlet.setDate_time(json.getString("date_time"));
				//	outlet.setDay(json.getInt("day"));
				//	outlet.setMonth(json.getInt("month"));
				//	outlet.setYear(json.getInt("year"));
					
				
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
				Toast.makeText(DayGraph.this, error, Toast.LENGTH_LONG).show();
			}
			else{	
				unit = (float)outlet.getUnit();
				watt = (float)outlet.getWatt();
				
				for(int i = 0; i <= unit; i++){
				if(i <= 5){
					bath = 0;
				}else if(i >= 6 && i <= 15){
					bath = bath +(float) 1.3576;
				}else if(i >= 16 && i <= 25){
					bath = bath +(float) 1.5445;
				}else if(i >= 26 && i<= 35){
					bath = bath +(float) 1.7968;
				}else if(i >= 36 && i <= 100){
					bath = bath +(float) 2.1800;
				}else if(i >= 101 && i <= 150){
					bath = bath +(float) 2.734;
				}else if(i >= 151 && i <= 400){
					bath = bath +(float) 2.7781;
				}else if(i>400){
					bath = bath +(float) 2.9780;
				}
				}
				
				list = new ArrayList<ChartItem>();
				   
		        for (int j = 0; j < 3; j++) {		            
		               list.add(new LineChartItem(generateDataLine(j + 1), getApplicationContext()));
		          
		        }	
		        cda = new ChartDataAdapter(getApplicationContext(), list);
		        lv.setAdapter(cda);	
			}
		}
		
	}//End of private class ReadData


	private void alertDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Not Outlet"+outlet.getDate_time())		
			   .setMessage("Plaese connect outlet"+outlet.getId()+"\n"+bath)
			   .setIcon(R.drawable.ic_launcher);
		builder.show();
	}					
	
	/** adapter that supports 3 different item types */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {
        
        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }
        
        @Override
        public int getItemViewType(int position) {           
            // return the views type
            return getItem(position).getItemType();
        }
        
        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }
    private LineData generateDataLine(int cnt) {
    	LineData cd; 
    	LineDataSet d1;
        ArrayList<Entry> e1 = new ArrayList<Entry>();
        ArrayList<Entry> e2 = new ArrayList<Entry>();
        ArrayList<Entry> e3 = new ArrayList<Entry>();
        
            e1.add(new Entry(watt, 1));
            e1.add(new Entry(watt, 2)); 
            e1.add(new Entry(watt, 3)); 
            e1.add(new Entry(watt, 4)); 
            e1.add(new Entry(watt, 5)); 
            e1.add(new Entry(watt, 6)); 
            e1.add(new Entry(watt, 7)); 
            e1.add(new Entry(watt, 8)); 
            e1.add(new Entry(watt, 9)); 
            e1.add(new Entry(watt, 10)); 
            e1.add(new Entry(watt, 11)); 
            e1.add(new Entry(watt, 12)); 
            e1.add(new Entry(watt, 13)); 
            e1.add(new Entry(watt, 14)); 
            e1.add(new Entry(watt, 15)); 
            e1.add(new Entry(watt, 16)); 
            e1.add(new Entry(watt, 17)); 
            e1.add(new Entry(watt, 18)); 
            e1.add(new Entry(watt, 19)); 
            e1.add(new Entry(watt, 20)); 
            e1.add(new Entry(watt, 21)); 
            e1.add(new Entry(watt, 22)); 
            e1.add(new Entry(watt, 23)); 
            e1.add(new Entry(watt, 24)); 
            

            e2.add(new Entry(unit, 1));
            e2.add(new Entry(unit, 2)); 
            e2.add(new Entry(unit, 3)); 
            e2.add(new Entry(unit, 4)); 
            e2.add(new Entry(unit, 5)); 
            e2.add(new Entry(unit, 6)); 
            e2.add(new Entry(unit, 7)); 
            e2.add(new Entry(unit, 8)); 
            e2.add(new Entry(unit, 9)); 
            e2.add(new Entry(unit, 10)); 
            e2.add(new Entry(unit, 11)); 
            e2.add(new Entry(unit, 12)); 
            e2.add(new Entry(unit, 13)); 
            e2.add(new Entry(unit, 14)); 
            e2.add(new Entry(unit, 15)); 
            e2.add(new Entry(unit, 16)); 
            e2.add(new Entry(unit, 17)); 
            e2.add(new Entry(unit, 18)); 
            e2.add(new Entry(unit, 19)); 
            e2.add(new Entry(unit, 20)); 
            e2.add(new Entry(unit, 21)); 
            e2.add(new Entry(unit, 22)); 
            e2.add(new Entry(unit, 23)); 
            e2.add(new Entry(unit, 24)); 
           

            e3.add(new Entry(bath, 1));
            e3.add(new Entry(bath, 2)); 
            e3.add(new Entry(bath, 3)); 
            e3.add(new Entry(bath, 4)); 
            e3.add(new Entry(bath, 5)); 
            e3.add(new Entry(bath, 6)); 
            e3.add(new Entry(bath, 7)); 
            e3.add(new Entry(bath, 8)); 
            e3.add(new Entry(bath, 9)); 
            e3.add(new Entry(bath, 10)); 
            e3.add(new Entry(bath, 11)); 
            e3.add(new Entry(bath, 12)); 
            e3.add(new Entry(bath, 13)); 
            e3.add(new Entry(bath, 14)); 
            e3.add(new Entry(bath, 15)); 
            e3.add(new Entry(bath, 16)); 
            e3.add(new Entry(bath, 17)); 
            e3.add(new Entry(bath, 18)); 
            e3.add(new Entry(bath, 19)); 
            e3.add(new Entry(bath, 20)); 
            e3.add(new Entry(bath, 21)); 
            e3.add(new Entry(bath, 22)); 
            e3.add(new Entry(bath, 23)); 
            e3.add(new Entry(bath, 24)); 

        if(cnt == 1){
            d1 = new LineDataSet(e1, "Watt ");
            d1.setLineWidth(2.5f);
            d1.setCircleSize(4.5f);
            d1.setHighLightColor(Color.rgb(244, 117, 117));
            d1.setDrawValues(false);
       
            ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
            sets.add(d1);

            cd = new LineData(getMonths(), sets);
        	
        }else if(cnt == 2){
            d1 = new LineDataSet(e2, "Unit ");
            d1.setLineWidth(2.5f);
            d1.setCircleSize(4.5f);
            d1.setHighLightColor(Color.rgb(244, 117, 117));
            d1.setDrawValues(false);
       
            ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
            sets.add(d1);

            cd = new LineData(getMonths(), sets);
            
        }else 

        d1 = new LineDataSet(e3, "Bath ");
        d1.setLineWidth(2.5f);
        d1.setCircleSize(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);
   
        ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
        sets.add(d1);

        cd = new LineData(getMonths(), sets);
        
        return cd;
    }
    


    private ArrayList<String> getMonths() {

        ArrayList<String> m = new ArrayList<String>();
        m.add("1");
        m.add("2");
        m.add("3");
        m.add("4");
        m.add("5");
        m.add("6");
        m.add("7");
        m.add("8");
        m.add("9");
        m.add("10");
        m.add("11");
        m.add("12");
        m.add("13");
        m.add("14");
        m.add("15");
        m.add("16");
        m.add("17");
        m.add("18");
        m.add("19");
        m.add("20");
        m.add("21");
        m.add("22");
        m.add("23");
        m.add("24");
        return m;
    }
	

}
