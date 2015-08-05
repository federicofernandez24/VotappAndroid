package com.votapp.fede.votapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.votapp.fede.votapp.R;
import com.votapp.fede.votapp.api.ApiTypes;
import com.votapp.fede.votapp.controller.AppController;
import com.votapp.fede.votapp.bus.BusProvider;
import com.votapp.fede.votapp.api.ConsultorApi;
import com.votapp.fede.votapp.domain.User;
import com.votapp.fede.votapp.domain.utils.Constants;
import com.votapp.fede.votapp.events.LoadAuthenticateEvent;
import com.votapp.fede.votapp.events.LoadedErrorEvent;
import com.votapp.fede.votapp.events.LoadedMeEvent;
import com.votapp.fede.votapp.util.ResponseToString;

import org.apache.commons.codec.binary.Hex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit.Callback;
import retrofit.ResponseCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class Login extends ActionBarActivity {

    private Bus mBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mBus = BusProvider.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPause() {
        super.onPause();
        getBus().unregister(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        getBus().register(this);
    }

    // Use some kind of injection, so that we can swap in a mock for tests.
    // Here we just use simple getter/setter injection for simplicity.
    private Bus getBus() {
        if (mBus == null) {
            mBus = BusProvider.getInstance();
        }
        return mBus;
    }

    public void setBus(Bus bus) {
        mBus = bus;
    }

    public void onClickLogin(View view) {
        EditText userName = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);

        getBus().post(new LoadAuthenticateEvent(userName.getText().toString(), password.getText().toString()));

    }

    @Subscribe
    public void onLogin(LoadAuthenticateEvent loadAuthenticateEvent) {


        User user = new User();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String pass = loadAuthenticateEvent.getPassword();
            md.update(pass.getBytes("UTF-8"));
            byte[] digest = md.digest();

            String passHex = new String(Hex.encodeHex(digest));

            user.setUsername(loadAuthenticateEvent.getUsername());
            user.setPassword(passHex);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Callback callback = new ResponseCallback() {
            @Override
            public void success(Response response) {

                ResponseToString rsto = new ResponseToString();

                String result = rsto.procesarBody(response);

                mBus.post(new LoadedMeEvent(result));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                mBus.post(new LoadedErrorEvent(retrofitError));
            }
        };
        ConsultorApi mApi = (ConsultorApi) AppController.getApiOfType(ApiTypes.USER_API);
        mApi.login(user, callback);
    }
    @Subscribe
    public void onLogin(LoadedMeEvent loadedMeEvent) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Constants.USER_TOKEN_RESPONSE, loadedMeEvent.getValor());
        startActivity(intent);
    }
}
