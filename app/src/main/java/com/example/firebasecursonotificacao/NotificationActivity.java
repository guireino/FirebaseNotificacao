package com.example.firebasecursonotificacao;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class NotificationActivity extends AppCompatActivity {

    private ImageView imgView;
    private TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        imgView = (ImageView)findViewById(R.id.imgView_Notification);
        txtView = (TextView)findViewById(R.id.txtView_Notification);

        String url = getIntent().getStringExtra("url");
        String msg = getIntent().getStringExtra("mensagem");

        int id = getIntent().getIntExtra("idNotificacao",-1);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(id);

        showNotification(url, msg);
    }

    private void showNotification(String url, String msg) {

        Picasso.get().load(url).into(imgView);
        txtView.setText(msg);
    }
}