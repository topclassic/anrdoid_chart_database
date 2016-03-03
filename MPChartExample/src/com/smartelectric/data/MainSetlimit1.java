package com.smartelectric.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xxmassdeveloper.mpchartexample.R;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class MainSetlimit1 extends Activity implements OnItemClickListener {
	
	ListView lvOutlet;
	TextView show;
	Button select;
	int limit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_editdata);
		lvOutlet = (ListView)findViewById(R.id.ListViewOutlet);
		lvOutlet.setOnItemClickListener(this);


		
		ReadData task1 = new ReadData();
		task1.execute(new String[]{"http://192.168.43.130/elec_setlimit.php?format=json"});
	}
	Outlet outlet = new Outlet();
	ArrayList<Outlet> listOutlet;
	OutletArrayAdapter adapter;
	
	private class ReadData extends AsyncTask<String, Void, Boolean>{

		private ProgressDialog dialog = new ProgressDialog(MainSetlimit1.this);
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
				Toast.makeText(MainSetlimit1.this, error, Toast.LENGTH_LONG).show();
			}
			else{
				adapter = new OutletArrayAdapter(MainSetlimit1.this, R.layout.list_layout_setlimit, listOutlet);
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
		    		
		    TextView outletname = (TextView) listItem.findViewById(R.id.OutletName);
		    TextView elecpower = (TextView) listItem.findViewById(R.id.ElecPower);
		    TextView eleclimit = (TextView) listItem.findViewById(R.id.ElecLimit);

		    Outlet showoutlet = listOutlet.get(position);
		    
		    outletname.setText("  Name : " + showoutlet.getOutletname()+"  ");
		    elecpower.setText("  Unit: "+showoutlet.getPower());
		    eleclimit.setText("  Limit: "+showoutlet.getLimit());		 
		     			
			return listItem;
		}
			
	}//end of private class ProductArrayAdapter

	
	int main_id;
	@Override
	public void onItemClick(AdapterView<?> parent, View clickedView, int pos, long id) {
		Outlet clickedOutlet = (Outlet) adapter.getItem(pos);
		main_id = clickedOutlet.getId();
		numberPicker();		
	}
	
	//Dialog numberPicker
	private void numberPicker(){
		NumberPicker picker = new NumberPicker(this);
		picker.setMaxValue(20000);
		picker.setMinValue(0);
		NumberPicker.OnValueChangeListener myVal = new NumberPicker.OnValueChangeListener() {			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				//newVal is number select
				limit = newVal;
			}
		};
		picker.setOnValueChangedListener(myVal);
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(picker);
		builder.setTitle("Select Limit")
		   	   .setIcon(R.drawable.meter);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				outlet.setLimit(limit);
				UpdateData taskUpdate = new UpdateData();
				updateTrigger = "Update";
				taskUpdate.execute(new String[]{"http://192.168.43.130/elec_edit.php?id=" + main_id});
				Intent intent = new Intent(getApplicationContext(),MainSetlimit1.class);
				startActivity(intent); 
				finish();
			}
		});
		builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
	}
	String updateTrigger = "";	
	private class UpdateData extends AsyncTask<String, Void, Boolean>{
		private ProgressDialog dialog = new ProgressDialog(MainSetlimit1.this);
		private String error;
		
		String text = "";
		
		@Override
		protected void onPreExecute() {
			dialog.setMessage("Editting Data...");
			dialog.show();
		}
		
		InputStream is1;
		@Override
		protected Boolean doInBackground(String... urls) {
			for(String url: urls){
				try {
					ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
					pairs.add(new BasicNameValuePair("btnSubmit", updateTrigger));				
					pairs.add(new BasicNameValuePair("txtElec_limit",  outlet.getLimit()+"".toString()));					
					
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(url); 
					post.setEntity(new UrlEncodedFormEntity(pairs));
					HttpResponse response = client.execute(post);
					is1 = response.getEntity().getContent();
					
				} catch (ClientProtocolException e) {
					error = "ClientProtocolException: " + e.getMessage();
					return false;
				} catch (IOException e) {
					error = "ClientProtocolException: " + e.getMessage();
				}				
			}
			
			return true;	
		}	
		
		/*		@Override
		protected void onPostExecute(Boolean result) {
			if(dialog.isShowing()){
				dialog.dismiss();
			}
			
			if(result == false){
				Toast.makeText(MainSetlimit1.this, error, Toast.LENGTH_LONG).show();
			}
			else{
				if(is1 == null){
					Toast.makeText(MainSetlimit1.this, "Sending Wrong Parameters", Toast.LENGTH_LONG).show();
				}
				else{
					Toast.makeText(MainSetlimit1.this, "Edit Success", Toast.LENGTH_LONG).show();
					
				}		
			}
		}
		*/
	}
}
