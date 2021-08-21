package com.bradchao.hiskioweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);
    }

    public void fetchOpendata(View view) {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                "https://opendata.cwb.gov.tw/api/v1/rest/datastore/F-C0032-001?Authorization=CWB-E291D06C-CF52-470E-B20A-4D1C3257FEAE",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("bradlog", response);
                        parseJSON(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("bradlog", error.toString());
                    }
                }
        );
        queue.add(request);
    }

    private void parseJSON(String json){
        try{
            JSONObject root = new JSONObject(json);
            if (root.getBoolean("success")){
                JSONObject records = root.getJSONObject("records");
                String datasetDescription = records.getString("datasetDescription");
                Log.v("bradlog", datasetDescription);
                JSONArray location = records.getJSONArray("location");
                for (int i=0; i<location.length(); i++){
                    JSONObject area = location.getJSONObject(i);
                    String locationName = area.getString("locationName");
                    Log.v("bradlog", locationName);
                    JSONArray weatherElement = area.getJSONArray("weatherElement");
                    for (int j=0; j<weatherElement.length(); j++){
                        JSONObject element = weatherElement.getJSONObject(j);
                        String elementName = element.getString("elementName");
                        if (elementName.equals("Wx")){
                            Log.v("bradlog", "天氣概況");
                            JSONArray times = element.getJSONArray("time");
                            for (int k=0; k<times.length(); k++){
                                JSONObject time = times.getJSONObject(k);
                                String startTime = time.getString("startTime");
                                String endTime = time.getString("endTime");
                                JSONObject parameter = time.getJSONObject("parameter");
                                String parameterName = parameter.getString("parameterName");
                                Log.v("bradlog", startTime+" ~ " + endTime + " : " + parameterName);
                            }
                        }else if (elementName.equals("PoP")){
                            Log.v("bradlog", "降雨機率");
                        }
                    }
                    Log.v("bradlog", "---------");
                }
            }
        }catch (Exception e){
            Log.v("bradlog", e.toString());
        }
    }
}


