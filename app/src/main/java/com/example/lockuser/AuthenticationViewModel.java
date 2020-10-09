package com.example.lockuser;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Patterns;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import static com.example.lockuser.helpers.Constants.SIGN_IN_URL;
import static com.example.lockuser.helpers.Constants.SIGN_UP_URL;

public class AuthenticationViewModel extends AndroidViewModel {

    public enum AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,          // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    private SharedPreferences sharedPreferences = getApplication().getSharedPreferences("preferences", Context.MODE_PRIVATE);

    public final MutableLiveData<AuthenticationState> authenticationState =
            new MutableLiveData<>();

    public AuthenticationViewModel(Application application) {
        super(application);

        if (jwtExists()) {
            authenticationState.setValue(AuthenticationState.AUTHENTICATED);
        } else {
            authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
        }

        fetchIpAddressLocalServerAndStoreItToSharedPreferences();
    }


    private boolean jwtExists() {
        return sharedPreferences.contains("access_token");
    }

    private String signInUrl() {
        return sharedPreferences.getString("ip_server", "") + "login/";
    }

    private String signUpUrl() {
        return sharedPreferences.getString("ip_server", "") + "signup/";
    }

    public void signIn(String email, String password) {
        authenticate(email, password, signInUrl());
    }

    public void signUp(String email, String password) {
        authenticate(email, password, signUpUrl());
    }

    public void authenticate(String email_address, String password, String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplication());
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("email_address", email_address);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String jwt = (String) response.get("access_token");
                    sharedPreferences.edit().putString("access_token", jwt).apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                authenticationState.setValue(AuthenticationState.AUTHENTICATED);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    Toast.makeText(getApplication(), jsonError, Toast.LENGTH_LONG).show();
                }

                authenticationState.setValue(AuthenticationState.INVALID_AUTHENTICATION);
            }
        });

        queue.add(jsonObjectRequest);


    }

    public void signOut() {
        sharedPreferences.edit().remove("access_token").apply();
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
    }

    public void fetchIpAddressLocalServerAndStoreItToSharedPreferences() {
        String jsonUrl = "https://api.jsonbin.io/b/5f526b43993a2e110d3e53d6/1";

        RequestQueue queue = Volley.newRequestQueue(getApplication());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, jsonUrl, null,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String ipServer = (String) response.get("ip_server");
                    sharedPreferences.edit().putString("ip_server", ipServer).apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    Toast.makeText(getApplication(), jsonError, Toast.LENGTH_LONG).show();
                }
            }
        });

        queue.add(jsonObjectRequest);

    }
}
