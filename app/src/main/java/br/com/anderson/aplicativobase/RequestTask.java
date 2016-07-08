package br.com.anderson.aplicativobase;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DevMaker on 7/6/16.
 */


public class RequestTask extends AsyncTask<String, String, JSONObject> {

    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    ProgressDialog dialog;
    Context context;
    RequestResult requestResult;
    String method= "GET";
    HashMap<String,String> params = new HashMap<>();
    JSONObject jsonPost;

    public HashMap<String,String> getHeaders(){
        HashMap<String,String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/x-www-form-urlencoded;");
        headers.put("charset", "UTF-8");
        return headers;
    }

    public RequestTask(Context context, RequestResult requestResult,String method){
        this.requestResult = requestResult;
        this.context = context;
        this.method = method;
    }

    public RequestTask(Context context, RequestResult requestResult, String method,HashMap<String,String> params){
        this.requestResult = requestResult;
        this.context = context;
        this.method = method;
        this.params = params;
    }

    public RequestTask(Context context, RequestResult requestResult, String method,JSONObject json){
        this.requestResult = requestResult;
        this.context = context;
        this.method = method;
        this.jsonPost = json;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Buscando dados...");
        dialog.show();
    }

    private String body(){
        if(params != null && params.entrySet().size() > 0){
            StringBuilder encodedParams = new StringBuilder();
            try {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    encodedParams.append(URLEncoder.encode(entry.getKey(), DEFAULT_PARAMS_ENCODING));
                    encodedParams.append('=');
                    encodedParams.append(URLEncoder.encode(entry.getValue(), DEFAULT_PARAMS_ENCODING));
                    encodedParams.append('&');
                }
                return encodedParams.toString();
            } catch (UnsupportedEncodingException uee) {
                throw new RuntimeException("Encoding not supported: " + DEFAULT_PARAMS_ENCODING, uee);
            }
        }
        else{
            if(jsonPost != null)
                return  jsonPost.toString();
        }
        return "";
    }

    @Override
    protected JSONObject doInBackground(String... data) {
        JSONObject jsonObject = new JSONObject();
        try {

            String request = data[0];
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(method);

            for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
                connection.setRequestProperty(entry.getKey(),entry.getValue());
            }

            connection.setUseCaches(false);
            String datasend= body();
            if(!datasend.isEmpty()){
                // String value =  "login=" + URLEncoder.encode("devmaker","UTF-8") + "&senha="+ URLEncoder.encode("devmaker3348+","UTF-8") ;
                DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
                Log.i("requestlog", "Login data: " + datasend);//
                wr.writeBytes(body());
                wr.flush();
                wr.close();
            }
            String line;
            if (connection.getResponseCode() > 199  && connection.getResponseCode() < 300) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                while((line = br.readLine()) != null){
                    Log.i("Requesstlog",line);
                    jsonObject = new JSONObject(line);
                }
            }
            else
            {
                jsonObject.put("code",connection.getResponseCode());
                try {
                    JSONObject json = null;
                    try {
                        String msg =    connection.getResponseMessage();
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8"));
                        while((line = br.readLine()) != null){
                            Log.i("requestlog",line);
                            try {
                                json = new JSONObject(line);
                                jsonObject.put("erro",json);
                            }catch (Exception ex){
                                jsonObject.put("erro",msg);
                            }
                        }

                    }catch (Exception ex){
                        jsonObject.put("erro","Ocorreu um erro");
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }
        catch (UnknownHostException e){
            try {
                jsonObject.put("erro","Verifique a conexão com a internet.");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            return jsonObject;
        }catch (Exception e){
            try {
                jsonObject.put("erro","Ocorreu um erro tente novamente.");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            return jsonObject;
        }
        return jsonObject;
    }
    @Override
    protected void onPostExecute(JSONObject response) {
        super.onPostExecute(response);
        dialog.dismiss();
        if(response.has("erro")){
            try {
                requestResult.onError(response.getInt("code"),response.toString());
            }catch (Exception ex){
                requestResult.onError(0,response.toString());
            }
        }
        else
            requestResult.onSucess(response);

    }

    public interface RequestResult{
        public void onSucess(JSONObject result);
        public void onError(int code,String message);
    }
}
