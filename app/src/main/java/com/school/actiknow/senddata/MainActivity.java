package com.school.actiknow.senddata;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText etMessage;
    Button btSendMessage;
    String Message1;

    ArrayList<String> myArrayList=new ArrayList<String>();
    ArrayList<String> myAList=new ArrayList<String>();

    JSONArray jsonarray = null;
    private ProgressDialog pDialog;
    private String URL_ITEMS4 = "http://actiknow-demo.com/offline/submit";

  //  private String URL_ITEMS4 = "http://actiknow-demo.com/myschoolapi/v1/admin/home";


    int flag;
    int len;


    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        int flag2 = sharedpreferences.getInt("flag", 0);
        int size = sharedpreferences.getInt("size", 0);

        for (int j = 0; j < size; j++) {
            myAList.add(sharedpreferences.getString("val" + j, ""));
            String  Message2 =  myAList.get(j);

            if (flag2 == 1) {
                new SendMessage().execute(String.valueOf(Message2));

            }
            sharedpreferences= getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            sharedpreferences.edit().remove("flag").commit();
        }

            etMessage = (EditText) findViewById(R.id.etmessage);
            btSendMessage = (Button) findViewById(R.id.btSend);
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


            btSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message1 = etMessage.getText().toString();

                    Toast.makeText(MainActivity.this, Message1, Toast.LENGTH_SHORT).show();
                    if (NetworkConnection.isNetworkAvailable(MainActivity.this)) {
                        new SendMessage().execute(String.valueOf(Message1));
                        flag = 0;

                    }
                    else
                    {
                        flag = 1;
                        myArrayList.add(Message1);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        for (int i = 0; i < myArrayList.size(); i++) {
                            editor.putString("val" + i, myArrayList.get(i));
                            editor.putInt("flag", flag);
                        }
                        editor.putInt("size", myArrayList.size());
                        editor.commit();
                    }
                }
            });
    }

    private class SendMessage extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... arg) {
            String text = arg[0];

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("text", text));

            ServiceHandler serviceClient = new ServiceHandler();
            String json = serviceClient.makeServiceCall(URL_ITEMS4, ServiceHandler.POST, params);


            Log.d("url: ", "> " + URL_ITEMS4);
            Log.d("aadsd", "karman :" + json);

            if (json != null) {
                try {
                    Log.d("try", "in the try");
                    JSONObject jsonObj = new JSONObject(json);
                    Log.d("jsonObject", "new json Object");

                    jsonarray = jsonObj.getJSONArray("details");
                    Log.d("json aray", "" + jsonarray);
                    len = jsonarray.length();
                    Log.d("len", "get array length=" + len);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        HashMap<String, String> map = new HashMap<String, String>();

                        JSONObject c = jsonarray.getJSONObject(i);
                    }
                } catch (JSONException e) {
                    Log.d("catch", "in the catch");
                    e.printStackTrace();
                }
            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater ().inflate (R.menu.menu_main, menu);
        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
            case android.R.id.home:
               break;
            case R.id.logout:
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected (item);
    }

}
