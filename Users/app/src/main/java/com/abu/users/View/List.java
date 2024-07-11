package com.abu.users.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abu.users.Controller.MyDBHelper;
import com.abu.users.Controller.MyRecyclerAdapter;
import com.abu.users.Model.ItemModel;
import com.abu.users.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class List extends AppCompatActivity {
    private RecyclerView rvList;
    private ArrayList<ItemModel> arrayList;
    private MyProgress myProgress;
    private String savedData;
    private SharedPreferences sharedPreferences;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        myProgress = new MyProgress(this);
        rvList = (RecyclerView) findViewById(R.id.rv_list_id);
        arrayList = new ArrayList<>();

        sharedPreferences = getSharedPreferences("my_host_pref", MODE_PRIVATE);
        savedData = sharedPreferences.getString("saved_data", "");

        mGetResult();


    }

    public void mGetResult(){
        myProgress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StringRequest request = new StringRequest(Request.Method.GET, savedData+MainActivity.GET_URL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONArray jsonArray = new JSONArray(response);
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject object = jsonArray.getJSONObject(i);
                                                Log.d("day2", jsonArray.getJSONObject(0)+",  p");
                                                ItemModel single = new ItemModel(object.getString(MyDBHelper.CLOUD_DB_COLUMN_NAME), object.getString(MyDBHelper.CLOUD_DB_COLUMN_PHONE), object.getInt(MyDBHelper.CLOUD_DB_COLUMN_GENDER), object.getString(MyDBHelper.CLOUD_DB_COLUMN_ADDRESS)  );
                                                arrayList.add(single);
                                            }
                                            MyRecyclerAdapter adapter = new MyRecyclerAdapter(List.this, arrayList);
                                            rvList.setLayoutManager(new LinearLayoutManager(List.this));
                                            rvList.setAdapter(adapter);
                                            myProgress.dismiss();

                                        } catch (JSONException e) {
                                            myProgress.dismiss();
                                            Toast.makeText(List.this, "Error1 :"+e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                        myProgress.dismiss();
                                        Toast.makeText(List.this, "Error2 :"+error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                        RequestQueue requestQueue = Volley.newRequestQueue(List.this);
                        requestQueue.add(request);

                    }
                });


            }
        }).start();

    };

    public class MyProgress extends ProgressDialog {


        public MyProgress(Context context) {
            super(context);
            setMessage("Getting Data...");
        }
    }
}