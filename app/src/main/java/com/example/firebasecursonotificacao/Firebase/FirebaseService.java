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

        FirebaseApp.initializeApp(this);

        if(Msg.getData().size() > 0){

            String msg = Msg.getData().get("mensagem");
            String title = Msg.getData().get("titulo");
            String name = Msg.getData().get("nome");

            String url = Msg.getData().get("urlimagem");

            String mensagem = msg + " -- " + name + " -- " + url;

            sendNotification_1(title, mensagem, url);

        }else if(Msg.getNotification() != null){

            Log.d("jafapps.com ", Msg.getNotification().getTitle());
            Log.d("jafapps.com ", Msg.getNotification().getBody());

            String title = Msg.getNotification().getTitle();
            String msg = Msg.getNotification().getBody();

            sendNotification_0(title, msg);
        }
    }

    private void sendNotification_0(final String title,final String msg){

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Se a atividade iniciada já estiver em execução na tarefa atual

        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

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

        notificationManager.notify(0, notification.build());
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

                //Ação de abrir

                Intent intent_open = new Intent(getBaseContext(), NotificationActivity.class);

                // colocando valores
                intent_open.putExtra("url", url);
                intent_open.putExtra("mensagem", msg);
                intent_open.putExtra("idNotificacao", id);

                intent_open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Se a atividade iniciada já estiver em execução na tarefa atual

                PendingIntent pendingIntent_open = PendingIntent.getActivity(getBaseContext(), id, intent_open, PendingIntent.FLAG_UPDATE_CURRENT);

                //criando botao notificacao
                NotificationCompat.Action open = new NotificationCompat.Action(R.drawable.ic_baseline_adb_24, "open", pendingIntent_open);

                // ----------------------------------------------

                //Ação de fechar

                Intent intent_close = new Intent(getBaseContext(), NotificationBroadcast.class);

                intent_close.putExtra("idNotificacao", id);
                intent_close.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent_close = PendingIntent.getBroadcast(getBaseContext(), id, intent_close, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Action close = new NotificationCompat.Action(0, "close", pendingIntent_close);

                // ----------------------------------------------

                Intent intent = new Intent(getBaseContext(), NotificationActivity.class);

                // colocando valores
                intent.putExtra("url", url);
                intent.putExtra("mensagem", msg);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Se a atividade iniciada já estiver em execução na tarefa atual

                PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), id, intent, PendingIntent.FLAG_ONE_SHOT);

                String canal = getString(R.string.default_notification_channel_id);

                //Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                // colocando som notificacao baixando no site https://notificationsounds.com
                Uri som = Uri.parse("android:resource://" + getApplicationContext().getPackageName() + "/" + R.raw.notificationsom);

                // criando configuracao notificacao personalizada
                NotificationCompat.Builder notification = new NotificationCompat.Builder(getBaseContext(), canal)
                        .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title).setContentText(msg)
                        .setSound(som).setAutoCancel(true).setLargeIcon(bitmap)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                        //.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .addAction(open).addAction(close)
                        .setVibrate(new long[] {1000, 1000})
                        .setContentIntent(pendingIntent);

                // criando gereciador notificacao
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){  // verificando versao android pra cima 25
                    NotificationChannel channel = new NotificationChannel(canal, "canal", NotificationManager.IMPORTANCE_DEFAULT);

                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM).build();

                    channel.setSound(som, audioAttributes);
                    channel.setVibrationPattern(new long[] {1000, 1000}); // fazer ceular vibrar nas vercao mais novas android

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