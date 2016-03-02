package com.smartelectric.data;

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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.xxmassdeveloper.mpchartexample.R;

public class Cut_Out extends Activity implements OnItemClickListener {
	
	ListView lvOutlet;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_editdata);
		lvOutlet = (ListView)findViewById(R.id.ListViewOutlet);
		lvOutlet.setOnItemClickListener(this);
		
		ReadData task1 = new ReadData();
		task1.execute(new String[]{"http://192.168.43.130/indexsmartelectric.php?format=json"});
	}
	
	ArrayList<Outlet> listOutlet;
	OutletArrayAdapter adapter;
	
	private class ReadData extends AsyncTask<String, Void, Boolean>{

		private ProgressDialog dialog = new ProgressDialog(Cut_Out.this);
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
					readoutlet.setId(json.getInt("pro_id"));
					readoutlet.setOutletID(json.getString("outletid"));
					readoutlet.setOutletname(json.getString("outletname"));
					readoutlet.setPower(json.getInt("elecpower"));
					readoutlet.setLimit(json.getInt("eleclimit")); 
	
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
				Toast.makeText(Cut_Out.this, error, Toast.LENGTH_LONG).show();
			}
			else{
				adapter = new OutletArrayAdapter(Cut_Out.this, R.layout.list_layout_cut_out, listOutlet);
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
		    TextView elecpower = (TextView) listItem.findViewById(R.id.ElecPower);
		    TextView eleclimit = (TextView) listItem.findViewById(R.id.ElecLimit);

		    Outlet showoutlet = listOutlet.get(position);
		    

		    outletid.setText("  Outlet ID : " + showoutlet.getOutletID()+"  ");
		    outletname.setText("  Name : " + showoutlet.getOutletname()+"  ");
		    elecpower.setText("  POWER: "+showoutlet.getPower());
		    eleclimit.setText("  Limit: "+showoutlet.getLimit());		 
		     			
			return listItem;
		}
			
	}//end of private class ProductArrayAdapter

	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View clickedView, int pos, long id) {
		Outlet clickedOutlet = (Outlet) adapter.getItem(pos);
		int pro_id = clickedOutlet.getId();
		
		Intent in = new Intent(this, EditData.class);
		in.putExtra("pro_id", pro_id);
		startActivity(in);
		finish();	
	}
}
