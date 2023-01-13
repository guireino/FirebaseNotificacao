package com.example.firebasecursonotificacao.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class Util {

    public static boolean statusInternet_MoWi(Context context) {

        ConnectivityManager conexao = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conexao != null) {

            // PARA DISPOSTIVOS NOVOS
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                NetworkCapabilities recursosRede = conexao.getNetworkCapabilities(conexao.getActiveNetwork());

                if (recursosRede != null) {//VERIFICAMOS SE RECUPERAMOS ALGO

                    if (recursosRede.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {

                        //VERIFICAMOS SE DISPOSITIVO TEM 3G
                        return true;

                    } else if (recursosRede.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {

                        //VERIFICAMOS SE DISPOSITIVO TEM WIFFI
                        return true;
                    }
                    //NÃO POSSUI UMA CONEXAO DE REDE VÁLIDA
                    return false;
                }

            } else {//COMECO DO ELSE

                // PARA DISPOSTIVOS ANTIGOS  (PRECAUÇÃO)
                NetworkInfo informacao = conexao.getActiveNetworkInfo();

                if (informacao != null && informacao.isConnected()) {
                    return true;
                }else{
                    return false;
                }

            }
        }

        return false;
    }

    public static String getTopic(Context context, String id, String nameTopic){

        // esse comando vai faz ter acesso arquivo do id
        SharedPreferences preferences = context.getSharedPreferences(nameTopic,0);

        String value = preferences.getString(id,"");

        return value;
    }

    public static void setTopic(Context context, String id, String nameTopic){

        // esse comando vai faz ter acesso arquivo do id
        SharedPreferences preferences = context.getSharedPreferences(id,0);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(id, nameTopic);
        editor.commit();
    }

}