package com.example.firebasecursonotificacao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.firebasecursonotificacao.Util.DialogProgress;
import com.example.firebasecursonotificacao.Util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Switch switch_Sports, switch_Politica;

    private EditText edtxt_NomeUsuario;
    private Button btn_CadastrarToken, btn_Enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        switch_Sports = (Switch) findViewById(R.id.switch_Sports);
        switch_Politica = (Switch) findViewById(R.id.switch_Politica);

        edtxt_NomeUsuario = (EditText) findViewById(R.id.edtxt_NomeUsuario);

        btn_CadastrarToken = (Button) findViewById(R.id.btn_CadastrarToken);
        btn_Enviar = (Button) findViewById(R.id.btn_EnviarNotificacao);

        btn_CadastrarToken.setOnClickListener(this);
        btn_Enviar.setOnClickListener(this);

        //switch_Sports.setOnClickListener(this);
        //switch_Politica.setOnClickListener(this);

        configurationSwitch();

        // colocando o topic no postman para enviar mensagem para usuario
        FirebaseMessaging.getInstance().subscribeToTopic("usuariosaplicativo");

        switch_Sports.setOnCheckedChangeListener(this);
        switch_Politica.setOnCheckedChangeListener(this);

        // gerando token
        FirebaseInstallations.getInstance().getToken(false)
                .addOnCompleteListener(new OnCompleteListener<InstallationTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<InstallationTokenResult> task) {
                if(!task.isSuccessful()){
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult().getToken();
                Log.d("jonejaf_token", "jonejaf_token: " + token);
            }
        });

    }

    private void configurationSwitch() {

        String topicSports = Util.getTopic(getBaseContext(), "sports", "sports");
        String topicPolicy = Util.getTopic(getBaseContext(), "Politica", "Politica");

        if(!topicSports.isEmpty()){  // verificar se esta vazio
            switch_Sports.setChecked(true);
        }else{
            switch_Sports.setChecked(false);
        }

        if(!topicPolicy.isEmpty()){  // verificar se esta vazio
            switch_Politica.setChecked(true);
        }else{
            switch_Politica.setChecked(false);
        }
    }

    // ======================================= TRATAMENTO DE CLICK SWITCHS =======================================

    @Override
    public void onCheckedChanged(CompoundButton compoundBtn, boolean b) {

        switch (compoundBtn.getId()){

            case R.id.switch_Sports:

                switchSports();
                break;

            case R.id.switch_Politica:

                switchPolicy();
                break;
        }
    }

    // ======================================= TRATAMENTO DE CLICK =======================================

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            /*

            case R.id.switch_Sports:

                switchSports();

            break;

            case R.id.switch_Politica:

                switchPolicy();
            break;

            */

            case R.id.btn_CadastrarToken:
                getToken_0();
                //Toast.makeText(getBaseContext(), "Button Cadastrar Token", Toast.LENGTH_LONG).show();
            break;

            case R.id.btn_EnviarNotificacao:
                startNotification();
                //Toast.makeText(getBaseContext(), "Button Enviar Notificacao", Toast.LENGTH_LONG).show();
            break;

        }
    }

    private void startNotification() {

        JSONObject notification = new JSONObject();
        JSONObject dados = new JSONObject();

        String name = edtxt_NomeUsuario.getText().toString();

        try { // enviando mensagem personalizada usuario

            notification.put("to", "/topics/usuariosaplicativo");

            dados.put("mensagem", "Oi meu nome e gui");
            dados.put("titulo", "Modulo 3");
            dados.put("nome", name);
            dados.put("urlimagem", "https://cdn-images-1.medium.com/max/800/1*AoarrKQjCE0zVJkxl9za8Q.jpeg");

            notification.put("data", dados);

            String site = "https://fcm.googleapis.com/fcm/send";

            sendNotification(site, notification);

        }catch(Exception e){

        }

    }

    private void getToken_0(){

        String authorization = "162857612724";
        String firebase = "FCM";

        new Thread(new Runnable() { // salvando token no fcm
            @Override
            public void run() {

                //   try {
                //String token = FirebaseInstanceId.getInstance().getToken(authorization, firebase);
                //   }catch (IOException e){
                //Log.d("token: --> ", token);
                //   }

                FirebaseMessaging.getInstance().deleteToken();

                // gerando token
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.w("erro ", "Fetching FCM registration token failed", task.getException());
                                    return;
                                }

                                // Get new FCM registration token
                                String token = task.getResult();
                                Log.d("token: ", token);
                            }
                        });
            }
        }).start();
    }

    private void getToken_1(){

        DialogProgress dialogProgress = new DialogProgress();
        dialogProgress.show(getSupportFragmentManager(), "2");

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {

            @Override
            public void onComplete(@NonNull Task<String> task) {

                dialogProgress.dismiss(); // finalizando dialogProgress

                if(task.isSuccessful()){
                    String token = task.getResult().toString();
                }else{
                    Log.d("erro: ", "token");
                }
            }
        });

    }

    // enviando notificacao personalizado
    private void sendNotification(String site, JSONObject notification) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // enviando as variaveis servidor firebase
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, site, notification,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null) {
                            Log.i("response", "response: " + response);
                        } else {
                            Log.i("response null", "response: Data Null");
                        }
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error is ", "" + error);
                        error.printStackTrace();
                    }

                })
        {
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> header = new HashMap<String, String>();

                header.put("Content-Type", "application/json");
                header.put("Authorization", "key=AAAAJesR9bQ:APA91bHbAHIBTacMLA6iX4to0jIMhcdfv9Avcuvdz3mzeGOlbTzKvsU40YA1DNi5PFCKjuZjZSLjkXFdt08r0KM0qAwfN_fD1sLHVAgyxjz-ZItZ4_bqWilfEK-4XNZn9zfPAWpcRmAH");

                return header;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private void switchSports() {

        if(Util.statusInternet_MoWi(getBaseContext())){
            notifySports();
        }else{
            //switch_Sports.setChecked(false);
            configurationSwitch();
            Toast.makeText(getBaseContext(), "Sem conexão com a internet - Verifique o Wifi ou 3G", Toast.LENGTH_LONG).show();
        }
    }

    private void notifySports() {

        FirebaseApp.initializeApp(this);

        boolean statusSports = switch_Sports.isChecked();

        if(statusSports){

            FirebaseMessaging.getInstance().subscribeToTopic("sports").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isComplete()){
                        Toast.makeText(getBaseContext(), "Sports Realizado com Sucesso", Toast.LENGTH_LONG).show();
                        Util.setTopic(getBaseContext(), "sports", "sports");  // savando se switch esta true ou false
                    }else{
                        //switch_Sports.setChecked(false); // esse comando nao vai deixar switch ficar true
                        configurationSwitch();
                        Toast.makeText(getBaseContext(), "Erro - Inscrição em Sports não sucedida", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }else{  // Cancelada Sports

            FirebaseMessaging.getInstance().unsubscribeFromTopic("sports").addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isComplete()){
                        Toast.makeText(getBaseContext(), "Inscrição Sports Cancelada", Toast.LENGTH_LONG).show();
                        Util.setTopic(getBaseContext(), "sports", "");  // savando se switch esta true ou false
                    }else{
                        //switch_Sports.setChecked(true); // esse comando nao vai deixar switch ficar true
                        configurationSwitch();
                        Toast.makeText(getBaseContext(), "Erro - Cancelamento de Inscrição em Sports não sucedida", Toast.LENGTH_LONG).show();
                    }
                }
            });

            //FirebaseMessaging.getInstance().unsubscribeFromTopic("sports");
        }
    }

    // ------------------------------------------ switchPolicy ------------------------------------------

    private void switchPolicy(){

        if(Util.statusInternet_MoWi(getBaseContext())){
            notifyPolicy();
        }else{
            //switch_Politica.setChecked(false);
            configurationSwitch();
            Toast.makeText(getBaseContext(), "Sem conexão com a internet - Verifique o Wifi ou 3G", Toast.LENGTH_LONG).show();
        }
    }

    private void notifyPolicy(){

        boolean statusPolitica = switch_Politica.isChecked();

        if(statusPolitica){

            FirebaseApp.initializeApp(this);

            FirebaseMessaging.getInstance().subscribeToTopic("Politica").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isComplete()){
                        Toast.makeText(getBaseContext(), "Inscrição Politica Realizado com Sucesso", Toast.LENGTH_LONG).show();
                        Util.setTopic(getBaseContext(), "Politica", "Politica");  // savando se switch esta true ou false
                    }else{
                        //switch_Politica.setChecked(false); // esse comando nao vai deixar switch ficar true
                        configurationSwitch();
                        Toast.makeText(getBaseContext(), "Erro - Inscrição em Politica não sucedida", Toast.LENGTH_LONG).show();
                    }

                }
            });

        }else{  // Cancelada Politica

            FirebaseMessaging.getInstance().unsubscribeFromTopic("Politica").addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isComplete()){
                        Toast.makeText(getBaseContext(), "Inscrição Sports Cancelada", Toast.LENGTH_LONG).show();
                        Util.setTopic(getBaseContext(), "Politica", ""); // savando se switch esta true ou false
                    }else{
                        //switch_Politica.setChecked(true); // esse comando nao vai deixar switch ficar true
                        configurationSwitch();
                        Toast.makeText(getBaseContext(), "Erro - Cancelamento de Inscrição em Politica não sucedida", Toast.LENGTH_LONG).show();
                    }

                }
            });

            //FirebaseMessaging.getInstance().unsubscribeFromTopic("Politica");
        }
    }
}