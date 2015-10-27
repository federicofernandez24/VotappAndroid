package com.votapp.fede.votapp.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.votapp.fede.votapp.Encuesta;
import com.votapp.fede.votapp.R;
import com.votapp.fede.votapp.api.ApiTypes;
import com.votapp.fede.votapp.api.ConsultorApi;
import com.votapp.fede.votapp.bus.BusProvider;
import com.votapp.fede.votapp.controller.AppController;
import com.votapp.fede.votapp.domain.Emergencia;
import com.votapp.fede.votapp.domain.utils.Constants;
import com.votapp.fede.votapp.events.EmergencyEvent;
import com.votapp.fede.votapp.events.GetAyudaEvent;
import com.votapp.fede.votapp.events.GetEncuestasEvent;
import com.votapp.fede.votapp.events.LoadedErrorEvent;
import com.votapp.fede.votapp.events.LoadedMeEvent;
import com.votapp.fede.votapp.util.ResponseToString;
import com.votapp.fede.votapp.views.fragments.NavigationDrawerFragment;
import com.votapp.fede.votapp.views.fragments.ayuda;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import retrofit.Callback;
import retrofit.ResponseCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, LocationListener {

    private Bus mBus;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    /*
    * App data:
    * - token devuelto por el login.
    * */
    private String userToken;
    private String celular;
    private String username;
    private int idUser;
    private JSONObject payloadToken;
    private int consultoraID;
    private JSONArray encuestasJson;
    private Location location;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        final Button button = (Button) findViewById(R.id.button_emergencia);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Emergencia!")
                        .setMessage("Quieres mandar un mensaje de ayuda a tu consultora?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getBus().post(new EmergencyEvent());
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            }
        });
        // Obtengo los datos del intent que llamo a esta activity despues del Login
        Intent intent = getIntent();
        username = "Unknown";
        if (intent.getStringExtra(Constants.USER_TOKEN_RESPONSE) != null) {
            userToken = intent.getStringExtra(Constants.USER_TOKEN_RESPONSE);
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constants.USER_TOKEN_RESPONSE, userToken);
            editor.commit();
        }
        else {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            userToken = sharedPref.getString(Constants.USER_TOKEN_RESPONSE, "");
        }
        String[] jwtParts = userToken.split("\\.");

        //byte[] jwtHeader = Base64.decodeBase64(jwtParts[0]);
        byte[] data = Base64.decode(jwtParts[1], Base64.DEFAULT);

        //byte[] jwtPayload = Base64.decodeBase64(jwtParts[1]);
        //Jwt<Header,String> hola = Jwts.parser().parsePlaintextJwt(userToken);
        //String body = hola.getBody();
        try {
            String text = new String(data, "UTF-8");
            payloadToken = new JSONObject(text);
            consultoraID = payloadToken.getInt("idConsultora");
            username = payloadToken.getString("username");
            idUser = payloadToken.getInt("idUsuario");
            celular = payloadToken.getString("numConsultora");
            if (celular.equalsIgnoreCase("null")) {
                celular = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //idConsultora = Integer.parseInt(valor);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        TextView message = (TextView) findViewById(R.id.usermsg_welcome);
        message.setText("Bienvenido " + username + "!");

        mBus = BusProvider.getInstance();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.home_content, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.menu_option1);
                getBus().post(new GetEncuestasEvent(consultoraID));
                break;
            case 2:
                mTitle = getString(R.string.menu_option2);
                getBus().post(new GetAyudaEvent());
                break;
            case 3:
                SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
        getBus().post(new GetEncuestasEvent(consultoraID));
    }
    @Subscribe
    public void onInitialize(GetEncuestasEvent getEncuestasEvent) {
        Callback callback = new ResponseCallback() {
            @Override
            public void success(Response response) {

                ResponseToString rsto = new ResponseToString();

                String result = rsto.procesarBody(response);
                try {
                    encuestasJson = new JSONArray(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mBus.post(new LoadedMeEvent("OK"));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                mBus.post(new LoadedErrorEvent(retrofitError));
            }
        };
        ConsultorApi mApi = (ConsultorApi) AppController.getApiOfType(ApiTypes.USER_API);
        mApi.getEncuestasByIdConsultora(consultoraID, callback);

    }
    @Subscribe
    public void onLogin(LoadedMeEvent loadedMeEvent) {


        int total = encuestasJson.length();
        ListView lista = (ListView) findViewById(R.id.encuestasList);
        try {

            String[] stringarray = new String[total];
            for (int i = 0; i < total; i++) {
                JSONObject json = encuestasJson.getJSONObject(i);
                stringarray[i] = json.getString("nombre");
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringarray);
            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(HomeActivity.this, Encuesta.class);
                    try {
                        intent.putExtra(Constants.ENCUESTA_JSON, encuestasJson.getJSONObject(position).toString());
                        intent.putExtra(Constants.CALLER, "HOME");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }
            });
            lista.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Subscribe
    public void onAlarm(EmergencyEvent emergencyEvent) {

        Emergencia locationEmergencia = new Emergencia();
        locationEmergencia.setLongitud((location!=null)?location.getLongitude():0);
        locationEmergencia.setLatitud((location!=null)?location.getLatitude():0);
        locationEmergencia.setIdEncuestador(idUser);
        locationEmergencia.setIdConsultora(consultoraID);

        if(celular != null) {
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(celular, null, "AYUDA! Encuestador " + this.username + " en peligro." +
                    " Lugar: (latitud, longitud):(" +
                    //location.getLatitude()+","+location.getLongitude()+
                    ")", null, null);
            Toast.makeText(getApplicationContext(),"SMS ENVIADO PIDIENDO AYUDA",Toast.LENGTH_SHORT).show();
        }
        Callback callback = new ResponseCallback() {
            @Override
            public void success(Response response) {

                Toast.makeText(getApplicationContext(),"ALERTA RECIBIDA",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(getApplicationContext(),"error: Corre por tu vida",Toast.LENGTH_SHORT).show();
            }
        };
        ConsultorApi mApi = (ConsultorApi) AppController.getApiOfType(ApiTypes.USER_API);
        mApi.alertarEmergencia(locationEmergencia, callback);


    }

    @Subscribe
    public void onAlarm(GetAyudaEvent ayudaEvent) {
       // Intent intent = new Intent(HomeActivity.this, Ayuda.class);
        //startActivity(intent);
        /*android.app.FragmentManager fm = getFragmentManager();
        android.app.FragmentTransaction ft = fm.beginTransaction();
        fm.beginTransaction();
        ayuda fragOne = new ayuda();
        ft.show(fragOne);*/

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.home_content, new ayuda())
                .commit();

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

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_menu_index, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((HomeActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
