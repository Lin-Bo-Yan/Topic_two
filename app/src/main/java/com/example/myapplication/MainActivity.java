package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView address,temp,refresh_temperature,timestamp;
    OkHttpClient client = new OkHttpClient().newBuilder().build();
    SharedPreferences sp;
    String locationSp,temperatureSp,timestampSp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temp = findViewById(R.id.temp);
        address = findViewById(R.id.address);
        timestamp = findViewById(R.id.timestamp);
        sendPOST();

        refresh_temperature = findViewById(R.id.refresh_temperature);
        refresh_temperature.setOnClickListener(view -> {
            sendPOST();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sp=getApplicationContext().getSharedPreferences("MyUser", Context.MODE_PRIVATE);
        locationSp = sp.getString("location","");
        temperatureSp = sp.getString("temperature","");
        timestampSp = sp.getString("timestamp","");
        address.setText(locationSp);
        temp.setText(temperatureSp);
        timestamp.setText(timestampSp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sp.edit().clear();
        sp.edit().commit();
    }

    private void sendPOST() {
        String url = "https://ms81api.lale.fun/interview/v1/temperature";
        Map<String, String> map = new HashMap();
        map.put("location","Hsinchu");
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new JSONObject(map).toString());
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.v("joe", "Failure:  "+e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String json = response.body().string();
                        //Log.v("joe", "Successful: "+json);
                        parseReponseAndSharedPreferences(json);
                    }
                }
            });
        }catch (Exception e){
            Log.e("joe", "Exception: "+e.toString());
        }
    }
    private void parseReponseAndSharedPreferences(String json){
        try {
            JSONObject root =new JSONObject(json);
            String location=root.getString("location");
            String temperature=root.getString("temperature");
            String timestamp=root.getString("timestamp");
            sharedPreferences(location,temperature,timestamp);
        }catch (JSONException e){
            Log.e("joe",e.toString());
        }
    }
    private void sharedPreferences(String location,String temperature,String timestamp){
        sp = getSharedPreferences("MyUser", MODE_PRIVATE);
        sp.edit().putString("location", location).apply();
        sp.edit().putString("temperature", temperature).apply();
        sp.edit().putString("timestamp", timestamp).apply();
        sp.edit().commit();
    }
}





//MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//RequestBody body = RequestBody.create(JSON, "{\"location\":\"Hsinchu\"}");

//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("location", "Hsinchu")
//                .build();