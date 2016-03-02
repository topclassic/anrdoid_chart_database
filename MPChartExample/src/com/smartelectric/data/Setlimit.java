package com.smartelectric.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xxmassdeveloper.mpchartexample.R;

public class Setlimit extends Activity implements OnClickListener {
	
	  
	EditText etexteleclimit;
	TextView textname;
	Button btnUpdate, btnDelete;
	int main_id;
	String main_outletname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setlimit);
		textname = (TextView)findViewById(R.id.textname);
		etexteleclimit = (EditText)findViewById(R.id.etexteleclimit);
		btnUpdate = (Button)findViewById(R.id.btnUpdate);
		btnDelete = (Button)findViewById(R.id.btnDelete);
		btnUpdate.setOnClickListener(this);
		btnDelete.setOnClickListener(this);

		
		Bundle extras = getIntent().getExtras();
		if(extras == null){
			return;
		}
		
		main_id = extras.getInt("main_id");
		
		ReadData task1 = new ReadData();
		task1.execute(new String[]{"http://192.168.43.130/elec_setlimit.php?format=json&id=" + main_id});
		
	}
	
	Outlet outlet = new Outlet();
	
	private class ReadData extends AsyncTask<String, Void, Boolean>{

		private ProgressDialog dialog = new ProgressDialog(Setlimit.this);
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
										
				//	outlet.setId(json.getInt("outlet_id"));
				//	outlet.setOutletID(json.getString("outlet_id"));
					outlet.setOutletname(json.getString("outlet_name"));
					outlet.setPower(json.getDouble("elec_power"));
					outlet.setLimit(json.getInt("elec_limit")); 
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
				Toast.makeText(Setlimit.this, error, Toast.LENGTH_LONG).show();
			}
			else{		
				etexteleclimit.setText("" + outlet.getLimit()); 	
				textname.setText("Outlet Name: " + outlet.getOutletname());
			}
		}
		
	}//End of private class ReadData

	String updateTrigger = "";
	
	@Override
	public void onClick(View sender) {
		if(sender.getId() == R.id.btnUpdate){
			UpdateData taskUpdate = new UpdateData();
			updateTrigger = "Update";
			taskUpdate.execute(new String[]{"http://192.168.43.130/elec_edit.php?id=" + main_id});
			Intent in = new Intent(this, MainSetlimit.class);
			startActivity(in);
			finish();
		}
		else if(sender.getId() == R.id.btnDelete){
			Builder msgBox = new AlertDialog.Builder(this);
			msgBox.setMessage("Are you sure to clear text");
			msgBox.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					etexteleclimit.setText("");	
				}
			});
			msgBox.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
									
				}
			});
		
			msgBox.show();
			

		}
		
	}
	
	private class UpdateData extends AsyncTask<String, Void, Boolean>{
		private ProgressDialog dialog = new ProgressDialog(Setlimit.this);
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
					pairs.add(new BasicNameValuePair("txtElec_limit", etexteleclimit.getText().toString()));
					pairs.add(new BasicNameValuePair("txtOutlet_name", outlet.getOutletname().toString()));	
					
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
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(dialog.isShowing()){
				dialog.dismiss();
			}
			
			if(result == false){
				Toast.makeText(Setlimit.this, error, Toast.LENGTH_LONG).show();
			}
			else{
				if(is1 == null){
					Toast.makeText(Setlimit.this, "Sending Wrong Parameters", Toast.LENGTH_LONG).show();
				}
				else{
					Toast.makeText(Setlimit.this, "Edit Success", Toast.LENGTH_LONG).show();
					
				}
				
				
			}
		}
	}

}
