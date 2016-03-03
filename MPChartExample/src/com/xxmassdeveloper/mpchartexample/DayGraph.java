
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
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Demonstrates the use of charts inside a ListView. IMPORTANT: provide a
 * specific height attribute for the chart inside your listview-item
 * 
 * @author Philipp Jahoda
 */
public class DayGraph extends DemoBase implements OnItemClickListener{
	ListView lvOutlet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ReadData task1 = new ReadData();
		task1.execute(new String[]{"http://192.168.43.130/elec_index.php?format=json"});
		
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_listview_chart);
        
        ListView lv = (ListView) findViewById(R.id.listView1);

        ArrayList<ChartItem> list = new ArrayList<ChartItem>();

        // 30 items
        for (int i = 0; i < 3; i++) {
            
                list.add(new LineChartItem(generateDataLine(i + 1), getApplicationContext()));         
        }      
        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(cda);
    }
    ArrayList<Outlet> listOutlet;
    OutletArrayAdapter adapter;
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
    
    /**
     * generates a random ChartData object with just one DataSet
     * 
     * @return
     */
    private LineData generateDataLine(int cnt) {
    	LineData cd; 
    	LineDataSet d1;
        ArrayList<Entry> e1 = new ArrayList<Entry>();

        for (int i = 0; i < 24; i++) {
            e1.add(new Entry((int) (Math.random() * 65) + 40, i));
        }
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
            d1 = new LineDataSet(e1, "Unit ");
            d1.setLineWidth(2.5f);
            d1.setCircleSize(4.5f);
            d1.setHighLightColor(Color.rgb(244, 117, 117));
            d1.setDrawValues(false);
       
            ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
            sets.add(d1);

            cd = new LineData(getMonths(), sets);
            
        }else

        d1 = new LineDataSet(e1, "Bath ");
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
				//	readoutlet.setLimit(json.getInt("elec_limit"));
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
				Toast.makeText(DayGraph.this, error, Toast.LENGTH_LONG).show();
			}
			else{
				adapter = new OutletArrayAdapter(DayGraph.this, R.layout.list_layout_edit, listOutlet);
				lvOutlet.setAdapter(adapter);
			}
		}
    }
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
    	}
    	public void onItemClick(AdapterView<?> parent, View clickedView, int pos, long id) {
    		Outlet clickedOutlet = (Outlet) adapter.getItem(pos);
    		//	main_id = clickedOutlet.getId();

    	}

}
