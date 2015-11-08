package com.votapp.fede.votapp;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.votapp.fede.votapp.api.ApiTypes;
import com.votapp.fede.votapp.api.ConsultorApi;
import com.votapp.fede.votapp.bus.BusProvider;
import com.votapp.fede.votapp.controller.AppController;
import com.votapp.fede.votapp.domain.Opinion;
import com.votapp.fede.votapp.domain.User;
import com.votapp.fede.votapp.domain.utils.Constants;
import com.votapp.fede.votapp.events.CreateOpinionEvent;
import com.votapp.fede.votapp.events.LoadAuthenticateEvent;
import com.votapp.fede.votapp.events.LoadedErrorEvent;
import com.votapp.fede.votapp.events.LoadedMeEvent;
import com.votapp.fede.votapp.util.ResponseToString;
import com.votapp.fede.votapp.util.SystemUiHider;
import com.votapp.fede.votapp.views.HomeActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit.Callback;
import retrofit.ResponseCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Encuesta extends Activity {


    private Bus mBus;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    /** Votapp
     *
     */
    private JSONObject encuesta;
    private int idEncuesta;
    private boolean porCandidato;
    private JSONArray candidatos;
    private JSONArray partidos;
    private boolean preguntarEdad;
    private boolean preguntarSexo;
    private boolean preguntarLista;
    private boolean preguntarNivelEstudio;
    private boolean preguntarTrabaja;
    private boolean preguntarIngresos;
    private JSONArray listas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_encuesta);
        setupActionBar();

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        Intent intent = getIntent();
        String jsonString = intent.getStringExtra(Constants.ENCUESTA_JSON);
        candidatos = new JSONArray();
        partidos = new JSONArray();
        listas = new JSONArray();
        try {
            encuesta = new JSONObject(jsonString);
            idEncuesta = encuesta.getInt("id");
            porCandidato = encuesta.getBoolean("porCandidato");
            candidatos = encuesta.getJSONArray("dataCandidatos");
            partidos = encuesta.getJSONArray("dataPartidos");
            preguntarLista = encuesta.getBoolean("preguntarLista");
            preguntarEdad = encuesta.getBoolean("preguntarEdad");
            preguntarSexo = encuesta.getBoolean("preguntarSexo");
            preguntarNivelEstudio = encuesta.getBoolean("preguntarNivelEstudio");
            preguntarTrabaja = encuesta.getBoolean("preguntarSiTrabaja");
            preguntarIngresos = encuesta.getBoolean("preguntarIngresos");

            TextView pregunta_principal =  (TextView) findViewById(R.id.pregunta_principal);
            Spinner spinner_principal = (Spinner) findViewById(R.id.combo_principal);
            int total = 0;
            if (porCandidato){
                total = candidatos.length();
                pregunta_principal.setText("Usted que candidato piensa votar?");
                String[] candidatos_spinner = new String[total];
                for (int i = 0; i < total; i++) {
                    JSONObject json = candidatos.getJSONObject(i);
                    candidatos_spinner[i] = json.getString("nombre");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, candidatos_spinner );
                spinner_principal.setAdapter(adapter);
            } else {
                total = partidos.length();
                pregunta_principal.setText("Usted que partido piensa votar?");
                String[] partidos_spinner = new String[total];
                for (int i = 0; i < total; i++) {
                    JSONObject json = partidos.getJSONObject(i);
                    partidos_spinner[i] = json.getString("nombre");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, partidos_spinner );
                spinner_principal.setAdapter(adapter);
            }
            spinner_principal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        boolean enBlanco = false;
                        if (porCandidato) {
                            if (position < candidatos.length()) {
                                listas = candidatos.getJSONObject(position).getJSONArray("dataListas");
                            } else {
                                enBlanco = true;
                            }
                        } else {
                            if (position < partidos.length()) {
                                listas = partidos.getJSONObject(position).getJSONArray("dataListas");
                            } else {
                                enBlanco = true;
                            }
                        }
                        TextView pregunta_lista = (TextView) findViewById(R.id.pregunta_lista);
                        Spinner spinner_lista = (Spinner) findViewById(R.id.combo_lista);
                        if (preguntarLista) {
                            if(enBlanco){
                                String[] listas_spinner = new String[1];
                                listas_spinner[1] = new String("En Blanco");
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(Encuesta.this, android.R.layout.simple_spinner_dropdown_item, listas_spinner);
                                spinner_lista.setAdapter(adapter);
                            }else {
                                    int total = listas.length();
                                    pregunta_lista.setText("Usted que lista piensa votar?");
                                    String[] listas_spinner = new String[total + 1];
                                    for (int i = 0; i < total; i++) {
                                        JSONObject json = listas.getJSONObject(i);
                                        listas_spinner[i] = String.valueOf(json.getInt("numero"));
                                    }
                                    listas_spinner[total] = new String("No sabe");
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Encuesta.this, android.R.layout.simple_spinner_dropdown_item, listas_spinner);
                                    spinner_lista.setAdapter(adapter);

                            }
                        } else {
                            pregunta_lista.setVisibility(View.INVISIBLE);
                            spinner_lista.setVisibility(View.INVISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            TextView pregunta_lista = (TextView) findViewById(R.id.pregunta_lista);
            Spinner spinner_lista = (Spinner) findViewById(R.id.combo_lista);
            if (preguntarLista){
                total = listas.length();
                pregunta_lista.setText("Usted que lista piensa votar?");
                String[] listas_spinner = new String[total+2];
                for (int i = 0; i < total; i++) {
                    JSONObject json = listas.getJSONObject(i);
                    listas_spinner[i] = String.valueOf(json.getInt("numero"));
                }
                listas_spinner[total] = new String("No sabe");
                listas_spinner[total+1] = new String("En Blanco");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listas_spinner );
                spinner_lista.setAdapter(adapter);
            } else {
                pregunta_lista.setVisibility(View.INVISIBLE);
                spinner_lista.setVisibility(View.INVISIBLE);
            }
            TextView pregunta_edad = (TextView) findViewById(R.id.pregunta_edad);
            EditText input_edad = (EditText) findViewById(R.id.input_edad);
            if (preguntarEdad){
                pregunta_edad.setText("Cuántos años tiene?");
            } else {
                pregunta_edad.setVisibility(View.INVISIBLE);
                input_edad.setVisibility(View.INVISIBLE);
            }
            TextView pregunta_sexo = (TextView) findViewById(R.id.pregunta_sexo);
            Spinner combo_sexo = (Spinner) findViewById(R.id.combo_sexo);
            if (preguntarSexo){
                pregunta_sexo.setText("Sexo :");
                String[] sexo_spinner = new String[] {"Masculino","Femenino","Otro"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sexo_spinner );
                combo_sexo.setAdapter(adapter);
            } else {
                pregunta_sexo.setVisibility(View.INVISIBLE);
                combo_sexo.setVisibility(View.INVISIBLE);
            }
            TextView pregunta_nivel = (TextView) findViewById(R.id.pregunta_nivel);
            Spinner combo_nivel = (Spinner) findViewById(R.id.combo_nivel);
            if (preguntarNivelEstudio){
                pregunta_nivel.setText("Nivel de Estudios :");
                String[] nivel_spinner = new String[] {"Primaria","Secundaria","Terciaria","Blanco","NS_NC"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nivel_spinner );
                combo_nivel.setAdapter(adapter);
            } else {
                pregunta_nivel.setVisibility(View.INVISIBLE);
                combo_nivel.setVisibility(View.INVISIBLE);
            }
            TextView pregunta_trabaja = (TextView) findViewById(R.id.pregunta_trabaja);
            Spinner combo_trabaja = (Spinner) findViewById(R.id.combo_trabaja);
            if (preguntarTrabaja){
                pregunta_trabaja.setText("Trabaja :");
                String[] trabaja_spinner = new String[] {"Si","No"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, trabaja_spinner );
                combo_trabaja.setAdapter(adapter);
            } else {
                pregunta_trabaja.setVisibility(View.INVISIBLE);
                combo_trabaja.setVisibility(View.INVISIBLE);
            }
            TextView pregunta_ingresos = (TextView) findViewById(R.id.pregunta_ingreso);
            EditText input_ingresos = (EditText) findViewById(R.id.input_ingreso);
            if (preguntarIngresos){
                pregunta_ingresos.setText("Ingresos :");
            } else {
                pregunta_ingresos.setVisibility(View.INVISIBLE);
                input_ingresos.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

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

    public void onClickAltaOpinion(View view) {
        Opinion opinion = new Opinion();

        opinion.setIdEncuesta(this.idEncuesta);
        Spinner spinner_principal = (Spinner) findViewById(R.id.combo_principal);
        String valor = (String) spinner_principal.getSelectedItem();
        if (porCandidato){
            int idCandidato = getIdCandidato(valor);
            opinion.setIdCandidato(idCandidato);
        } else {
            int idPartido = getIdPartido(valor);
            opinion.setIdPartido(idPartido);
        }
        Spinner spinner_lista = (Spinner) findViewById(R.id.combo_lista);
        if (preguntarLista){
            int lista;
            String seleccion = (String) spinner_lista.getSelectedItem();
            if(seleccion == null){
                lista = -1;
            } else if(seleccion.equalsIgnoreCase("No sabe")){
                lista = -1;
            } else if(seleccion.equalsIgnoreCase("En Blanco")){
                lista = 0;
            } else {
                lista = getIdLista(Integer.valueOf(seleccion));
            }
            opinion.setIdLista(lista);
        }
        EditText input_edad = (EditText) findViewById(R.id.input_edad);
        if (preguntarEdad){
            int edad = Integer.parseInt( input_edad.getText().toString() );
            opinion.setEdad(edad);
        }
        Spinner combo_sexo = (Spinner) findViewById(R.id.combo_sexo);
        if (preguntarSexo){
            String sexo = (String) combo_sexo.getSelectedItem();
            opinion.setSexo((sexo!=null)?sexo:"");
        }
        Spinner combo_nivel = (Spinner) findViewById(R.id.combo_nivel);
        if (preguntarNivelEstudio){
            String nivelEstudio = (String) combo_nivel.getSelectedItem();
            opinion.setNivelEstudio((nivelEstudio!=null)?nivelEstudio:"");
        }
        Spinner combo_trabaja = (Spinner) findViewById(R.id.combo_trabaja);
        if (preguntarTrabaja){
            String trabaja = (String) combo_trabaja.getSelectedItem();
            if (trabaja!=null){
                if(trabaja.equalsIgnoreCase("Si")){
                    opinion.setTrabaja(true);
                }else{
                    opinion.setTrabaja(false);
                }
            }
        }
        EditText input_ingresos = (EditText) findViewById(R.id.input_ingreso);
        if (preguntarIngresos){
            double ingresos = Integer.parseInt(input_ingresos.getText().toString());
            opinion.setIngresos(ingresos);
        }

        getBus().post(new CreateOpinionEvent(opinion));
    }

    private int getIdLista(int nro_lista) {
        int total = listas.length();
        try {
            for (int i = 0; i < total; i++) {
                JSONObject json = listas.getJSONObject(i);
                if(nro_lista==json.getInt("numero")){
                    return json.getInt("id");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getIdPartido(String valor) {
        int total = partidos.length();
        try {
        for (int i = 0; i < total; i++) {
            JSONObject json = partidos.getJSONObject(i);
            if(valor.equals(json.getString("nombre"))){

                return json.getInt("id");
            }
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getIdCandidato(String valor) {
        int total = candidatos.length();
        try {
            for (int i = 0; i < total; i++) {
                JSONObject json = candidatos.getJSONObject(i);
                if(valor.equals(json.getString("nombre"))){

                    return json.getInt("id");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // TODO: If Settings has multiple levels, Up should navigate up
            // that hierarchy.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onLogin(CreateOpinionEvent createOpinionEvent) {


        Opinion opinion = createOpinionEvent.getOpinion();

        Callback callback = new ResponseCallback() {
            @Override
            public void success(Response response) {

                Toast.makeText(Encuesta.this,"Opinion creada correctamente.", Toast.LENGTH_LONG);

                mBus.post(new LoadedMeEvent("OK"));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(Encuesta.this, "No se pudo dar de alta la eleccion, intente nuevamente.", Toast.LENGTH_LONG);
            }
        };
        ConsultorApi mApi = (ConsultorApi) AppController.getApiOfType(ApiTypes.USER_API);
        mApi.altaOpinion(opinion, callback);
    }
    @Subscribe
    public void onCreateOk(LoadedMeEvent loadedMeEvent) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
