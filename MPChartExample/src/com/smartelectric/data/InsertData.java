package com.smartelectric.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xxmassdeveloper.mpchartexample.R;

public class InsertData extends Activity implements View.OnClickListener{

    private EditText editTextAdd;
    private Button buttonAdd;

    private static final String ADD = "http://192.168.43.130/smartelectric.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insertdata);

        editTextAdd = (EditText) findViewById(R.id.editTextAdd);
        buttonAdd = (Button) findViewById(R.id.buttonAdd);

        buttonAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonAdd){
            addOutlet();
        }
    }

    private void addOutlet() {
        String outletname = editTextAdd.getText().toString().trim();
        Add(outletname);
    }

    private void Add(String outletname) {
        String urlSuffix = "?outletname="+outletname; 
        
        class addOutletname extends AsyncTask<String, Void, String>{

            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(InsertData.this, "Please Wait",null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(ADD+s);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String result;

                    result = bufferedReader.readLine();

                    return result;
                }catch(Exception e){
                    return null;
                }
            }
        }

        addOutletname add = new addOutletname();
        add.execute(urlSuffix);
    }
}



