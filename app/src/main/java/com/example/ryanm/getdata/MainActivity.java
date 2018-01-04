package com.example.ryanm.getdata;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
    static final String apiurl = "http://10.0.0.238";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            LinearLayout layout = (LinearLayout)findViewById(R.id.data_layout);
            response = cleanJson(response);
            try{
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray array = object.getJSONArray("machines");
                layout.removeAllViews();
                for(int i = 0; i < array.length(); i++)
                {
                    TextView temp = new TextView(getApplicationContext());
                    temp.setGravity(Gravity.CENTER);
                    temp.setPadding(0,5,0,5);
                    temp.setBackgroundColor(Color.BLUE);
                    temp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    temp.setText(array.getJSONObject(i).getString("Name"));
                    layout.addView(temp);
                }
            }catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
    }
}

