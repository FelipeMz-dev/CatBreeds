package com.example.catbreeds.services;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiHelper {
    private static final String URL = "https://api.thecatapi.com/v1/";
    public Context currentContext;
    public String accessToken;

    private MutableLiveData<ApiResult> apiResult = new MutableLiveData<>();
    public LiveData<ApiResult> getResult() {
        return apiResult;
    }

    public ApiHelper(Context currentContext){
        this.currentContext = currentContext;
        SharedPreferences preferences = currentContext.getSharedPreferences("API-KEY", MODE_PRIVATE);
        this.accessToken = preferences.getString("x-api-key", "bda53789-d59e-46cd-9bc4-2936630fde39");
    }

    public void GetData(){
        RequestQueue requestQueue = Volley.newRequestQueue(this.currentContext);
        String url_breeds = URL + "breeds";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url_breeds,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        apiResult.setValue(new ApiResult(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        apiResult.setValue(null);
                        Log.e("error-getData", error.toString());
                        Toast.makeText(currentContext, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-api-key", "bda53789-d59e-46cd-9bc4-2936630fde39");
                return params;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }
    public void GetImageBreed(String referenceImage){
        RequestQueue requestQueue = Volley.newRequestQueue(this.currentContext);
        String url_breeds = URL + "images/" + referenceImage;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url_breeds,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        apiResult.setValue(new ApiResult(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        apiResult.setValue(null);
                        Log.e("error-getImagesBreeds", error.toString());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-api-key", "bda53789-d59e-46cd-9bc4-2936630fde39");
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
    public void GetFlags(String flags_url){
        RequestQueue requestQueue = Volley.newRequestQueue(this.currentContext);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                flags_url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        apiResult.setValue(new ApiResult(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        apiResult.setValue(null);
                        Log.e("error-getFlags", error.toString());
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

}
