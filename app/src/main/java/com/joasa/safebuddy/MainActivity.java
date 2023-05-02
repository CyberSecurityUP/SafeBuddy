package com.joasa.safebuddy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_ID = 1;
    private ListView lvTrustedContacts;
    private List<String> trustedContacts;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Adicione isso dentro do método onCreate
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Certifique-se de que esta linha esteja antes de findViewById
        Button btnAddContact = findViewById(R.id.btnAddContact);

        EditText etName = findViewById(R.id.etName);
        EditText etPhoneNumber = findViewById(R.id.etPhoneNumber);
        Button btnSaveContact = findViewById(R.id.btnSaveContact);


        btnSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String phoneNumber = etPhoneNumber.getText().toString().trim();

                if (!name.isEmpty() && !phoneNumber.isEmpty()) {
                    String contact = name + " - " + phoneNumber;
                    trustedContacts.add(contact);
                    adapter.notifyDataSetChanged();
                    etName.setText("");
                    etPhoneNumber.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Por favor, preencha os campos Nome e Número de Telefone.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Button btnAddContact = findViewById(R.id.btnAddContact);
        Button btnSendWhatsApp = findViewById(R.id.btnSendWhatsApp);
        Button btnCallEmergency = findViewById(R.id.btnCallEmergency);

        // btnAddContact.setOnClickListener(new View.OnClickListener() {
           //  @Override
            // public void onClick(View v) {
                // Adicione a lógica para adicionar contatos de confiança
            // }
        // });

        btnSendWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageViaWhatsApp();
            }
        });

        btnCallEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEmergency();
            }
        });

        // Iniciar a notificação perguntando se está tudo bem a cada 5 minutos
        startWellBeingNotifications();
    }

    private void startWellBeingNotifications() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "wellbeing_channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Está tudo bem?")
                .setContentText("Toque aqui para confirmar que está tudo bem.")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5 * 60 * 1000); // A cada 5 minutos
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }
            }
        }).start();
    }

    private void sendMessageViaWhatsApp() {
        String message = "Mensagem automática de verificação de segurança.";

        for (String contact : trustedContacts) {
            // O número de telefone deve estar no formato internacional, e.g. "+55xxxxxxxxx"
            String phoneNumber = contact.split(" - ")[1];
            String uri = "https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + Uri.encode(message);
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(sendIntent);
        }
    }

    private void callEmergency() {
        // Altere o número de telefone de emergência conforme necessário
        String emergencyNumber = "911";
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + emergencyNumber));
        startActivity(callIntent);
    }
}
