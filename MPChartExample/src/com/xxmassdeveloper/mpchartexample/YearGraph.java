
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

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.smartelectric.data.Outlet;
import com.xxmassdeveloper.mpchartexample.listviewitems.ChartItem;
import com.xxmassdeveloper.mpchartexample.listviewitems.LineChartItem;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Demonstrates the use of charts inside a ListView. IMPORTANT: provide a
 * specific height attribute for the chart inside your listview-item
 * 
 * @author Philipp Jahoda
 */
public class YearGraph extends DemoBase {
	float watt;
	float unit;
	float bath;
	ChartDataAdapter cda;
	ArrayList<BarData> list;
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

		private ProgressDialog dialog = new ProgressDialog(YearGraph.this);
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
				Toast.makeText(YearGraph.this, error, Toast.LENGTH_LONG).show();
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
				
				list = new ArrayList<BarData>();
				   
		        for (int i = 0; i < 3; i++) {		            
		        		list.add(generateData(i + 1));
		          
		        }	
		        cda = new ChartDataAdapter(getApplicationContext(), list);
		        lv.setAdapter(cda);	
			}
		}
		
	}//End of private class ReadData
    private class ChartDataAdapter extends ArrayAdapter<BarData> {

        private Typeface mTf;

        public ChartDataAdapter(Context context, List<BarData> objects) {
            super(context, 0, objects);

            mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            BarData data = getItem(position);

            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();

                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item_barchart, null);
                holder.chart = (BarChart) convertView.findViewById(R.id.chart);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // apply styling
            data.setValueTypeface(mTf);
            data.setValueTextColor(Color.BLACK);
            holder.chart.setDescription("");
            holder.chart.setDrawGridBackground(false);

            XAxis xAxis = holder.chart.getXAxis();
            xAxis.setPosition(XAxisPosition.BOTTOM);
            xAxis.setTypeface(mTf);
            xAxis.setDrawGridLines(false);
            
            YAxis leftAxis = holder.chart.getAxisLeft();
            leftAxis.setTypeface(mTf);
            leftAxis.setLabelCount(5, false);
            leftAxis.setSpaceTop(15f);
            
            YAxis rightAxis = holder.chart.getAxisRight();
            rightAxis.setTypeface(mTf);
            rightAxis.setLabelCount(5, false);
            rightAxis.setSpaceTop(15f);

            // set data
            holder.chart.setData(data);
            
            // do not forget to refresh the chart
//            holder.chart.invalidate();
            holder.chart.animateY(700, Easing.EasingOption.EaseInCubic);

            return convertView;
        }

        private class ViewHolder {

            BarChart chart;
        }
    }

    /**
     * generates a random ChartData object with just one DataSet
     * 
     * @return
     */
    private BarData generateData(int cnt) {
    	 BarData cd;
    	 BarDataSet d;
    	 ArrayList<BarEntry> e1 = new ArrayList<BarEntry>();
         ArrayList<BarEntry> e2 = new ArrayList<BarEntry>();
         ArrayList<BarEntry> e3 = new ArrayList<BarEntry>();
         
             e1.add(new BarEntry(watt, 0));
             e1.add(new BarEntry(watt, 1)); 
             e1.add(new BarEntry(watt, 2)); 
             e1.add(new BarEntry(watt, 3)); 
             e1.add(new BarEntry(watt, 4)); 
             e1.add(new BarEntry(watt, 5)); 
             e1.add(new BarEntry(watt, 6)); 
             e1.add(new BarEntry(watt, 7)); 
             e1.add(new BarEntry(watt, 8)); 
             e1.add(new BarEntry(watt, 9)); 
             e1.add(new BarEntry(watt, 10)); 
             e1.add(new BarEntry(watt, 11)); 
             
             e2.add(new BarEntry(unit, 0));
             e2.add(new BarEntry(unit, 1)); 
             e2.add(new BarEntry(unit, 2)); 
             e2.add(new BarEntry(unit, 3)); 
             e2.add(new BarEntry(unit, 4)); 
             e2.add(new BarEntry(unit, 5)); 
             e2.add(new BarEntry(unit, 6)); 
             e2.add(new BarEntry(unit, 7)); 
             e2.add(new BarEntry(unit, 8)); 
             e2.add(new BarEntry(unit, 9)); 
             e2.add(new BarEntry(unit, 10)); 
             e2.add(new BarEntry(unit, 11)); 

             e3.add(new BarEntry(bath, 0));
             e3.add(new BarEntry(bath, 1)); 
             e3.add(new BarEntry(bath, 2)); 
             e3.add(new BarEntry(bath, 3)); 
             e3.add(new BarEntry(bath, 4)); 
             e3.add(new BarEntry(bath, 5)); 
             e3.add(new BarEntry(bath, 6)); 
             e3.add(new BarEntry(bath, 7)); 
             e3.add(new BarEntry(bath, 8)); 
             e3.add(new BarEntry(bath, 9)); 
             e3.add(new BarEntry(bath, 10)); 
             e3.add(new BarEntry(bath, 11)); 
            
        if(cnt == 1){
        d = new BarDataSet(e1, "Watt ");    
        d.setBarSpacePercent(20f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setBarShadowColor(Color.rgb(203, 203, 203));
        
        ArrayList<BarDataSet> sets = new ArrayList<BarDataSet>();
        sets.add(d);
        
        cd = new BarData(getMonths(), sets);
        
        }else if(cnt == 2){
            d = new BarDataSet(e2, "Unit ");    
            d.setBarSpacePercent(20f);
            d.setColors(ColorTemplate.VORDIPLOM_COLORS);
            d.setBarShadowColor(Color.rgb(203, 203, 203));
            
            ArrayList<BarDataSet> sets = new ArrayList<BarDataSet>();
            sets.add(d);
            
            cd = new BarData(getMonths(), sets);
        }else
        	
        d = new BarDataSet(e3, "Bath ");    
        d.setBarSpacePercent(20f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setBarShadowColor(Color.rgb(203, 203, 203));
        
        ArrayList<BarDataSet> sets = new ArrayList<BarDataSet>();
        sets.add(d);
        
        cd = new BarData(getMonths(), sets);
        return cd;
    }

    private ArrayList<String> getMonths() {

        ArrayList<String> m = new ArrayList<String>();
        m.add("Jan");
        m.add("Feb");
        m.add("Mar");
        m.add("Apr");
        m.add("May");
        m.add("Jun");
        m.add("Jul");
        m.add("Aug");
        m.add("Sep");
        m.add("Okt");
        m.add("Nov");
        m.add("Dec");
        return m;
    }
}
