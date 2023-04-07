package com.example.firebasecursonotificacao.Firebase;

import static android.provider.Settings.System.getString;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.firebasecursonotificacao.MainActivity;
import com.example.firebasecursonotificacao.NotificationActivity;
import com.example.firebasecursonotificacao.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

public class FirebaseService extends FirebaseMessagingService {

    // essa metado so vai ser executavel quando app teve em primeiro plano
    @Override
    public void onMessageReceived(@NonNull RemoteMessage Msg) {

        if(Msg.getData().size() > 0 ){// esse codigo é executado quando a notificacao vem de outro celular

            String msg = Msg.getData().get("mensagem");
            String titulo = Msg.getData().get("titulo");
            String nome = Msg.getData().get("nome");

            String urlimagem = Msg.getData().get("urlimagem");

            String mensagem = msg+ " -- "+ nome + " -- "+ urlimagem;

            Log.d("jafapps.com", titulo);
            Log.d("jafapps.com", mensagem);

            sendNotification_1(titulo,mensagem, urlimagem);

        }else if (Msg.getNotification() != null){// esse codigo é executado qunado a notificacao vem do firebase

            Log.d("jafapps.com", Msg.getNotification().getTitle());
            Log.d("jafapps.com", Msg.getNotification().getBody());

            String titulo = Msg.getNotification().getTitle();
            String msg = Msg.getNotification().getBody();

            sendNotification_0(titulo, msg);
        }
    }

    private void sendNotification_0(final String title,final String msg){

        final int id = (int) (System.currentTimeMillis() / 1000);

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Se a atividade iniciada já estiver em execução na tarefa atual

        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), id, intent, PendingIntent.FLAG_ONE_SHOT);

        String canal = getString(R.string.default_notification_channel_id);

        Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // criando configuracao notificacao
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getBaseContext(), canal)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title).setContentText(msg)
                .setSound(som).setAutoCancel(true).setContentIntent(pendingIntent);

        // criando gereciador notificacao
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){  // verificando versao android pra cima 25
            NotificationChannel channel = new NotificationChannel(canal, "canal", NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(id, notification.build());
    }

    private void sendNotification_1(final String title, final String msg, final String url){

        final int id = (int) (System.currentTimeMillis() / 1000);

        Glide.with(getBaseContext()).asBitmap().load(url).listener(new RequestListener<Bitmap>() {

            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap bitmap, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                // action open

                Log.d("send ", "sendNotification_1 title: " + title);
                Log.d("send ", "sendNotification_1 msg: " + msg);
                Log.d("send ", "sendNotification_1 url: " + url);

                Intent intent_Abrir = new Intent(getBaseContext(), NotificationActivity.class);

                intent_Abrir.putExtra("url", url);
                intent_Abrir.putExtra("mensagem", msg);
                intent_Abrir.putExtra("idNotificacao", id);

                intent_Abrir.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent_Abrir = PendingIntent.getActivity(getBaseContext(), id, intent_Abrir, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Action abrir = new NotificationCompat.Action(R.drawable.ic_baseline_lock_open_24,"Abrir",pendingIntent_Abrir);

                // action de close

                Intent intent_Fechar = new Intent(getBaseContext(), NotificationBroadcast.class);

                intent_Fechar.putExtra("idNotificacao", id);
                intent_Fechar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pending_Fechar = PendingIntent.getBroadcast(getBaseContext(), id, intent_Fechar, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Action fechar = new NotificationCompat.Action(0,"Fechar", pending_Fechar);

                // ==========================================================================================

                Intent intent = new Intent(getBaseContext(), NotificationActivity.class);

                intent.putExtra("url", url);
                intent.putExtra("mensagem", msg);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), id, intent, PendingIntent.FLAG_ONE_SHOT);

                String canal = getString(R.string.default_notification_channel_id);

                // Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                Uri som = Uri.parse("android:resource://"+getApplicationContext().getPackageName() + "/" + R.raw.notificationsom);

                NotificationCompat.Builder notification = new NotificationCompat.Builder(getBaseContext(),canal)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setSound(som)
                        .setAutoCancel(true)
                        .setLargeIcon(bitmap)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                        //.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .addAction(abrir)
                        .addAction(fechar)
                        .setVibrate(new long[]  {1000,1000})
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                    NotificationChannel channel = new NotificationChannel(canal,"canal", NotificationManager.IMPORTANCE_DEFAULT);

                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build();

                    channel.setSound(som, audioAttributes);
                    channel.setVibrationPattern(new long[] {1000,1000});

                    notificationManager.createNotificationChannel(channel);
                }

                notificationManager.notify(id, notification.build());

                return false;
            }
        }).submit();

        /*

        Bitmap bitmap = null;

        try {
            bitmap = Picasso.get().load(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

         */
    }
    

    @Override
    public void onNewToken(@NonNull String token) {

        //FirebaseMessaging.getInstance().getToken();
        //FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        Log.d("jonejaf_token", "jonejaf_token: " + token);
        //System.out.println("jonejaf_token: " + token);

        super.onNewToken(token);
    }
}