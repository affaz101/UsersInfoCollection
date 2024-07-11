package com.abu.users.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.abu.users.Controller.MyDBHelper;
import com.abu.users.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextInputEditText etName, etPhone, etAddress, etHost;
    RelativeLayout rlHostContainer;
    RadioGroup rgGender;
    LinearLayout btnSync, llCountContainer;
    TextView tvCount;
    AppCompatButton btnSave, btnResult;
    MyDBHelper myDBHelper;
    MyProgress myProgress;
    View v_switch;
    ImageView ivReset;

    public static final String ROOT_URL_BACKUP = "http://192.168.0.130:8080";
    public static  String ROOT_URL = "";
    public static  String GET_URL = "/api/api.php?all";
    public static  String POST_URL = "/api/api.php";

    public static final String CLOUD_DB_CONSTANT_NAME = "u_name";
    public static final String CLOUD_DB_CONSTANT_PHONE = "u_phone";
    public static final String CLOUD_DB_CONSTANT_GENDER = "u_gender";
    public static final String CLOUD_DB_CONSTANT_ADDRESS = "u_address";

    private SharedPreferences sharedPreferences;
    String savedData;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (TextInputEditText) findViewById(R.id.et_name_id);
        etPhone = (TextInputEditText) findViewById(R.id.et_phone_id);
        etAddress = (TextInputEditText) findViewById(R.id.et_addr_id);
        etAddress = (TextInputEditText) findViewById(R.id.et_addr_id);
        etHost = (TextInputEditText) findViewById(R.id.et_host_id);
        rlHostContainer = (RelativeLayout) findViewById(R.id.host_container_id);
        ivReset = (ImageView) findViewById(R.id.iv_reset_id);
        rgGender = (RadioGroup) findViewById(R.id.rg_gender_id);
        btnSave = (AppCompatButton) findViewById(R.id.btn_save_id);
        btnResult = (AppCompatButton) findViewById(R.id.btn_result_id);
        btnSync = (LinearLayout) findViewById(R.id.ll_btn_sync_id);
        llCountContainer = (LinearLayout) findViewById(R.id.ll_data_count_id);
        tvCount = (TextView)findViewById(R.id.tv_data_count_id);
        v_switch = (View) findViewById(R.id.view_switch_id);
        myDBHelper = new MyDBHelper(MainActivity.this);
        myProgress = new MyProgress(this);

        sharedPreferences = getSharedPreferences("my_host_pref", MODE_PRIVATE);
        savedData = sharedPreferences.getString("saved_data", "");
        etHost.setText(savedData);

        getRowCount();
        btnSync.setOnClickListener(this);
        btnResult.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        v_switch.setOnClickListener(this);
        ivReset.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_save_id){
            mBeginValidationAndInsertion();
        } else if (view.getId() == R.id.ll_btn_sync_id) {
            mSynchronizationBegin();
        } else if (view.getId() == R.id.btn_result_id) {
            Intent intent = new Intent(MainActivity.this, List.class);
            startActivity(intent);
        } else if (view.getId() == R.id.iv_reset_id) {
            etHost.setText(ROOT_URL_BACKUP);
        } else if (view.getId() == R.id.view_switch_id) {
            if (rlHostContainer.getVisibility() == View.VISIBLE){
                rlHostContainer.setVisibility(View.GONE);
            }else {
                rlHostContainer.setVisibility(View.VISIBLE);
            }
            saveDataToSharedPreferences(etHost.getText().toString().trim());
        }
    }
    private void saveDataToSharedPreferences(String data) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("saved_data", data);
        editor.apply();
    }

    private void mSynchronizationBegin(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SQLiteDatabase db = myDBHelper.getReadableDatabase();
                        String sql = "SELECT * FROM " + MyDBHelper.TABLE_NAME ;
                        Cursor cursor = db.rawQuery(sql, null);
                        if (cursor.moveToFirst()){
                            myProgress.show();

                            int c = cursor.getCount();
                            for (int i = 0; i<c;i++ ){
                                int id = cursor.getInt(0);
                                String nn = cursor.getString(1);
                                String pp = cursor.getString(2);
                                int gg = cursor.getInt(3);
                                String ad = cursor.getString(4);

                                mMySync(id, nn, pp, gg, ad);

                                cursor.moveToNext();
                            }

//                            myProgress.dismiss();
                        }

                        db.close();
                        cursor.close();

                    }
                });




            }
        }).start();




    };
    private void mMySync(int id, String n, String p, int g, String d){

        new Thread(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        sharedPreferences = getSharedPreferences("my_host_pref", MODE_PRIVATE);
                        savedData = sharedPreferences.getString("saved_data", "");
                        StringRequest request = new StringRequest(Request.Method.POST,
                                savedData+POST_URL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            Toast.makeText(MainActivity.this,  jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                                            mDeleteRowFromSqlite(id);
                                            myProgress.dismiss();

                                        } catch (JSONException e) {

                                        }
                                    }
                                },

                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        myProgress.dismiss();
                                        Toast.makeText(MainActivity.this, error.getMessage() , Toast.LENGTH_SHORT).show();
                                        Log.d("day1", "msg:d  ");
                                    }
                                }){
                            @Nullable
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<>();
                                map.put(CLOUD_DB_CONSTANT_NAME, n);
                                map.put(CLOUD_DB_CONSTANT_PHONE, p);
                                map.put(CLOUD_DB_CONSTANT_GENDER, String.valueOf(g));
                                map.put(CLOUD_DB_CONSTANT_ADDRESS, d);

                                return map;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                        requestQueue.add(request);

                    }
                });

            }
        }).start();


    };

    private void mDeleteRowFromSqlite(int id){
        try {
            String deleteQuery = "DELETE FROM "+MyDBHelper.TABLE_NAME+" WHERE id = "+id+";";
            SQLiteDatabase db = myDBHelper.getWritableDatabase();

            db.execSQL(deleteQuery);
//            Toast.makeText(this, "Row deleted successfully!", Toast.LENGTH_SHORT).show();
            db.close();
            getRowCount();
        }catch (Exception e){
            Toast.makeText(this, "problem occurs when deleting sqlite row data", Toast.LENGTH_SHORT).show();

        }

    };
    private void mBeginValidationAndInsertion(){
        String nm = etName.getText().toString().trim();
        String ph = etPhone.getText().toString().trim();
        RadioButton rb = (RadioButton)findViewById(rgGender.getCheckedRadioButtonId());
        int gn = mGetGenderNumber(rb);
        String adr = etAddress.getText().toString().trim();

        boolean isEverythingOK = false;
        isEverythingOK = mValidateFields(etName, false);
        isEverythingOK = mValidateFields(etAddress, false);
        isEverythingOK = mValidateFields(etPhone, true);

        if (isEverythingOK){
            mInsertData(nm, ph, gn, adr);
        }else {
            Toast.makeText(this, "Is all input ok? Check please.." , Toast.LENGTH_SHORT).show();
        }
    };
    private boolean mValidateFields(TextInputEditText et, boolean isPhone){
        boolean isOk = false;
        if (!isPhone){
            if (et.getText().toString().trim().isEmpty()){
                et.setError("Can't be Empty.");
            }else {
                isOk = true;
            }
        }else {
            if (et.getText().toString().trim().isEmpty()){
                et.setError("Can't be Empty.");
            } else if (et.getText().toString().trim().length() != 11)  {
                et.setError("Enter Valid Phone Number");
            }else {
                isOk = true;
            }

        }
        return isOk;

    };
    private int mGetGenderNumber(RadioButton rb){
        int g = 0;
        if (rb.getId() == R.id.rb_male_id){
            g = 1;
        } else if (rb.getId() == R.id.rb_female_id) {
            g=2;
        }else {
            g=3;
        }
        return g;
    };
    protected void mInsertData(String name, String phone, int gender, String address){
        if (!isPhoneExists(phone)){

            myProgress.show();
            new Thread(new Runnable() {
                @Override
                public void run() {



                    SQLiteDatabase db = myDBHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(MyDBHelper.COLUMN_NAME, name);
                    values.put(MyDBHelper.COLUMN_PHONE, phone);
                    values.put(MyDBHelper.COLUMN_GENDER, gender);
                    values.put(MyDBHelper.COLUMN_ADDRESS, address);

                    long result = db.insert(MyDBHelper.TABLE_NAME, null, values);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myProgress.dismiss();

                            if (result == -1){
                                Toast.makeText(MainActivity.this, "Data Insertion Failed!", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(MainActivity.this, "Data Insertion Successful!", Toast.LENGTH_SHORT).show();
                            }

                            getRowCount();
                        }
                    });

                    db.close();

                }
            }).start();

        }else {
            Toast.makeText(MainActivity.this, "This phone is already exist.", Toast.LENGTH_SHORT).show();
        }

    };
    public boolean isPhoneExists(String sphone) {

        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        String[] sp = {sphone};
        String sql = "SELECT id FROM " + MyDBHelper.TABLE_NAME + " WHERE " + MyDBHelper.COLUMN_PHONE + " = ?";
        Cursor cursor = db.rawQuery(sql, sp);
        long count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }
    public void getRowCount() {
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        int c = (int) DatabaseUtils.queryNumEntries(db, MyDBHelper.TABLE_NAME);
        tvCount.setText(String.valueOf(c));
        if (c>0){
            llCountContainer.setVisibility(View.VISIBLE);
        }else {
            llCountContainer.setVisibility(View.GONE);
        }
        db.close();
    }

    public class MyProgress extends ProgressDialog{


        public MyProgress(Context context) {
            super(context);
            setMessage("Saving Data...");
        }
    }
}