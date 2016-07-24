package ru.edocs_lab.test4.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import ru.edocs_lab.test4.request.RequestManager;

public class MainActivity extends Activity {

    private RequestManager requestManager;
    private InputMethodManager imm;
    private EditText etCount;
    private Button btnGo;
    private View progressContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestManager = RequestManager.getInstance();
        requestManager.init(this.getApplicationContext());
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        progressContainer = findViewById(R.id.progressContainer);
        progressContainer.setVisibility(View.INVISIBLE);
        etCount = (EditText)findViewById(R.id.etCount);
        etCount.setText(requestManager.getLastCount());
        if (etCount.requestFocus()) {
            etCount.postDelayed(new Runnable(){
                @Override
                public void run() {
                    etCount.requestFocus();
                    imm.showSoftInput(etCount, 0);
                }
            }, 100);
        }
        btnGo = (Button)findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etCount.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.no_count_msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (requestManager.checkConnection()) {
                    try {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    } catch (NullPointerException e) {/* ну и ладно, не очень-то и хотелось.. */}
                    progressContainer.setVisibility(View.VISIBLE);
                    requestManager.setInProcess(true);
                    new LoadPointsTask().execute(etCount.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, R.string.no_internets_msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (requestManager.isInProcess()) {
            Toast.makeText(this, R.string.back_press_msg, Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    private class LoadPointsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return requestManager.doRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressContainer.setVisibility(View.INVISIBLE);
            requestManager.setInProcess(false);
            if (!result.isEmpty()) {
                parseResult(result);
            } else {
                Toast.makeText(MainActivity.this, R.string.unsup_encod_msg, Toast.LENGTH_SHORT).show();
            }
        }

        private void parseResult(String result) {
            JSONObject respJsonObj;
            try {
                respJsonObj = new JSONObject(result);
                int resCode = respJsonObj.getInt("result");
                respJsonObj = respJsonObj.getJSONObject("response");
                switch (resCode) {
                    case 0:
                        JSONArray jsonArray = respJsonObj.getJSONArray("points");
                        Intent i = new Intent(MainActivity.this, DataActivity.class);
                        i.putExtra(DataActivity.EXTRA_JSON_STRING, jsonArray.toString());
                        startActivity(i);
                        break;
                    case -100:
                        Toast.makeText(MainActivity.this, R.string.wrong_params_msg, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        String errBase64 = respJsonObj.getString("message");
                        byte[] errBytes = Base64.decode(errBase64, Base64.DEFAULT);
                        try {
                            String errStr = new String(errBytes, "UTF-8");
                            Toast.makeText(MainActivity.this, errStr, Toast.LENGTH_SHORT).show();
                        } catch (UnsupportedEncodingException e) {
                            Toast.makeText(MainActivity.this, R.string.unsup_encod_msg, Toast.LENGTH_SHORT).show();
                        }
                }
            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, R.string.unsup_encod_msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
