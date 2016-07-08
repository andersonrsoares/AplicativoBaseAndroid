package br.com.anderson.aplicativobase;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RequestActivity extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Request");
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24px);


        textView = (TextView) findViewById(R.id.textView2);

      Button button = (Button) findViewById(R.id.buttonrequesttaskget);
      button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
                requesTaskGet();
          }
      });


        Button button2 = (Button) findViewById(R.id.buttonrequestvolleyget);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requesVolleyGet();
            }
        });
    }


    public void requesTaskGet(){
        RequestTask requestTask = new RequestTask(this, new RequestTask.RequestResult() {
            @Override
            public void onSucess(JSONObject result) {
                Log.d("return", result.toString());
                textView.setText(result.toString());
            }

            @Override
            public void onError(int code, String message) {
                Log.d("return", message);
                textView.setText(message);
            }
        },"GET");
        requestTask.execute("http://easysuport.devmaker.com.br/webservice/get/order/1/3/1");
    }

    public void requesVolleyGet(){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Buscando dados...");
        dialog.show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://easysuport.devmaker.com.br/webservice/get/order/1/3/1",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("return", response.toString());
                        textView.setText(response.toString());
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("return", "Error: " + error.getMessage());
                textView.setText(error.getMessage());
                dialog.dismiss();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(1000 * 15, 0, 1f));
        Volley.newRequestQueue(this).add(request);
    }



//    Map<String, String> params = new HashMap<>();
//    params.put("grant_type", "password");
//    params.put("client_secret", senha.getText().toString());
//    params.put("client_id", email.getText().toString());
//    String body = "";
//    try {
//        body = "grant_type=password&" +  "client_secret=" +senha.getText().toString() + "&client_id="+ email.getText().toString();
//    }catch (Exception ex){
//
//    }
//    CustomRequest request = new CustomRequest(Request.Method.POST,MyApplication.SERVER_URL +"/token",body, new Response.Listener<JSONObject>() {
//        @Override
//        public void onResponse(JSONObject response) {
//            Log.d(TAG, response.toString());
//            if(response.has("error")){
//                try {
//                    Toast.makeText(LoginActivity.this,response.getString("error_description"), Toast.LENGTH_SHORT).show();
//                }catch (Exception ex){
//                    Toast.makeText(LoginActivity.this,"Ocorreu um erro ao fazer o login.", Toast.LENGTH_SHORT).show();
//                }
//            }else{
//                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
//                preferences.edit().putString("token",response.toString());
//                openMain();
//            }
//            dialog.hide();
//        }
//    }, new Response.ErrorListener() {
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            VolleyLog.d(TAG, "Error: " + error.getMessage());
//            dialog.hide();
//            Toast.makeText(LoginActivity.this,"Ocorreu um erro ao fazer o login.", Toast.LENGTH_SHORT).show();
//        }
//    });
//    request.setRetryPolicy(new DefaultRetryPolicy(1000 * 15, 0, 1f));
//    Volley.newRequestQueue(this).add(request);
}
