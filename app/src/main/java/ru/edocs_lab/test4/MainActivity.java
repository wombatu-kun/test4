package ru.edocs_lab.test4;

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

public class MainActivity extends Activity {

    private InputMethodManager imm;
    private EditText etCount;
    private Button btnGo;
    private View progressContainer;
    private boolean inProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inProgress = false;
        progressContainer = findViewById(R.id.progressContainer);
        progressContainer.setVisibility(View.INVISIBLE);
        etCount = (EditText)findViewById(R.id.etCount);
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
                if (NetworkUtils.checkConnection(MainActivity.this)) {
                    try {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    } catch (NullPointerException e) {/* ну и ладно, не очень-то и хотелось.. */}
                    progressContainer.setVisibility(View.VISIBLE);
                    inProgress = true;
                    new LoadPointsTask().execute(etCount.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, R.string.no_internets_msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class LoadPointsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return NetworkUtils.getResponseString(params[0], MainActivity.this.getApplicationContext());
        }
        @Override
        protected void onPostExecute(String result) {
            progressContainer.setVisibility(View.INVISIBLE);
            inProgress = false;
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

    @Override
    public void onBackPressed() {
        if (inProgress) {
            Toast.makeText(this, R.string.back_press_msg, Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }

    }
}
