package com.example.ryanm.getdata;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView theresponse;
    ProgressBar progress;
    static final String apiurl = "http://10.0.0.57:9999";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        theresponse = (TextView)findViewById(R.id.response_view);
        progress = (ProgressBar) findViewById(R.id.progress);
        Button mybutton = (Button) findViewById(R.id.mybutton);
        mybutton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v){
                new RetrieveData().execute();
            }
        });
    }

    public static String cleanJson(String json)
    {
        json = json.substring(1,json.length()-1);
        json = json.replace("\\", "");
        return json;
    }
    class RetrieveData extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
            theresponse.setText("");
        }

        protected String doInBackground(Void... urls){
            try {
                URL url = new URL(apiurl + "/api/machine/getallmachines");
                System.out.println(url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                try{
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while((line = br.readLine()) != null)
                    {
                        sb.append(line);
                    }
                    br.close();
                    return sb.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "ERROR";
            }
            response = cleanJson(response);
            progress.setVisibility(View.GONE);
            String output = "";
            try{
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray array = object.getJSONArray("machines");
                System.out.println(array.getJSONObject(0).getString("Name"));
                output = output + array.getJSONObject(0).getString("Name");
                System.out.println(array.getString(1));
            }catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
            theresponse.setText(output);
        }
    }
}

